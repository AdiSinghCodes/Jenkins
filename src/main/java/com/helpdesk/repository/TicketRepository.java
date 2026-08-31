package com.helpdesk.repository;

import com.helpdesk.model.Category;
import com.helpdesk.model.Priority;
import com.helpdesk.model.Status;
import com.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(Status status);

    List<Ticket> findByPriority(Priority priority);

    List<Ticket> findByCategory(Category category);

    long countByStatus(Status status);

    long countByPriority(Priority priority);

    @Query("SELECT t FROM Ticket t WHERE " +
           "LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.requesterName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Ticket> searchTickets(@Param("keyword") String keyword);

    List<Ticket> findTop5ByOrderByCreatedAtDesc();
}
