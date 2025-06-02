package dev.roomwarrior.wedding.controller;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.model.GuestModel;
import dev.roomwarrior.wedding.service.GuestService;
import jakarta.validation.Valid;
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
        if (!model.containsAttribute("guestModel")) {
            model.addAttribute("guestModel", GuestModel.builder()
                    .attending(AttendingEnum.YES)
                    .build());
        }
        return "wedding";
    }

    @PostMapping("/rsvp")
    public String rsvp(@Valid GuestModel guestModel, RedirectAttributes redirectAttributes) {
        guestService.saveGuest(guestModel);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("guest", guestModel);
        return "redirect:/";
    }
}
