package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.response.exception.handler.ChannelHandler;

import static channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto.*;

public interface ChannelService {
    String editChannelConcept(EditChannelConceptReqDto request);
	/**
	 * 채널 ID로 채널의 존재 여부를 검증합니다.
	 *
	 * @param channelId 채널 ID
	 * @throws ChannelHandler 채널이 존재하지 않을 경우 예외 발생
	 */
	void validateChannelByIdAndMember(Long channelId);
	/**
	 * 채널의 타겟 정보를 수정합니다.
	 */
	Channel editChannelTarget(Long channelId, EditChannelTargetReqDto request);
}
