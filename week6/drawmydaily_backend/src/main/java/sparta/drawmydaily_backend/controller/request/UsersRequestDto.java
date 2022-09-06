package sparta.drawmydaily_backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsersRequestDto {
    @NotBlank
    //@Size(min = 4, max = 12) //이름(아이디)는 4자 이상 12자이네
    //@Pattern(regexp = "[a-zA-Z\\d]*${3,12}") //최소 4자 이상, 12자 이하 알파벳 대소문자(a~z, A~Z), 숫자(0~9)로 구성
    private String name;

    @NotBlank
    //@Size(min = 4, max = 32) //비밀번호는 4자 이상, 32이자 이네
    //@Pattern(regexp = "[a-z\\d]*${3,32}") //최소 4자 이상이며, 32자 이하 알파벳 소문자(a~z), 숫자(0~9)
    private String password;

    @NotBlank
    private String passwordConfirm;//비밀번호 일치 재확인
}
