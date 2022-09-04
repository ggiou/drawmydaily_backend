package com.sparta.post_2.controller.dto;

import com.sparta.post_2.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    private String email;

    public static MemberResponseDto of(Member member){
        return new MemberResponseDto(member.getNickname());
    }
}
