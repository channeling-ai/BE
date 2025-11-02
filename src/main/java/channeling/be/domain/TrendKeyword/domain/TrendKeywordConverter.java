package channeling.be.domain.TrendKeyword.domain;

import channeling.be.domain.TrendKeyword.presentation.TrendKeywordResDTO;

import java.util.List;
import java.util.stream.Collectors;

public class TrendKeywordConverter {

    public static TrendKeywordResDTO.TrendKeywordListDTO toChannelKeywordDTO(
            List<TrendKeyword> realTimeKeywords,
            List<TrendKeyword> channelKeywords
    ) {
        List<TrendKeywordResDTO.TrendKeywordInfo> realTimeList = realTimeKeywords.stream()
                .map(TrendKeywordConverter::toTrendKeywordInfo)
                .collect(Collectors.toList());

        List<TrendKeywordResDTO.TrendKeywordInfo> channelList = channelKeywords.stream()
                .map(TrendKeywordConverter::toTrendKeywordInfo)
                .collect(Collectors.toList());

        return TrendKeywordResDTO.TrendKeywordListDTO.builder()
                .realTimeTrendKeywordList(realTimeList)
                .channelTrendKeywordInfoList(channelList)
                .build();
    }

    /**
     * 공통 변환 로직
     */
    private static TrendKeywordResDTO.TrendKeywordInfo toTrendKeywordInfo(TrendKeyword trendKeyword) {
        return TrendKeywordResDTO.TrendKeywordInfo.builder()
                .trendKeywordId(trendKeyword.getId())
                .keywordType(trendKeyword.getKeywordType())
                .keyword(trendKeyword.getKeyword())
                .score(trendKeyword.getScore())
                .createdAt(trendKeyword.getCreatedAt())
                .build();
    }
}
