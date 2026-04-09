package Arts.Gallery.controller;

import Arts.Gallery.entity.Order;
import Arts.Gallery.entity.OrderItem; // 🚩 Pudhusa add pannadhu
import Arts.Gallery.entity.User;
import Arts.Gallery.entity.CartItem;  // 🚩 CartItem import (unga package path-kku etha maari mathikonga)
import Arts.Gallery.repository.OrderRepository;
import Arts.Gallery.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList; // 🚩 Pudhusa add pannadhu
import java.util.List;      // 🚩 Pudhusa add pannadhu

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final OrderRepository orderRepository;

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
        if (user == null) {
            return "redirect:/login";
        }

        BigDecimal totalBigDecimal = cartService.getTotal(session);

        // 🚩 1. Cart-la irukkura items-ah edukkurom
        List<CartItem> cartItems = cartService.getCartItems(session);

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setTotalAmount(totalBigDecimal.doubleValue());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("SUCCESS");

        // 🚩 2. CartItems-ah eduthu OrderItems-ah mathurom
        List<OrderItem> orderItemsList = new ArrayList<>();
        if (cartItems != null) {
            for (CartItem cartItem : cartItems) {
                OrderItem item = new OrderItem();
                item.setOrder(order); // Intha item intha order-odathu nu link pandrom

                // Note: CartItem class-la getArtwork(), getPrice(), getQuantity() irukkanum
                item.setArtwork(cartItem.getArtwork());

                // Oruvela unga price BigDecimal-ah irundha `.doubleValue()` podunga, illana apdiye podunga
                item.setPrice(cartItem.getPrice().doubleValue());
                item.setQuantity(cartItem.getQuantity());

                orderItemsList.add(item);
            }
        }

        // 🚩 3. Order-kulla andha items list-ah set pandrom
        order.setItems(orderItemsList);

        // 🚩 4. Save pannumbodhu Order + OrderItems rendume DB-la save aagidum (Cascade logic)
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