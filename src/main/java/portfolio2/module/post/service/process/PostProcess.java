package portfolio2.module.post.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountPredicate;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.notification.NotificationType;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.post.service.PostType;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagPredicate;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_VIEW_URL;

@RequiredArgsConstructor
@Component
public class PostProcess {

    private final AccountRepository accountRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailSendingProcessForPost emailSendingProcessForPost;

    public Post saveNewPost(Account sessionAccount, PostNewPostRequestDto postRequestDto){
        Account authorAccountInDb = accountRepository.findByUserId(sessionAccount.getUserId());
        Post newPost = new Post();
        newPost.setAuthor(authorAccountInDb);
        newPost.setTitle(postRequestDto.getTitle());
        newPost.setContent(postRequestDto.getContent());
        LocalDateTime currentTime = LocalDateTime.now();
        newPost.setFirstWrittenDateTime(currentTime);
        newPost.setLastModifiedDateTime(currentTime);
        return postRepository.save(newPost);
    }

    public Post addTagToNewPost(Post savedNewPostInDb, PostNewPostRequestDto postNewPostRequestDto){
        // 태그 처리
        if (!postNewPostRequestDto.getTagTitleOnPost().isEmpty()){
            String[] tagArray = postNewPostRequestDto.getTagTitleOnPost().split(",");
            for(String tagTitle : tagArray){
                Tag newTagOnNewPost;
                Tag existingTagInDb = tagRepository.findByTitle(tagTitle);
                if(existingTagInDb == null){
                    // 해당 타이틀로 기존에 데이터베이스에 존재하는 태그가 없으면,
                    newTagOnNewPost = new Tag();
                    newTagOnNewPost.setTitle(tagTitle);
                    newTagOnNewPost = tagRepository.save(newTagOnNewPost);
                }else{
                    newTagOnNewPost = existingTagInDb;
                }
                savedNewPostInDb.getCurrentTag().add(newTagOnNewPost);
            }
        }
        return savedNewPostInDb;
    }

    public void sendNotificationAboutNewPost(Post newPost){
        Iterable<Account> accountForNewPost = accountRepository.findAll(AccountPredicate.findAccountByTagOfNewPost(newPost.getCurrentTag()));
        accountForNewPost.forEach(account -> {
            if(account != newPost.getAuthor()){
                Iterable<Tag> allTagInNewPostAndAccount
                        = tagRepository.findAll(TagPredicate.findAllTagByAccountInterestTagAndTagOfNewPost(account.getInterestTag(), newPost.getCurrentTag()));
                if(account.isNotificationNewPostWithMyInterestTagByWeb()){
                    this.saveWebNotification(PostType.NEW, newPost, account, allTagInNewPostAndAccount);
                }
                if(account.isEmailVerified() && account.isNotificationNewPostWithMyInterestTagByEmail()){
                    emailSendingProcessForPost.sendNotificationEmailForPostWithInterestTag(
                            PostType.NEW, account, newPost, allTagInNewPostAndAccount);
                }
            }
        });
    }

    public Post updateTagOfPost(Post postInDbToUpdate, PostUpdateRequestDto postUpdateRequestDto){
        // 태그 처리
        postInDbToUpdate.getBeforeTag().clear();
        postInDbToUpdate.getBeforeTag().addAll(postInDbToUpdate.getCurrentTag());
        postInDbToUpdate.getCurrentTag().clear();
        if (!postUpdateRequestDto.getTagTitleOnPost().isEmpty()){
            String[] tagArray = postUpdateRequestDto.getTagTitleOnPost().split(",");
            for(String tagTitle : tagArray){
                Tag newTagOnNewPost;
                Tag existingTagInDb = tagRepository.findByTitle(tagTitle);
                if(existingTagInDb == null){
                    // 해당 타이틀로 기존에 데이터베이스에 존재하는 태그가 없으면,
                    newTagOnNewPost = new Tag();
                    newTagOnNewPost.setTitle(tagTitle);
                    newTagOnNewPost = tagRepository.save(newTagOnNewPost);
                }else{
                    newTagOnNewPost = existingTagInDb;
                }
                postInDbToUpdate.getCurrentTag().add(newTagOnNewPost);
            }
        }
        return postInDbToUpdate;
    }

    public void sendNotificationAboutUpdatedPost(Post updatedPost){
        Iterable<Tag> allTagOfOnlyNewTagOfUpdatedPostAndAccount
                = tagRepository.findAll(TagPredicate.findOnlyNewTagOfUpdatedPost(updatedPost.getCurrentTag(), updatedPost.getBeforeTag()));
        List<Tag> onlyNewTag = new ArrayList<>();
        allTagOfOnlyNewTagOfUpdatedPostAndAccount.forEach(onlyNewTag::add);
        Iterable<Account> accountForUpdatedPost = accountRepository.findAll(
                AccountPredicate.findAccountByOnlyNewTagOfUpdatedPost(onlyNewTag)
        );
        accountForUpdatedPost.forEach(account -> {
            if(account != updatedPost.getAuthor()){
                Iterable<Tag> onlyNewTagForAccount
                        = tagRepository.findAll(TagPredicate.findAllTagOfOnlyNewTagOfUpdatedPostAndInterestTagOfAccount(
                        onlyNewTag, account.getInterestTag()
                ));
                if(account.isNotificationMyInterestTagAddedToExistingPostByWeb()){
                    this.saveWebNotification(PostType.UPDATED, updatedPost, account, onlyNewTagForAccount);
                }
                if(account.isEmailVerified() && account.isNotificationMyInterestTagAddedToExistingPostByEmail()){
                    emailSendingProcessForPost.sendNotificationEmailForPostWithInterestTag(
                            PostType.UPDATED, account, updatedPost, onlyNewTagForAccount);
                }
            }
        });
    }

    private void saveWebNotification(PostType postType, Post post, Account account, Iterable<Tag> allTag) {
        Notification notification = new Notification();
        if(postType == PostType.NEW){
            notification.setNotificationType(NotificationType.NEW_POST_WITH_MY_INTEREST_TAG_IS_POSTED);
        }else if(postType == PostType.UPDATED){
            notification.setNotificationType(NotificationType.MY_INTEREST_TAG_IS_ADDED_TO_UPDATED_POST);
        }
        notification.setTitle(post.getTitle());
        notification.setLink(String.format(POST_VIEW_URL + "/%d", post.getId()));
        notification.setAccount(account);
        allTag.forEach(tag -> {
            notification.getCommonTag().add(tag);
        });
        notification.setCreatedDateTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
