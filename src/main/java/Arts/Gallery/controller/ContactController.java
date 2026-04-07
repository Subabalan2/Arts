package Arts.Gallery.controller;

import Arts.Gallery.entity.ContactMessage;
import Arts.Gallery.repository.ContactRepository;
import Arts.Gallery.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {

    private final ContactRepository contactRepository;
    private final CartService cartService;

    @GetMapping
    public String contactPage(Model model, HttpSession session) {
        model.addAttribute("contactMessage", new ContactMessage());
        model.addAttribute("cartCount", cartService.getCartCount(session));
        model.addAttribute("currentPage", "contact");
        return "pages/contact";
    }

    @PostMapping
    public String submitContact(@Valid @ModelAttribute ContactMessage contactMessage,
                                BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("cartCount", cartService.getCartCount(session));
            model.addAttribute("currentPage", "contact");
            return "pages/contact";
        }
        contactRepository.save(contactMessage);
        model.addAttribute("success", true);
        model.addAttribute("cartCount", cartService.getCartCount(session));
        model.addAttribute("currentPage", "contact");
        return "pages/contact";
    }
}
