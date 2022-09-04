package sparta.drawmydaily_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.service.MyPageService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @RequestMapping(value = "/api/auth/user/mypage", method = RequestMethod.GET)
    public ResponseDto<?> getMyPage(HttpServletRequest request){
        return myPageService.getMypage(request);
    }//로그인 된 사용자의 마이페이지 불러오기 -> login/signup외 모든 페이지 header의 마이페이지 버튼 클릭 시
}
