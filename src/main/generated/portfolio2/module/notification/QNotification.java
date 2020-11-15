package portfolio2.module.notification;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = -1019378256L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotification notification = new QNotification("notification");

    public final portfolio2.module.account.QAccount account;

    public final ListPath<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag> commonTag = this.<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag>createList("commonTag", portfolio2.module.tag.Tag.class, portfolio2.module.tag.QTag.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdDateTime = createDateTime("createdDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath link = createString("link");

    public final BooleanPath linkVisited = createBoolean("linkVisited");

    public final EnumPath<NotificationType> notificationType = createEnum("notificationType", NotificationType.class);

    public final BooleanPath ringBellChecked = createBoolean("ringBellChecked");

    public final StringPath title = createString("title");

    public QNotification(String variable) {
        this(Notification.class, forVariable(variable), INITS);
    }

    public QNotification(Path<? extends Notification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotification(PathMetadata metadata, PathInits inits) {
        this(Notification.class, metadata, inits);
    }

    public QNotification(Class<? extends Notification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new portfolio2.module.account.QAccount(forProperty("account")) : null;
    }

}

