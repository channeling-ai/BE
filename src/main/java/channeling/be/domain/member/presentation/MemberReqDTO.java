package channeling.be.domain.member.presentation;

import org.springframework.web.multipart.MultipartFile;

public class MemberReqDTO {
	public record updateSnsReq(
		String instagramLink,
		String tiktokLink,
		String facebookLink,
		String twitterLink
	) {
	}

	public record ProfileImageUpdateReq(
		MultipartFile image   // 새 프로필 이미지
	) {}
}
