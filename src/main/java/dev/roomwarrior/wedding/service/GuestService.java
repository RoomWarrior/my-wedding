package dev.roomwarrior.wedding.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.roomwarrior.wedding.model.Guest;
import dev.roomwarrior.wedding.model.GuestDto;
import dev.roomwarrior.wedding.model.GuestSummaryInfo;
import dev.roomwarrior.wedding.repository.GuestRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestService {
    
    private final GuestRepository guestRepository;
    
    public GuestDto saveGuest(GuestDto guestDto) {
        return guestRepository.save(guestDto.toEntity()).toDto();
    }
    
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    public GuestSummaryInfo guestSummaryInfo() {
        return GuestSummaryInfo.builder().build();
    }
} 