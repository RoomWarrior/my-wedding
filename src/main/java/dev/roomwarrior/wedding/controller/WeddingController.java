package dev.roomwarrior.wedding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.model.GuestDto;
import dev.roomwarrior.wedding.service.GuestService;

@Controller
public class WeddingController {

    @Autowired
    private GuestService guestService;

    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("guest")) {
            model.addAttribute("guest", GuestDto.builder()
                    .attending(AttendingEnum.YES)
                    .build());
        }
        return "wedding";
    }

    @PostMapping("/rsvp")
    public String rsvp(GuestDto guestDto, RedirectAttributes redirectAttributes) {
        guestService.saveGuest(guestDto);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("guest", guestDto);
        return "redirect:/";
    }
}