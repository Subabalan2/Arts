package Arts.Gallery.controller;

import Arts.Gallery.entity.Artwork;
import Arts.Gallery.entity.User;
import Arts.Gallery.repository.OrderRepository;
import Arts.Gallery.repository.UserRepository;
import Arts.Gallery.service.ArtworkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ArtworkService artworkService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // 1. ADMIN LOGIN PAGE - Lowercase path for Render/Linux
    @GetMapping("/login")
    public String adminLoginPage() {
        return "pages/admin-login";
    }

    // 2. UPDATED ADMIN LOGIN LOGIC (Database + Master Admin Backup)
    @PostMapping("/login")
    public String handleAdminLogin(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpSession session,
                                   Model model) {

        Optional<User> adminOpt = userRepository.findByEmail(email.trim());

        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
            // DB check for ADMIN role
            if (admin.getPassword().equals(password) && "ADMIN".equals(admin.getRole())) {
                session.setAttribute("loggedInUser", admin);
                return "redirect:/admin/dashboard";
            }
        }

        // Master Admin (Safe Backup)
        if ("sivabalan@gmail.com".equals(email) && "admin@123".equals(password)) {
            User master = new User();
            master.setName("Master Admin");
            master.setEmail(email);
            master.setRole("ADMIN");
            session.setAttribute("loggedInUser", master);
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", true);
        return "pages/admin-login";
    }

    // 3. ADMIN DASHBOARD - Security Check Added
    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        // Role check pannala-na yaaru venaalum dashboard URL poga mudiyum, adhan indha check:
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/admin/login";
        }

        model.addAttribute("artworks", artworkService.getAllAvailable());
        model.addAttribute("orders", orderRepository.findAll());
        return "pages/admin-dashboard";
    }

    // 4. SHOW ADD ARTWORK FORM
    @GetMapping("/artworks/add")
    public String showAddForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/admin/login";

        model.addAttribute("artwork", new Artwork());
        return "pages/admin-add-art";
    }

    // 5. SAVE ARTWORK WITH IMAGE UPLOAD (As per your request, path not changed)
    @PostMapping("/artworks/save")
    public String saveArtwork(@ModelAttribute Artwork artwork,
                              @RequestParam("imageFile") MultipartFile file,
                              HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/admin/login";

        try {
            if (!file.isEmpty()) {
                String rootPath = System.getProperty("user.dir");
                // API Path remains same as your original code
                Path uploadPath = Paths.get(rootPath, "src", "main", "resources", "static", "images");

                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                artwork.setImagePath("images/" + fileName);
            }
            artwork.setAvailable(true);
            artworkService.save(artwork);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/artworks/add?error";
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    @PostMapping("/delete/{id}")
    public String deleteArtwork(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/admin/login";
        }

        try {
            artworkService.deleteById(id);
            System.out.println("Artwork ID: " + id + " deleted!");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard?error=delete_failed";
        }

        return "redirect:/admin/dashboard?deleted";
    }
    // 6. SHOW EDIT FORM (Idhu unga code-la illa, adhaan 404 varudhu)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/admin/login";
        }

        // Database-la irundhu antha artwork-ah edukkurom
        Artwork artwork = artworkService.getById(id);
        if (artwork == null) {
            return "redirect:/admin/dashboard?error=notfound";
        }

        model.addAttribute("artwork", artwork);
        return "pages/admin-edit-art"; // Inga unga HTML file name correct-ah irukanum
    }

    // 7. HANDLE UPDATE ACTION
    @PostMapping("/update/{id}")
    public String updateArtwork(@PathVariable Long id,
                                @ModelAttribute Artwork artwork,
                                @RequestParam("imageFile") MultipartFile file,
                                HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/admin/login";

        try {
            Artwork existingArtwork = artworkService.getById(id);

            // Artwork basic details update
            existingArtwork.setTitle(artwork.getTitle());
            existingArtwork.setPrice(artwork.getPrice());
            existingArtwork.setDescription(artwork.getDescription());

            // Pudhu image upload panna mattum path-ah mathuvom
            if (!file.isEmpty()) {
                String rootPath = System.getProperty("user.dir");
                Path uploadPath = Paths.get(rootPath, "src", "main", "resources", "static", "images");
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                existingArtwork.setImagePath("images/" + fileName);
            }

            artworkService.save(existingArtwork);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/admin/edit/" + id + "?error";
        }
        return "redirect:/admin/dashboard?updated";
    }
}