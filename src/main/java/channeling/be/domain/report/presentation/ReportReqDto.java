package channeling.be.domain.report.presentation;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ReportReqDto {
    @Schema(name = "CreateReportByUrlRequest", description = "유튜브 영상 URL로 리포트 생성 요청 DTO")
    public record createReportByUrl(
            @Schema(description = "요청 비디오 URL 주소", example = "https://www.youtube.com/shorts/_EWJ5Q0Ujbs")
            @NotNull(message = "null이서는 안됩니다.")
            String url   // 분석 요청 비디오 링크
    ) {}
}
