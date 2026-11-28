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
  private static final List<TicketStatus> BLOCKING = List.of(
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

    public List<Map<String, Object>> list(String status, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.MECHANIC, Role.ADMIN);
        List<Ticket> ts = status == null ? tickets.findAllByOrderByCreatedAtDesc() : tickets.findByStatusOrderByCreatedAtDesc(TicketStatus.valueOf(status));
        return (user.role == Role.ADMIN ? ts : ts.stream()
                    .filter(ticket -> ticket.status == TicketStatus.PENDING || user.id.equals(ticket.mechanic == null ? null : ticket.mechanic.id))
                    .toList())
            .stream().map(ticketMapper::toView).toList();
    }

    public Map<String, Object> detail(Long id, HttpSession session) {
        User user = access.currentUser(session);
        Ticket ticket = tickets.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (user.role == Role.CUSTOMER && !user.id.equals(ticket.customer.id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (user.role == Role.MECHANIC && ticket.status != TicketStatus.PENDING && ticket.mechanic != null && !user.id.equals(ticket.mechanic.id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return ticketMapper.toView(ticket);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> status(Long id, Map<String, String> body, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.MECHANIC, Role.ADMIN);
        services.lockAllForBooking();

        Ticket ticket = tickets.lockById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        TicketStatus next = TicketStatus.valueOf(body.get("status"));
        if (next == TicketStatus.ACCEPTED) {
            if (ticket.status != TicketStatus.PENDING || tickets.mechanicOverlap(user.id, BLOCKING, ticket.startAt, ticket.endAt))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A ticketet már elfogadták, vagy ütközik a saját időpontoddal.");
            ticket.mechanic = user;
            ticket.status = next;
        }
        else {
            if (ticket.mechanic == null || !ticket.mechanic.id.equals(user.id))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Csak a hozzárendelt szerelő módosíthatja ezt a munkát.");
            boolean ok = switch (ticket.status) {
                case ACCEPTED -> next == TicketStatus.IN_PROGRESS || next == TicketStatus.PENDING;
                case IN_PROGRESS -> next == TicketStatus.WAITING_FOR_PARTS || next == TicketStatus.AWAITING_CUSTOMER;
                case WAITING_FOR_PARTS -> next == TicketStatus.IN_PROGRESS;
                case COMPLAINT -> next == TicketStatus.IN_PROGRESS;
                default -> false;
            };
            if (!ok)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Érvénytelen státuszváltás.");
            ticket.status = next;
            if (next == TicketStatus.PENDING)
                ticket.mechanic = null;
            if (next == TicketStatus.AWAITING_CUSTOMER)
                ticket.closedAt = LocalDateTime.now();
        }

        return ticketMapper.toView(ticket);
    }

    @Transactional
    public Map<String, Object> confirm(Long id, Map<String, Boolean> body, HttpSession session) {
        User user = access.currentUser(session);
        access.requireRole(user, Role.CUSTOMER);

        Ticket ticket = tickets.lockById(id).orElseThrow();
        if (!user.id.equals(ticket.customer.id) || ticket.status != TicketStatus.AWAITING_CUSTOMER)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ez a munka nem vár visszaigazolásra.");

        ticket.status = Boolean.TRUE.equals(body.get("approved")) ? TicketStatus.RESOLVED : TicketStatus.COMPLAINT;
        
        return ticketMapper.toView(ticket);
    }
}
