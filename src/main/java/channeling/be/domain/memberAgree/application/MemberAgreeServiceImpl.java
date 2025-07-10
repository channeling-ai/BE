package channeling.be.domain.memberAgree.application;

import channeling.be.domain.memberAgree.domain.MemberAgree;
import channeling.be.domain.memberAgree.domain.repository.MemberAgreeRepository;
import channeling.be.domain.memberAgree.presentation.MemberAgreeDto;
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
    public MemberAgree editMemberAgree(MemberAgreeDto.EditReqDto dto) {
        // TODO : 로그인 멤버 정보로 조회하는 로직 추가 필요
        MemberAgree memberAgree = memberAgreeRepository.findById(dto.getId())
                .orElseThrow(() -> new MemberHandler(ErrorStatus._MEMBER_AGREE_NOT_FOUND));

        memberAgree.editDayContentEmailAgree(dto.getDayContentEmailAgree());
        memberAgree.editMarketingEmailAgree(dto.getMarketingEmailAgree());

        return memberAgree;
    }
}
