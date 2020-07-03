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

    public Account emailVerifyAndLogInIfLoggedInByOwnAccount(EmailVerificationRequestDto emailVerificationRequestDto, Account sessionAccount) {
        // 이메일 인증
        Account emailVerifiedAccountInDb = emailVerificationProcess.verifyEmail(emailVerificationRequestDto);

        // 현재 로그인 된 상태라면,
        if(sessionAccount != null){
            // 본인 계정으로 로그인 상태라면, 세션 업데이트 후 반환
            if(emailVerificationProcess.isOwnerLoggedIn(sessionAccount, emailVerifiedAccountInDb))
                return logInOrSessionUpdateProcess.loginOrSessionUpdate(emailVerifiedAccountInDb);
        }

        // 기존에 로그아웃 상태였거나, 다른 계정으로 로그인 상태였다면 상태 유지
        return emailVerifiedAccountInDb;
    }
}
