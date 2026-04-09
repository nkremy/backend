package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Ticket;
import com.profileapp.backend.entity.TicketPriorite;
import com.profileapp.backend.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByDateCreationDesc();

    List<Ticket> findAllByTypeOrderByDateCreationDesc(TicketType type);

    List<Ticket> findAllByPrioriteOrderByDateCreationDesc(TicketPriorite priorite);

    List<Ticket> findAllByTypeAndPrioriteOrderByDateCreationDesc(TicketType type, TicketPriorite priorite);

    List<Ticket> findAllByLuFalseOrderByDateCreationDesc();

    long countByLuFalse();

    long countByLuFalseAndType(TicketType type);

    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.lu = true WHERE t.lu = false")
    void markAllAsRead();
}
