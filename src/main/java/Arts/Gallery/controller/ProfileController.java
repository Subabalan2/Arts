package Arts.Gallery.controller;


import Arts.Gallery.entity.User;
import Arts.Gallery.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final OrderRepository orderRepository;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // 1. Check if user is logged in
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Login pannala na login page-ku poidu
        }

        // 2. Intha user panna orders mattum yedukkurom
        // OrderRepository-la findByUser method irukkanum
        model.addAttribute("userOrders", orderRepository.findByUser(loggedInUser));
        model.addAttribute("user", loggedInUser);

        return "pages/profile";
    }
}
