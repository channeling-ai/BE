package channeling.be.domain.channel.presentation;

import channeling.be.domain.channel.application.ChannelService;
import channeling.be.response.exception.handler.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static channeling.be.domain.channel.presentation.converter.ChannelConverter.*;
import static channeling.be.domain.channel.presentation.dto.request.ChannelRequestDto.*;
import static channeling.be.domain.channel.presentation.dto.response.ChannelResponseDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    @PatchMapping("editConcept")
    public ApiResponse<EditChannelConceptResDto> editChannelConcept(@RequestBody EditChannelConceptReqDto request) {
        return ApiResponse.onSuccess(toEditChannelConceptResDTo(channelService.editChannelConcept(request)));
    }

}
