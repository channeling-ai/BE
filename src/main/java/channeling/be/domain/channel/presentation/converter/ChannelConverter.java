package channeling.be.domain.channel.presentation.converter;

import static channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto.*;

public class ChannelConverter {
    public static EditChannelConceptResDto toEditChannelConceptResDTo(String concept) {
        return EditChannelConceptResDto.builder()
                .updatedConcept(concept)
                .message("성공적으로 채널 컨셉을 수정하였습니다.").build();
    }

}
