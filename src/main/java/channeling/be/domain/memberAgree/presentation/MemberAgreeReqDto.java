package channeling.be.domain.memberAgree.presentation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

public class MemberAgreeReqDto {

    @Data
    public static class Edit {
        @NotNull
        private Boolean marketingEmailAgree;
        @NotNull
        private Boolean dayContentEmailAgree;
    }
}
