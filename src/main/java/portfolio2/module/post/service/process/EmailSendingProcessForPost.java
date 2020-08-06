package portfolio2.module.post.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import portfolio2.infra.config.AppProperties;
import portfolio2.infra.email.EmailMessage;
import portfolio2.infra.email.EmailService;
import portfolio2.module.account.Account;
import portfolio2.module.post.Post;
import portfolio2.module.post.service.PostType;
import portfolio2.module.tag.Tag;

import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_VIEW_URL;

@RequiredArgsConstructor
@Component
public class EmailSendingProcessForPost {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public void sendNotificationEmailForPostWithInterestTag(PostType postType, Account account, Post newPost, Iterable<Tag> allTagInNewPostAndAccount) {
        Context context = new Context();
        String content = null;
        if(postType == PostType.NEW){
            content = "의 태그가 달린 새로운 게시물이 게시되었습니다!";
        }else if(postType == PostType.UPDATED){
            content = "의 태그가 기존 게시물에 추가되었습니다!";
        }
        context.setVariable("nickname", account.getNickname());
        context.setVariable("userId", account.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("allTagInNewPostAndAccount", allTagInNewPostAndAccount);
        context.setVariable("content", content);
        context.setVariable("post", newPost);
        context.setVariable("newPostLink", String.format(POST_VIEW_URL + "/%d", newPost.getId()));

        String message = templateEngine.process(
                "email/email-content/notification-email-for-post-with-interest-tag", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getVerifiedEmail())
                .subject("TH 관심 태그 게시물 알림")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
