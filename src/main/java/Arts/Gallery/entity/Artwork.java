package Arts.Gallery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "artworks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;

    @Enumerated(EnumType.STRING)
    private ArtCategory category;

    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean available;
    private String dimensions;
    private String medium;
    private String imagePath;

    public enum ArtCategory {
        PAINTING, DIGITAL, PHOTOGRAPHY, MIXED_MEDIA
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return this.imagePath;
    }
}