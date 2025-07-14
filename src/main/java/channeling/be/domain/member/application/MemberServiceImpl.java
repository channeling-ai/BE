package channeling.be.domain.member.application;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.domain.repository.MemberRepository;
import channeling.be.domain.member.presentation.MemberConverter;
import channeling.be.domain.member.presentation.MemberReqDTO;
import channeling.be.domain.member.presentation.MemberResDTO;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.MemberHandler;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	@Override
	public MemberResDTO.updateSnsRes updateSns(MemberReqDTO.updateSnsReq updateSnsReq) {
		//TODO: 자신의 정보를 수정하는 것인지 확인 필요
		Long memberId = 1L; // 임시로 1L로 설정, 실제로는 인증된 멤버의 ID를 사용해야 함
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_NOT_FOUND));
		member.updateSnsLinks(
			updateSnsReq.instagramLink(),
			updateSnsReq.tiktokLink(),
			updateSnsReq.facebookLink(),
			updateSnsReq.twitterLink()
		);

		return MemberConverter.toUpdatedSns(
			member.getInstagramLink(),
			member.getTiktokLink(),
			member.getFacebookLink(),
			member.getTwitterLink()
		);
	}
  
    @Override
    public Member findOrCreateMember() {
        return null;
    }
}
