package portfolio2.dto.account.profile.update;

import lombok.Data;


@Data
public class NotificationUpdateRequestDto {

    private boolean notificationLikeOnMyPostByWeb;

    private boolean notificationLikeOnMyReplyByWeb;

    private boolean notificationReplyOnMyPostByWeb;

    private boolean notificationReplyOnMyReplyByWeb;

    private boolean notificationNewPostWithMyTagByWeb;


    private boolean notificationLikeOnMyPostByEmail;

    private boolean notificationLikeOnMyReplyByEmail;

    private boolean notificationReplyOnMyPostByEmail;

    private boolean notificationReplyOnMyReplyByEmail;

    private boolean notificationNewPostWithMyTagByEmail;
}
