package channeling.be.domain.TrendKeyword.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrendKeyword is a Querydsl query type for TrendKeyword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrendKeyword extends EntityPathBase<TrendKeyword> {

    private static final long serialVersionUID = -804430366L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrendKeyword trendKeyword = new QTrendKeyword("trendKeyword");

    public final channeling.be.domain.common.QBaseEntity _super = new channeling.be.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath keyword = createString("keyword");

    public final EnumPath<TrendKeywordType> keywordType = createEnum("keywordType", TrendKeywordType.class);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final channeling.be.domain.video.domain.QVideo video;

    public QTrendKeyword(String variable) {
        this(TrendKeyword.class, forVariable(variable), INITS);
    }

    public QTrendKeyword(Path<? extends TrendKeyword> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrendKeyword(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrendKeyword(PathMetadata metadata, PathInits inits) {
        this(TrendKeyword.class, metadata, inits);
    }

    public QTrendKeyword(Class<? extends TrendKeyword> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.video = inits.isInitialized("video") ? new channeling.be.domain.video.domain.QVideo(forProperty("video"), inits.get("video")) : null;
    }

}

