package channeling.be.domain.memberAgree.presentation;

import channeling.be.domain.memberAgree.domain.MemberAgree;

public class MemberAgreeConverter {

    public static MemberAgreeReqDto.EditResDto toEditMemberAgreeResDto(MemberAgree memberAgree) {
        return MemberAgreeReqDto.EditResDto.builder()
                .id(memberAgree.getId())
                .dayContentEmailAgree(memberAgree.getDayContentEmailAgree())
                .marketingEmailAgree(memberAgree.getMarketingEmailAgree())
                .MemberId(memberAgree.getMember().getId())
                .build();
    }
}
