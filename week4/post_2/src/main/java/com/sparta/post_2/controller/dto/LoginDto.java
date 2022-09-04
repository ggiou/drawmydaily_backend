package com.sparta.post_2.controller.dto;

import com.sparta.post_2.model.Authority;
import com.sparta.post_2.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.sparta.post_2.model.Member.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String nickname;
    private String password;

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
