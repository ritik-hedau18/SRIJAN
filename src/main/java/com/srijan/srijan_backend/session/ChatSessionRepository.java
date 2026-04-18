package com.srijan.srijan_backend.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findBySessionId(String sessionId);

    List<ChatSession> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    boolean existsBySessionId(String sessionId);
}