package dev.roomwarrior.wedding.controller;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.model.Guest;
import dev.roomwarrior.wedding.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class WeddingController {

    private final GuestService guestService;

    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("guest")) {
            model.addAttribute("guest", Guest.builder()
                    .attending(AttendingEnum.YES)
                    .build());
        }
        return "wedding";
    }

    @PostMapping("/rsvp")
    public String rsvp(Guest guest, RedirectAttributes redirectAttributes) {
        guestService.saveGuest(guest);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("guest", guest);
        return "redirect:/";
    }
}
