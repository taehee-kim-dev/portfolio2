package portfolio2.module.post;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -919124528L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final portfolio2.module.account.QAccount author;

    public final ListPath<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag> beforeTag = this.<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag>createList("beforeTag", portfolio2.module.tag.Tag.class, portfolio2.module.tag.QTag.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final ListPath<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag> currentTag = this.<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag>createList("currentTag", portfolio2.module.tag.Tag.class, portfolio2.module.tag.QTag.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> firstWrittenDateTime = createDateTime("firstWrittenDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastModifiedDateTime = createDateTime("lastModifiedDateTime", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new portfolio2.module.account.QAccount(forProperty("author")) : null;
    }

}

