package channeling.be.domain.channel.presentation.converter;

import channeling.be.domain.channel.domain.Channel;

import static channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto.*;

public class ChannelConverter {
    public static EditChannelConceptResDto toEditChannelConceptResDto(Channel channel) {
        return EditChannelConceptResDto.builder()
                .channelId(channel.getId())
                .updatedConcept(channel.getConcept())
                .build();
    }
    public static EditChannelTargetResDto toEditChannelTargetResDto(Channel channel) {
        return EditChannelTargetResDto.builder()
                .channelId(channel.getId())
                .updatedTarget(channel.getTarget())
                .build();
    }

}
