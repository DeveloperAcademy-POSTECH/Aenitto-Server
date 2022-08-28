package com.firefighter.aenitto.messages.dto.response;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.domain.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor

public class MemoriesResponse {
    private final Memory memoriesWithManitto;
    private final Memory memoriesWithManittee;

    public static MemoriesResponse of(Member myManitto, Member myManittee,
                                      List<Message> memoriesWithManitto, List<Message> memoriesWithManittee) {
        return MemoriesResponse.builder()
                .memoriesWithManittee(Memory.of(myManittee, memoriesWithManittee))
                .memoriesWithManitto(Memory.of(myManitto, memoriesWithManitto))
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Memory {
        private final MemberInfo member;
        private final List<MessageResponse> message;

        public static Memory of(Member member, List<Message> messages) {
            return Memory.builder().member(new MemberInfo(member))
                    .message(MessageResponse.listOf(messages)).build();
        }

        @Getter
        @NoArgsConstructor(force = true)
        public static class MemberInfo {
            private final String nickname;

            public MemberInfo(Member member) {
                nickname = member.getNickname();
            }
        }
    }
}
