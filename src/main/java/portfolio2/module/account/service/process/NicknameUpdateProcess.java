package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.AccountNicknameUpdateRequestDto;
import portfolio2.module.account.service.process.EmailSendingProcess;

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
