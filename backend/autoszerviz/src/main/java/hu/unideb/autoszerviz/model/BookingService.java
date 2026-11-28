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
    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(17, 0);

    private static final int SLOT_INTERVAL_MINUTES = 30;
    private static final int MIN_BOOKING_ADVANCE_HOURS = 1;
    private static final int LAST_WORKDAY = 5;

    private static final List<TicketStatus> RESERVED_BOOKING_STATUSES = List.of(
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


    private boolean isWeekend(LocalDate date) { return date.getDayOfWeek().getValue() > LAST_WORKDAY; }
    private boolean isOutsideWorkingHours(LocalDateTime start, ServiceType svc) {
        LocalTime end = start.toLocalTime().plusMinutes(svc.durationMinutes);
        return start.toLocalTime().isBefore(OPENING_TIME) || end.isAfter(CLOSING_TIME);
    }
    private boolean isTooSoon(LocalDateTime start) { return start.isBefore(LocalDateTime.now().plusHours(MIN_BOOKING_ADVANCE_HOURS)); }
    
    private boolean overlaps(Ticket ticket, LocalDateTime start, LocalDateTime end) { return ticket.startAt.isBefore(end) && ticket.endAt.isAfter(start); }
    private long countOverlappingBookings(LocalDateTime start, LocalDateTime end) {
        return tickets.findAll().stream()
                .filter(ticket -> RESERVED_BOOKING_STATUSES.contains(ticket.status))
                .filter(ticket -> overlaps(ticket, start, end))
                .count();
    }

    private long countActiveMechanics() {
        return users.findAll().stream()
                .filter(user -> user.active && user.role == Role.MECHANIC)
                .count();
    }





    // Saját foglalások
    public List<Map<String, Object>> mine(HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);
        LocalDateTime now = LocalDateTime.now();
        return tickets.findByCustomerIdOrderByStartAtDesc(user.id).stream()
                .sorted(Comparator.comparingLong(ticket -> Math.abs(Duration.between(now, ticket.startAt).toMinutes())))
                .map(ticketMapper::toView)
                .toList();
    }

    // Szabad időpontok
    public List<String> availability(Long serviceId, String date, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);
        ServiceType svc = services.findById(serviceId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "A szolgáltatás nem található."));

        LocalDate selectedDate = LocalDate.parse(date);
        if (isWeekend(selectedDate))
            return List.of();

        long capacity = countActiveMechanics();
        if (capacity == 0)
            return List.of();

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> existingTickets = tickets.findAll().stream()
                .filter(ticket -> RESERVED_BOOKING_STATUSES.contains(ticket.status))
                .toList();
        List<String> availableSlots = new ArrayList<>();
        for (LocalTime startTime = OPENING_TIME; !startTime.plusMinutes(svc.durationMinutes).isAfter(CLOSING_TIME); startTime = startTime.plusMinutes(SLOT_INTERVAL_MINUTES)) {
            LocalDateTime start = selectedDate.atTime(startTime);
            LocalDateTime end = start.plusMinutes(svc.durationMinutes);
            if (isTooSoon(start))
                continue;

            long reserved = existingTickets.stream()
                    .filter(ticket -> overlaps(ticket, start, end))
                    .count();
            if (capacity > reserved)
                availableSlots.add(startTime.toString());
        }

        return availableSlots;
    }

    // Új foglalás
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> create(Map<String, Object> body, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);

        Long vehicleId = Long.valueOf(body.get("vehicleId").toString());
        Vehicle vehicle = vehicles
            .findByIdAndOwnerIdAndActiveTrue(vehicleId, user.id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Válassz saját járművet."));

        Long serviceId = Long.valueOf(body.get("serviceId").toString());
        ServiceType svc = services.lockAllForBooking().stream()
                .filter(s -> s.id.equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "A szolgáltatás nem található."));

        LocalDateTime start = LocalDateTime.parse((String) body.get("startAt"));
        LocalDateTime end = start.plusMinutes(svc.durationMinutes);
        boolean invalidDate = isWeekend(start.toLocalDate());
        boolean invalidTime = isOutsideWorkingHours(start, svc);
        boolean invalidAdvance = isTooSoon(start);
        boolean invalidSlot = start.getMinute() % SLOT_INTERVAL_MINUTES != 0;
        if (invalidDate || invalidTime || invalidAdvance || invalidSlot) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Válassz legalább 1 órával későbbi, hétköznapi, nyitvatartási időn belüli időponmtot.");
        
        long capacity = countActiveMechanics();
        long reserved = countOverlappingBookings(start, end);
        if (capacity == 0 || reserved >= capacity)
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

    // Foglalás lemondás
    @Transactional
    public void cancel(Long id, HttpSession session) {
        User user = access.currentUser(session);

        Ticket ticket = tickets.lockById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!user.id.equals(ticket.customer.id) || ticket.status != TicketStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Csak függőben lévő foglalás mondható le.");
        ticket.status = TicketStatus.CANCELLED;
    }
}
