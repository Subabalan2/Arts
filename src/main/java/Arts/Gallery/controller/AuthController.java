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

//    @GetMapping("/login")
//    public String loginPage() {
//        return "pages/login";
//    }
//
//    // Normal User Login
//    @PostMapping("/login")
//    public String login(@RequestParam String username,
//                        @RequestParam String password,
//                        HttpSession session,
//                        Model model) {
//        Optional<User> userOpt = userRepository.findByEmail(username.trim());
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            if (user.getPassword().equals(password)) {
//                session.setAttribute("loggedInUser", user);
//                return "ADMIN".equals(user.getRole()) ? "redirect:/admin/dashboard" : "redirect:/gallery";
//            }
//        }
//        model.addAttribute("error", true);
//        return "pages/login";
//    }
@GetMapping("/login")
public String loginPage() {
    return "pages/login";
}

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> userOpt = userRepository.findByEmail(username.trim());

        if(userOpt.isPresent()) {

            User user = userOpt.get();

            if(user.getPassword().equals(password)) {

                session.setAttribute("loggedInUser", user);

                if("ADMIN".equals(user.getRole())){
                    return "redirect:/admin/dashboard";
                }else{
                    return "redirect:/gallery";
                }

            }
        }

        model.addAttribute("error", true);

        return "pages/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "pages/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) return "pages/register";
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email already exists!");
            return "pages/register";
        }
        user.setRole("USER");
        userRepository.save(user);
        return "redirect:/login?success";
    }

    // 🔥 SECRET ADMIN REGISTRATION (Only for you)
    // Intha path-ah yaarkittayum solladha: /admin/register-secret
    @GetMapping("/admin/register-secret")
    public String adminRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "pages/admin-register"; // Intha HTML-ah namma create pannanum
    }

    @PostMapping("/admin/register-secret")
    public String registerAdmin(@Valid @ModelAttribute("user") User user,
                                @RequestParam String secretKey,
                                BindingResult result, Model model) {

        // Intha key match aana thaan ADMIN role kidaikkum
        if (!"SSARTZ@2026".equals(secretKey)) {
            model.addAttribute("keyError", "Invalid Secret Key!");
            return "pages/admin-register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email already exists!");
            return "pages/admin-register";
        }

        user.setRole("ADMIN"); // 🛡️ Force Admin Role
        userRepository.save(user);
        return "redirect:/admin/login?success";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}