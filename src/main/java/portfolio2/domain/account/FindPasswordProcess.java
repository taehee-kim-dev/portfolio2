package portfolio2.domain.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.email.EmailSendingProcess;
import portfolio2.dto.request.account.FindPasswordRequestDto;
import portfolio2.dto.request.account.setting.PasswordUpdateRequestDto;

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
