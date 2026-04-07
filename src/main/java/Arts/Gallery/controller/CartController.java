package Arts.Gallery.controller;

import Arts.Gallery.entity.Order;
import Arts.Gallery.entity.User;
import Arts.Gallery.repository.OrderRepository; // 1. Puthiyathaa add pannirukken
import Arts.Gallery.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final OrderRepository orderRepository; // 2. Idha inject panna dhaan DB-la save aagum

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        model.addAttribute("cartItems", cartService.getCartItems(session));
        model.addAttribute("total", cartService.getTotal(session));
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "pages/cart";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session,
                            @RequestHeader(value = "Referer", defaultValue = "/gallery") String referer) {
        cartService.addToCart(id, session);
        return "redirect:" + referer;
    }

    @PostMapping("/buy-now/{id}")
    public String buyNow(@PathVariable Long id, HttpSession session) {
        cartService.addToCart(id, session);
        return "redirect:/cart/checkout";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        cartService.removeFromCart(id, session);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        // Security Check: Login pannala na ulla vidaadhey
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        model.addAttribute("cartItems", cartService.getCartItems(session));
        model.addAttribute("total", cartService.getTotal(session));
        model.addAttribute("cartCount", cartService.getCartCount(session));
        return "pages/checkout";
    }

    @PostMapping("/checkout/confirm")
    public String confirmOrder(@RequestParam String address, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        // Inga dhaan problem: CartService oru BigDecimal tharaadhu nu check pannu
        BigDecimal totalBigDecimal = cartService.getTotal(session);

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);

        // conversion inga dhaan nadakkudhu:
        order.setTotalAmount(totalBigDecimal.doubleValue());

        order.setOrderDate(LocalDateTime.now());
        order.setStatus("SUCCESS");

        orderRepository.save(order);
        cartService.clearCart(session);
        return "redirect:/cart/success";
    }

    @GetMapping("/success")
    public String orderSuccess(Model model, HttpSession session) {
        model.addAttribute("cartCount", 0);
        return "pages/order-success";
    }
}