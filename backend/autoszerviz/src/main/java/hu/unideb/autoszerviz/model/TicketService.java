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
public class TicketService {
    private static final List<TicketStatus> BLOCKING_STATUSES = List.of(
                                TicketStatus.ACCEPTED,
                                TicketStatus.IN_PROGRESS,
                                TicketStatus.WAITING_FOR_PARTS,
                                TicketStatus.AWAITING_CUSTOMER);

    private final AccessService access;
    private final TicketViewMapper ticketMapper;
    private final UserRepository users;
    private final ServiceTypeRepository services;
    private final TicketRepository tickets;

    public TicketService(AccessService access, TicketViewMapper ticketMapper, UserRepository users, ServiceTypeRepository services, TicketRepository tickets) {
        this.access = access;
        this.ticketMapper = ticketMapper;
        this.users = users;
        this.services = services;
        this.tickets = tickets;
    }


    // Ticket elfogadás (szerelőhöz rendelés)
    private void acceptTicket(Ticket ticket, User mechanic) {
        boolean alreadyAccepted = ticket.status != TicketStatus.PENDING;
        boolean hasScheduleConflict = tickets.mechanicOverlap(mechanic.id, BLOCKING_STATUSES, ticket.startAt, ticket.endAt);
        if (alreadyAccepted || hasScheduleConflict)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A ticketet már elfogadták, vagy ütközik egy saját időpontoddal.");
        
        ticket.mechanic = mechanic;
        ticket.status = TicketStatus.ACCEPTED;
    }

    // Szerelőhöz rendelt ticket státuszmódosítása
    private void updateAssignedTicket(Ticket ticket, User mechanic, TicketStatus nextStatus) {
        checkAssignedMechanic(ticket, mechanic);
        validateStatusTransition(ticket.status, nextStatus);

        ticket.status = nextStatus;
        if (nextStatus == TicketStatus.PENDING)
            ticket.mechanic = null;
        if (nextStatus == TicketStatus.AWAITING_CUSTOMER)
            ticket.closedAt = LocalDateTime.now();
    }

    // Ellenőrzi hogy a szerelőhöz tartozik-e a ticket
    private void checkAssignedMechanic(Ticket ticket, User mechanic) {
        boolean isAssignedToMechanic = ticket.mechanic != null && ticket.mechanic.id.equals(mechanic.id);
        if (!isAssignedToMechanic)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Csak a hozzárendelt szerelő módosíthatja ezt a munkát.");
    }

    // Ellenőrzi a státuszváltás érvényességét
    private void validateStatusTransition(TicketStatus currentStatus, TicketStatus nextStatus) {
        boolean isValidTransition = switch (currentStatus) {
            case ACCEPTED -> nextStatus == TicketStatus.IN_PROGRESS || nextStatus == TicketStatus.PENDING;
            case IN_PROGRESS ->nextStatus == TicketStatus.WAITING_FOR_PARTS || nextStatus == TicketStatus.AWAITING_CUSTOMER;
            case WAITING_FOR_PARTS -> nextStatus == TicketStatus.IN_PROGRESS;
            case COMPLAINT -> nextStatus == TicketStatus.IN_PROGRESS;
            default -> false;
        };
        if (!isValidTransition)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Érvénytelen státuszváltás.");
        
    }

    // Ügyfél jogosultságának ellenőrzése
    private void checkCustomerOwnership(Ticket ticket, User customer) {
        if (!customer.id.equals(ticket.customer.id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    // Foglalás részleteinek megtekintési jogosultsága
    private void checkDetailPermission(Ticket ticket, User user) {
        if (user.role == Role.CUSTOMER)
            checkCustomerOwnership(ticket, user);

        if (user.role == Role.MECHANIC && ticket.status != TicketStatus.PENDING && ticket.mechanic != null && !user.id.equals(ticket.mechanic.id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    // Foglalás lekérdezése státusz alapján
    private List<Ticket> findTicketsByStatus(String status) {
        if (status == null)
            return tickets.findAllByOrderByCreatedAtDesc();

        TicketStatus ticketStatus = TicketStatus.valueOf(status);
        return tickets.findByStatusOrderByCreatedAtDesc(ticketStatus);
    }

    // Foglalások szűrése a felhasználó jogosultsága alapján
    private List<Ticket> filterTicketsByUser(List<Ticket> ticketList, User user) {
        if (user.role == Role.ADMIN)
            return ticketList;

        return ticketList.stream()
                .filter(ticket -> ticket.status == TicketStatus.PENDING
                                || (ticket.mechanic != null && user.id.equals(ticket.mechanic.id)))
                .toList();
    }





    // Ticketek listája státusz és jog alapján
    public List<Map<String, Object>> list(String status, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.MECHANIC, Role.ADMIN);

        List<Ticket> ticketList = findTicketsByStatus(status);
        return filterTicketsByUser(ticketList, user).stream()
                .map(ticketMapper::toView)
                .toList();
    }

    // Ticket adatainak lekérdezése
    public Map<String, Object> detail(Long id, HttpSession session) {
        User user = access.currentUser(session);
        Ticket ticket = tickets.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        checkDetailPermission(ticket, user);
        
        return ticketMapper.toView(ticket);
    }

    // Ticket státusz módosítés
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> status(Long id, Map<String, String> body, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.MECHANIC, Role.ADMIN);
        services.lockAllForBooking();

        Ticket ticket = tickets.lockById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TicketStatus nextStatus = TicketStatus.valueOf(body.get("status"));
        if (nextStatus == TicketStatus.ACCEPTED)
            acceptTicket(ticket, user);
        else
            updateAssignedTicket(ticket, user, nextStatus);

        return ticketMapper.toView(ticket);
    }


    @Transactional
    public Map<String, Object> confirm(Long id, Map<String, Boolean> body, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);

        Ticket ticket = tickets.lockById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        checkCustomerOwnership(ticket, user);
        if (ticket.status != TicketStatus.AWAITING_CUSTOMER)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ez a munka nem vár visszaigazolásra.");

        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        ticket.status = approved ? TicketStatus.RESOLVED : TicketStatus.COMPLAINT;
        
        return ticketMapper.toView(ticket);
    }
}
