package store.aurora.search.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import store.aurora.book.dto.category.CategoryResponseDTO;
import store.aurora.config.security.filter.JwtAuthenticationFilter;
import store.aurora.feign_client.UserClient;  // UserClient 임포트 추가
import store.aurora.book.CategoryService;
import store.aurora.feign_client.search.BookSearchClient;
import store.aurora.feign_client.search.ElasticSearchClient;
import store.aurora.search.dto.BookSearchResponseDTO;
import org.springframework.data.domain.Page;
import store.aurora.search.service.SearchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SearchController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})class SearchControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SearchService searchService;

    @MockBean
    private UserClient userClient;

    @MockBean
    private BookSearchClient bookSearchClient;

    @MockBean
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private SearchController searchController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }


    @DisplayName("keyword가 없을 때 처리")
    @Test
    void testSearchWithoutKeyword() throws Exception {
        String type = "title";
        int pageNum = 1;
        String orderBy = "salePrice";
        String orderDirection = "asc";

        BookSearchResponseDTO bookSearchResponseDTO = new BookSearchResponseDTO();
        bookSearchResponseDTO.setId(1L);
        bookSearchResponseDTO.setTitle("기본 검색 책");
        List<BookSearchResponseDTO> books = Collections.singletonList(bookSearchResponseDTO);
        Page<BookSearchResponseDTO> page = new PageImpl<>(books);

        // 빈 키워드로 검색
        when(searchService.searchBooks(anyString(), eq(type), eq(""), eq(pageNum-1), eq(orderBy), eq(orderDirection)))
                .thenReturn(page);

        mockMvc.perform(get("/books/search")
                        .param("type", type)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection))
                .andExpect(status().isOk())
                .andExpect(view().name("search/bookSearchResults"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", books));
    }
    @DisplayName("keyword가 빈 문자열일 때 책 전체에서처리")
    @Test
    void testSearchWithEmptyKeyword() throws Exception {
        String type = "title";
        int pageNum = 1;
        String orderBy = "salePrice";
        String orderDirection = "asc";

        BookSearchResponseDTO bookSearchResponseDTO = new BookSearchResponseDTO();
        bookSearchResponseDTO.setId(1L);
        bookSearchResponseDTO.setTitle("기본 검색 책");
        List<BookSearchResponseDTO> books = Collections.singletonList(bookSearchResponseDTO);
        Page<BookSearchResponseDTO> page = new PageImpl<>(books);

        // 빈 문자열로 검색
        when(searchService.searchBooks(anyString(), eq(type), eq(""), eq(pageNum-1), eq(orderBy), eq(orderDirection)))
                .thenReturn(page);

        mockMvc.perform(get("/books/search")
                        .param("type", type)
                        .param("keyword", "")  // keyword 파라미터가 빈 문자열
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection))
                .andExpect(status().isOk())
                .andExpect(view().name("search/bookSearchResults"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", books));
    }

    @DisplayName("잘못된 페이지 번호 처리")
    @Test
    void testSearchWithInvalidPageNum() throws Exception {
        String keyword = "한강";
        String type = "title";
        String invalidPageNum = "-1"; // 잘못된 페이지 번호

        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("type", type)
                        .param("pageNum", invalidPageNum))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }

    @DisplayName("검색 결과가 없을 때 처리")
    @Test
    void testSearchWithNoResults() throws Exception {
        String keyword = "존재하지 않는 책";
        String type = "title";
        int pageNum = 1;
        String orderBy = "salePrice";
        String orderDirection = "asc";

        Page<BookSearchResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList());

        when(searchService.searchBooks(anyString(), eq(type), eq(keyword), eq(pageNum-1), eq(orderBy), eq(orderDirection)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("type", type)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy", orderBy)
                        .param("orderDirection", orderDirection))
                .andExpect(status().isOk())
                .andExpect(view().name("search/bookSearchResults"))
                .andExpect(model().attribute("message", "\"" + keyword + "\"로 검색된 결과가 없습니다."))
                .andExpect(model().attribute("books", Collections.emptyList()));
    }

    @DisplayName("카테고리 타입으로 검색 처리 - 올바른 카테고리 ID")
    @Test
    void testSearchWithCategoryType() throws Exception {
        String keyword = "1"; // 카테고리 ID
        String type = "category";
        int pageNum = 1;

        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("소설");
        categoryDTO.setDepth(0);
        categoryDTO.setParentId(null);
        categoryDTO.setParentName(null);
        categoryDTO.setChildren(new ArrayList<>());

        BookSearchResponseDTO bookSearchResponseDTO = new BookSearchResponseDTO();
        bookSearchResponseDTO.setId(1L);
        bookSearchResponseDTO.setTitle("소설책");

        List<BookSearchResponseDTO> books = Collections.singletonList(bookSearchResponseDTO);
        Page<BookSearchResponseDTO> page = new PageImpl<>(books, PageRequest.of(0, 1), 1);

        when(searchService.searchBooks(anyString(), eq(type), eq(keyword), eq(0), eq(""), eq("")))
                .thenReturn(page);
        when(categoryService.findById(Long.parseLong(keyword)))
                .thenReturn(categoryDTO);

        mockMvc.perform(get("/books/search")
                        .param("keyword", keyword)
                        .param("type", type)
                        .param("pageNum", String.valueOf(pageNum))
                        .param("orderBy", "")
                        .param("orderDirection", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("search/bookSearchResults"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", books))
                .andExpect(model().attribute("categories", categoryDTO));
    }

}
