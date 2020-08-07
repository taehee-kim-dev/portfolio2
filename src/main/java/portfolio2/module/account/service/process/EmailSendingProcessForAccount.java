package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import portfolio2.infra.config.AppProperties;
import portfolio2.infra.email.EmailMessage;
import portfolio2.infra.email.EmailService;
import portfolio2.module.account.Account;

import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.CHECK_EMAIL_VERIFICATION_LINK_URL;
import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL;

@RequiredArgsConstructor
@Component
public class EmailSendingProcessForAccount {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public void sendEmailVerificationEmail(Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("userId", account.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("emailVerificationLink", CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?userId=" + account.getUserId() +
                "&email=" + account.getEmailWaitingToBeVerified() +
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
