package channeling.be.domain.dummy.presentation;

import channeling.be.response.code.BaseErrorCode;
import channeling.be.response.exception.GeneralException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.comment.domain.CommentType;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.application.ReportService;
import channeling.be.domain.report.domain.PageType;
import channeling.be.domain.report.domain.Report;
import channeling.be.domain.report.presentation.ReportConverter;
import channeling.be.domain.report.presentation.ReportResDto;
import channeling.be.domain.video.application.VideoService;
import channeling.be.response.code.status.ErrorStatus;
import channeling.be.response.exception.handler.ApiResponse;
import channeling.be.response.exception.handler.ReportHandler;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/dummies")
public class DummyController implements DummySwagger {

    /**
     * GET /api/reports/{reportId}/{section}
     * reportId: 1,2
     * section: overview, comments 등
     */
    @GetMapping("/{reportId}/{section}")
    public String getDummyReportSection(
            @PathVariable("reportId") String reportId,
            @PathVariable("section") String section) throws IOException {

        // resources/dummies/reports/{reportId}/{section}.json
        String path = String.format("dummies/reports/%s/%s.json", reportId, section);
        ClassPathResource resource = new ClassPathResource(path);
        // 존재하지 않으면 예외 던짐 → 전역 핸들러에서 처리
        if (!resource.exists()) {
            throw new GeneralException(ErrorStatus._DUMMY_NOT_FOUND);
        }
        return FileCopyUtils.copyToString(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
    }


    @GetMapping("/{reportId}/comments")
    public String getDummyReportComments(
            @PathVariable("reportId") String reportId,
            @RequestParam("commentType") CommentType commentType )throws IOException {

        // resources/dummies/reports/{reportId}/{section}.json
        String path = String.format("dummies/reports/%s/comment_%s.json", reportId, commentType.toString().toLowerCase());
        ClassPathResource resource = new ClassPathResource(path);
        // 존재하지 않으면 예외 던짐 → 전역 핸들러에서 처리
        if (!resource.exists()) {
            throw new GeneralException(ErrorStatus._DUMMY_NOT_FOUND);
        }
        return FileCopyUtils.copyToString(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
    }

    @GetMapping("/videos")
    public String getDummyVideos() throws IOException {
        String path = "dummies/videos/list.json";
        ClassPathResource resource = new ClassPathResource(path);
        // 존재하지 않으면 예외 던짐 → 전역 핸들러에서 처리
        if (!resource.exists()) {
            throw new GeneralException(ErrorStatus._DUMMY_NOT_FOUND);
        }
        return FileCopyUtils.copyToString(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
    }
}
