package Arts.Gallery.controller;

import Arts.Gallery.entity.User;
import Arts.Gallery.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "pages/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> userOpt = userRepository.findByEmail(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("loggedInUser", user);
                if ("ADMIN".equals(user.getRole())) {
                    return "redirect:/admin/dashboard";
                }
                return "redirect:/gallery";
            }
        }

        model.addAttribute("error", true);
        return "pages/login";
    }

    // FIX 1: Indha method-la 'Model' add panni 'new User()' anuppanum
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "pages/register";
    }

    // FIX 2: Path-ah HTML-ku thagundha maari '/register' nu maathittaen
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {

        // Validation errors (Email format, Password size etc.)
        if (result.hasErrors()) {
            return "pages/register";
        }

        // Duplicate Email Check
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email already exists!");
            return "pages/register";
        }

        user.setRole("USER");
        userRepository.save(user);
        return "redirect:/login?success";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}