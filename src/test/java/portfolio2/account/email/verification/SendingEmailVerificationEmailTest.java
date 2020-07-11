package portfolio2.account.email.verification;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.email.EmailService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static portfolio2.account.config.TestAccountInfo.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class SendingEmailVerificationEmailTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogInProcessForTest signUpAndLogInProcessForTest;

    @Autowired
    private SignUpAndLogOutProcessForTest signUpAndLogOutProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @MockBean
    private EmailService emailService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 무조건 로그인 상태에서만 보낼 수 밖에 없다.
    @DisplayName("이메일 인증 이메일 전송 - 1회 - 성공")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void sendOneTimeSuccess(){
        // 회원가입 후 로그인 상태
        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
        // 전송 전 값 저장
        // 카운트 값
        int beforeSendCount = accountBeforeSend.getCountOfSendingEmailVerificationEmail();
        // 회원가입 직후 1 확인
        assertEquals(1, beforeSendCount);
        // 첫 번째 카운트 값 세팅 시간
        LocalDateTime beforeSendFirstCountSetAt = accountBeforeSend.getFirstCountOfSendingEmailVerificationEmailSetAt();
        // 회원가입 직후 존재
        assertNotNull(beforeSendFirstCountSetAt);
        // 기존 인증된 이메일 값 세팅
        // 회원가입 직후 null 상태
        assertNull(accountBeforeSend.getVerifiedEmail());
        // 존재하는 걸로 세팅
        String beforeSendVerifiedEmail = "before@email.com";
        accountBeforeSend.setVerifiedEmail(beforeSendVerifiedEmail);
        // 기존 토큰 값
        String beforeSendEmailToken = accountBeforeSend.getEmailVerificationToken();
        // 회원가입 직후 토큰값 존재
        assertNotNull(beforeSendEmailToken);
        // 기존 인증 대기 중 이메일 값
        String beforeSendEmailWaitingToBeVerified = accountBeforeSend.getEmailWaitingToBeVerified();
        // 기존 값 확인
        assertEquals(TEST_EMAIL, beforeSendEmailWaitingToBeVerified);
        //세팅값 저장
        accountRepository.save(accountBeforeSend);

        
    }

}
