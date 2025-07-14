package channeling.be.domain.member.application;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.member.presentation.MemberReqDTO;
import channeling.be.domain.member.presentation.MemberResDTO;

public interface MemberService {

    Member findOrCreateMember();
	  MemberResDTO.updateSnsRes updateSns(MemberReqDTO.updateSnsReq updateSnsReq);

}
