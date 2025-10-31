package channeling.be.domain.dummy.presentation;

import channeling.be.domain.comment.domain.CommentType;
import channeling.be.global.infrastructure.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/dummies")
@RequiredArgsConstructor
public class DummyController implements DummySwagger {
    private final S3Service s3Service;

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
        String url = s3Service.getUrl(path);
        try (InputStream inputStream = new URL(url).openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

    }


    @GetMapping("/{reportId}/comments")
    public String getDummyReportComments(
            @PathVariable("reportId") String reportId,
            @RequestParam("commentType") CommentType commentType) throws IOException {

        // resources/dummies/reports/{reportId}/{section}.json
        String path = String.format("dummies/reports/%s/comment_%s.json", reportId, commentType.toString().toLowerCase());
        String url = s3Service.getUrl(path);
        try (InputStream inputStream = new URL(url).openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }


    }

    @GetMapping("/videos")
    public String getDummyVideos() throws IOException {
        String path = "dummies/videos/list.json";
        String url = s3Service.getUrl(path);
        try (InputStream inputStream = new URL(url).openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
