package sparta.drawmydaily_backend.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AmazonS3ServiceConfig {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey; //접근 키
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey; //시크릿 키
    @Value("${cloud.aws.region.static}")
    private String region; //지역

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
} //aws에 s3에 접근하기 위해서는 IAM 연결 CONFIG
