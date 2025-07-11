package channeling.be.domain.member.presentation;

public class MemberReqDTO {
	public record updateSnsReq(
		String instagramLink,
		String tiktokLink,
		String facebookLink,
		String twitterLink
	) {
	}
}
