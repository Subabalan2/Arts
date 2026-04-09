package Arts.Gallery.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItem {

    private Long id;

    private Long artworkId;
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private int quantity;
    private Artwork artwork;

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public CartItem(Long id, String title, String imageUrl, BigDecimal price, int quantity) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public CartItem() {
    }
}