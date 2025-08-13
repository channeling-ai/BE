package channeling.be.domain.member.presentation;

import jakarta.validation.constraints.NotNull;
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
		@NotNull(message = "프로필 이미지는 필수입니다.")
		MultipartFile image   // 새 프로필 이미지
	) {}
}
