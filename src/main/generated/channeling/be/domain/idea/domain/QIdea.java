package channeling.be.domain.idea.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QIdea is a Querydsl query type for Idea
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QIdea extends EntityPathBase<Idea> {

    private static final long serialVersionUID = 1285087256L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QIdea idea = new QIdea("idea");

    public final channeling.be.domain.common.QBaseEntity _super = new channeling.be.domain.common.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath hashTag = createString("hashTag");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isBookMarked = createBoolean("isBookMarked");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final channeling.be.domain.video.domain.QVideo video;

    public QIdea(String variable) {
        this(Idea.class, forVariable(variable), INITS);
    }

    public QIdea(Path<? extends Idea> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QIdea(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QIdea(PathMetadata metadata, PathInits inits) {
        this(Idea.class, metadata, inits);
    }

    public QIdea(Class<? extends Idea> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.video = inits.isInitialized("video") ? new channeling.be.domain.video.domain.QVideo(forProperty("video"), inits.get("video")) : null;
    }

}

