package channeling.be.global.infrastructure.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FastApiScheduler {
    //환경변수에서 FASTAPI_URL 환경변수 불러오기
    @Value("${FASTAPI_URL:http://localhost:8000}")
    private String baseFastApiUrl;

    private final RestTemplate restTemplate;

    // 매일 오전 2시에 호출
    @Scheduled(cron = "0 0 2 * * ?")
    public void callFastApi() {
        String url = baseFastApiUrl + "/trend-keywords"; // FastAPI 주소
        try {
            String response = restTemplate.postForObject(url, null, String.class);
            System.out.println("FastAPI Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}