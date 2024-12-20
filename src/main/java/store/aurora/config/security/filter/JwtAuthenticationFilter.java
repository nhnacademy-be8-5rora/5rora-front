package store.aurora.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import store.aurora.config.security.constants.SecurityConstants;
import store.aurora.auth.dto.response.UserUsernameAndRoleResponse;
import store.aurora.feignClient.UserClient;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserClient userClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtToken = getCookieValue(request);

        if(jwtToken.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        //토큰을 가지고 api 호출
        UserUsernameAndRoleResponse usernameAndRole = userClient.getUsernameAndRole(SecurityConstants.BEARER_TOKEN_PREFIX + jwtToken.get()); //todo http status code로 판단하기

        SecurityContextHolder.getContext().setAuthentication(makeAuthentication(usernameAndRole));

        filterChain.doFilter(request, response);
    }

    private Optional<String> getJwtToken(HttpServletRequest request){
        String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)){
            return Optional.of(authHeader);
        }

        return Optional.empty();
    }

    private UsernamePasswordAuthenticationToken makeAuthentication(UserUsernameAndRoleResponse usernameAndRole){
        return new UsernamePasswordAuthenticationToken(
                usernameAndRole.username(),
                null,
                List.of(new SimpleGrantedAuthority(usernameAndRole.role().name()))
        );
    }

    private Optional<String> getCookieValue(HttpServletRequest request){

        if (Objects.isNull(request.getCookies())) {
            return Optional.empty();
        }

        for (Cookie cookie : request.getCookies()) {
            if(
                    cookie.getName().equals(SecurityConstants.TOKEN_COOKIE_NAME)
                            && Objects.nonNull(cookie.getValue())
                            && !cookie.getValue().isEmpty()
                            && !cookie.getValue().isBlank()
            ){
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}
