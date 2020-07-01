package portfolio2.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import portfolio2.config.AppProperties;
import portfolio2.domain.account.Account;
import portfolio2.mail.EmailMessage;
import portfolio2.mail.EmailService;

import static portfolio2.config.UrlAndViewName.*;

@RequiredArgsConstructor
@Component
public class SendEmail {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public void sendEmailVerificationEmail(Account newAccount) {
        Context context = new Context();
        context.setVariable("nickname", newAccount.getNickname());
        context.setVariable("userId", newAccount.getUserId());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("emailVerificationLink", CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + newAccount.getEmailWaitingToBeVerified() +
                "&token=" + newAccount.getEmailVerificationToken());

        String message = templateEngine.process("email/email-for-email-verification", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmailWaitingToBeVerified())
                .subject("TH 이메일 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
