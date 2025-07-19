package channeling.be.domain.member.presentation;


import lombok.Builder;

public class MemberResDTO {
	/**
	 * 멤버의 기본 정보를 반환하는 API의 응답 DTO입니다.
	 */

	/**
	 * 멤버의 sns 정보를 수정하는 API의 응답 DTO입니다.
	 */
	@Builder
	public record updateSnsRes(
		String instagramLink,
		String tiktokLink,
		String facebookLink,
		String twitterLink
	) {
	}

	/**
	 * 멤버의 프로필 사진 정보를 수정하는 API의 응답 DTO입니다.
	 */
	@Builder
	public record updateProfileImageRes(
			String updatedProfileImage
	) {
	}
}
