package portfolio2.domain.process.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.process.email.EmailSendingProcess;
import portfolio2.dto.request.account.FindPasswordRequestDto;

@RequiredArgsConstructor
@Component
public class FindPasswordProcess {

    private final AccountRepository accountRepository;
    private final EmailSendingProcess emailSendingProcess;

    public void sendFindPasswordEmail(FindPasswordRequestDto findPasswordRequestDto) {
        Account accountToFindPassword = accountRepository.findByVerifiedEmail(findPasswordRequestDto.getEmail());
        accountToFindPassword.generateShowPasswordUpdatePageToken();
        emailSendingProcess.sendFindPasswordEmail(accountToFindPassword);
    }
}
