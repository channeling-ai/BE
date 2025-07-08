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
}
