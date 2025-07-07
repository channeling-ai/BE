package channeling.be.domain.channel.application;

import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;

public interface ChannelService {
    String editChannelConcept(ChannelRequestDto.EditChannelConceptReqDto request);
}
