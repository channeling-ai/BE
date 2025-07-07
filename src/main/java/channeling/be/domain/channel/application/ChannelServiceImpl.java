package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.channel.presentation.ChannelResDTO;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChannelServiceImpl implements ChannelService {

	private final ChannelRepository channelRepository;

	@Override
	public void validateChannelByIdAndMember(Long channelId) {
		boolean isExist=channelRepository.existsById(channelId);
		if (!isExist) {
			throw new ChannelHandler(ErrorStatus._CHANNEL_NOT_FOUND);
		}
		//TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
	}
}
