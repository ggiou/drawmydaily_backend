
# 그림일기(Draw My Daily)

소소한 일상의 이야기를 그림으로 그려보세요! Draw My Daily에서 그림일기를 공유하고 다른 사람들과 함께 이야기를 나누세요!

## 구현한 기능
- 게시물 CRUD (formData로 사진, 텍스트 업로드)
- 댓글 CRD
- 로그인 회원가입
- 마이페이지

## 와이어 프레임

![image](https://user-images.githubusercontent.com/110383614/189154576-b07c3193-3829-4e17-8500-c94b0ce13539.png)

## API 명세서

![dmd_api](https://user-images.githubusercontent.com/110383614/189156458-f44eed23-6752-4da4-91bc-f6a5aa1634e6.png)

## 트러블슈팅

### `BE`
- aws s3 서버와 연동, 파일 서버와 s3서버에서의 동시 cud 문제  
(1)처음에 이부분에 대해 제대로 이해하지 못해서 s3 서버와 연결하기 위해 properties에 버킷이름, 시크릿, 인증 키, 지역만 넣음 -> s3 인증 키를 넣어 iam 사용자를 안 만들어 실패
(2) iam의 시크릿, 인증 키를 넣어줘야하는 걸 알고,  AmazonS3Config 생성 ->  s3와 연동 성공, 파일이 올라가나 delete, update에 권한이 없다는 에러 발생
(3) iam 권한 수정 (AmazonS3FullAccess) -> 권한 인증이 되지만 null 값 반환 에러 
(4) 원래는 매핑시키는 방식으로 할려고 서버 db에 따로 image에 관한 컬럼을 만들어 줬는데이렇게 할 시 파일과 jason 형식의 데이터를 전달 받지 못하는 걸 알고 multipart/form-data로
보내기 위해 수정 -> null 값 반환 에러 
(5) post entity에 setter을 빠트려서 생성 되지 않아 null 값 -> 성공 
    
 - back, front의 연결 에러
(1) cors 에러를 막기 위해  mapping에 allow하는 webMvcConfig 생성 -> front에서 메소드 사용, 서버 연결, 헤더에 정보 전달 성공, header에 정보를 저장 했지만 회원 가입, 로그인시 refreshtoken 문제
(2) Refresh-Token --> RefreshToken 변경  ->  성공


### `FE`
- formData와 이미지file
 이미지file과 다른 text들을 함께 서버로 보내고 싶다면 기존에 사용하던 객체로 묶어 보내주는 방법을 사용하면 안 됨. 
 formData에 append해서 formData를 보내줘야하고, 서버측에서는 formData를 받아서 처리를 해줘야 함.
 text를 json형식으로 변환하고, 이미지file만 formData형식으로 보내는 것은 좋은 방법이 아님 (blob 처리하면 텍스트를 다시 파일로 생성하여 보내는 것)
 FE, BE모두 이미지파일과 텍스트데이터를 같이 보내주고 받아서 처리해본 경험이 없어서 해결하는데 시간이 걸림

- 변경사항이 화면에 바로 반영되지 않는 문제
 useEffect의 의존배열을 활용해서 변경사항이 생기면 리렌더링되도록 만들어서 해결

- 게시글 상세페이지에서 content가 나오지 않는 문제
  FE팀원이 API명세서를 잘못보고 다른 url로 GET요청을 보내서 응답으로 온 데이터를 활용하느라 content가 없었음. 
  백엔드에서 content를 추가로 전달해주어 데이터를 활용할 수 있었음. 
  API설계, 명세서 참고에 대한 중요성을 알 수 있었음

- 조건부 렌더링, 유효성 검사 처리
  삼항연산자를 활용하여 회원가입 부분 유효성검사 해결
  비회원에게는 작성권한이 없기 때문에 조건부렌더링으로 작성버튼 제거
  
## 구현 화면
- ### `메인페이지`
![image](https://user-images.githubusercontent.com/110383614/189157821-681c7a59-92ba-435f-9f8a-383e4c9282a7.png)


## More
- `전시회 링크` https://hanghae99-v2.spartacodingclub.kr/v2/exhibitions/innovation
- `S.A링크` https://zest-replace-17b.notion.site/11-SA-ddbb79fd26d54e36b79e1db094403d33
- `FE` https://github.com/ggiou/draw_my_daliy_backend   
- `BE` https://github.com/hyerriimm/W6_Draw_My_Daily

