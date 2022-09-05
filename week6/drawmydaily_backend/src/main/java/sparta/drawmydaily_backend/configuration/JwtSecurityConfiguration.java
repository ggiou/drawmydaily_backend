package sparta.drawmydaily_backend.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sparta.drawmydaily_backend.jwt.JwtFilter;
import sparta.drawmydaily_backend.jwt.TokenProvider;
import sparta.drawmydaily_backend.service.UserDetailsServiceImpl;

@RequiredArgsConstructor
public class JwtSecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public void configure(HttpSecurity httpSecurity) {
        JwtFilter customJWTFilter = new JwtFilter(SECRET_KEY, tokenProvider, userDetailsService);
        httpSecurity.addFilterBefore(customJWTFilter, UsernamePasswordAuthenticationFilter.class);
    } //회원의 이름, 비밀번호 필터 실행전에 jwt custom 필터 먼저 실행
} //jwt 시큐리티 설정 파일 -> 해줘야 필터가 제 순서대로 돌아감

