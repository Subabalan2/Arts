package Arts.Gallery.repository;



import Arts.Gallery.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    List<Artwork> findByCategory(Artwork.ArtCategory category);
    List<Artwork> findByAvailableTrue();
    List<Artwork> findByCategoryAndAvailableTrue(Artwork.ArtCategory category);

    @Query("SELECT a FROM Artwork a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Artwork> searchArtworks(@Param("keyword") String keyword);
    List<Artwork> findAllByOrderByPriceAsc();
    List<Artwork> findAllByOrderByPriceDesc();
}