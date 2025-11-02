package channeling.be.domain.dummy.presentation;

import channeling.be.domain.comment.domain.CommentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "더미레포트 관련 API", description = "더미레포트 관련 API입니다. id에 1 혹은 2를 넣어주세요")
public interface DummySwagger {

    @Operation(
            summary = "리포트 섹션 조회",
            description = "reportId와 section에 따라 더미 JSON 파일을 그대로 반환합니다."
    )
    @GetMapping("/{reportId}/{section}")
    String getDummyReportSection(
            @Parameter(description = "요청 리포트 아이디", example = "1")
            @PathVariable String reportId,
            @Parameter(description = "요청 섹션 이름")
            @PathVariable ReportSection section
    ) throws IOException;


    @Operation(
            summary = "리포트 댓글 타입별 조회",
            description = "reportId와 commentType 에 따라 더미 JSON 파일을 그대로 반환합니다."
    )
    @GetMapping("/{reportId}/comments")
    String getDummyReportComments(
            @Parameter(description = "요청 리포트 아이디", example = "1")
            @PathVariable("reportId") String reportId,
            @Parameter(description = "댓글 타입 (NEUTRAL, POSITIVE, NEGATIVE, ADVICE_OPINION,)", example = "NEUTRAL")
            @RequestParam("commentType") CommentType commentType )throws IOException;


    @Operation(summary = "더미 비디오 리스트 조회", description = "더미 JSON 파일에서 비디오 리스트를 반환합니다.")
    @GetMapping("/videos")
    String getDummyVideos() throws IOException;


    @Operation(summary = "더미 비디오 단일 정보 조회", description = "더미 JSON 파일에서 비디오 정보를 반환합니다.")
    @GetMapping("/videos/{videoId}")
    String getDummyVideo(
            @Parameter(description = "요청 영상 아이디", example = "1")
            @PathVariable("videoId") String videoId);
}
