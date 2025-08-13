package channeling.be.domain.video.presentaion;

import channeling.be.domain.member.domain.Member;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "비디오 API", description = "비디오(영상) 관련 API입니다.")
public interface VideoSwagger {

    @Operation(summary = "영상 정보 조회", description = "요청한 영상의 정보를 조회합니다.\n" +
            "하단 ReportVideoInfo 을 참고해주세요. (ctrl + f)")
    @GetMapping("/{video-id}")
    ApiResponse<VideoResDTO.ReportVideoInfo> getVideoInfo (
            @Parameter(description = "요청 비디오 아이디", example = "1")
            @PathVariable("video-id") Long videoId,
            @Parameter(hidden = true) Member loginMember);
}
