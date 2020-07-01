package portfolio2.account.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.account.SignUpService;

import static portfolio2.account.config.TestAccountInfo.*;
import static portfolio2.account.config.TestAccountInfo.TEST_PASSWORD_2;

@Component
@RequiredArgsConstructor
public class OnlySignUpProcessForTest {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account signUpDefault(){
        Account account = new Account();
        account.setUserId(TEST_USER_ID);
        account.setNickname(TEST_NICKNAME);
        account.setEmailWaitingToBeVerified(TEST_EMAIL);
        account.setPassword(passwordEncoder.encode(TEST_PASSWORD));

        return accountRepository.save(account);
    }

    public Account signUpNotDefaultWithSuffix(int suffix){

        String userId = null;
        String nickname = null;
        String email = null;
        String password = null;

        switch(suffix){
            case 1:
                userId = TEST_USER_ID_1;
                nickname = TEST_NICKNAME_1;
                email = TEST_EMAIL_1;
                password = TEST_PASSWORD_1;
                break;
            case 2:
                userId = TEST_USER_ID_2;
                nickname = TEST_NICKNAME_2;
                email = TEST_EMAIL_2;
                password = TEST_PASSWORD_2;
                break;
            default:
                throw new IllegalArgumentException("SignUpAndLogInProcess의 signUpAndLogIn의 매개변수 잘못넘김");
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setNickname(nickname);
        account.setEmailWaitingToBeVerified(email);
        account.setPassword(passwordEncoder.encode(password));

        return accountRepository.save(account);
    }
}
