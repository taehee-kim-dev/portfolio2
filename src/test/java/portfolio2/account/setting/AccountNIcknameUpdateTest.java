package portfolio2.account.setting;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.SignUpAndLoggedInEmailNotVerified;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.email.EmailSendingProcess;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static portfolio2.account.config.TestAccountInfo.TEST_USER_ID;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class AccountNIcknameUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailSendingProcess emailSendingProcess;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 모두 무조건 로그인 상태여야 함

//    @DisplayName("모두 정상 입력 - 이메일 인증된 상태")
//    @SignUpAndLoggedInEmailNotVerified
//    @Test
//    void successWithEmailVerified() throws Exception{
//        Account beforeUpdate = accountRepository.findByUserId(TEST_USER_ID);
//        beforeUpdate.setEmailVerified(true);
//        beforeUpdate.setEmailFirstVerified(true);
//        beforeUpdate.
//    }
}
