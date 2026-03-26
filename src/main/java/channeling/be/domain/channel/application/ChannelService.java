package channeling.be.domain.channel.application;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ChannelHandler;

import static channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto.*;

public interface ChannelService {
	/**
	 * 채널의 컨셉 정보를 수정합니다.
	 */
	Channel editChannelConcept(Long channelId, EditChannelConceptReqDto request, Member member);
	/**
	 * 채널 ID로 채널의 존재 여부를 검증합니다.
	 *
	 * @param channelId 채널 ID
	 * @throws ChannelHandler 채널이 존재하지 않을 경우 예외 발생
	 */
	void validateChannelByIdAndMember(Long channelId,Member member);
	/**
	 * 채널의 타겟 정보를 수정합니다.
	 */
	Channel editChannelTarget(Long channelId, EditChannelTargetReqDto request, Member member);

	/**
	 * 기본 채널 정보를 조회하거나, 신규 사용자면 YouTube API로 채널을 생성합니다.
	 * YouTube API 호출은 트랜잭션 밖에서, DB 저장은 TransactionTemplate 안에서 수행합니다.
	 *
	 * @return ChannelCreationResult(channel, isChannelNew) — isChannelNew는 채널 신규 여부
	 */
	ChannelCreationResult createOrGetBasicChannel(Member member, String googleAccessToken);

	record ChannelCreationResult(Channel channel, boolean isChannelNew) {}

	/**
	 * 채널 정보를 조회합니다.
	 */
	Channel getChannel(Long channelId, Member member);
}
