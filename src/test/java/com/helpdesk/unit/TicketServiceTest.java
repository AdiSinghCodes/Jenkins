package com.helpdesk.unit;

import com.helpdesk.model.*;
import com.helpdesk.repository.TicketRepository;
import com.helpdesk.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Unit Test 1: Should generate ticket number and default status OPEN on creation")
    public void testCreateTicketDefaultInitialization() {
        Ticket ticket = Ticket.builder()
                .title("Database connection timeout")
                .description("PostgreSQL server dropping connection under heavy load.")
                .category(Category.NETWORK)
                .priority(Priority.HIGH)
                .requesterName("Aditya Singh")
                .requesterEmail("aditya@company.com")
                .build();

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket created = ticketService.createTicket(ticket);

        assertNotNull(created.getTicketNumber(), "Ticket number should be generated");
        assertTrue(created.getTicketNumber().startsWith("TICK-"));
        assertEquals(Status.OPEN, created.getStatus(), "Default status should be OPEN");
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Unit Test 2: Should update ticket status and assigned agent correctly")
    public void testUpdateTicketStatus() {
        Ticket existingTicket = Ticket.builder()
                .id(1L)
                .ticketNumber("TICK-1001")
                .title("Wi-Fi password reset")
                .status(Status.OPEN)
                .build();

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket updated = ticketService.updateTicketStatus(1L, Status.IN_PROGRESS, "Agent Alex", "Investigating AP signal");

        assertEquals(Status.IN_PROGRESS, updated.getStatus());
        assertEquals("Agent Alex", updated.getAssignedAgent());
        assertEquals("Investigating AP signal", updated.getResolutionNotes());
    }

    @Test
    @DisplayName("Unit Test 3: Should calculate accurate dashboard metrics")
    public void testGetDashboardMetrics() {
        when(ticketRepository.count()).thenReturn(10L);
        when(ticketRepository.countByStatus(Status.OPEN)).thenReturn(4L);
        when(ticketRepository.countByStatus(Status.IN_PROGRESS)).thenReturn(3L);
        when(ticketRepository.countByStatus(Status.RESOLVED)).thenReturn(2L);
        when(ticketRepository.countByStatus(Status.CLOSED)).thenReturn(1L);
        when(ticketRepository.countByPriority(Priority.URGENT)).thenReturn(2L);

        Map<String, Long> metrics = ticketService.getDashboardMetrics();

        assertEquals(10L, metrics.get("total"));
        assertEquals(4L, metrics.get("open"));
        assertEquals(3L, metrics.get("inProgress"));
        assertEquals(2L, metrics.get("resolved"));
        assertEquals(2L, metrics.get("urgent"));
    }
}
