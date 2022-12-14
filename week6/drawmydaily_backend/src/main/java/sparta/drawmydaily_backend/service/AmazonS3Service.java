package sparta.drawmydaily_backend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sparta.drawmydaily_backend.controller.response.ResponseDto;
import sparta.drawmydaily_backend.repository.PostRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Component
@Service
@RequiredArgsConstructor
@Slf4j //CMD에 오류 출력
public class AmazonS3Service {


    private final AmazonS3Client amazonS3Client;
    private final PostRepository postRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //파일 업로드
    @Transactional //db상태 변화를 위해 수행하는 작업
    public ResponseDto<?> uploadFile(MultipartFile multipartFile){
        if(validateFileExists(multipartFile)) //유효파일인지 = 빈 파일인지 확인
            return ResponseDto.fail("NO_EXIST_FILE", "등록된 이미지가 없습니다.");
        String fileName = createFileName(multipartFile.getOriginalFilename()); //난수 파일 이름 생성(난수이름+파일이름)
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize()); //ObjectMetadata에 파일 타입, byte크기 넣어주기. 넣지않으면 IDE상에서 설정하라는 권장로그가 뜸

        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)); //S3 업로드
        }catch (IOException e){
            return ResponseDto.fail("FILE_UPLOAD_FAIL", "파일 업로드 실패"); //업로드 실패 오류 표시
        }
        String imageURL= amazonS3Client.getUrl(bucketName, fileName).toString();
        return ResponseDto.success(imageURL);
    }

    @Transactional
    public void removeFile(String imageURL) throws IOException {
        if (postRepository.findByImageURL(imageURL).isEmpty()) {
            return; //이미지가 이미 없으면 삭제 된 것
        }
        String urlFileName = imageURL.substring(46);
        String imageKey = URLDecoder.decode(urlFileName,"UTF-8"); //한글 인코딩
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, imageKey); //삭제 request 생성
            amazonS3Client.deleteObject(request); //s3에서 파일 삭제
    }//파일 삭제

    private String createFileName(String originalFilename) {
        return UUID.randomUUID().toString().concat(originalFilename);
    }//네트워크 상에서 고유성이 보장되는 id를 만들어 줌, 즉 유니크한 파일 이름 생성

    private boolean validateFileExists(MultipartFile multipartFile) {
        return multipartFile.isEmpty();
    } //빈 파일인지 확인, 실제있는 파일인지 확인



}
