package com.helpdesk.controller;

import com.helpdesk.model.*;
import com.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public String listTickets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            Model model) {

        List<Ticket> tickets;
        if (search != null && !search.trim().isEmpty()) {
            tickets = ticketService.searchTickets(search);
            model.addAttribute("searchQuery", search);
        } else if (status != null) {
            tickets = ticketService.filterByStatus(status);
            model.addAttribute("selectedStatus", status);
        } else if (priority != null) {
            tickets = ticketService.filterByPriority(priority);
            model.addAttribute("selectedPriority", priority);
        } else {
            tickets = ticketService.getAllTickets();
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("categories", Category.values());
        return "ticket-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("categories", Category.values());
        return "ticket-create";
    }

    @PostMapping
    public String createTicket(@ModelAttribute Ticket ticket, RedirectAttributes redirectAttributes) {
        Ticket created = ticketService.createTicket(ticket);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Ticket " + created.getTicketNumber() + " created successfully!");
        return "redirect:/tickets";
    }

    @GetMapping("/{id}")
    public String viewTicketDetail(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID: " + id));
        model.addAttribute("ticket", ticket);
        model.addAttribute("statuses", Status.values());
        return "ticket-detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Ticket ticket = ticketService.getTicketById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket ID: " + id));
        model.addAttribute("ticket", ticket);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("categories", Category.values());
        return "ticket-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTicket(@PathVariable Long id, @ModelAttribute Ticket ticket, RedirectAttributes redirectAttributes) {
        ticketService.updateTicket(id, ticket);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket updated successfully!");
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            @RequestParam(required = false) String assignedAgent,
            @RequestParam(required = false) String resolutionNotes,
            RedirectAttributes redirectAttributes) {

        ticketService.updateTicketStatus(id, status, assignedAgent, resolutionNotes);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket status updated to " + status);
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ticketService.deleteTicket(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ticket deleted successfully!");
        return "redirect:/tickets";
    }
}
