package Arts.Gallery.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user; // Yaaru order panna?

    private String address; // Enga anupunom?
    private Double totalAmount;
    private LocalDateTime orderDate; // Entha time-ku?
    private String status; // "PENDING" or "SUCCESS"
}
