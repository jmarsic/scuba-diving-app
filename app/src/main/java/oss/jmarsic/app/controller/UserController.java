package oss.jmarsic.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import oss.jmarsic.app.model.Dive;
import oss.jmarsic.app.model.Notification;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.service.DiveService;
import oss.jmarsic.app.service.NotificationService;
import oss.jmarsic.app.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DiveService diveService;

    @GetMapping
    public String userPage(Model model, Principal principal, @RequestParam(defaultValue = "0") int page) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", userService.findByEmail(principal.getName()));

        if (!user.isPasswordChanged()) {
            return "redirect:/user/change-password";
        }

        Notification latestNotification = notificationService.getLatestNotification();

        Page<Dive> divePage = diveService.getDivesBtUserId(user.getId(), page, 10);
        model.addAttribute("dives", divePage.getContent());
        model.addAttribute("totalPages", divePage.getTotalPages());
        model.addAttribute("currentPage", page);

        model.addAttribute("notification", latestNotification);
        model.addAttribute("user", user);
        model.addAttribute("averageDepth", diveService.calculateAverageDepth());
        model.addAttribute("depths", diveService.getAllDepths());
        model.addAttribute("durations", diveService.getAllDurations());
        return "user";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @RequestParam String fullName, @RequestParam String email, @RequestParam String newPassword, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        user.setFullName(fullName);
        user.setEmail(email);

        if (newPassword != null && !newPassword.isEmpty()) {
            userService.changePassword(user, newPassword);
        }

        userService.save(user);
        return "redirect:/user";
    }

    @GetMapping("change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("user", new User());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        userService.changePassword(user, newPassword);
        return "redirect:/user";
    }

    @PostMapping("/mark-notification-read/{id}")
    public String markNotificationRead(@PathVariable UUID id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        notificationService.markAsRead(id, user);
        return "redirect:/user";
    }

    @GetMapping("/add-dive")
    public String addDiveForm(Model model) {
        model.addAttribute("dive", new Dive());
        return "add-dive";
    }

    @PostMapping("/add-dive")
    public String saveDive(@ModelAttribute("dive") @Valid Dive dive, BindingResult result, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        List<Dive> divesForDate = diveService.findByDateAndUser(dive.getDate(), user);
        if(divesForDate.size() >= 2) {
            result.rejectValue("date", "error.dive", "You can only log two dives per day!");
            System.out.println("You can only log two dives per day!");
        }

        if(dive.getDuration() > 120) {
            result.rejectValue("duration", "error.dive", "A dive cannot be longer than 2 hours!");
            System.out.println("A dive cannot be longer than 2 hours!");
        }

        if(dive.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())) {
            result.rejectValue("date", "error.dive", "You cannot log dives for future date!");
            System.out.println("You cannot log dives for future date!");
        }

        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "add-dive";
        }

        dive.setUser(user);
        diveService.saveDive(dive);
        return "redirect:/user";
    }

    @GetMapping("/edit-dive/{id}")
    public String editDiveForm(@PathVariable UUID id, Model model) {
        Dive dive = diveService.findById(id);
        model.addAttribute("dive", dive);
        return "edit-dive";
    }

    @PostMapping("/edit-dive/{id}")
    public String updateDive(@PathVariable UUID id, @ModelAttribute Dive dive, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Dive existingDive = diveService.findById(id);

        if (existingDive != null && existingDive.getUser().getId().equals(user.getId())) {
            existingDive.setLocation(dive.getLocation());
            existingDive.setDate(dive.getDate());
            existingDive.setDepth(dive.getDepth());
            existingDive.setDuration(dive.getDuration());
            existingDive.setAdditionalInfo(dive.getAdditionalInfo());
            diveService.saveDive(existingDive);
        }
        return "redirect:/user";
    }

    @GetMapping("/delete-dive/{id}")
    public String deleteDive(@PathVariable UUID id) {
        diveService.deleteDive(id);
        return "redirect:/user";
    }
}
