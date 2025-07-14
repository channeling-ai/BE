package channeling.be.domain.memberAgree.application;

import channeling.be.domain.member.domain.Member;
import channeling.be.domain.memberAgree.domain.MemberAgree;
import channeling.be.domain.memberAgree.domain.repository.MemberAgreeRepository;
import channeling.be.domain.memberAgree.presentation.MemberAgreeReqDto;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.MemberHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAgreeServiceImpl implements MemberAgreeService {

    private final MemberAgreeRepository memberAgreeRepository;

    @Transactional
    @Override
    public MemberAgree editMemberAgree(MemberAgreeReqDto.Edit dto, Member member) {
        MemberAgree memberAgree = memberAgreeRepository.findById(dto.getId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_AGREE_NOT_FOUND));

        if (!memberAgree.getMember().getId().equals(member.getId())) {
            throw new MemberHandler(ErrorStatus._MEMBER_AGREE_NOT_ALLOW);
        }

        memberAgree.editDayContentEmailAgree(dto.getDayContentEmailAgree());
        memberAgree.editMarketingEmailAgree(dto.getMarketingEmailAgree());

        return memberAgree;
    }
}
