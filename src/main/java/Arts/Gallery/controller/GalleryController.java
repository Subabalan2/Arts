package Arts.Gallery.controller;

import Arts.Gallery.entity.Artwork;
import Arts.Gallery.repository.ArtworkRepository;
import Arts.Gallery.service.ArtworkService;
import Arts.Gallery.service.CartService;
import lombok.RequiredArgsConstructor;
import Arts.Gallery.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gallery")
public class GalleryController {

    private final ArtworkService artworkService;
    private final CartService cartService;
    private final ArtworkRepository artworkRepository;

    @GetMapping
    public String gallery(@RequestParam(required = false, defaultValue = "ALL") String category,
                          Model model, HttpSession session) {

        // LOGIN CHECK: Session-la user illana login page-ku thalliru
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Artwork> artworks = artworkService.getByCategory(category);
        model.addAttribute("artworks", artworks);
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "pages/gallery";
    }

    @GetMapping("/{id}")
    public String artworkDetail(@PathVariable Long id, Model model, HttpSession session) {

        // DETAILS PAGE-kum adhe check kandippa venum
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Artwork artwork = artworkService.getById(id);
        model.addAttribute("artwork", artwork);
        return "pages/artwork-detail";
    }
    public String showGallery(@RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "sort", required = false) String sort,
                              Model model) {
        List<Artwork> artworks;

        // 🔍 Search and Sort Logic Integration
        if (search != null && !search.isEmpty()) {
            artworks = artworkRepository.searchArtworks(search);
        } else if ("price_low".equals(sort)) {
            artworks = artworkRepository.findAllByOrderByPriceAsc();
        } else if ("price_high".equals(sort)) {
            artworks = artworkRepository.findAllByOrderByPriceDesc();
        } else {
            artworks = artworkRepository.findAll();
        }

        model.addAttribute("artworks", artworks);
        model.addAttribute("currentPage", "gallery");
        return "pages/gallery";
    }
}
