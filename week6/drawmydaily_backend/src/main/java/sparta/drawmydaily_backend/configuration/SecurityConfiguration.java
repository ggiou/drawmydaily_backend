package sparta.drawmydaily_backend.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import sparta.drawmydaily_backend.jwt.AccessDeniedHandlerException;
import sparta.drawmydaily_backend.jwt.AuthenticationEntryPointException;
import sparta.drawmydaily_backend.jwt.TokenProvider;
import sparta.drawmydaily_backend.service.UserDetailsServiceImpl;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity //SpringSecurityFilterChain이 자동으로 포함
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration {
    @Value("${jwt.secret}")
    String SECRET_KEY; //시크릿키에 임의로 지정한 키 값 받아오기

    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    } //password 암호화 해주는 인코더 설정

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**");
    } // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception{
        http.cors(); //CORS는 한 도메인 또는 Origin의 웹 페이지가 다른 도메인 (도메인 간 요청)을 가진 리소스에 액세스 할 수 있게하는 보안 메커니즘
        //서버와 클라이언트가 정해진 헤더를 통해 서로 요청이나 응답에 반응할지 결정하는 방식

        http.csrf().disable()
                //csrf-> 정상적 사용자가 의도치 않은 위조요청 보내는 것 -> 현재는 x, disable

                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointException)
                .accessDeniedHandler(accessDeniedHandlerException)
                //handler, 에러 처리 설정

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //스프링 시큐리티가 생성하지도 않고 기존 것을 사용하지도 않음 ->JWT 같은 토큰방식을 쓸때 사용하는 설정

                .and()
                .authorizeRequests()
                 //시큐리티 처리에 HttpServletRequest를 이용

                .antMatchers("/api/users/**").permitAll()
                .antMatchers("/api/posts/**").permitAll()
                .antMatchers("/api/comments/**").permitAll()
                .anyRequest().authenticated()
                //위 네 개 빼고 다 권한 받아야 이용가능 루트

                .and()
                .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService ));

        return http.build();
    }

}
