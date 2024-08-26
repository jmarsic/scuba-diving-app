package oss.jmarsic.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import oss.jmarsic.app.model.Role;
import oss.jmarsic.app.model.User;
import oss.jmarsic.app.service.RoleService;
import oss.jmarsic.app.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin";
    }

    @PostMapping("/add-user")
    public String addUser(@Valid @RequestParam String fullName, @RequestParam String email, @RequestParam String password, @RequestParam String role) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Role userRole = roleService.findByName(role).orElseThrow(() -> new IllegalArgumentException("Invalid role!"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userService.save(user);

        return "redirect:/admin";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam("query") String query, Model model) {
        List<User> users = userService.searchByFullName(query);
        model.addAttribute("users", users);
        return "admin/user-list";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable UUID id, Model model, Principal principal) {
        User user = userService.findById(id);
        User loggedInUser = userService.findByEmail(principal.getName());

        if (!user.getId().equals(loggedInUser.getId())) {
            userService.deleteById(id);
        } else {
            System.out.println("You cannot delete an admin account!");
            model.addAttribute("errorMessage", "You cannot delete your own account!");
        }
        return "redirect:/admin";
    }
}
