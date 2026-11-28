package hu.unideb.autoszerviz.model;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.*;
import jakarta.servlet.http.HttpSession;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {
    private static final List<TicketStatus> HOLDING = List.of(
                                    TicketStatus.PENDING,
                                    TicketStatus.ACCEPTED,
                                    TicketStatus.IN_PROGRESS,
                                    TicketStatus.WAITING_FOR_PARTS,
                                    TicketStatus.AWAITING_CUSTOMER);

    private final AccessService access;
    private final TicketViewMapper ticketMapper;
    private final UserRepository users;
    private final VehicleRepository vehicles;
    private final ServiceTypeRepository services;
    private final TicketRepository tickets;

    public BookingService(AccessService access, TicketViewMapper ticketMapper, UserRepository users, VehicleRepository vehicles, ServiceTypeRepository services, TicketRepository tickets) {
        this.access = access;
        this.ticketMapper = ticketMapper;
        this.users = users;
        this.vehicles = vehicles;
        this.services = services;
        this.tickets = tickets;
    }

    public List<Map<String, Object>> mine(HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);
        return tickets.findByCustomerIdOrderByStartAtDesc(user.id).stream()
            .sorted(Comparator.comparingLong(t -> Math.abs(Duration.between(LocalDateTime.now(), t.startAt).toMinutes())))
            .map(ticketMapper::toView)
            .toList();
    }

    public List<String> availability(Long serviceId, String date, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);
        ServiceType svc = services.findById(serviceId).orElseThrow();
        LocalDate d = LocalDate.parse(date);
        if (d.getDayOfWeek().getValue() > 5) return List.of();

        long capacity = users.findAll().stream().filter(mech -> mech.active && mech.role == Role.MECHANIC).count();

        List<String> slots = new ArrayList<>();
        for (LocalTime tm = LocalTime.of(8, 0); !tm.plusMinutes(svc.durationMinutes).isAfter(LocalTime.of(17, 0)); tm = tm.plusMinutes(30)) {
            LocalDateTime start = d.atTime(tm);
            LocalDateTime end = start.plusMinutes(svc.durationMinutes);
            if (start.isBefore(LocalDateTime.now().plusHours(1)))
                continue;

            long used = tickets.findAll().stream()
                    .filter(t -> HOLDING.contains(t.status) && t.startAt.isBefore(end) && t.endAt.isAfter(start))
                    .count();
            if (capacity > used)
                slots.add(tm.toString());
        }

        return slots;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> create(Map<String, Object> body, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);
        Vehicle vehicle = vehicles
            .findByIdAndOwnerIdAndActiveTrue(Long.valueOf(body.get("vehicleId").toString()), user.id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Válassz saját járművet."));

        List<ServiceType> lockedServices = services.lockAllForBooking();
        ServiceType svc = lockedServices.stream()
            .filter(x -> x.id.equals(Long.valueOf(body.get("serviceId").toString())))
            .findFirst()
            .orElseThrow();

        LocalDateTime start = LocalDateTime.parse((String) body.get("startAt"));
        if (start.isBefore(LocalDateTime.now().plusHours(1))
                || start.getDayOfWeek().getValue() > 5
                || start.toLocalTime().isBefore(LocalTime.of(8, 0))
                || start.toLocalTime().plusMinutes(svc.durationMinutes).isAfter(LocalTime.of(17, 0))
                || start.getMinute() % 30 != 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Válassz legalább 1 órával korábbi, hétköznapi, nyitvatartási időn belüli időponmtot.");
        LocalDateTime end = start.plusMinutes(svc.durationMinutes);

        long capacity = users.findAll().stream().filter(mech -> mech.active && mech.role == Role.MECHANIC).count();
        if (capacity == 0
                || tickets.findAll().stream()
                    .filter(t -> HOLDING.contains(t.status))
                    .filter(t -> t.startAt.isBefore(end) && t.endAt.isAfter(start))
                    .count()
                >= capacity)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Erre az időpontra nincs szabad szervizkapacitás.");
    
        Ticket ticket = new Ticket();
        ticket.customer = user;
        ticket.vehicle = vehicle;
        ticket.serviceType = svc;
        ticket.startAt = start;
        ticket.endAt = end;
        ticket.description = (String) body.getOrDefault("description", "");
        tickets.save(ticket);
        return ticketMapper.toView(ticket);
    }

    @Transactional
    public void cancel(Long id, HttpSession session) {
        User user = access.currentUser(session);
        Ticket ticket = tickets.lockById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!user.id.equals(ticket.customer.id) || ticket.status != TicketStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Csak függőben lévő foglalás mondható le.");
        ticket.status = TicketStatus.CANCELLED;
    }
}
