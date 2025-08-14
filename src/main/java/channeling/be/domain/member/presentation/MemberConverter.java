package channeling.be.domain.member.presentation;

import channeling.be.domain.member.domain.Member;

import java.time.LocalDateTime;
import java.util.PrimitiveIterator;

public class MemberConverter {

	/**
	 * 업데이트 된 SNS 링크를 반환하는 메서드입니다.
	 *
	 * @return 업데이트 된 SNS 링크
	 */
	public static MemberResDTO.updateSnsRes toUpdatedSns(String instagramLink, String tiktokLink, String facebookLink, String twitterLink) {
		return MemberResDTO.updateSnsRes.builder()
				.instagramLink(instagramLink)
				.tiktokLink(tiktokLink)
				.facebookLink(facebookLink)
				.twitterLink(twitterLink)
				.build();
	}

	public static MemberResDTO.updateProfileImageRes toUpdatedProfileImage(String updatedProfileUrl) {
		return MemberResDTO.updateProfileImageRes.builder()
				.updatedProfileImage(updatedProfileUrl)
				.build();
	}

	public static MemberResDTO.getMemberInfo toGetMemberInfo(Member member, Boolean marketingEmailAgree, Boolean dayContentEmailAgree) {
		return MemberResDTO.getMemberInfo.builder()
				.memberId(member.getId())
				.nickname(member.getNickname())
				.googleEmail(member.getGoogleEmail())
				.profileImage(member.getProfileImage())
				.instagramLink(member.getInstagramLink())
				.tiktokLink(member.getTiktokLink())
				.facebookLink(member.getFacebookLink())
				.twitterLink(member.getTwitterLink())
				.marketingEmailAgree(marketingEmailAgree)
				.dayContentEmailAgree(dayContentEmailAgree)
				.build();

	}
}
