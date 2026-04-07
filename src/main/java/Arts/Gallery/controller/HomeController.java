package Arts.Gallery.controller;


import Arts.Gallery.entity.Artwork;
import Arts.Gallery.service.ArtworkService;
import Arts.Gallery.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ArtworkService artworkService;
    private final CartService cartService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Artwork> featured = artworkService.getAllAvailable();
        model.addAttribute("featured", featured.size() > 6 ? featured.subList(0, 6) : featured);
        model.addAttribute("cartCount", cartService.getCartCount(session));
        model.addAttribute("currentPage", "home");
        return "pages/home";
    }

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        model.addAttribute("cartCount", cartService.getCartCount(session));
        model.addAttribute("currentPage", "about");
        return "pages/about";
    }
}
