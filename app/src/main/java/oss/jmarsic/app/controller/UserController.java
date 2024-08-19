package oss.jmarsic.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.service.UserService;

import java.util.UUID;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }

    @GetMapping("/users")
    public String listAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @PostMapping("/user")
    public String addUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userService.save(user);
        return "redirect:/user";
    }

    @GetMapping("user/{id}")
    public String getUserById(@PathVariable UUID id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user";
    }
}
