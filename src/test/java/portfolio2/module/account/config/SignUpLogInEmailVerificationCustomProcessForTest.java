package portfolio2.module.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.EmailVerificationRequestDto;
import portfolio2.module.account.dto.request.SignUpRequestDto;
import portfolio2.module.account.service.EmailVerificationService;
import portfolio2.module.account.service.SignUpService;

import static portfolio2.module.account.config.TestAccountInfo.*;

@Component
@RequiredArgsConstructor
public class SignUpLogInEmailVerificationCustomProcessForTest {

    private final SignUpService signUpService;
    private final AccountRepository accountRepository;
    private final EmailVerificationService emailVerificationService;

    public Account withCustomProperties(String userId, String nickname, String email, String password, boolean emailVerified, boolean logIn){

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(userId)
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();

        signUpService.signUp(signUpRequestDto);

        if(emailVerified) {
            Account signedUpAccount = accountRepository.findByUserId(userId);

            EmailVerificationRequestDto emailVerificationRequestDto = new EmailVerificationRequestDto();
            emailVerificationRequestDto.setUserId(userId);
            emailVerificationRequestDto.setEmail(email);
            emailVerificationRequestDto.setToken(signedUpAccount.getEmailVerificationToken());
            emailVerificationService.verifyEmailAndUpdateSessionIfLoggedInByEmailVerifiedAccount
                    (emailVerificationRequestDto, signedUpAccount);
        }

        if(!logIn)
            SecurityContextHolder.getContext().setAuthentication(null);

        return accountRepository.findByUserId(userId);
    }
}
