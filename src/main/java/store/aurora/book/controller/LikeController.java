package store.aurora.book.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import store.aurora.common.JwtUtil;
import store.aurora.feignClient.book.LikeClient;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class LikeController {

    private final LikeClient likeClient;

    @PostMapping("/likes/{bookId}")
    public ResponseEntity<Boolean> doLike(@PathVariable("bookId") String bookId,
                                          HttpServletRequest request) {
        String jwt = JwtUtil.getJwtFromCookie(request);
        boolean isLiked = likeClient.doLike(jwt, bookId);  // 서버에서 좋아요 상태 처리
        return ResponseEntity.ok(isLiked);  // 응답으로 좋아요 상태(true/false) 반환
    }
}
