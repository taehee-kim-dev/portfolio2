//package portfolio2.account.email.verification;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.tomcat.jni.Local;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import portfolio2.account.config.*;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.AccountRepository;
//import portfolio2.domain.account.CustomPrincipal;
//import portfolio2.mail.EmailMessage;
//import portfolio2.mail.EmailService;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static portfolio2.account.config.TestAccountInfo.*;
//import static portfolio2.config.UrlAndViewName.*;
//
//@Slf4j
//@SpringBootTest
//@AutoConfigureMockMvc
//public class SendingEmailVerificationEmailTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private SignUpAndLogInProcessForTest signUpAndLogInProcessForTest;
//
//    @Autowired
//    private SignUpAndLogOutProcessForTest signUpAndLogOutProcessForTest;
//
//    @Autowired
//    private LogInAndOutProcess logInAndOutProcess;
//
//    @MockBean
//    private EmailService emailService;
//
//    @AfterEach
//    void afterEach(){
//        accountRepository.deleteAll();
//    }
//
//    // 무조건 로그인 상태에서만 보낼 수 밖에 없다.
//    @DisplayName("이메일 인증 이메일 전송 - 1회 - 성공")
//    @SignUpAndLoggedIn
//    @Test
//    void sendOneTimeSuccess(){
//
//        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
//
//    }
//
//}
