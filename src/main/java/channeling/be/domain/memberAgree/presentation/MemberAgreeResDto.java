package channeling.be.domain.memberAgree.presentation;

import lombok.Builder;
import lombok.Data;

public class MemberAgreeResDto {

    @Data
    @Builder
    public static class Edit {
        private Long id;
        private Boolean marketingEmailAgree;
        private Boolean dayContentEmailAgree;
        private Long MemberId;
    }
}
