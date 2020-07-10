package portfolio2.dto.request.account.setting;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
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
