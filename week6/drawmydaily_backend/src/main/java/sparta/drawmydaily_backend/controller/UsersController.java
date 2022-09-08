package sparta.drawmydaily_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sparta.drawmydaily_backend.controller.request.LoginRequestDto;
import sparta.drawmydaily_backend.controller.request.UsersRequestDto;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class UsersController {

    private final UserService userService;

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public ResponseDto<?> allUserName(){
        return userService.allUserName();
    }
    //회원가입 된 모든 사용자 정보(비밀번호 제외)


    @RequestMapping(value = "/api/users/signup", method = RequestMethod.POST)
    public ResponseDto<?> signup(@RequestBody @Valid UsersRequestDto requestDto){
        return userService.createUser(requestDto);
    }
    //회원가입 -> signin 페이지(완료를 눌러야 데이터가 전송되 회원가입 성공되니..)

    @RequestMapping(value = "/api/users/login", method = RequestMethod.POST)
    public ResponseDto<?> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response){
        return userService.login(requestDto, response);
    }
    //로그인 -> login 페이지(로그인을 해야 헤더에 토큰, 쿠키 등 응답받음)

    @RequestMapping(value = "/api/auth/users/logout", method = RequestMethod.POST)
    public ResponseDto<?> logout(HttpServletRequest request){
        return userService.logout(request);
    }
    //로그아웃

}
