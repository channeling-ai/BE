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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberAgreeServiceImpl implements MemberAgreeService {

    private final MemberAgreeRepository memberAgreeRepository;

    @Transactional
    @Override
    public MemberAgree editMemberAgree(MemberAgreeReqDto.Edit dto, Member member) {
        return memberAgreeRepository.findByMemberId(member.getId())
                .map(existingAgree -> {
                    existingAgree.editDayContentEmailAgree(dto.getDayContentEmailAgree());
                    existingAgree.editMarketingEmailAgree(dto.getMarketingEmailAgree());
                    return existingAgree;
                })
                .orElseGet(() -> memberAgreeRepository.save(
                        MemberAgree.builder()
                                .dayContentEmailAgree(dto.getDayContentEmailAgree())
                                .marketingEmailAgree(dto.getMarketingEmailAgree())
                                .member(member)
                                .build()
                ));
    }
}
