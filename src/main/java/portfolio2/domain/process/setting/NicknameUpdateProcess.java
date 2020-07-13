package portfolio2.domain.process.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.process.email.EmailSendingProcess;
import portfolio2.dto.request.account.setting.AccountNicknameUpdateRequestDto;

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
