package portfolio2.domain.account.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.email.EmailSendingProcess;
import portfolio2.dto.request.account.setting.AccountNicknameUpdateRequestDto;
import portfolio2.dto.request.account.setting.PasswordUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class NicknameUpdateProcess {

    private final AccountRepository accountRepository;
    private final EmailSendingProcess emailSendingProcess;

    public Account updateNickname(Account sessionAccount, AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto) {
        Account accountInDbToUpdate = accountRepository.findByUserId(sessionAccount.getUserId());
        accountInDbToUpdate.setNicknameBeforeUpdate(accountInDbToUpdate.getNickname());
        accountInDbToUpdate.setNickname(accountNicknameUpdateRequestDto.getNickname());
        return accountInDbToUpdate;
    }

    public Account sendNicknameUpdateNotificationEmail(Account updatedAccount) {
        updatedAccount.generateShowPasswordUpdatePageToken();
        emailSendingProcess.sendNicknameUpdateNotificationEmail(updatedAccount);
        return updatedAccount;
    }
}
