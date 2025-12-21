package channeling.be.domain.idea.domain;

import channeling.be.domain.channel.domain.Channel;
import channeling.be.domain.common.BaseEntity;
import channeling.be.domain.idea.domain.event.IdeaEntityHandler;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Idea extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false, length = 100)
    private String title; // 제목

    @Column(nullable = false, length = 255)
    private String content; // 내용

    @Column(nullable = false, length = 255)
    private String hashTag; // 해시태그 Json 리스트

    @Column(nullable = false)
    private Boolean isBookMarked; // 북마크 여부

    public Boolean switchBookMarked() {
        this.isBookMarked = !this.isBookMarked;
        return this.isBookMarked;
    }


}
