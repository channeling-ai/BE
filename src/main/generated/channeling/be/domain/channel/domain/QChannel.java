package channeling.be.domain.channel.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChannel is a Querydsl query type for Channel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChannel extends EntityPathBase<Channel> {

    private static final long serialVersionUID = -622847470L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChannel channel = new QChannel("channel");

    public final channeling.be.domain.common.QBaseEntity _super = new channeling.be.domain.common.QBaseEntity(this);

    public final EnumPath<ChannelHashTag> channelHashTag = createEnum("channelHashTag", ChannelHashTag.class);

    public final DateTimePath<java.time.LocalDateTime> channelUpdateAt = createDateTime("channelUpdateAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> comment = createNumber("comment", Long.class);

    public final StringPath concept = createString("concept");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final DateTimePath<java.time.LocalDateTime> joinDate = createDateTime("joinDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final StringPath link = createString("link");

    public final channeling.be.domain.member.domain.QMember member;

    public final StringPath name = createString("name");

    public final NumberPath<Long> share = createNumber("share", Long.class);

    public final NumberPath<Long> subscribe = createNumber("subscribe", Long.class);

    public final StringPath target = createString("target");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> videoCount = createNumber("videoCount", Long.class);

    public final NumberPath<Long> view = createNumber("view", Long.class);

    public QChannel(String variable) {
        this(Channel.class, forVariable(variable), INITS);
    }

    public QChannel(Path<? extends Channel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChannel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChannel(PathMetadata metadata, PathInits inits) {
        this(Channel.class, metadata, inits);
    }

    public QChannel(Class<? extends Channel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new channeling.be.domain.member.domain.QMember(forProperty("member")) : null;
    }

}

