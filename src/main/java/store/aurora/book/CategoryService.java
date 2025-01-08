package store.aurora.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.feign_client.book.CategoryClient;

@Service
public class CategoryService {

    private final CategoryClient categoryClient;


    @Autowired
    public CategoryService(@Lazy CategoryClient categoryClient) {
        this.categoryClient = categoryClient;
    }

    public CategoryResponseDTO findById(Long id) {
        id = id == null ? 0 : id;
        try {
            ResponseEntity<CategoryResponseDTO> response = categoryClient.findById(id);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Category service responded with an error: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve category with id: " + id, e);
        }
    }
}
