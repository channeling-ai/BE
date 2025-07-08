package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_FOUND;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChannelServiceImpl implements ChannelService {

	private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public String editChannelConcept(ChannelRequestDto.EditChannelConceptReqDto request) {
        Long channelId = request.getChannelId(); // id로 채널 조회 -> 추후 로그인 멤버 가져온 후, 멤버로 조회하는 걸로 바궈야 할 듯..? 일대일이니까..
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));
        channel.editConcept(request.getConcept()); // 더티체킹
        return request.getConcept();
    }

    @Override
	public void validateChannelByIdAndMember(Long channelId) {
		boolean isExist=channelRepository.existsById(channelId);
		if (!isExist) {
			throw new ChannelHandler(_CHANNEL_NOT_FOUND);
		}
		//TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
	}

}
