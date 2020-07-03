package portfolio2.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.SignUpRequestDto;
import portfolio2.service.account.SignUpService;

import static portfolio2.account.config.TestAccountInfo.*;

@Component
@RequiredArgsConstructor
public class SignUpAndLogInProcessForTest {

    private final SignUpService signUpService;
    private final AccountRepository accountRepository;

    public Account signUpAndLogInDefault(){
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TEST_USER_ID)
                .nickname(TEST_NICKNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        signUpService.signUp(signUpRequestDto);

        return accountRepository.findByUserId(TEST_USER_ID);
    }

    public Account signUpAndLogInNotDefaultWith(String testUserId){

        String userId = null;
        String nickname = null;
        String email = null;
        String password = null;

        switch(testUserId){
            case TEST_USER_ID_1:
                userId = TEST_USER_ID_1;
                nickname = TEST_NICKNAME_1;
                email = TEST_EMAIL_1;
                password = TEST_PASSWORD_1;
                break;
            case TEST_USER_ID_2:
                userId = TEST_USER_ID_2;
                nickname = TEST_NICKNAME_2;
                email = TEST_EMAIL_2;
                password = TEST_PASSWORD_2;
                break;
            default:
                throw new IllegalArgumentException("SignUpAndLogInProcess의 signUpAndLogIn의 매개변수 잘못넘김");
        }

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(userId)
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();

        signUpService.signUp(signUpRequestDto);
        return accountRepository.findByUserId(userId);
    }
}
