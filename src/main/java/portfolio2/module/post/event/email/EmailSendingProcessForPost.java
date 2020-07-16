package portfolio2.module.post.event.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import portfolio2.infra.config.AppProperties;
import portfolio2.infra.email.EmailMessage;
import portfolio2.infra.email.EmailService;
import portfolio2.module.account.Account;
import portfolio2.module.post.Post;
import portfolio2.module.tag.Tag;

import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.CHECK_EMAIL_VERIFICATION_LINK_URL;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_VIEW_URL;

@RequiredArgsConstructor
@Component
public class EmailSendingProcessForPost {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public void sendNotificationEmailForNewPostWithInterestTag(Account account, Post newPost, Iterable<Tag> allTagInNewPostAndAccount) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("userId", account.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("allTagInNewPostAndAccount", allTagInNewPostAndAccount);
        context.setVariable("post", newPost);
        context.setVariable("newPostLink", String.format(POST_VIEW_URL + "/%d", newPost.getId()));

        String message = templateEngine.process(
                "email/email-content/notification-email-for-new-post-with-interest-tag", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getVerifiedEmail())
                .subject("TH 관심 태그 게시물 알림")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
