package sparta.drawmydaily_backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sparta.drawmydaily_backend.controller.request.TokenDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.domain.RefreshToken;
import sparta.drawmydaily_backend.domain.Users;
import sparta.drawmydaily_backend.domain.UserDetailsImpl;
import sparta.drawmydaily_backend.repository.RefreshTokenRepository;
import sparta.drawmydaily_backend.shared.Authority;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    //권한을 주기 위한 key 부여
    private static final String BEARER_PREFIX = "Bearer ";
    //JWT 혹은 OAuth에 대한 토큰을 사용, 토큰 앞에 Bearer 문자열 필요해서 고정
    private static final long ACCESS_TOKEN_EXPIREE_TIME = 1000 * 60 * 60 * 24; //엑세스 토큰 만료  = 30분(현 1일)
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; //리프레시 토큰 만료  = 7일

    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;
    //  private final UserDetailsServiceImpl userDetailsService;

    public TokenProvider(@Value("${jwt.secret}") String secretKey, RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    } //secret(비밀키)을 decode해서 반환(암호화 해제 해서 확인)

    public TokenDto generateTokenDto(Users users){
        long now = (new Date().getTime());
        //만료시간 설정을 위해 현재 시간 가져오기

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIREE_TIME); //access 토큰 만료시간 설정 완료
        String accessToken = Jwts.builder()
                .setSubject(users.getName())
                .claim(AUTHORITIES_KEY, Authority.ROLE_USER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        //accessToken 생성

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        //refreshToken 생성

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(users.getId())
                .users(users)
                .keyValue(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenObject);
        //refreshToken 생성 후 이를 user에 매핑

        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    } //토큰 생성

    public Users getUserFromAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return null;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getUsers();
    } //사용자(유저, 회원가입한 사용자)에게 인증 부여

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e){
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    } //토큰이 유효한지 검사 (유효, 만료, 지원, 잘못된 토큰인지 예외처리)

    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(Users users){
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUsers(users);
        return optionalRefreshToken.orElse(null);
    } //현재 있는 refreshtoken인지 확인

    @Transactional
    public ResponseDto<?> deleteRefreshToken(Users users) {
        RefreshToken refreshToken = isPresentRefreshToken(users);
        if (null == refreshToken) {
            return ResponseDto.fail("TOKEN_NOT_FOUND", "존재하지 않는 Token 입니다.");
        }

        refreshTokenRepository.delete(refreshToken);
        return ResponseDto.success("success");
    } //refreshtoken 삭제
}
