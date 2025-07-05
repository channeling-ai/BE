package channeling.be.domain.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1377275486L;

    public static final QMember member = new QMember("member1");

    public final channeling.be.domain.common.QBaseEntity _super = new channeling.be.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath facebookLink = createString("facebookLink");

    public final StringPath googleEmail = createString("googleEmail");

    public final StringPath googleId = createString("googleId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath instagramLink = createString("instagramLink");

    public final StringPath nickname = createString("nickname");

    public final StringPath profileImage = createString("profileImage");

    public final StringPath tiktokLink = createString("tiktokLink");

    public final StringPath twitterLink = createString("twitterLink");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

