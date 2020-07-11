package portfolio2.domain.account.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.email.EmailSendingProcess;
import portfolio2.domain.email.SendingEmailVerificationEmailProcess;
import portfolio2.dto.request.account.setting.AccountEmailUpdateRequestDto;
import portfolio2.dto.request.account.setting.PasswordUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class EmailUpdateProcess {

    private final AccountRepository accountRepository;
    private final SendingEmailVerificationEmailProcess sendingEmailVerificationEmailProcess;

    public Account updateEmail(Account sessionAccount, AccountEmailUpdateRequestDto accountEmailUpdateRequestDto) {
        Account accountToUpdate = accountRepository.findByUserId(sessionAccount.getUserId());
        // 인증 대기 이메일 설정
        accountToUpdate.setEmailWaitingToBeVerified(accountEmailUpdateRequestDto.getEmail());
        // 인증 메일 발송
        return sendingEmailVerificationEmailProcess.sendEmailVerificationEmail(accountToUpdate);
    }
}
