package portfolio2.dto.account.profileupdate;

import lombok.Data;


@Data
public class NotificationUpdateRequestDto {

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
