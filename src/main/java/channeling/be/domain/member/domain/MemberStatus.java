package channeling.be.domain.member.domain;

/**
 * 회원 상태를 나타내는 Enum
 * - ACTIVE: 활성 상태 (정상 이용 가능)
 * - WITHDRAWN: 탈퇴 상태 (30일 이내 복구 가능)
 */
public enum MemberStatus {
    ACTIVE,
    WITHDRAWN
}
