package Arts.Gallery.service;



import Arts.Gallery.entity.Artwork;
import Arts.Gallery.repository.ArtworkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;

    public List<Artwork> getAllAvailable() {
        return artworkRepository.findByAvailableTrue();
    }

    public List<Artwork> getByCategory(String category) {
        if (category == null || category.isEmpty() || category.equals("ALL")) {
            return artworkRepository.findByAvailableTrue();
        }
        Artwork.ArtCategory cat = Artwork.ArtCategory.valueOf(category.toUpperCase());
        return artworkRepository.findByCategoryAndAvailableTrue(cat);
    }

    // ArtworkService.java kulla poyi...
    public Artwork getById(Long id) {
        // repository.findById(id) thaan Optional-ah tharum.
        // Athu pakkathulayae orElseThrow() potta thaan Artwork-ah maarum.
        return artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Art not found with id: " + id));
    }


    public Artwork save(Artwork artwork) {
        return artworkRepository.save(artwork);
    }
    public void update(Long id, Artwork updatedArt, MultipartFile file) {
        Artwork existingArt = artworkRepository.findById(id).orElseThrow();

        existingArt.setTitle(updatedArt.getTitle());
        existingArt.setPrice(updatedArt.getPrice());
        existingArt.setDescription(updatedArt.getDescription());

        if (file != null && !file.isEmpty()) {
            // Puthu image upload panna andha path-ah update pannu (Save logic maariye)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            // ... (Unga file saving logic inga poodu)
            existingArt.setImagePath("images/" + fileName);
        }

        artworkRepository.save(existingArt);
    }
    public void deleteById(Long id) {
        artworkRepository.deleteById(id);
    }

}
