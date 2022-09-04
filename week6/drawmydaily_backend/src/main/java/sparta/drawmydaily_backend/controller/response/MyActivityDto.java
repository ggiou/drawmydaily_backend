package sparta.drawmydaily_backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyActivityDto {
    private String name;
    private List<PostResponseDto> postResponseDtoList;
}
