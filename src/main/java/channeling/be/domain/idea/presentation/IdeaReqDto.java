package channeling.be.domain.idea.presentation;

import channeling.be.domain.video.domain.VideoType;
import io.swagger.v3.oas.annotations.media.Schema;

public class IdeaReqDto {

    @Schema(name = "CreateIdeaReqDto", description = "아이디어 생성 요청 DTO")
    public record CreateIdeaReqDto(
            @Schema(description = "아이디어 생성 키워드", example = "여행, 브이로그")
            String keyword,
            @Schema(description = "아이디어 생성 영상 형", example = "LONG/SHORTS")
            VideoType videoType,
            @Schema(description = "추가 입력 사항", example = "여행 관련 도파민이 올라가는 영상 아이디어를 5개 생성해줘")
            String detail
    ) {}
}
