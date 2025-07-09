package channeling.be.domain.channel.presentation.dto.request;

import lombok.Getter;

public class ChannelRequestDto {
    @Getter
    public static class EditChannelConceptReqDto{
        String concept; // 수정할 컨셉 정보
    }

    @Getter
    public static class EditChannelTargetReqDto{
        String target; // 수정할 타겟 정보
    }
}
