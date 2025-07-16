package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_FOUND;
import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_MEMBER;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChannelServiceImpl implements ChannelService {
	private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public Channel editChannelConcept(Long channelId, ChannelRequestDto.EditChannelConceptReqDto request) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND)); // id로 채널 조회 -> 추후 로그인 멤버 가져온 후, 멤버로 조회하는 걸로 바궈야 할 듯..? 일대일이니까..
        channel.editConcept(request.getConcept()); // 더티체킹
        return channel;
    }

    @Override
	public void validateChannelByIdAndMember(Long channelId) {
		boolean isExist=channelRepository.existsById(channelId);
		if (!isExist) {
			throw new ChannelHandler(_CHANNEL_NOT_FOUND);
		}
		//TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
	}

    @Override
    @Transactional
    public Channel editChannelTarget(Long channelId, ChannelRequestDto.EditChannelTargetReqDto request) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));// id로 채널 조회 -> 추후 로그인 멤버 가져온 후, 멤버로 조회하는 걸로 바궈야 할 듯..? 일대일이니까..
        channel.editTarget(request.getTarget()); // 더티체킹
        return channel;
    }

    @Override
    public Channel getChannel(Long channelId, Member loggedInMember) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

        if (!channel.getMember().getId().equals(loggedInMember.getId())) {
            throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
        }

        return channel;
    }
}
