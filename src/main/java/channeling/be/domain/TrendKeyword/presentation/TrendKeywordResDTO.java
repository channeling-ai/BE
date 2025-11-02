package channeling.be.domain.TrendKeyword.presentation;

import channeling.be.domain.TrendKeyword.domain.TrendKeywordType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class TrendKeywordResDTO {

    @Builder
    public record TrendKeywordListDTO (
        @Schema(description = "리포트 - 트랜드 키워드")
        List<TrendKeywordInfo> realTimeTrendKeywordList,
        @Schema(description = "리포트 - 체널 키워드")
        List<TrendKeywordInfo> channelTrendKeywordInfoList
    ){}


    @Builder
    public record TrendKeywordInfo(
        Long trendKeywordId,
        TrendKeywordType keywordType,
        String keyword,
        Integer score,
        LocalDateTime createdAt
    ){}
}
