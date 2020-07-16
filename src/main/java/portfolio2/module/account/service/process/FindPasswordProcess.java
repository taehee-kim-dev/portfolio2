package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.FindPasswordRequestDto;

@RequiredArgsConstructor
@Component
public class FindPasswordProcess {

    private final AccountRepository accountRepository;
    private final EmailSendingProcessForAccount emailSendingProcessForAccount;

    public void sendFindPasswordEmail(FindPasswordRequestDto findPasswordRequestDto) {
        Account accountToFindPassword = accountRepository.findByVerifiedEmail(findPasswordRequestDto.getEmail());
        accountToFindPassword.generateShowPasswordUpdatePageToken();
        emailSendingProcessForAccount.sendFindPasswordEmail(accountToFindPassword);
    }
}
