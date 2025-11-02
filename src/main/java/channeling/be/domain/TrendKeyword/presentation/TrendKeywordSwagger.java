package channeling.be.domain.TrendKeyword.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "키워드 API", description = "키워드 관련 API입니다.")
public interface TrendKeywordSwagger {

    @Operation(summary = "키워드 리스트를 조회합니다.", description = "실시간, 채널 트렌드 키워드를 한 번에 반환합니다.")
    @GetMapping("/channel")
    ApiResponse<TrendKeywordResDTO.TrendKeywordListDTO> channelKeywordList(@Parameter(hidden = true) @LoginMember Member member);
}
