package store.aurora.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotNull
    @Positive
    private int regularPrice;

    @NotNull
    @Positive
    private int salePrice;

    private boolean packaging = false;

    private Integer stock = 100;

    @NotBlank
    private String explanation;

    private String contents;

    @NotBlank
    private String isbn;

    @NotNull
    private LocalDate publishDate;

    @NotNull
    private boolean isSale;

    @NotNull
    private List<Long> categoryIds;

    private List<Long> tagIds;

    @NotBlank
    private String publisherName;

    private String seriesName;
}