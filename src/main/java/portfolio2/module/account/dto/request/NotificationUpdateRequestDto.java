package portfolio2.module.account.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class NotificationUpdateRequestDto {

    private boolean notificationLikeOnMyPostByWeb;

    private boolean notificationLikeOnMyCommentByWeb;


    private boolean notificationCommentOnMyPostByWeb;

    private boolean notificationCommentOnMyCommentByWeb;


    private boolean notificationNewPostWithMyInterestTagByWeb;


    private boolean notificationLikeOnMyPostByEmail;

    private boolean notificationLikeOnMyCommentByEmail;


    private boolean notificationCommentOnMyPostByEmail;

    private boolean notificationCommentOnMyCommentByEmail;


    private boolean notificationNewPostWithMyInterestTagByEmail;
}
