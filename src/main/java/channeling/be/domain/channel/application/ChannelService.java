package channeling.be.domain.channel.application;

import channeling.be.domain.channel.presentation.ChannelResDTO;
import channeling.be.response.exception.GeneralException;

public interface ChannelService {
	/**
	 * 채널 ID로 채널의 존재 여부를 검증합니다.
	 *
	 * @param channelId 채널 ID
	 * @throws channeling.be.response.exception.handler.ChannelHandler 채널이 존재하지 않을 경우 예외 발생
	 */
	void validateChannelByIdAndMember(Long channelId);
}
