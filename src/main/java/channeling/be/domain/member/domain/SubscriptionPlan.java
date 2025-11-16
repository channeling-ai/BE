package channeling.be.domain.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPlan {

    FREE(3, 12),
    BASIC(10, 30),
    ENTERPRISE(10, 30), // TODO : 추후 수정 필요
    ADMIN(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final int reportLimit;
    private final int ideaLimit;
}