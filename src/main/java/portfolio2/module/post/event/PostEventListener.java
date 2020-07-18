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
import java.util.ArrayList;
import java.util.List;

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
        Post postInDb = postRepository.findPostWithCurrentTagById(postPostedEvent.getPost().getId());
        switch (postPostedEvent.getPostEventType()){
            case NEW:
                Iterable<Account> accountForNewPost = accountRepository.findAll(AccountPredicate.findAccountByTagOfNewPost(postInDb.getCurrentTag()));
                accountForNewPost.forEach(account -> {
                    if(account != postInDb.getAuthor()){
                        Iterable<Tag> allTagInNewPostAndAccount
                                = tagRepository.findAll(TagPredicate.findAllTagByAccountInterestTagAndTagOfNewPost(account.getInterestTag(), postInDb.getCurrentTag()));
                        if(account.isEmailVerified() && account.isNotificationNewPostWithMyInterestTagByEmail()){
                            emailSendingProcessForPost.sendNotificationEmailForPostWithInterestTag(
                                    postPostedEvent.getPostEventType(), account, postInDb, allTagInNewPostAndAccount);
                        }
                        if(account.isNotificationNewPostWithMyInterestTagByWeb()){
                            saveWebNotification(postPostedEvent.getPostEventType(), postInDb, account, allTagInNewPostAndAccount);
                        }
                    }
                });
                break;
            case UPDATED:
                Iterable<Tag> allTagOfOnlyNewTagOfUpdatedPostAndAccount
                        = tagRepository.findAll(TagPredicate.findOnlyNewTagOfUpdatedPost(postInDb.getCurrentTag(), postInDb.getBeforeTag()));
                List<Tag> onlyNewTag = new ArrayList<>();
                allTagOfOnlyNewTagOfUpdatedPostAndAccount.forEach(onlyNewTag::add);
                Iterable<Account> accountForUpdatedPost = accountRepository.findAll(
                        AccountPredicate.findAccountByOnlyNewTagOfUpdatedPost(onlyNewTag)
                );
                accountForUpdatedPost.forEach(account -> {
                    if(account != postInDb.getAuthor()){
                        Iterable<Tag> onlyNewTagForAccount
                                = tagRepository.findAll(TagPredicate.findAllTagOfOnlyNewTagOfUpdatedPostAndInterestTagOfAccount(
                                onlyNewTag, account.getInterestTag()
                        ));
                        if(account.isEmailVerified() && account.isNotificationMyInterestTagAddedToExistingPostByEmail()){
                            emailSendingProcessForPost.sendNotificationEmailForPostWithInterestTag(
                                    postPostedEvent.getPostEventType(), account, postInDb, onlyNewTagForAccount);
                        }
                        if(account.isNotificationMyInterestTagAddedToExistingPostByWeb()){
                            saveWebNotification(postPostedEvent.getPostEventType(), postInDb, account, onlyNewTagForAccount);
                        }
                    }
                });
        }
    }

    private void saveWebNotification(PostEventType postEventType, Post post, Account account, Iterable<Tag> allTag) {
        Notification notification = new Notification();
        if(postEventType == PostEventType.NEW){
            notification.setNotificationType(NotificationType.NEW_POST_WITH_MY_INTEREST_TAG_IS_POSTED);
        }else if(postEventType == PostEventType.UPDATED){
            notification.setNotificationType(NotificationType.MY_INTEREST_TAG_IS_ADDED_TO_UPDATED_POST);
        }
        notification.setTitle(post.getTitle());
        notification.setLink(String.format(POST_VIEW_URL + "/%d", post.getId()));
        notification.setChecked(false);
        notification.setAccount(account);
        allTag.forEach(tag -> {
            notification.getCommonTag().add(tag);
        });
        notification.setCreatedDateTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
