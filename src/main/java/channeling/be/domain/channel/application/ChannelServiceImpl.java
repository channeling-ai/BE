package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.channel.presentation.converter.ChannelConverter;
import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.youtube.YouTubeApiService;
import channeling.be.global.infrastructure.youtube.dto.res.YoutubeChannelResDTO;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_FOUND;
import static channeling.be.response.code.status.ErrorStatus._CHANNEL_NOT_MEMBER;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class ChannelServiceImpl implements ChannelService {
	private final ChannelRepository channelRepository;
	private final YouTubeApiService youTubeApiService;

    @Override
    @Transactional
    public Channel editChannelConcept(Long channelId, ChannelRequestDto.EditChannelConceptReqDto request,Member loginMember) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

        if (!channel.getMember().getId().equals(loginMember.getId())) {
            throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
        }
        channel.editConcept(request.getConcept()); // 더티체킹
        return channel;
    }

	@Override
	public void validateChannelByIdAndMember(Long channelId,Member member) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

		if (!channel.getMember().getId().equals(member.getId())) {
			throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
		}
	}

    @Override
    @Transactional
    public Channel editChannelTarget(Long channelId, ChannelRequestDto.EditChannelTargetReqDto request, Member loginMember) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelHandler(_CHANNEL_NOT_FOUND));

        if (!channel.getMember().getId().equals(loginMember.getId())) {
            throw new ChannelHandler(_CHANNEL_NOT_MEMBER);
        }
        channel.editTarget(request.getTarget()); // 더티체킹
        return channel;
    }

	// ─── 채널 생성/조회 ───

	@Override
	@Transactional(propagation = Propagation.NEVER) // 외부 API 호출 포함, 트랜잭션 관리 직접
	public ChannelCreationResult createOrGetBasicChannel(Member member, String googleAccessToken) {
		Optional<Channel> existing = channelRepository.findByMember(member);
		if (existing.isPresent()) {
			return new ChannelCreationResult(existing.get(), false);
		}

		// YouTube API 호출
		YoutubeChannelResDTO.Item item = youTubeApiService.fetchChannelDetails(googleAccessToken);
		long shares = youTubeApiService.fetchAllVideoShares(
				googleAccessToken, item.getSnippet().getPublishedAt(), LocalDateTime.now());

		// save()는 Repository 자체 @Transactional로 독립 write tx 실행
		Channel newChannel = channelRepository.save(ChannelConverter.toNewChannel(item, member, shares, "0"));
		return new ChannelCreationResult(newChannel, true);
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
