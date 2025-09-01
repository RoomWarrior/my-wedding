package dev.roomwarrior.wedding.controller;

import dev.roomwarrior.wedding.enums.AttendingEnum;
import dev.roomwarrior.wedding.model.GuestModel;
import dev.roomwarrior.wedding.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final GuestService guestService;


    @GetMapping
    public String admin(@RequestParam(required = false) String search,
                        @RequestParam(required = false) AttendingEnum attending,
                        Model model) {

        var guestStat = guestService.guestAdminInfo(search, attending);

        model.addAttribute("guests", guestStat.getGuests());
        model.addAttribute("totalVoted", guestStat.getTotalGuests());
        model.addAttribute("willCome", guestStat.getAttendingGuests());
        model.addAttribute("search", search);
        model.addAttribute("selectedAttending", attending);
        model.addAttribute("attendingOptions", AttendingEnum.values());
        return "admin";
    }
}
