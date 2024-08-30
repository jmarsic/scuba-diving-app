package oss.jmarsic.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
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
    public String userPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", userService.findByEmail(principal.getName()));
        if (!user.isPasswordChanged()) {
            return "redirect:/user/change-password";
        }

        Notification latestNotification = notificationService.getLatestNotification();
        System.out.println("Latest notification: " + (latestNotification != null ? latestNotification.getMessage() : "No notification found."));

        model.addAttribute("notification", latestNotification);
        model.addAttribute("user", user);
        model.addAttribute("dives", diveService.getDivesBtUserId(user.getId()));
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
            user.setPassword(newPassword);
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

    @GetMapping("/add-dive")
    public String addDiveForm(Model model) {
        model.addAttribute("dive", new Dive());
        return "add-dive";
    }

    @PostMapping("/add-dive")
    public String saveDive(@ModelAttribute("dive") @Valid Dive dive, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "add-dive";
        }
        User user = userService.findByEmail(principal.getName());
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
