package channeling.be.domain.channel.presentation;

import channeling.be.domain.auth.annotation.LoginMember;
import channeling.be.domain.channel.application.ChannelService;
import channeling.be.domain.member.domain.Member;
import channeling.be.domain.report.application.ReportService;
import channeling.be.domain.report.presentation.dto.ReportResDTO;
import channeling.be.domain.video.application.VideoService;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.presentaion.VideoResDTO;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static channeling.be.domain.channel.presentation.converter.ChannelConverter.*;
import static channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto.*;
import static channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
@Tag(name = "채널 API", description = "채널 관련 API입니다.")
public class ChannelController implements ChannelSwagger{
	private final VideoService videoService;
	private final ChannelService channelService;
	private final ReportService reportService;

	// TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
	@GetMapping("/{channel-id}/videos")
	@Override
	public ApiResponse<ChannelResDTO.ChannelVideoList> getChannelVideos(
		@LoginMember Member member,
		@PathVariable("channel-id") Long channelId,
		@RequestParam(value = "type") VideoCategory type,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "8") int size) {
		channelService.validateChannelByIdAndMember(channelId,member);
		Page<VideoResDTO.VideoBrief> videoBriefPage = videoService.getChannelVideoListByType(channelId,type,page, size);
		return ApiResponse.onSuccess(ChannelConverter.toChannelVideoList(channelId, videoBriefPage));
	}

	// // TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
  // @GetMapping("/{channel-id}/videos")
  // @Operation(summary = "채널의 비디오 리스트 조회 API", description = "특정 채널의 비디오 리스트를 조회합니다.")
  // public ApiResponse<ChannelResDTO.ChannelVideoList> getChannelVideos(
  //   @PathVariable("channel-id") Long channelId,
  //   @RequestParam(value = "type") VideoCategory type,
  //   @RequestParam(value = "cursor",required = false) LocalDateTime cursor,
  //   @RequestParam(value = "size", defaultValue = "8") int size) {
  //   channelService.validateChannelByIdAndMember(channelId);
  //   Slice<VideoResDTO.VideoBrief> videoBriefSlice = videoService.getChannelVideoListByTypeAfterCursor(channelId,type,cursor, size);
  //   return ApiResponse.onSuccess(ChannelConverter.toChannelVideoList(channelId, videoBriefSlice));
  // }

	@GetMapping("/{channel-id}/reports")
	@Operation(summary = "채널의 레포트 조회 API (page)", description = "특정 채널의 레포트 리스트를 페이지를 통해 조회합니다.")
	public ApiResponse<ChannelResDTO.ChannelReportList> getReports(@LoginMember Member member,
		@PathVariable("channel-id") Long channelId,
		@RequestParam(value = "type") VideoCategory type,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@RequestParam(value = "size", defaultValue = "8") int size){
		channelService.validateChannelByIdAndMember(channelId,member);
		Page<ReportResDTO.ReportBrief> reportBriefPage=reportService.getChannelReportListByType(channelId,type,page,size);
		return ApiResponse.onSuccess(ChannelConverter.toChannelReportList(channelId,reportBriefPage));


	}
	@Override
	@PatchMapping("/{channel-id}/concept")
	public ApiResponse<EditChannelConceptResDto> editChannelConcept(@PathVariable("channel-id") Long channelId,
																	@RequestBody EditChannelConceptReqDto request,
																	@LoginMember Member member) {
		return ApiResponse.onSuccess(toEditChannelConceptResDto(channelService.editChannelConcept(channelId, request, member)));
	}

	@Override
	@PatchMapping("/{channel-id}/target")
	public ApiResponse<EditChannelTargetResDto> editChannelTarget(@PathVariable("channel-id") Long channelId,
                                                                  @RequestBody EditChannelTargetReqDto request,
                                                                  @LoginMember Member member) {
		return ApiResponse.onSuccess(toEditChannelTargetResDto((channelService.editChannelTarget(channelId, request, member))));
	}

	@Override
	@GetMapping("{channel-id}")
	public ApiResponse<ChannelResDTO.ChannelInfo> getChannel(@PathVariable("channel-id") Long channelId,
                                                             @LoginMember Member loginMember) {
		 return ApiResponse.onSuccess(ChannelConverter.toChannelResDto(channelService.getChannel(channelId, loginMember)));
	}
}
