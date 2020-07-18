package portfolio2.module.post.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountPredicate;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.notification.NotificationType;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.event.email.EmailSendingProcessForPost;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagPredicate;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;

import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_VIEW_URL;

@Slf4j
@Async
@Transactional
@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final PostRepository postRepository;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final NotificationRepository notificationRepository;
    private final EmailSendingProcessForPost emailSendingProcessForPost;

    @EventListener
    public void handlePostPostedEvent(PostPostedEvent postPostedEvent){
        switch (postPostedEvent.getPostEventType()){
            case NEW:
                Post newPost = postRepository.findPostWithCurrentTagById(postPostedEvent.getNewPost().getId());
                Iterable<Account> accounts = accountRepository.findAll(AccountPredicate.findByTag(newPost.getCurrentTag()));
                accounts.forEach(account -> {
                    if(account != newPost.getAuthor()){
                        Iterable<Tag> allTagInNewPostAndAccount
                                = tagRepository.findAll(TagPredicate.findAllTagByAccountInterestTagAndPostTag(account.getInterestTag(), newPost.getCurrentTag()));
                        if(account.isEmailVerified() && account.isNotificationNewPostWithMyInterestTagByEmail()){
                            emailSendingProcessForPost.sendNotificationEmailForNewPostWithInterestTag(account, newPost, allTagInNewPostAndAccount);
                        }

                        if(account.isNotificationNewPostWithMyInterestTagByWeb()){
                            saveWebNotification(newPost, account, allTagInNewPostAndAccount);
                        }
                    }
                });
                break;
            case UPDATED:
                // accountRepository.findAll(AccountPredicate.findByTag())
        }

    }

    private void saveWebNotification(Post newPost, Account account, Iterable<Tag> allTagInNewPostAndAccount) {
        Notification notification = new Notification();
        notification.setTitle(newPost.getTitle());
        notification.setLink(String.format(POST_VIEW_URL + "/%d", newPost.getId()));
        notification.setChecked(false);
        notification.setAccount(account);
        allTagInNewPostAndAccount.forEach(tag -> {
            notification.getCommonTag().add(tag);
        });
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setNotificationType(NotificationType.POST_WITH_MY_INTEREST_TAG_IS_POSTED);
        notificationRepository.save(notification);
    }
}
