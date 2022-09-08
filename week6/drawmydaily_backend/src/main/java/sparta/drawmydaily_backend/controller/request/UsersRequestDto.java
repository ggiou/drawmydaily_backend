package sparta.drawmydaily_backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class UsersRequestDto {
    @NotBlank
    @Size(min = 3, max = 12 )
    @Pattern(regexp = "[a-z\\d]*${3,12}", message = "아이디는 최소 3글자 이상 12자 이하의 소문자와 숫자로만 이루어져야 합니다.")
    private String name;

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{3,20}$",message = "비밀번호는 최소 3글자 이상 20자 이하며 하나의 대소문자와 숫자가 포함되야 합니다.")
    private String password;

    @NotBlank
    private String passwordConfirm;//비밀번호 일치 재확인
}
