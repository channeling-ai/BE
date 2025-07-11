package channeling.be.domain.member.presentation;

import java.time.LocalDateTime;
import java.util.PrimitiveIterator;

public class MemberConverter {

	/**
	 * 업데이트 된 SNS 링크를 반환하는 메서드입니다.
	 * @return 업데이트 된 SNS 링크
	 */
	public static MemberResDTO.updateSnsRes toUpdatedSns(String instagramLink,String tiktokLink,String facebookLink,String twitterLink) {
		return MemberResDTO.updateSnsRes.builder()
			.instagramLink(instagramLink)
			.tiktokLink(tiktokLink)
			.facebookLink(facebookLink)
			.twitterLink(twitterLink)
			.build();
	}
}
