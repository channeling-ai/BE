package channeling.be.domain.member.application;

import channeling.be.domain.member.domain.Member;

public interface MemberService {
    Member findOrCreateMember();

}
