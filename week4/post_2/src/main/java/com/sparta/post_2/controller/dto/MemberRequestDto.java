package com.sparta.post_2.controller.request;

import com.sparta.post_2.model.Authority;
import com.sparta.post_2.model.Member;
import com.sparta.post_2.controller.request.LoginDto;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.sparta.post_2.model.Member.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {
    private String nickname;
    private String password;
    private String passwordComfirm;

    public Member toMember(PasswordEncoder passwordEncoder){
        return builder()
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication(){
        return new UsernamePasswordAuthenticationToken(nickname, password);
    }


}
