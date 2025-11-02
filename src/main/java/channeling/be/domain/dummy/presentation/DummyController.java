package channeling.be.domain.dummy.presentation;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.global.infrastructure.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

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
            @PathVariable("section") ReportSection section) throws IOException {

        // resources/dummies/reports/{reportId}/{section}.json
        String path = String.format("dummies/reports/%s/%s.json", reportId, section.toString().toLowerCase());
        System.out.println(path);
        // S3에서 바로 읽어서 반환
        return s3Service.getFileContent(path);

    }


    @GetMapping("/{reportId}/comments")
    public String getDummyReportComments(
            @PathVariable("reportId") String reportId,
            @RequestParam("commentType") CommentType commentType) throws IOException {

        // resources/dummies/reports/{reportId}/{section}.json
        String path = String.format("dummies/reports/%s/comment_%s.json", reportId, commentType.toString().toLowerCase());
        System.out.println(path);
        return s3Service.getFileContent(path);



    }

    @GetMapping("/videos")
    public String getDummyVideos() throws IOException {
        String path = "dummies/videos/list.json";
        return s3Service.getFileContent(path);

    }
}
