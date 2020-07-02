package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.email.EmailVerificationProcess;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.dto.account.EmailVerificationRequestDto;

@Transactional
@RequiredArgsConstructor
@Service
public class EmailVerificationService {

    private final EmailVerificationProcess emailVerificationProcess;
    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public Account emailVerifyAndLogIn(EmailVerificationRequestDto emailVerificationRequestDto) {
        // 이메일 인증
        Account emailVerifiedAccountInDb = emailVerificationProcess.verifyEmail(emailVerificationRequestDto);
        return emailVerifiedAccountInDb;
    }
}
