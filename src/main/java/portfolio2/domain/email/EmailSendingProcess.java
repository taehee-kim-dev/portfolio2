package portfolio2.domain.email;

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

        String message = templateEngine.process("email/email-for-email-verification", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmailWaitingToBeVerified())
                .subject("TH 이메일 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
