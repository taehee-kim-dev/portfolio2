package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.process.email.EmailVerificationProcess;
import portfolio2.domain.process.account.LogInOrSessionUpdateProcess;
import portfolio2.dto.request.account.EmailVerificationRequestDto;
import portfolio2.dto.response.account.EmailVerificationResponseDto;

@Transactional
@RequiredArgsConstructor
@Service
public class EmailVerificationService {

    private final EmailVerificationProcess emailVerificationProcess;
    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public EmailVerificationResponseDto verifyEmailAndUpdateSessionIfLoggedInByEmailVerifiedAccount
            (EmailVerificationRequestDto emailVerificationRequestDto, Account sessionAccount) {

        EmailVerificationResponseDto emailVerificationResponseDto = new EmailVerificationResponseDto();
        // 이메일 인증
        Account emailVerifiedAccountInDb = emailVerificationProcess.verifyEmail(emailVerificationRequestDto);

        // 이메일 인증된 계정으로 현재 로그인 중이라면,
        if(sessionAccount != null && emailVerificationProcess.isEmailVerifiedAccountLoggedIn(sessionAccount, emailVerifiedAccountInDb)){
            // 이메일 인증된 계정으로 로그인 상태라면, 해당 상태값 저장, 세션 업데이트 후 세션 계정 반환.
            emailVerificationResponseDto.setEmailVerifiedAccountLoggedIn(true);
            emailVerificationResponseDto.setUpdatedSessionAccount
                    (logInOrSessionUpdateProcess.loginOrSessionUpdate(emailVerifiedAccountInDb));
        }else{
            // 그 외의 경우 해당 상태값만 저장.
            emailVerificationResponseDto.setEmailVerifiedAccountLoggedIn(false);
        }

        // 이메일 인증된 계정은 무조건 반환되어야 함.
        emailVerificationResponseDto.setEmailVerifiedAccountInDb(emailVerifiedAccountInDb);
        return emailVerificationResponseDto;
    }
}
