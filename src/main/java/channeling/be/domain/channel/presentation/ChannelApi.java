package channeling.be.domain.channel.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;
import channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.presentaion.VideoResDTO;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Tag(name = "채널 API", description = "채널 관련 API입니다.")
public interface ChannelApi {

    @Operation(summary = "타입별 영상 조회 API", description = "내 채널의 타입(LONG, SHORT)별 영상을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공입니다.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChannelResDTO.ChannelVideoList.class))}
            )
    })
    ApiResponse<ChannelResDTO.ChannelVideoList> getChannelVideos(
            @PathVariable("channel-id") Long channelId,
            @RequestParam(value = "type") VideoCategory type,
            @RequestParam(value = "cursor", required = false) LocalDateTime cursor,
            @RequestParam(value = "size", defaultValue = "8") int size);



    @Operation(summary = "채널 컨셉 수정 API", description = "내 채널의 컨셉을 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공입니다.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChannelResponseDto.EditChannelConceptResDto.class))}
            )
    })
     ApiResponse<ChannelResponseDto.EditChannelConceptResDto> editChannelConcept(@PathVariable("channel-id") Long channelId, @RequestBody ChannelRequestDto.EditChannelConceptReqDto request);



    @Operation(summary = "채널 타겟 수정 API", description = "내 채널의 타겟을 수정합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공입니다.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChannelResponseDto.EditChannelTargetResDto.class))}
            )
    })
     ApiResponse<ChannelResponseDto.EditChannelTargetResDto> editChannelTarget(@PathVariable("channel-id") Long channelId, @RequestBody ChannelRequestDto.EditChannelTargetReqDto request);



    @Operation(summary = "채널 정보 조회 API", description = "내 채널 정보(대시보드, 타겟, 컨셉)을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공입니다.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChannelResDTO.ChannelInfo.class))}
            )
    })
     ApiResponse<ChannelResDTO.ChannelInfo> getChannel(@PathVariable("channel-id") Long channelId, @Parameter(hidden = true) @LoginMember Member loginMember);



    @Operation(summary = "채널 추천 영상 조회 API", description = "(홈화면) 자신의 채널의 추천 영상을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공입니다.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChannelResDTO.PageDto.class))}
            )
    })
    ApiResponse<ChannelResDTO.PageDto> getRecommendedChannelVideos(
            @PathVariable("channel-id") Long channelId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(hidden = true) @LoginMember Member loginMember);
}
