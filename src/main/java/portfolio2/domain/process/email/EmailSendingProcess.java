package portfolio2.domain.process.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import portfolio2.config.AppProperties;
import portfolio2.domain.account.Account;
import portfolio2.email.EmailMessage;
import portfolio2.email.EmailService;

import static portfolio2.controller.config.UrlAndViewNameAboutAccount.*;

@RequiredArgsConstructor
@Component
public class EmailSendingProcess {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public void sendEmailVerificationEmail(Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("userId", account.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("emailVerificationLink", CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + account.getEmailWaitingToBeVerified() +
                "&token=" + account.getEmailVerificationToken());

        String message = templateEngine.process("email/email-content/email-for-email-verification", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmailWaitingToBeVerified())
                .subject("TH 이메일 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    public void sendPasswordUpdateNotificationEmail(Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("userId", account.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("passwordUpdateLink", CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + account.getVerifiedEmail() +
                "&token=" + account.getShowPasswordUpdatePageToken());

        String message = templateEngine.process("email/email-content/notification-email-about-password-update", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getVerifiedEmail())
                .subject("TH 비밀번호 변경 알림")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    public void sendNicknameUpdateNotificationEmail(Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("userId", account.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("beforeNickname", account.getNicknameBeforeUpdate());
        context.setVariable("afterNickname", account.getNickname());
        context.setVariable("passwordUpdateLink", CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + account.getVerifiedEmail() +
                "&token=" + account.getShowPasswordUpdatePageToken());

        String message = templateEngine.process("email/email-content/notification-email-about-nickname-update", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getVerifiedEmail())
                .subject("TH 닉네임 변경 알림")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    public void sendFindPasswordEmail(Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("userId", account.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("passwordUpdateLink", CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + account.getVerifiedEmail() +
                "&token=" + account.getShowPasswordUpdatePageToken());

        String message = templateEngine.process("email/email-content/email-for-find-password", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getVerifiedEmail())
                .subject("TH 비밀번호 찾기")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
