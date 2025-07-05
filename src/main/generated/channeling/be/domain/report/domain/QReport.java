package channeling.be.domain.report.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = 1866436114L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReport report = new QReport("report");

    public final channeling.be.domain.common.QBaseEntity _super = new channeling.be.domain.common.QBaseEntity(this);

    public final NumberPath<Long> adviceComment = createNumber("adviceComment", Long.class);

    public final NumberPath<Long> comment = createNumber("comment", Long.class);

    public final NumberPath<Long> commentChannelAvg = createNumber("commentChannelAvg", Long.class);

    public final NumberPath<Long> commentTopicAvg = createNumber("commentTopicAvg", Long.class);

    public final NumberPath<Integer> concept = createNumber("concept", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath leaveAnalyze = createString("leaveAnalyze");

    public final NumberPath<Long> likeChannelAvg = createNumber("likeChannelAvg", Long.class);

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final NumberPath<Long> likeTopicAvg = createNumber("likeTopicAvg", Long.class);

    public final NumberPath<Long> negativeComment = createNumber("negativeComment", Long.class);

    public final NumberPath<Long> neutralComment = createNumber("neutralComment", Long.class);

    public final StringPath optimization = createString("optimization");

    public final NumberPath<Long> positiveComment = createNumber("positiveComment", Long.class);

    public final NumberPath<Long> revisit = createNumber("revisit", Long.class);

    public final NumberPath<Long> seo = createNumber("seo", Long.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final channeling.be.domain.video.domain.QVideo video;

    public final NumberPath<Long> view = createNumber("view", Long.class);

    public final NumberPath<Long> viewChannelAvg = createNumber("viewChannelAvg", Long.class);

    public final NumberPath<Long> viewTopicAvg = createNumber("viewTopicAvg", Long.class);

    public QReport(String variable) {
        this(Report.class, forVariable(variable), INITS);
    }

    public QReport(Path<? extends Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReport(PathMetadata metadata, PathInits inits) {
        this(Report.class, metadata, inits);
    }

    public QReport(Class<? extends Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.video = inits.isInitialized("video") ? new channeling.be.domain.video.domain.QVideo(forProperty("video"), inits.get("video")) : null;
    }

}

