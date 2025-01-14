package store.aurora.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReviewRequest {

    @NotNull(message = "평점은 필수 항목입니다.")
    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하이어야 합니다.")
    private Integer rating;

    @NotBlank(message = "리뷰 내용은 필수 항목입니다.")
    private String content;

//    private List<MultipartFile> files = new ArrayList<>();
}