package channeling.be.domain.memberAgree.presentation;

import channeling.be.domain.memberAgree.domain.MemberAgree;

public class MemberAgreeConverter {

    public static MemberAgreeResDto.Edit toEditMemberAgreeResDto(MemberAgree memberAgree) {
        return MemberAgreeResDto.Edit.builder()
                .id(memberAgree.getId())
                .dayContentEmailAgree(memberAgree.getDayContentEmailAgree())
                .marketingEmailAgree(memberAgree.getMarketingEmailAgree())
                .MemberId(memberAgree.getMember().getId())
                .build();
    }
}
