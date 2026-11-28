package hu.unideb.autoszerviz.model;

import hu.unideb.autoszerviz.entity.*;
import hu.unideb.autoszerviz.repository.*;
import jakarta.servlet.http.HttpSession;
import java.time.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final AccessService access;
    private final TicketViewMapper ticketMapper;
    private final TicketRepository tickets;
    private final VehicleRepository vehicles;

    public DashboardService(AccessService access, TicketViewMapper ticketMapper, TicketRepository tickets, VehicleRepository vehicles) {
        this.access = access;
        this.ticketMapper = ticketMapper;
        this.tickets = tickets;
        this.vehicles = vehicles;
    }

    public Map<String, Object> dashboard(HttpSession session) {
        User user = access.currentUser(session);
        if (user.role == Role.CUSTOMER) {
            List<Ticket> mine = tickets.findByCustomerIdOrderByStartAtDesc(user.id);
            return Map.of(
                "vehicles", vehicles.countByOwnerIdAndActiveTrue(user.id),

                "active", mine.stream()
                    .filter(ticket -> !List.of(TicketStatus.RESOLVED, TicketStatus.CANCELLED)
                    .contains(ticket.status))
                    .count(),

                "closed", mine.stream()
                    .filter(ticket -> ticket.status == TicketStatus.RESOLVED)
                    .count(),

                "recent", mine.stream()
                    .sorted(Comparator.comparing((Ticket ticket) -> ticket.createdAt).reversed())
                    .limit(5)
                    .map(ticketMapper::toView)
                    .toList());
        }

        access.requireRole(user, Role.MECHANIC, Role.ADMIN);
        List<Ticket> all = user.role == Role.ADMIN ? tickets.findAllByOrderByCreatedAtDesc() : tickets.findAllByOrderByCreatedAtDesc().stream()
            .filter(ticket -> ticket.status == TicketStatus.PENDING || user.id.equals(ticket.mechanic == null ? null : ticket.mechanic.id))
            .toList();
        return Map.of(
            "incoming", all.stream()
                .filter(ticket -> ticket.status == TicketStatus.PENDING).count(),

            "active", all.stream()
                .filter(ticket -> List.of(TicketStatus.ACCEPTED, TicketStatus.IN_PROGRESS, TicketStatus.WAITING_FOR_PARTS).contains(ticket.status))
                .count(),

            "waiting", all.stream()
                .filter(ticket -> ticket.status == TicketStatus.AWAITING_CUSTOMER)
                .count(),

            "recent", all.stream()
                .filter(ticket -> ticket.status == TicketStatus.PENDING)
                .limit(7)
                .map(ticketMapper::toView)
                .toList());
    }
}
