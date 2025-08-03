package channeling.be.domain.channel.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto;
import channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.video.domain.VideoCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import channeling.be.response.exception.handler.ApiResponse;

import org.springframework.web.bind.annotation.*;

@Tag(name = "채널 API", description = "채널 관련 API입니다.")
public interface ChannelSwagger {


        @Operation(summary = "채널의 비디오 목록 조회", description = "채널의 영상 목록을 페이지네이션으로 조회합니다.")
        @GetMapping("/{channel-id}/videos")
        ApiResponse<ChannelResDTO.ChannelVideoList> getChannelVideos(
                @Parameter(description = "요청 채널 아이디 (로그인 성공 시 바디에 포함 되어 있습니다.)", example = "1")
                @PathVariable("channel-id") Long channelId,
                @Parameter(description = "Long,Short 여부")
                @RequestParam(value = "type") VideoCategory type,
                @Parameter(description = "페이지 번호 (1부터 시작)", example = "1")
                @RequestParam(defaultValue = "1") int page,
                @Parameter(description = "페이지당 항목 수", example = "8")
                @RequestParam(defaultValue = "8") int size);


        @Operation(summary = "채널의 컨셉 수정", description = "채널의 컨셉을 수정합니다.")
        @PatchMapping("/{channel-id}/concept")
        ApiResponse<ChannelResponseDto.EditChannelConceptResDto> editChannelConcept(
                @Parameter(description = "요청 채널 아이디 (로그인 성공 시 바디에 포함 되어 있습니다.)", example = "1")
                @PathVariable("channel-id") Long channelId,
                @RequestBody ChannelRequestDto.EditChannelConceptReqDto request,
                @Parameter(hidden = true)
                @LoginMember Member member);


        @Operation(summary = "채널의 타겟 수정", description = "채널의 타겟을 수정합니다.")
        @PatchMapping("/{channel-id}/target")
        ApiResponse<ChannelResponseDto.EditChannelTargetResDto> editChannelTarget(
                @Parameter(description = "요청 채널 아이디 (로그인 성공 시 바디에 포함 되어 있습니다.)", example = "1")
                @PathVariable("channel-id") Long channelId,
                @RequestBody ChannelRequestDto.EditChannelTargetReqDto request,
                @Parameter(hidden = true)
                @LoginMember Member member);



        @Operation(summary = "채널의 대시보드 정보", description = "채널의 대시보드 정보를 조회합니다. 채널 정보 페이지의 상단의 채널 관련 정보를 반환합니다..")
        @GetMapping("{channel-id}")
        ApiResponse<ChannelResDTO.ChannelInfo> getChannel(
                @Parameter(description = "요청 채널 아이디 (로그인 성공 시 바디에 포함 되어 있습니다.)", example = "1")
                @PathVariable("channel-id") Long channelId,
                @Parameter(hidden = true)
                @LoginMember Member loginMember);
}


