package channeling.be.domain.channel.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class ChannelRequestDto {
    @Getter
    public static class EditChannelConceptReqDto{
        @Schema(type = "string", description = "수정할 채널의 컨셉입니다", example = "기술 리뷰")
        String concept; // 수정할 컨셉 정보
    }

    @Getter
    public static class EditChannelTargetReqDto{
        @Schema(type = "string", description = "수정할 채널의 타겟입니다", example = "20대 대학생")
        String target; // 수정할 타겟 정보
    }
}
