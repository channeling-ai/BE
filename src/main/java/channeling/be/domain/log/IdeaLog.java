package channeling.be.domain.log;

import channeling.be.domain.channel.domain.Channel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "idea_log")
public class IdeaLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ideaId;

    private Long channelId;

    @Column(nullable = false, length = 100)
    private String title; // 제목

    @Column(nullable = false, length = 255)
    private String content; // 내용

    @Column(nullable = false, length = 255)
    private String hashTag; // 해시태그 Json 리스트

    @Column(nullable = false)
    private Boolean isBookMarked; // 북마크 여부
}
