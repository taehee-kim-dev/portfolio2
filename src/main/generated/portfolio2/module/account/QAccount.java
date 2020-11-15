package portfolio2.module.account;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccount is a Querydsl query type for Account
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAccount extends EntityPathBase<Account> {

    private static final long serialVersionUID = -629306802L;

    public static final QAccount account = new QAccount("account");

    public final StringPath bio = createString("bio");

    public final NumberPath<Integer> countOfSendingEmailVerificationEmail = createNumber("countOfSendingEmailVerificationEmail", Integer.class);

    public final BooleanPath emailFirstVerified = createBoolean("emailFirstVerified");

    public final StringPath emailVerificationToken = createString("emailVerificationToken");

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final StringPath emailWaitingToBeVerified = createString("emailWaitingToBeVerified");

    public final DateTimePath<java.time.LocalDateTime> firstCountOfSendingEmailVerificationEmailSetDateTime = createDateTime("firstCountOfSendingEmailVerificationEmailSetDateTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag> interestTag = this.<portfolio2.module.tag.Tag, portfolio2.module.tag.QTag>createList("interestTag", portfolio2.module.tag.Tag.class, portfolio2.module.tag.QTag.class, PathInits.DIRECT2);

    public final StringPath location = createString("location");

    public final StringPath nickname = createString("nickname");

    public final StringPath nicknameBeforeUpdate = createString("nicknameBeforeUpdate");

    public final BooleanPath notificationCommentOnMyCommentByEmail = createBoolean("notificationCommentOnMyCommentByEmail");

    public final BooleanPath notificationCommentOnMyCommentByWeb = createBoolean("notificationCommentOnMyCommentByWeb");

    public final BooleanPath notificationCommentOnMyPostByEmail = createBoolean("notificationCommentOnMyPostByEmail");

    public final BooleanPath notificationCommentOnMyPostByWeb = createBoolean("notificationCommentOnMyPostByWeb");

    public final BooleanPath notificationLikeOnMyCommentByEmail = createBoolean("notificationLikeOnMyCommentByEmail");

    public final BooleanPath notificationLikeOnMyCommentByWeb = createBoolean("notificationLikeOnMyCommentByWeb");

    public final BooleanPath notificationLikeOnMyPostByEmail = createBoolean("notificationLikeOnMyPostByEmail");

    public final BooleanPath notificationLikeOnMyPostByWeb = createBoolean("notificationLikeOnMyPostByWeb");

    public final BooleanPath notificationMyInterestTagAddedToExistingPostByEmail = createBoolean("notificationMyInterestTagAddedToExistingPostByEmail");

    public final BooleanPath notificationMyInterestTagAddedToExistingPostByWeb = createBoolean("notificationMyInterestTagAddedToExistingPostByWeb");

    public final BooleanPath notificationNewPostWithMyInterestTagByEmail = createBoolean("notificationNewPostWithMyInterestTagByEmail");

    public final BooleanPath notificationNewPostWithMyInterestTagByWeb = createBoolean("notificationNewPostWithMyInterestTagByWeb");

    public final StringPath occupation = createString("occupation");

    public final StringPath password = createString("password");

    public final StringPath profileImage = createString("profileImage");

    public final StringPath showPasswordUpdatePageToken = createString("showPasswordUpdatePageToken");

    public final DateTimePath<java.time.LocalDateTime> signUpDateTime = createDateTime("signUpDateTime", java.time.LocalDateTime.class);

    public final StringPath userId = createString("userId");

    public final StringPath verifiedEmail = createString("verifiedEmail");

    public QAccount(String variable) {
        super(Account.class, forVariable(variable));
    }

    public QAccount(Path<? extends Account> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccount(PathMetadata metadata) {
        super(Account.class, metadata);
    }

}

