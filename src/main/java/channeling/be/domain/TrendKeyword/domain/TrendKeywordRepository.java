package channeling.be.domain.TrendKeyword.domain;

import channeling.be.domain.channel.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrendKeywordRepository extends JpaRepository<TrendKeyword, Long> {
    List<TrendKeyword> getTrendKeywordsByChannel(Channel channel);
    @Query("SELECT t FROM TrendKeyword t WHERE t.keywordType = 'REAL_TIME'")
    List<TrendKeyword> getRealTimeTrendKeywords();
}

