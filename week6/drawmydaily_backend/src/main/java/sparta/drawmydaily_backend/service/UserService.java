package sparta.drawmydaily_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.drawmydaily_backend.controller.request.LoginRequestDto;
import sparta.drawmydaily_backend.controller.request.TokenDto;
import sparta.drawmydaily_backend.controller.request.UsersRequestDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.controller.response.UsersResponseDto;
import sparta.drawmydaily_backend.domain.Users;
import sparta.drawmydaily_backend.jwt.TokenProvider;
import sparta.drawmydaily_backend.repository.UsersRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder; //비밀번호를 암호화 하는 역할
    private final TokenProvider tokenProvider;
    
    @Transactional
    public ResponseDto<?> createUser(UsersRequestDto requestDto) {
        if (null != isPresentUser(requestDto.getName())){
            return ResponseDto.fail("DUPLICAIED_NICKNAME", "중복된 닉네임을 입력했습니다.");
        } //아이디 중복 확인
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())){
            return ResponseDto.fail("PASSWORDS_NOT_MATCHED", "입력한 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        } //비밀번호 재입력 확인

        Users users = Users.builder()
                .name(requestDto.getName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .build();
        usersRepository.save(users); //회원정보 repository에 저장

        return ResponseDto.success(
                UsersResponseDto.builder()
                        .id(users.getId())
                        .name(users.getName())
                        .createdAt(users.getCreatedAt())
                        .modifiedAt(users.getModifiedAt())
                        .build()
        );
    }
    //회원 가입

    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Users users = isPresentUser(requestDto.getName());
        if (null== users){
            return ResponseDto.fail("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }//가입된 회원인지 확인
        if(!users.validatePassword(passwordEncoder, requestDto.getPassword())){
            return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.(password 오류)");
        }//패스워드 확인

        TokenDto tokenDto = tokenProvider.generateTokenDto(users); //토큰 생성
        tokenToHeaders(tokenDto, response); //생성한 토큰을 http 헤더로 전송

        return ResponseDto.success(
                UsersResponseDto.builder()
                        .id(users.getId())
                        .name(users.getName())
                        .createdAt(users.getCreatedAt())
                        .modifiedAt(users.getModifiedAt())
                        .build()
        ); //결과 출력
    }
    //로그인 -> 유효한 토큰 생성 및 헤더에 추가

    public ResponseDto<?> logout(HttpServletRequest request) {
        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token"))){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }//들여온 토큰이 유효한지 검사
        Users users = tokenProvider.getUserFromAuthentication();
        if(null == users){
            return ResponseDto.fail("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.");
        }//user가 존재 여부 확인

        return tokenProvider.deleteRefreshToken(users);
        //refreshToken 삭제 시 다시 발급이 안되니 즉 로그아웃
    }
    //로그아웃 -> 토큰 제거

    @Transactional(readOnly = true)
    public Users isPresentUser(String name) {
        Optional<Users> optionalUser = usersRepository.findByName(name);
        return optionalUser.orElse(null);
    }//현재 repository에 존재하는 회원인지 확인

    private void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken()); //access 토큰 헤더 추가
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken()); //refresh 토큰 헤더 추가
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString()); //access 토큰 만료 시간 헤어 추가
    }
}
