package channeling.be.domain.memberAgree.presentation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MemberAgreeReqDto {

    @Getter
    public static class Edit {
        @NotNull(message = "null 이어서는 안됩니다..")
        Boolean marketingEmailAgree;

        @NotNull(message = "null 이어서는 안됩니다..")
        Boolean dayContentEmailAgree;
    }
}
