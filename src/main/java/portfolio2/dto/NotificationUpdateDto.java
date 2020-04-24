package portfolio2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio2.domain.account.Account;


@Data
public class NotificationUpdateDto {

    private boolean notificationLikeOnMyPostByEmail;

    private boolean notificationLikeOnMyPostByWeb;

    private boolean notificationLikeOnMyReplyByEmail;

    private boolean notificationLikeOnMyReplyByWeb;

    private boolean notificationReplyOnMyPostByEmail;

    private boolean notificationReplyOnMyPostByWeb;

    private boolean notificationReplyOnMyReplyByEmail;

    private boolean notificationReplyOnMyReplyByWeb;

    private boolean notificationNewPostWithMyTagByEmail;

    private boolean notificationNewPostWithMyTagByWeb;
}
