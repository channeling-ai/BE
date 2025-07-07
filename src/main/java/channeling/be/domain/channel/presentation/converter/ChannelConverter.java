package channeling.be.domain.channel.presentation.converter;

import static channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto.*;

public class ChannelConverter {
    public static EditChannelConceptResDto toEditChannelConceptResDTo(String message) {
        return EditChannelConceptResDto.builder()
                .message(message).build();
    }

}
