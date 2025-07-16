package channeling.be.domain.memberAgree.domain;

import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberAgree extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Boolean marketingEmailAgree; // 첫 번째 질문 동의 여부

    @Column(nullable = false)
    private Boolean dayContentEmailAgree; // 두 번째 질문 동의 여부


    public void editMarketingEmailAgree(Boolean marketingEmailAgree) {
        this.marketingEmailAgree = marketingEmailAgree;
    }

    public void editDayContentEmailAgree(Boolean dayContentEmailAgree) {
        this.dayContentEmailAgree = dayContentEmailAgree;
    }
}
