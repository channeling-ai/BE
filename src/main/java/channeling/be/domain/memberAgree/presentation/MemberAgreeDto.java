package channeling.be.domain.memberAgree.presentation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

public class MemberAgreeDto {

    @Data
    public static class EditReqDto {
        @NotNull
        private Long id;
        @NotNull
        private Boolean marketingEmailAgree;
        @NotNull
        private Boolean dayContentEmailAgree;
    }

    @Data
    @Builder
    public static class EditResDto {
        private Long id;
        private Boolean marketingEmailAgree;
        private Boolean dayContentEmailAgree;
        private Long MemberId;
    }
}
