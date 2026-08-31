package com.helpdesk.service;

import com.helpdesk.model.*;
import com.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getRecentTickets() {
        return ticketRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<Ticket> searchTickets(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllTickets();
        }
        return ticketRepository.searchTickets(query.trim());
    }

    public List<Ticket> filterByStatus(Status status) {
        return ticketRepository.findByStatus(status);
    }

    public List<Ticket> filterByPriority(Priority priority) {
        return ticketRepository.findByPriority(priority);
    }

    @Transactional
    public Ticket createTicket(Ticket ticket) {
        if (ticket.getTicketNumber() == null || ticket.getTicketNumber().isEmpty()) {
            ticket.setTicketNumber("TICK-" + (1000 + (long)(Math.random() * 9000)));
        }
        if (ticket.getStatus() == null) {
            ticket.setStatus(Status.OPEN);
        }
        log.info("Creating new ticket: {} by {}", ticket.getTicketNumber(), ticket.getRequesterName());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicketStatus(Long id, Status newStatus, String agent, String resolutionNotes) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));

        log.info("Updating ticket status for {} from {} to {}", ticket.getTicketNumber(), ticket.getStatus(), newStatus);
        ticket.setStatus(newStatus);
        if (agent != null && !agent.trim().isEmpty()) {
            ticket.setAssignedAgent(agent);
        }
        if (resolutionNotes != null && !resolutionNotes.trim().isEmpty()) {
            ticket.setResolutionNotes(resolutionNotes);
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket updateTicket(Long id, Ticket updatedTicket) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with ID: " + id));

        existing.setTitle(updatedTicket.getTitle());
        existing.setDescription(updatedTicket.getDescription());
        existing.setCategory(updatedTicket.getCategory());
        existing.setPriority(updatedTicket.getPriority());
        existing.setStatus(updatedTicket.getStatus());
        existing.setDepartment(updatedTicket.getDepartment());
        existing.setAssignedAgent(updatedTicket.getAssignedAgent());
        existing.setResolutionNotes(updatedTicket.getResolutionNotes());

        return ticketRepository.save(existing);
    }

    @Transactional
    public void deleteTicket(Long id) {
        log.info("Deleting ticket with ID: {}", id);
        ticketRepository.deleteById(id);
    }

    public Map<String, Long> getDashboardMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("total", ticketRepository.count());
        metrics.put("open", ticketRepository.countByStatus(Status.OPEN));
        metrics.put("inProgress", ticketRepository.countByStatus(Status.IN_PROGRESS));
        metrics.put("resolved", ticketRepository.countByStatus(Status.RESOLVED));
        metrics.put("closed", ticketRepository.countByStatus(Status.CLOSED));
        metrics.put("urgent", ticketRepository.countByPriority(Priority.URGENT));
        return metrics;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initSampleData() {
        if (ticketRepository.count() == 0) {
            log.info("Seeding initial sample tickets into H2 database...");

            createTicket(Ticket.builder()
                    .ticketNumber("TICK-1001")
                    .title("VPN Connection drops continuously on Windows 11")
                    .description("The VPN client disconnects every 15 minutes when connected from home network.")
                    .category(Category.NETWORK)
                    .priority(Priority.HIGH)
                    .status(Status.OPEN)
                    .requesterName("Aditya Singh")
                    .requesterEmail("aditya.singh@company.com")
                    .department("Engineering")
                    .build());

            createTicket(Ticket.builder()
                    .ticketNumber("TICK-1002")
                    .title("Request access to Production Kubernetes Cluster")
                    .description("Need read-only kubectl access to staging and prod clusters for DevOps audit.")
                    .category(Category.ACCESS_RIGHTS)
                    .priority(Priority.URGENT)
                    .status(Status.IN_PROGRESS)
                    .requesterName("Sarah Connor")
                    .requesterEmail("sarah.c@company.com")
                    .department("DevOps")
                    .assignedAgent("IT Admin - Alex")
                    .build());

            createTicket(Ticket.builder()
                    .ticketNumber("TICK-1003")
                    .title("Monitor screen flickering issue")
                    .description("Secondary Dell 27 inch monitor flickers when connected via HDMI adapter.")
                    .category(Category.HARDWARE)
                    .priority(Priority.LOW)
                    .status(Status.RESOLVED)
                    .requesterName("John Doe")
                    .requesterEmail("john.d@company.com")
                    .department("Marketing")
                    .assignedAgent("IT Support - Bob")
                    .resolutionNotes("Replaced HDMI to DisplayPort adapter. Verified display functionality.")
                    .build());

            createTicket(Ticket.builder()
                    .ticketNumber("TICK-1004")
                    .title("IntelliJ IDEA License Renewal Required")
                    .description("Annual license expired today. Requesting license key activation.")
                    .category(Category.SOFTWARE)
                    .priority(Priority.MEDIUM)
                    .status(Status.CLOSED)
                    .requesterName("Rahul Sharma")
                    .requesterEmail("rahul.s@company.com")
                    .department("Engineering")
                    .assignedAgent("IT Support - Alice")
                    .resolutionNotes("Generated and assigned new JetBrains enterprise license key.")
                    .build());

            log.info("Sample tickets seeded successfully!");
        }
    }
}
