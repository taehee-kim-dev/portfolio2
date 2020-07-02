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

        // 기존에 본인 계정으로 로그인 상태였으면, 세션 업데이트 후 반환
        // 인증된 계정의 아이디와 세션 계정의 아이디가 같다면, 기존에 본인 계정으로 로그인하고 있던 상태.
        if(sessionAccount != null && emailVerifiedAccountInDb.getUserId().equals(sessionAccount.getUserId()))
            return logInOrSessionUpdateProcess.loginOrSessionUpdate(emailVerifiedAccountInDb);

        // 기존에 로그아웃 상태였거나, 다른 계정으로 로그인 상태였다면 상태 유지
        return emailVerifiedAccountInDb;

    }
}
