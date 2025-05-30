package dev.roomwarrior.wedding.repos;

import dev.roomwarrior.wedding.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepo extends JpaRepository<Guest, Long> {

}
