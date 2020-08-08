package portfolio2.infra.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile({"local", "test-with-aws-rds-portfolio2_test", "ci-test"})
@Component
public class ConsoleEmailService implements EmailService{

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("Sent console email: {}", emailMessage.getMessage());
    }
}
