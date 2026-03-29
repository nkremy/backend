package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @EntityGraph(attributePaths = {"echange", "agent", "analyse"})
    Optional<Message> findById(Long id);

    @EntityGraph(attributePaths = {"echange", "agent", "analyse"})
    List<Message> findAll();

    boolean existsByThreadIdGmail(String threadIdGmail);

    boolean existsByMessageIdGmail(String messageIdGmail);
}
