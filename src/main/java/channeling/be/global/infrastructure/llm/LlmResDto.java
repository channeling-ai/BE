package channeling.be.global.infrastructure.llm;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LlmResDto {
    public record ApiResDto<T>(
            String message,
            String code,
            int status,
            boolean isSuccess,
            T result
    ) {
    }

    public record CreateIdeasResDto(
            int id,
            String title,
            String content,
            @JsonProperty("channel_id")
            int channelId,
            @JsonProperty("hash_tag")
            String hashTag,
            @JsonProperty("is_book_marked")
            String isBookMarked,

            @JsonProperty("created_at")
            String createdAt,

            @JsonProperty("updated_at")
            String updatedAt
    ) {
    }
}
