package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.PasswordUpdateRequestDto;
import portfolio2.module.account.service.process.EmailSendingProcess;

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
