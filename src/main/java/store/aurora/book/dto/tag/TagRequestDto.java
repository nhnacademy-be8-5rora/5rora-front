package store.aurora.book.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagRequestDto {
    @NotBlank(message = "태그 이름은 필수입니다.")
    private String name; // 태그 이름
}