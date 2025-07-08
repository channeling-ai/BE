package channeling.be.domain.channel.presentation;

import channeling.be.domain.channel.application.ChannelService;
import channeling.be.domain.video.application.VideoService;
import channeling.be.domain.video.domain.VideoCategory;
import channeling.be.domain.video.presentaion.VideoResDTO;
import channeling.be.response.exception.handler.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
@Tag(name = "채널 API", description = "채널 관련 API입니다.")
public class ChannelController {
	private final VideoService videoService;
	private final ChannelService channelService;


	// // TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
	// @GetMapping("/{channel-id}/videos")
	// @Operation(summary = "채널의 비디오 리스트 조회 API (page)", description = "특정 채널의 비디오 리스트를 페이지를 통해 조회합니다.")
	// public ChannelResDTO.ChannelVideoList getChannelVideos(
	// 	@PathVariable("channel-id") Long channelId,
	// 	@RequestParam(value = "type") VideoCategory type,
	// 	@RequestParam(value = "page", defaultValue = "1") int page,
	// 	@RequestParam(value = "size", defaultValue = "8") int size) {
	// 	channelService.validateChannelByIdAndMember(channelId);
	// 	Slice<VideoResDTO.VideoBrief> videoBriefSlice = videoService.getChannelVideoListByType(channelId,type,page, size);
	// 	return ChannelConverter.toChannelVideoList(channelId, videoBriefSlice);
	// }

	// TODO: 추후 유저 + 채널 연관 관계 확인 로직 필요
	@GetMapping("/{channel-id}/videos")
	@Operation(summary = "채널의 비디오 리스트 조회 API (cursor)", description = "특정 채널의 비디오 리스트를 커서 기반으로 조회합니다.")
	public ChannelResDTO.ChannelVideoList getChannelVideosByCursor(
		@PathVariable("channel-id") Long channelId,
		@RequestParam(value = "type") VideoCategory type,
		@RequestParam(value = "cursor",required = false) LocalDateTime cursor,
		@RequestParam(value = "size", defaultValue = "8") int size) {
		channelService.validateChannelByIdAndMember(channelId);
		Slice<VideoResDTO.VideoBrief> videoBriefSlice = videoService.getChannelVideoListByTypeAfterCursor(channelId,type,cursor, size);
		return ChannelConverter.toChannelVideoList(channelId, videoBriefSlice);
	}

    @PatchMapping("/editConcept")
    public ApiResponse<EditChannelConceptResDto> editChannelConcept(@RequestBody EditChannelConceptReqDto request) {
        return ApiResponse.onSuccess(toEditChannelConceptResDTo(channelService.editChannelConcept(request)));
    }

}
