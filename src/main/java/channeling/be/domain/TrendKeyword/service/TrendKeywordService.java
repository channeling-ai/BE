package channeling.be.domain.TrendKeyword.service;

import channeling.be.domain.TrendKeyword.presentation.TrendKeywordResDTO;
import channeling.be.domain.member.domain.Member;

public interface TrendKeywordService {
    void updateChannelTrendKeyword(Member member);
    TrendKeywordResDTO.TrendKeywordListDTO getTrendKeywordList(Member member);

}
