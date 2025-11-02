package channeling.be.domain.TrendKeyword.presentation;

import channeling.be.domain.TrendKeyword.service.TrendKeywordService;
import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/trendKeywords")
public class TrendKeywordController implements TrendKeywordSwagger{

    private final TrendKeywordService trendKeywordService;

    @GetMapping("/channel")
    public ApiResponse<TrendKeywordResDTO.TrendKeywordListDTO> channelKeywordList(@LoginMember Member member) {
        return ApiResponse.onSuccess(trendKeywordService.getTrendKeywordList(member));
    }

}
