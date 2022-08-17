package com.firefighter.aenitto.members.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(force = true)
public class ChangeNicknameRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Length(min = 1, max = 5, message = "성함은 5자 이하로 작성해주세요.")
    private String nickname;

    @Builder
    public ChangeNicknameRequest(String nickname) {
        this.nickname = nickname;
    }

}
