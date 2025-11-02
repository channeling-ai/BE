package channeling.be.domain.TrendKeyword.service;

import channeling.be.domain.TrendKeyword.domain.TrendKeyword;
import channeling.be.domain.TrendKeyword.domain.TrendKeywordConverter;
import channeling.be.domain.TrendKeyword.domain.TrendKeywordRepository;
import channeling.be.domain.TrendKeyword.presentation.TrendKeywordResDTO;
import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.channel.domain.repository.ChannelRepository;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.ChannelHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendKeywordServiceImpl implements TrendKeywordService {
    //환경변수에서 FASTAPI_URL 환경변수 불러오기
    @Value("${FASTAPI_URL:http://localhost:8000}")
    private String baseFastApiUrl;
    private final TrendKeywordRepository trendKeywordRepository;
    private final ChannelRepository channelRepository;
    private final RestTemplate restTemplate;

    @Async // 비동기로 호출
    @Transactional(readOnly = true)
    @Override
    public void updateChannelTrendKeyword(Member member) {
        Channel channel = channelRepository.findByMember(member).orElseThrow(() -> new ChannelHandler(ErrorStatus._MEMBER_NOT_FOUND));
        String url = baseFastApiUrl + "/trend-keywords/channel/{channelId}";

        try {
            String response = restTemplate.postForObject(url, null, String.class, channel.getId());
            System.out.println("FastAPI Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public TrendKeywordResDTO.TrendKeywordListDTO getTrendKeywordList(Member member) {
        Channel channel = channelRepository.findByMember(member).orElseThrow(() -> new ChannelHandler(ErrorStatus._MEMBER_NOT_FOUND));
        List<TrendKeyword> trendKeywordsByChannel = trendKeywordRepository.getTrendKeywordsByChannel(channel);
        List<TrendKeyword> realTimeTrendKeywords = trendKeywordRepository.getRealTimeTrendKeywords();
        return TrendKeywordConverter.toChannelKeywordDTO(realTimeTrendKeywords, trendKeywordsByChannel);
    }
}
