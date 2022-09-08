package sparta.drawmydaily_backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDto {
    private Long id;
    private String title;
    private String date;
    private String sayMe;
    private String user_name;
    private String imageURL;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
