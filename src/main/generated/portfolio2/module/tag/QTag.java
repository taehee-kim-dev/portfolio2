package portfolio2.module.tag;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTag is a Querydsl query type for Tag
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTag extends EntityPathBase<Tag> {

    private static final long serialVersionUID = -1642528728L;

    public static final QTag tag = new QTag("tag");

    public final ListPath<portfolio2.module.account.Account, portfolio2.module.account.QAccount> accounts = this.<portfolio2.module.account.Account, portfolio2.module.account.QAccount>createList("accounts", portfolio2.module.account.Account.class, portfolio2.module.account.QAccount.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<portfolio2.module.notification.Notification, portfolio2.module.notification.QNotification> notifications = this.<portfolio2.module.notification.Notification, portfolio2.module.notification.QNotification>createList("notifications", portfolio2.module.notification.Notification.class, portfolio2.module.notification.QNotification.class, PathInits.DIRECT2);

    public final ListPath<portfolio2.module.post.Post, portfolio2.module.post.QPost> postsOfBeforeTags = this.<portfolio2.module.post.Post, portfolio2.module.post.QPost>createList("postsOfBeforeTags", portfolio2.module.post.Post.class, portfolio2.module.post.QPost.class, PathInits.DIRECT2);

    public final ListPath<portfolio2.module.post.Post, portfolio2.module.post.QPost> postsOfCurrentTags = this.<portfolio2.module.post.Post, portfolio2.module.post.QPost>createList("postsOfCurrentTags", portfolio2.module.post.Post.class, portfolio2.module.post.QPost.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public QTag(String variable) {
        super(Tag.class, forVariable(variable));
    }

    public QTag(Path<? extends Tag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTag(PathMetadata metadata) {
        super(Tag.class, metadata);
    }

}

