package store.aurora.book.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorDTO {
    private String name;
    private AuthorRoleDTO.Role role;

    public AuthorDTO(String name, AuthorRoleDTO.Role role) {
        this.name = name;
        this.role = role;
    }

    // Getters and setters
}

