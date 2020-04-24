package portfolio2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio2.domain.account.Account;


@NoArgsConstructor
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

    public NotificationUpdateDto(Account account) {
        this.notificationLikeOnMyPostByEmail = account.isNotificationLikeOnMyPostByEmail();
        this.notificationLikeOnMyPostByWeb = account.isNotificationLikeOnMyPostByWeb();
        this.notificationLikeOnMyReplyByEmail = account.isNotificationLikeOnMyReplyByEmail();
        this.notificationLikeOnMyReplyByWeb = account.isNotificationLikeOnMyReplyByWeb();
        this.notificationReplyOnMyPostByEmail = account.isNotificationReplyOnMyPostByEmail();
        this.notificationReplyOnMyPostByWeb = account.isNotificationReplyOnMyPostByWeb();
        this.notificationReplyOnMyReplyByEmail = account.isNotificationReplyOnMyReplyByEmail();
        this.notificationReplyOnMyReplyByWeb = account.isNotificationReplyOnMyReplyByWeb();
        this.notificationNewPostWithMyTagByEmail = account.isNotificationNewPostWithMyTagByEmail();
        this.notificationNewPostWithMyTagByWeb = account.isNotificationNewPostWithMyTagByWeb();
    }
}
