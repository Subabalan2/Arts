package Arts.Gallery.service;


import Arts.Gallery.entity.CartItem;
import Arts.Gallery.repository.ArtworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ArtworkRepository artworkRepository;
    private static final String CART_SESSION_KEY = "cart";

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public List<CartItem> getCartItems(HttpSession session) {
        return getCart(session);
    }

    public void addToCart(Long artworkId, HttpSession session) {
        List<CartItem> cart = getCart(session);

        // 🚩 'getArtworkId()' use பண்ணி filter பண்ணுங்க (CartItem-ல இருக்கிற variable name-க்கு ஏத்த மாதிரி)
        Optional<CartItem> existing = cart.stream()
                .filter(i -> i.getArtworkId() != null && i.getArtworkId().equals(artworkId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + 1);
        } else {
            artworkRepository.findById(artworkId).ifPresent(art -> {
                // 1. Constructor வழியா பேசிக் டீடைல்ஸ் செட் பண்றோம்
                CartItem item = new CartItem(
                        art.getId(), art.getTitle(),
                        art.getImagePath(), art.getPrice(), 1
                );

                // 🚩 2. இதுதான் மிக முக்கியம்: முழு Artwork ஆப்ஜெக்ட்டையும் செட் பண்ணுங்க!
                item.setArtwork(art);
                item.setArtworkId(art.getId()); // ID-யும் தனியா இருந்தா செட் பண்ணிடுங்க

                cart.add(item);
            });
        }
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeFromCart(Long artworkId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(i -> i.getArtworkId().equals(artworkId));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public BigDecimal getTotal(HttpSession session) {
        return getCart(session).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartCount(HttpSession session) {
        return getCart(session).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
