package com.helpdesk.controller;

import com.helpdesk.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final TicketService ticketService;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("metrics", ticketService.getDashboardMetrics());
        model.addAttribute("recentTickets", ticketService.getRecentTickets());
        return "dashboard";
    }
}
