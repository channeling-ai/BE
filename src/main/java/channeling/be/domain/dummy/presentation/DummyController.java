package channeling.be.domain.dummy.presentation;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.global.infrastructure.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;

@RestController
@RequestMapping("/dummies")
@RequiredArgsConstructor
public class DummyController implements DummySwagger {
    private final S3Service s3Service;
    private final S3Client s3Client;
    /**
     * GET /api/reports/{reportId}/{section}
     * reportId: 1,2
     * section: overview, comments 등
     */
    @GetMapping("/{reportId}/{section}")
    public String getDummyReportSection(
            @PathVariable("reportId") String reportId,
            @PathVariable("section") ReportSection section) {

        checkPattern(reportId);

        String path = String.format("dummies/reports/%s/%s.json", reportId, section.toString().toLowerCase());
        System.out.println(path);
        // S3에서 바로 읽어서 반환
        return s3Service.getFileContent(path);

    }


    @GetMapping("/{reportId}/comments")
    public String getDummyReportComments(
            @PathVariable("reportId") String reportId,
            @RequestParam("commentType") CommentType commentType) {

        checkPattern(reportId);

        String path = String.format("dummies/reports/%s/comment_%s.json", reportId, commentType.toString().toLowerCase());
        System.out.println(path);
        return s3Service.getFileContent(path);

    }

    @GetMapping("/videos")
    public String getDummyVideos() {
        String path = "dummies/videos/list.json";
        return s3Service.getFileContent(path);

    }

    @GetMapping("/videos/{videoId}")
    public String getDummyVideo(
            @PathVariable("videoId") String videoId) {
        checkPattern(videoId);
        String path = String.format("dummies/videos/video_%s.json", videoId);
        return s3Service.getFileContent(path);
    }

    private void checkPattern(String urlString) {
        if (!urlString.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("요청 값에 허용되지 않은 문자가 포함되어 있습니다.");
        }
    }
}
