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
	 * 멤버에 해당하는 채널을 찾거나 생성합니다.
	 * 내부적으로 createOrGetBasicChannel → syncVideos → updateChannelStats 순으로 호출합니다.
	 */
	Channel updateOrCreateChannelByMember(Member member);

	/**
	 * 기본 채널 정보를 조회하거나, 신규 사용자면 YouTube API로 채널을 생성합니다.
	 * YouTube API 호출은 트랜잭션 밖에서, DB 저장은 @Transactional 안에서 수행합니다.
	 */
	Channel createOrGetBasicChannel(Member member, String googleAccessToken);

	/**
	 * 채널의 영상을 YouTube API로 조회하여 동기화합니다.
	 * YouTube API 호출은 트랜잭션 밖에서, DB 저장은 @Transactional 안에서 수행합니다.
	 */
	void syncVideos(Channel channel, String googleAccessToken);

	/**
	 * 채널의 통계(조회수, 구독자, 공유수 등)를 YouTube API로 갱신합니다.
	 * YouTube API 호출은 트랜잭션 밖에서, DB 저장은 @Transactional 안에서 수행합니다.
	 */
	void updateChannelStats(Channel channel, String googleAccessToken);

	/**
	 * 채널 정보를 조회합니다.
	 */
	Channel getChannel(Long channelId, Member member);
}
