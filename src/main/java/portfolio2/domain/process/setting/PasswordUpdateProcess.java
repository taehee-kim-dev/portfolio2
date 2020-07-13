package portfolio2.domain.process.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.process.email.EmailSendingProcess;
import portfolio2.dto.request.account.setting.PasswordUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class PasswordUpdateProcess {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendingProcess emailSendingProcess;

    public Account updatePassword(Account sessionAccount, PasswordUpdateRequestDto passwordUpdateRequestDto) {
        Account accountToUpdate = accountRepository.findByUserId(sessionAccount.getUserId());
        accountToUpdate.setPassword(passwordEncoder.encode(passwordUpdateRequestDto.getNewPassword()));
        return accountToUpdate;
    }

    public Account sendPasswordUpdateNotificationEmail(Account updatedAccount) {
        updatedAccount.generateShowPasswordUpdatePageToken();
        emailSendingProcess.sendPasswordUpdateNotificationEmail(updatedAccount);
        return updatedAccount;
    }
}
