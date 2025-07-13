package channeling.be.domain.member.application;

import channeling.be.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {
    @Override
    public Member findOrCreateMember() {
        return null;
    }
}
