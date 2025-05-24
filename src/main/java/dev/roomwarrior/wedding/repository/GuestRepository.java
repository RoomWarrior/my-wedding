package dev.roomwarrior.wedding.repository;

import dev.roomwarrior.wedding.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
} 