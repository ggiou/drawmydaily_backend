package sparta.drawmydaily_backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String grantType; //권한 부여
    private String accessToken; // 보호된 리소스에 액세스하는 데 사용되는 자격 증명
    private String refreshToken; //access Token의 유효기간이 지났을 때 새롭게 발급해주는 역할
    private Long accessTokenExpiresIn; //access token 만료 시간(유효기간)
}
