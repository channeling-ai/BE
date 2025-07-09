package channeling.be.domain.channel.presentation.converter;

import channeling.be.domain.channel.domain.Channel;

import static channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto.*;

public class ChannelConverter {
    public static EditChannelConceptResDto toEditChannelConceptResDto(String concept) {
        return EditChannelConceptResDto.builder()
                .updatedConcept(concept)
                .message("성공적으로 채널 컨셉을 수정하였습니다.").build();
    }
    public static EditChannelTargetResDto toEditChannelTargetResDto(Channel channel) {
        return EditChannelTargetResDto.builder()
                .channelId(channel.getId())
                .updatedTarget(channel.getTarget())
                .build();
    }

}
