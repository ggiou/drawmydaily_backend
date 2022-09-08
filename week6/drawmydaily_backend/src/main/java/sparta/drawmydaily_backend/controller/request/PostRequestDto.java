package sparta.drawmydaily_backend.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String title;

    @Size(max = 300, message = "게시글의 내용은 300자 이하로 입력할 수 있습니다.")
    private String content;

    private String sayMe;
    private String date;
    private MultipartFile image;
}
