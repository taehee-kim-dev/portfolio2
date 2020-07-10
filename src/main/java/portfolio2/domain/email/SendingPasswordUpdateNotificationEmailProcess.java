package portfolio2.domain.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

@RequiredArgsConstructor
@Component
public class SendingPasswordUpdateNotificationEmailProcess {

    private final AccountRepository accountRepository;
    private final EmailSendingProcess emailSendingProcess;

    public Account sendPasswordUpdateNotificationEmail(Account updatedAccount){
        updatedAccount.generateShowPasswordUpdatePageToken();
        emailSendingProcess.sendPasswordUpdateNotificationEmail(updatedAccount);
        return updatedAccount;
    }
}
