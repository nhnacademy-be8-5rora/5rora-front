package store.aurora.search.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.aurora.book.CategoryService;
import store.aurora.book.dto.category.CategoryDTO;
import store.aurora.common.JwtUtil;
import store.aurora.feignClient.BookSearchClient;
import store.aurora.search.Page;
import store.aurora.search.dto.BookSearchResponseDTO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BookSearchClient bookSearchClient;
    private final CategoryService categoryService;

    // 디버그용 요청 예시: http://5rora-test:8080/books/search?type=title&keyword=한강&pageNum=1&orderBy=salePrice&orderDirection=desc
    @GetMapping("/books/search")
    public String search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") String pageNum,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDirection,
            HttpServletRequest request,  // HttpServletRequest 추가
            Model model) {

        try {
            // orderBy가 null이면 "title"로 설정
            if (orderBy == null) {
                orderBy = "title";
            }

            // orderDirection이 null이면 "asc"로 설정
            if (orderDirection == null) {
                orderDirection = "asc";
            }

            // keyword 인코딩
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            // jwtToken이 없으면 빈 문자열로 설정
            String jwt = JwtUtil.getJwtFromCookie(request);
            if (jwt.equals("Bearer null")) {
                jwt = "";  // jwt가 null일 경우 빈 문자열 설정
            }

            int page = Integer.parseInt(pageNum) - 1; // 페이지 번호 0-based
            Page<BookSearchResponseDTO> bookSearchResponseDTOPage = bookSearchClient.searchBooksByKeyword(
                    jwt, type, encodedKeyword, pageNum, orderBy, orderDirection
            );
            List<BookSearchResponseDTO> books = bookSearchResponseDTOPage.getContent();
            if (type.equals("category")) {
                CategoryDTO categories = categoryService.findById(Long.parseLong(keyword)); // 카테고리 하위 목록 조회
                model.addAttribute("categories", categories);
            }
            // 검색 결과와 페이징 정보를 모델에 추가
            model.addAttribute("books", books);
            model.addAttribute("currentPage", page + 1); // 현재 페이지 (1-based)
            model.addAttribute("totalPages", bookSearchResponseDTOPage.getTotalPages()); // 전체 페이지 수
            model.addAttribute("keyword", keyword);
            model.addAttribute("type", type);
            model.addAttribute("orderBy", orderBy);
            model.addAttribute("orderDirection", orderDirection);

            return "search/bookSearchResults"; // templates/search/bookSearchResults.html 반환
        } catch (UnsupportedEncodingException e) {
            // 인코딩 에러 처리
            model.addAttribute("error", "잘못된 문자 인코딩이 포함되어 있습니다.");
            return "error";
        }
    }
}
