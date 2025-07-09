package channeling.be.domain.channel.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ChannelResponseDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditChannelConceptResDto{
        String message; //응답 메세지
        String updatedConcept; //수정한 컨셉 내용
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditChannelTargetResDto{
        Long channelId; // 채널 아이디
        String updatedTarget; //수정한 타겟 내용
    }
}
