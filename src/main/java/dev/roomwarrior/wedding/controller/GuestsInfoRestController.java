package dev.roomwarrior.wedding.controller;

import dev.roomwarrior.wedding.annotation.RequireApiKey;
import dev.roomwarrior.wedding.model.GuestModel;
import dev.roomwarrior.wedding.model.GuestSummaryInfo;
import dev.roomwarrior.wedding.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guests")
@RequiredArgsConstructor
@RequireApiKey
public class GuestsInfoRestController {

    private final GuestService guestService;

    @GetMapping
    public ResponseEntity<List<GuestModel>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @GetMapping("/summary")
    public ResponseEntity<GuestSummaryInfo> getGuestsSummaryInfo() {
        return ResponseEntity.ok(guestService.guestSummaryInfo());
    }

    @PostMapping("/init-new")
    public void initNewGuests(@RequestBody List<GuestModel> guestsModels) {
        guestService.initNewGuests(guestsModels);
    }
}
