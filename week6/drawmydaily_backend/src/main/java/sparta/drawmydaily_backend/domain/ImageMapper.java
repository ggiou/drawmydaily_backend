package sparta.drawmydaily_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageMapper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private String imgName;

    @Column
    private String url;
} //S3 이미지 업로드 후 게시글 업로드, 삭제, 수정 위해 URL, imgName 설정
