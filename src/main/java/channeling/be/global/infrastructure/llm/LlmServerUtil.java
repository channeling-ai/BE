package channeling.be.global.infrastructure.llm;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.idea.presentation.IdeaReqDto;
import channeling.be.domain.member.domain.Member;
import channeling.be.global.infrastructure.redis.RedisUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LlmServerUtil {

    private final RedisUtil redisUtil;
    private final ObjectMapper om;
    private final RestTemplate restTemplate;

    @Value("${FASTAPI_URL:http://localhost:8000}")
    private String baseFastApiUrl;

    public List<LlmResDto.CreateIdeasResDto> createIdeas(IdeaReqDto.CreateIdeaReqDto dto, Channel channel) {

        // url 설정
        String url = UriComponentsBuilder
                .fromHttpUrl(baseFastApiUrl + "/ideas")
                .toUriString();

        // requestbody 생성
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("channel_id", channel.getId().toString());
        requestBody.put("detail", dto.detail());
        requestBody.put("keyword", dto.keyword());
        if (dto.videoType()!=null) requestBody.put("video_type", dto.videoType().toString());

        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 생성
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            log.error("🚨 FastAPI 아이디어 생성 요청 실패 - HTTP 요청 오류, channelId: {}, URL: {}", 
                    channel.getId(), url, e);
            throw new RuntimeException("FastAPI 요청 실패", e);
        }
        
        String jsonBody = responseEntity.getBody();

        LlmResDto.ApiResDto<List<LlmResDto.CreateIdeasResDto>> apiResponse = null;

        try {
            apiResponse = om.readValue(jsonBody, new TypeReference<>() {});

            if (apiResponse == null || apiResponse.result() == null || !apiResponse.isSuccess()) {
                log.error("🚨 FastAPI 요청 실패 응답 - channelId: {}, Body: {}", channel.getId(), jsonBody);
                throw new RuntimeException("FastAPI 요청이 성공했으나, 아이디어 생성에 실패했습니다.");
            }

        } catch (Exception e) {
            log.error("🚨 FastAPI 응답 JSON 파싱 실패 - channelId: {}, Body: {}", 
                    channel.getId(), jsonBody, e);
            throw new RuntimeException("FastAPI 응답 파싱 실패", e);
        }

        return apiResponse.result();
    }
}
