package channeling.be.domain.TrendKeyword.domain.repository;

import channeling.be.domain.TrendKeyword.domain.TrendKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrendKeywordRepository extends JpaRepository<TrendKeyword, Long> {

    @Modifying
    @Query("""
    DELETE
    FROM TrendKeyword tk
    WHERE tk.report.id = :reportId AND tk.report.video.channel.member.id = :memberId
""")
    void deleteAllByReportAndMember(@Param("reportId") Long reportId, @Param("memberId") Long memberId);
}
