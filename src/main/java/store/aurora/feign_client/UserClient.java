package store.aurora.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.aurora.auth.dto.response.UserPwdAndRoleResponse;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.user.dto.request.SignUpRequest;
import store.aurora.user.dto.request.UserUpdateRequestDto;
import store.aurora.user.dto.request.VerificationRequest;
import store.aurora.user.dto.response.UserInfoResponseDto;

import java.time.LocalDateTime;
import java.util.Map;

@FeignClient(name = "userClient", url = "${api.gateway.base-url}" + "/api/users")
public interface UserClient {

    @GetMapping("/auth/details")
    ResponseEntity<UserPwdAndRoleResponse> getPasswordAndRole(@RequestHeader("UserId") String userId);

    @GetMapping("/auth/me")
    ResponseEntity<UserUsernameAndRoleResponse> getUsernameAndRole(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String jwtToken);

    @GetMapping("/auth/exists")
    ResponseEntity<Boolean> checkUserExists(@RequestHeader("userId") String userId);

    //회원가입
    @PostMapping
    ResponseEntity<Map<String, String>> signUp(@RequestBody SignUpRequest request,
                                               @RequestParam boolean isOauth);

    // 인증번호받기
    @PostMapping("/send-verification-code")
    ResponseEntity<Map<String, String>> sendCode(@RequestParam String phoneNumber);

    // 인증코드 검증
    @PostMapping("/verify-code")
    ResponseEntity<Map<String, String>> verifyCode(@RequestBody VerificationRequest request);

    // 회원탈퇴
    @DeleteMapping("/{userId}")
    ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId);

    // 휴면해제처리
    @PostMapping("/reactivate")
    ResponseEntity<Map<String, String>> reactivateUser(@RequestParam String userId);

    // 회원정보 조회
    @GetMapping("/info")
    ResponseEntity<UserInfoResponseDto> getUserInfo(@RequestHeader("userId") String userId);

    @PatchMapping("/{userId}/last-login")
    void updateLastLogin(@PathVariable String userId,
                         @RequestBody LocalDateTime lastLogin);

    // 회원정보 수정
    @PutMapping("/{userId}")
    ResponseEntity<Map<String, String>> updateUser(@PathVariable String userId,
                                                   @RequestBody UserUpdateRequestDto request);
}