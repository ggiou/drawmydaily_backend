package com.sparta.post_2.controller.dto;

import com.sparta.post_2.model.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    private String nickname;
    private Long id;
    private String createdDate;
    private String modifiedDate;

    public MemberDto(Member member){
        this.nickname = member.getNickname();
        this.id = member.getId();
        this.createdDate = member.getCreatedAt().toString();
        this.modifiedDate = member.getModifiedAt().toString();
    }
}
