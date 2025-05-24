package dev.roomwarrior.wedding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.roomwarrior.wedding.model.GuestSummaryInfo;
import dev.roomwarrior.wedding.service.GuestService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/guests")
@RequiredArgsConstructor
public class GuestsInfoRestController {

    private final GuestService guestService;

    @GetMapping
    public ResponseEntity<GuestSummaryInfo> getGuestsSummaryInfo() {
        return ResponseEntity.ok(guestService.guestSummaryInfo());
    }
}
