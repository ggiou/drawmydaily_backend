package sparta.drawmydaily_backend.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.service.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    public static String AUTHORIZATION_HEADER = "Authorization";
    public static String BEARER_PREFIX = "Bearer ";
    public static String AUTHORITIES_KEY = "auth";
    private final String SECRET_KEY;

    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)  throws IOException, ServletException{
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String jwt = resolveToken(request); //bearer 제외 된 토큰

        if (StringUtils.hasText(jwt)&&tokenProvider.validateToken(jwt)){
            Claims claims;
            try {
                claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody(); // getBody()를 이용해 앞서 토큰에 저장했던 data들이 담긴 claims 를 얻어옴
            }catch (ExpiredJwtException e){
                claims = e.getClaims();
            }//claims에 key 받아옴(Key키로 서명되었으면, 같은 Key가 JwtParserBuilder에 지정되어야 함)

            if(claims.getExpiration().toInstant().toEpochMilli() < Instant.now().toEpochMilli()){
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(
                        new ObjectMapper().writeValueAsString(
                                ResponseDto.fail("BAD_REQUEST", "Token이 유효햐지 않습니다.")
                        )
                );
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } //받아온 claims의 만료가 현재 저장된 만료보다 짧으면 즉 만료된 token이니 유효하지 않다는 에러

            String subject = claims.getSubject();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            //인증 키를 가져와 이를 새 권한을 지정해 map 해줌

            UserDetails principal = userDetailsService.loadUserByUsername(subject);
            //principal은 인증된 사용자 정보로 / 즉 위의 과정들이 다 진행된다면 인증(로그인)된 사용자니 이 사용자의 이름에 권한을 지정해 저장해둠
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
            //인증된 사용자 정보인 principal은 authenticaion에서 관리하니 이를 사용하기 위해 / 로그인 요청에서 username과 password를 가져와 여기에 인증된 사용자, jwt 토큰 키, 유효기간을 저장해줌)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //authentication을 securitycontextholder에서 관리
            //Spring으로 요청이 들어왔을때 헤더의 인증정보를 스레드 내 저장소에 담아놓고 해당 스레드에서 필요 시 꺼내서 사용
        }
        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER); //헤더에 넣어줌
        if (StringUtils.hasText(bearerToken)&&bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7);
        }
        return null;
    } //bearer 로 시작하면 그 뒤에 token 정보가 들어있으니 잘라서 token 정보만 넣어줌
}
