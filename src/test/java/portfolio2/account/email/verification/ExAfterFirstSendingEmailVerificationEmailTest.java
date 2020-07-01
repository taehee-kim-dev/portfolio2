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
//public class ExAfterFirstSendingEmailVerificationEmailTest {
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
//    @DisplayName("이메일 인증 이메일 1회 전송 성공 - 첫 미인증 상태 - 인증 대기중 메일로 재전송")
//    @SignUpAndLoggedIn
//    @Test
//    void sendEmailVerificationEmailNotEmailFirstVerifiedOneTimeSuccess() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend2 = accountRepository.findByUserId(TEST_USER_ID);
//        // 발송 이전 토큰
//        String beforeToken = accountBeforeSend2.getEmailVerificationToken();
//        // 발송 이전 첫 토큰 발생 시간
//        LocalDateTime beforeEmailVerificationTokenGeneratedAt
//                = accountBeforeSend2.getEmailVerificationTokenFirstGeneratedAt();
//        // 첫 인증 상태 false
//        assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//        // 이메일 인증 이메일 보낸 횟수 1
//        assertEquals(1, accountBeforeSend2.getCountOfSendingEmailVerificationEmail());
//
//        // 인증 이메일 보내기
//        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                .param("email", TEST_EMAIL)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
//
//        // 이메일 인증 상태 false
//        assertFalse(accountAfterSend.isEmailVerified());
//        // 인증된 이메일 없음
//        assertNull(accountAfterSend.getVerifiedEmail());
//        // 첫 인증 상태 false
//        assertFalse(accountBeforeSend2.isEmailFirstVerified());
//        // 토큰 바뀜
//        assertNotEquals(beforeToken, accountAfterSend.getEmailVerificationToken());
//        // 인증 대기 메일 동일
//        assertEquals(TEST_EMAIL, accountAfterSend.getEmailWaitingToBeVerified());
//        // 첫 토큰 발생 시간 동일
//        assertEquals(beforeEmailVerificationTokenGeneratedAt,
//                accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
//        // 이메일 전송 횟수 2회로 증가
//        assertEquals(2, accountAfterSend.getCountOfSendingEmailVerificationEmail());
//        // 이메일 총 전송 횟수 2회
//        verify(emailService, times(2)).sendEmail(any(EmailMessage.class));
//    }
//
//    @DisplayName("이메일 인증 이메일 5회 전송 성공 - 첫 미인증 상태 - 인증 대기중 메일로 재전송")
//    @SignUpAndLoggedIn
//    @Test
//    void NotEmailFirstVerifiedFiveTimeInRowSuccess() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend2 = accountRepository.findByUserId(TEST_USER_ID);
//        // 발송 이전 토큰
//        String beforeToken = accountBeforeSend2.getEmailVerificationToken();
//        // 발송 이전 첫 토큰 발생 시간
//        LocalDateTime beforeEmailVerificationTokenGeneratedAt
//                = accountBeforeSend2.getEmailVerificationTokenFirstGeneratedAt();
//        // 첫 인증 상태 false
//        assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//        // 이메일 인증 이메일 보낸 횟수 1
//        assertEquals(1, accountBeforeSend2.getCountOfSendingEmailVerificationEmail());
//
//        String beforeToken2 = null;
//        for(int time = 2; time <= 5; time++){
//            // 인증 이메일 보내기
//            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                    .param("email", TEST_EMAIL)
//                    .with(csrf()))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                    .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//            // 이메일 인증 이메일 보낸 후 계정 상태 확인
//            Account accountEmailSending = accountRepository.findByUserId(TEST_USER_ID);
//
//            // 이메일 인증 상태 false
//            assertFalse(accountEmailSending.isEmailVerified());
//            // 인증된 이메일 없음
//            assertNull(accountEmailSending.getVerifiedEmail());
//            // 첫 인증 상태 false
//            assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//            // 토큰 바뀜
//            assertNotEquals(beforeToken2, accountEmailSending.getEmailVerificationToken());
//
//            beforeToken2 = accountEmailSending.getEmailVerificationToken();
//
//            // 인증 대기 메일 동일
//            assertEquals(TEST_EMAIL, accountEmailSending.getEmailWaitingToBeVerified());
//            // 첫 토큰 발생 시간 동일
//            assertEquals(beforeEmailVerificationTokenGeneratedAt,
//                    accountEmailSending.getEmailVerificationTokenFirstGeneratedAt());
//            // 이메일 전송 횟수 증가
//            assertEquals(time, accountEmailSending.getCountOfSendingEmailVerificationEmail());
//            // 이메일 총 전송 횟수 2회
//            verify(emailService, times(time)).sendEmail(any(EmailMessage.class));
//        }
//    }
//
//    @DisplayName("이메일 인증 이메일 연속 6회전송 실패")
//    @SignUpAndLoggedIn
//    @Test
//    void NotEmailFirstVerifiedSixTimeInRowFail() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend2 = accountRepository.findByUserId(TEST_USER_ID);
//        // 발송 이전 토큰
//        String beforeToken = accountBeforeSend2.getEmailVerificationToken();
//        // 발송 이전 첫 토큰 발생 시간
//        LocalDateTime beforeEmailVerificationTokenGeneratedAt
//                = accountBeforeSend2.getEmailVerificationTokenFirstGeneratedAt();
//        // 첫 인증 상태 false
//        assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//        // 이메일 인증 이메일 보낸 횟수 1
//        assertEquals(1, accountBeforeSend2.getCountOfSendingEmailVerificationEmail());
//
//        String beforeToken2 = null;
//        for(int time = 2; time <= 5; time++){
//            // 인증 이메일 보내기
//            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                    .param("email", TEST_EMAIL)
//                    .with(csrf()))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                    .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//            // 이메일 인증 이메일 보낸 후 계정 상태 확인
//            Account accountEmailSending = accountRepository.findByUserId(TEST_USER_ID);
//
//            // 이메일 인증 상태 false
//            assertFalse(accountEmailSending.isEmailVerified());
//            // 인증된 이메일 없음
//            assertNull(accountEmailSending.getVerifiedEmail());
//            // 첫 인증 상태 false
//            assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//            // 토큰 바뀜
//            assertNotEquals(beforeToken2, accountEmailSending.getEmailVerificationToken());
//
//            beforeToken2 = accountEmailSending.getEmailVerificationToken();
//
//            // 인증 대기 메일 동일
//            assertEquals(TEST_EMAIL, accountEmailSending.getEmailWaitingToBeVerified());
//            // 첫 토큰 발생 시간 동일
//            assertEquals(beforeEmailVerificationTokenGeneratedAt,
//                    accountEmailSending.getEmailVerificationTokenFirstGeneratedAt());
//            // 이메일 전송 횟수 증가
//            assertEquals(time, accountEmailSending.getCountOfSendingEmailVerificationEmail());
//            // 이메일 총 전송 횟수 증가
//            verify(emailService, times(time)).sendEmail(any(EmailMessage.class));
//        }
//
//        // 5번 전송 완료
//
//        // 6번째 연속 전송
//
//        // 인증 이메일 보내기
//        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                .param("email", TEST_EMAIL)
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(view().name(CANNOT_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME))
//                .andExpect(model().attributeExists("cannotSendError"))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
//
//        // 이메일 인증 상태 false
//        assertFalse(accountAfterSend.isEmailVerified());
//        // 인증된 이메일 없음
//        assertNull(accountAfterSend.getVerifiedEmail());
//        // 첫 인증 상태 false
//        assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//        // 5번 째 토큰이랑 안바뀜
//        assertEquals(beforeToken2, accountAfterSend.getEmailVerificationToken());
//
//        // 인증 대기 메일 동일
//        assertEquals(TEST_EMAIL, accountAfterSend.getEmailWaitingToBeVerified());
//        // 첫 토큰 발생 시간 동일
//        assertEquals(beforeEmailVerificationTokenGeneratedAt,
//                accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
//        // 이메일 전송 횟수 5회 고정
//        assertEquals(5, accountAfterSend.getCountOfSendingEmailVerificationEmail());
//        // 이메일 총 전송 횟수 5회 고정
//        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
//    }
//
//    @DisplayName("12시간 후 6번째 전송 성공")
//    @SignUpAndLoggedIn
//    @Test
//    void SuccessAfter12Hours() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend2 = accountRepository.findByUserId(TEST_USER_ID);
//        // 발송 이전 토큰
//        String beforeToken = accountBeforeSend2.getEmailVerificationToken();
//        // 발송 이전 첫 토큰 발생 시간
//        LocalDateTime beforeEmailVerificationTokenGeneratedAt
//                = accountBeforeSend2.getEmailVerificationTokenFirstGeneratedAt();
//        // 첫 인증 상태 false
//        assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//        // 이메일 인증 이메일 보낸 횟수 1
//        assertEquals(1, accountBeforeSend2.getCountOfSendingEmailVerificationEmail());
//
//        String beforeToken2 = null;
//        for(int time = 2; time <= 5; time++){
//            // 인증 이메일 보내기
//            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                    .param("email", TEST_EMAIL)
//                    .with(csrf()))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                    .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//            // 이메일 인증 이메일 보낸 후 계정 상태 확인
//            Account accountEmailSending = accountRepository.findByUserId(TEST_USER_ID);
//
//            // 이메일 인증 상태 false
//            assertFalse(accountEmailSending.isEmailVerified());
//            // 인증된 이메일 없음
//            assertNull(accountEmailSending.getVerifiedEmail());
//            // 첫 인증 상태 false
//            assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//            // 토큰 바뀜
//            assertNotEquals(beforeToken2, accountEmailSending.getEmailVerificationToken());
//
//            beforeToken2 = accountEmailSending.getEmailVerificationToken();
//
//            // 인증 대기 메일 동일
//            assertEquals(TEST_EMAIL, accountEmailSending.getEmailWaitingToBeVerified());
//            // 첫 토큰 발생 시간 동일
//            assertEquals(beforeEmailVerificationTokenGeneratedAt,
//                    accountEmailSending.getEmailVerificationTokenFirstGeneratedAt());
//            // 이메일 전송 횟수 증가
//            assertEquals(time, accountEmailSending.getCountOfSendingEmailVerificationEmail());
//            // 이메일 총 전송 횟수 증가
//            verify(emailService, times(time)).sendEmail(any(EmailMessage.class));
//        }
//
//        // 5번 전송 완료
//
//        // 6번째 연속 전송 - 12시간 후
//        // 시간 12시간 1분 이후로 세팅
//        Account accountForTimeSetting = accountRepository.findByUserId(TEST_USER_ID);
//        accountForTimeSetting.setEmailVerificationTokenFirstGeneratedAt(
//                beforeEmailVerificationTokenGeneratedAt.minusHours(12).minusMinutes(1));
//        accountRepository.save(accountForTimeSetting);
//
//        // 인증 이메일 보내기
//        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                .param("email", TEST_EMAIL)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
//
//        // 이메일 인증 상태 false
//        assertFalse(accountAfterSend.isEmailVerified());
//        // 인증된 이메일 없음
//        assertNull(accountAfterSend.getVerifiedEmail());
//        // 첫 인증 상태 false
//        assertFalse(accountBeforeSend2.isEmailFirstVerified());
//
//        // 5번 째 토큰이랑 바뀜
//        assertNotEquals(beforeToken2, accountAfterSend.getEmailVerificationToken());
//
//        // 인증 대기 메일 동일
//        assertEquals(TEST_EMAIL, accountAfterSend.getEmailWaitingToBeVerified());
//        // 첫 토큰 발생 시간 다름
//        assertNotEquals(beforeEmailVerificationTokenGeneratedAt,
//                accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
//        // 이메일 전송 횟수 1회로 초기화
//        assertEquals(1, accountAfterSend.getCountOfSendingEmailVerificationEmail());
//        // 이메일 총 전송 횟수 6회로 증가
//        verify(emailService, times(6)).sendEmail(any(EmailMessage.class));
//    }
//
//    // 이전에 인증된 사례가 있을 때
//    @DisplayName("이메일 인증 이메일 1회 전송 성공 - 기존 인증 상태 - 새로운 메일로 전송")
//    @SignUpAndLoggedIn
//    @Test
//    void alreadyVerifiedEmailSuccess() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountForSettingBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
//        accountForSettingBeforeSend.setVerifiedEmail(TEST_EMAIL);
//        accountForSettingBeforeSend.setEmailFirstVerified(true);
//        accountForSettingBeforeSend.setEmailVerified(true);
//        Account accountBeforeSend = accountRepository.save(accountForSettingBeforeSend);
//
//        // 발송 이전 토큰
//        String beforeToken = accountBeforeSend.getEmailVerificationToken();
//        // 발송 이전 첫 토큰 발생 시간
//        LocalDateTime beforeEmailVerificationTokenGeneratedAt
//                = accountBeforeSend.getEmailVerificationTokenFirstGeneratedAt();
//        // 인증 상태 true
//        assertTrue(accountBeforeSend.isEmailVerified());
//
//        // 이메일 인증 이메일 보낸 횟수 1
//        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
//
//        String newEmail = "new@email.com";
//
//        // 인증 이메일 보내기
//        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                .param("email", newEmail)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
//
//        // 이메일 인증 상태 false
//        assertFalse(accountAfterSend.isEmailVerified());
//        // 인증된 이메일 없음
//        assertNull(accountAfterSend.getVerifiedEmail());
//        // 첫 인증 상태 true
//        assertTrue(accountBeforeSend.isEmailFirstVerified());
//        // 토큰 바뀜
//        assertNotEquals(beforeToken, accountAfterSend.getEmailVerificationToken());
//        // 인증 대기 메일 새로운 이메일로
//        assertEquals(newEmail, accountAfterSend.getEmailWaitingToBeVerified());
//        // 첫 토큰 발생 시간 동일
//        assertEquals(beforeEmailVerificationTokenGeneratedAt,
//                accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
//        // 이메일 전송 횟수 2회로 증가
//        assertEquals(2, accountAfterSend.getCountOfSendingEmailVerificationEmail());
//        // 이메일 총 전송 횟수 2회
//        verify(emailService, times(2)).sendEmail(any(EmailMessage.class));
//    }
//
//    @DisplayName("이메일 인증 이메일 5회 전송 성공 - 기존 인증 상태 - 새로운 메일로 전송")
//    @SignUpAndLoggedIn
//    @Test
//    void alreadyVerifiedEmailFiveTimesInRowSuccess() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 세팅
//        Account accountForSettingBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
//        accountForSettingBeforeSend.setVerifiedEmail(TEST_EMAIL);
//        accountForSettingBeforeSend.setEmailFirstVerified(true);
//        accountForSettingBeforeSend.setEmailVerified(true);
//        Account accountBeforeSend = accountRepository.save(accountForSettingBeforeSend);
//
//        // 발송 이전 토큰
//        String beforeToken = accountBeforeSend.getEmailVerificationToken();
//
//        // 발송 이전 첫 토큰 발생 시간
//        LocalDateTime beforeEmailVerificationTokenGeneratedAt
//                = accountBeforeSend.getEmailVerificationTokenFirstGeneratedAt();
//
//        // 첫 인증 상태 true
//        assertTrue(accountBeforeSend.isEmailFirstVerified());
//
//        // 현재 인증 상태 true
//        assertTrue(accountBeforeSend.isEmailVerified());
//
//        // 이메일 인증 이메일 보낸 횟수 1
//        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
//
//        String beforeToken2 = null;
//
//        for(int time = 2; time <= 5; time++){
//
//            String newEmail = "new@email.com" + time;
//
//            // 인증 이메일 보내기
//            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                    .param("email", newEmail)
//                    .with(csrf()))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                    .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//            // 이메일 인증 이메일 보낸 후 계정 상태 확인
//            Account accountEmailSending = accountRepository.findByUserId(TEST_USER_ID);
//
//            // 이메일 인증 상태 false
//            assertFalse(accountEmailSending.isEmailVerified());
//            // 인증된 이메일 없음
//            assertNull(accountEmailSending.getVerifiedEmail());
//            // 첫 인증 상태 true
//            assertTrue(accountBeforeSend.isEmailFirstVerified());
//
//            // 토큰 바뀜
//            assertNotEquals(beforeToken2, accountEmailSending.getEmailVerificationToken());
//
//            beforeToken2 = accountEmailSending.getEmailVerificationToken();
//
//            // 인증 대기 메일 바뀜
//            assertEquals(TEST_EMAIL + time, accountEmailSending.getEmailWaitingToBeVerified());
//            // 첫 토큰 발생 시간 동일
//            assertEquals(beforeEmailVerificationTokenGeneratedAt,
//                    accountEmailSending.getEmailVerificationTokenFirstGeneratedAt());
//            // 이메일 전송 횟수 증가
//            assertEquals(time, accountEmailSending.getCountOfSendingEmailVerificationEmail());
//            // 이메일 총 전송 횟수 증가
//            verify(emailService, times(time)).sendEmail(any(EmailMessage.class));
//        }
//    }
////
////    @DisplayName("이메일 인증 이메일 연속 6회전송 실패")
////    @SignUpAndLoggedIn
////    @Test
////    void NotEmailFirstVerifiedSixTimeInRowFail() throws Exception{
////
////        // 이메일 인증 이메일 보낸 전 계정 상태 확인
////        Account accountBeforeSend2 = accountRepository.findByUserId(TEST_USER_ID);
////        // 발송 이전 토큰
////        String beforeToken = accountBeforeSend2.getEmailVerificationToken();
////        // 발송 이전 첫 토큰 발생 시간
////        LocalDateTime beforeEmailVerificationTokenGeneratedAt
////                = accountBeforeSend2.getEmailVerificationTokenFirstGeneratedAt();
////        // 첫 인증 상태 false
////        assertFalse(accountBeforeSend2.isEmailFirstVerified());
////
////        // 이메일 인증 이메일 보낸 횟수 1
////        assertEquals(1, accountBeforeSend2.getCountOfSendingEmailVerificationEmail());
////
////        String beforeToken2 = null;
////        for(int time = 2; time <= 5; time++){
////            // 인증 이메일 보내기
////            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                    .param("email", TEST_EMAIL)
////                    .with(csrf()))
////                    .andExpect(status().is3xxRedirection())
////                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                    .andExpect(authenticated().withUsername(TEST_USER_ID));
////
////            // 이메일 인증 이메일 보낸 후 계정 상태 확인
////            Account accountEmailSending = accountRepository.findByUserId(TEST_USER_ID);
////
////            // 이메일 인증 상태 false
////            assertFalse(accountEmailSending.isEmailVerified());
////            // 인증된 이메일 없음
////            assertNull(accountEmailSending.getVerifiedEmail());
////            // 첫 인증 상태 false
////            assertFalse(accountBeforeSend2.isEmailFirstVerified());
////
////            // 토큰 바뀜
////            assertNotEquals(beforeToken2, accountEmailSending.getEmailVerificationToken());
////
////            beforeToken2 = accountEmailSending.getEmailVerificationToken();
////
////            // 인증 대기 메일 동일
////            assertEquals(TEST_EMAIL, accountEmailSending.getEmailWaitingToBeVerified());
////            // 첫 토큰 발생 시간 동일
////            assertEquals(beforeEmailVerificationTokenGeneratedAt,
////                    accountEmailSending.getEmailVerificationTokenFirstGeneratedAt());
////            // 이메일 전송 횟수 증가
////            assertEquals(time, accountEmailSending.getCountOfSendingEmailVerificationEmail());
////            // 이메일 총 전송 횟수 증가
////            verify(emailService, times(time)).sendEmail(any(EmailMessage.class));
////        }
////
////        // 5번 전송 완료
////
////        // 6번째 연속 전송
////
////        // 인증 이메일 보내기
////        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                .param("email", TEST_EMAIL)
////                .with(csrf()))
////                .andExpect(status().isOk())
////                .andExpect(view().name(CANNOT_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME))
////                .andExpect(model().attributeExists("cannotSendError"))
////                .andExpect(authenticated().withUsername(TEST_USER_ID));
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
////
////        // 이메일 인증 상태 false
////        assertFalse(accountAfterSend.isEmailVerified());
////        // 인증된 이메일 없음
////        assertNull(accountAfterSend.getVerifiedEmail());
////        // 첫 인증 상태 false
////        assertFalse(accountBeforeSend2.isEmailFirstVerified());
////
////        // 5번 째 토큰이랑 안바뀜
////        assertEquals(beforeToken2, accountAfterSend.getEmailVerificationToken());
////
////        // 인증 대기 메일 동일
////        assertEquals(TEST_EMAIL, accountAfterSend.getEmailWaitingToBeVerified());
////        // 첫 토큰 발생 시간 동일
////        assertEquals(beforeEmailVerificationTokenGeneratedAt,
////                accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
////        // 이메일 전송 횟수 5회 고정
////        assertEquals(5, accountAfterSend.getCountOfSendingEmailVerificationEmail());
////        // 이메일 총 전송 횟수 5회 고정
////        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
////    }
////
////    @DisplayName("12시간 후 6번째 전송 성공")
////    @SignUpAndLoggedIn
////    @Test
////    void SuccessAfter12Hours() throws Exception{
////
////        // 이메일 인증 이메일 보낸 전 계정 상태 확인
////        Account accountBeforeSend2 = accountRepository.findByUserId(TEST_USER_ID);
////        // 발송 이전 토큰
////        String beforeToken = accountBeforeSend2.getEmailVerificationToken();
////        // 발송 이전 첫 토큰 발생 시간
////        LocalDateTime beforeEmailVerificationTokenGeneratedAt
////                = accountBeforeSend2.getEmailVerificationTokenFirstGeneratedAt();
////        // 첫 인증 상태 false
////        assertFalse(accountBeforeSend2.isEmailFirstVerified());
////
////        // 이메일 인증 이메일 보낸 횟수 1
////        assertEquals(1, accountBeforeSend2.getCountOfSendingEmailVerificationEmail());
////
////        String beforeToken2 = null;
////        for(int time = 2; time <= 5; time++){
////            // 인증 이메일 보내기
////            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                    .param("email", TEST_EMAIL)
////                    .with(csrf()))
////                    .andExpect(status().is3xxRedirection())
////                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                    .andExpect(authenticated().withUsername(TEST_USER_ID));
////
////            // 이메일 인증 이메일 보낸 후 계정 상태 확인
////            Account accountEmailSending = accountRepository.findByUserId(TEST_USER_ID);
////
////            // 이메일 인증 상태 false
////            assertFalse(accountEmailSending.isEmailVerified());
////            // 인증된 이메일 없음
////            assertNull(accountEmailSending.getVerifiedEmail());
////            // 첫 인증 상태 false
////            assertFalse(accountBeforeSend2.isEmailFirstVerified());
////
////            // 토큰 바뀜
////            assertNotEquals(beforeToken2, accountEmailSending.getEmailVerificationToken());
////
////            beforeToken2 = accountEmailSending.getEmailVerificationToken();
////
////            // 인증 대기 메일 동일
////            assertEquals(TEST_EMAIL, accountEmailSending.getEmailWaitingToBeVerified());
////            // 첫 토큰 발생 시간 동일
////            assertEquals(beforeEmailVerificationTokenGeneratedAt,
////                    accountEmailSending.getEmailVerificationTokenFirstGeneratedAt());
////            // 이메일 전송 횟수 증가
////            assertEquals(time, accountEmailSending.getCountOfSendingEmailVerificationEmail());
////            // 이메일 총 전송 횟수 증가
////            verify(emailService, times(time)).sendEmail(any(EmailMessage.class));
////        }
////
////        // 5번 전송 완료
////
////        // 6번째 연속 전송 - 12시간 후
////        // 시간 12시간 1분 이후로 세팅
////        Account accountForTimeSetting = accountRepository.findByUserId(TEST_USER_ID);
////        accountForTimeSetting.setEmailVerificationTokenFirstGeneratedAt(
////                beforeEmailVerificationTokenGeneratedAt.minusHours(12).minusMinutes(1));
////        accountRepository.save(accountForTimeSetting);
////
////        // 인증 이메일 보내기
////        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                .param("email", TEST_EMAIL)
////                .with(csrf()))
////                .andExpect(status().is3xxRedirection())
////                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                .andExpect(authenticated().withUsername(TEST_USER_ID));
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
////
////        // 이메일 인증 상태 false
////        assertFalse(accountAfterSend.isEmailVerified());
////        // 인증된 이메일 없음
////        assertNull(accountAfterSend.getVerifiedEmail());
////        // 첫 인증 상태 false
////        assertFalse(accountBeforeSend2.isEmailFirstVerified());
////
////        // 5번 째 토큰이랑 바뀜
////        assertNotEquals(beforeToken2, accountAfterSend.getEmailVerificationToken());
////
////        // 인증 대기 메일 동일
////        assertEquals(TEST_EMAIL, accountAfterSend.getEmailWaitingToBeVerified());
////        // 첫 토큰 발생 시간 다름
////        assertNotEquals(beforeEmailVerificationTokenGeneratedAt,
////                accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
////        // 이메일 전송 횟수 1회로 초기화
////        assertEquals(1, accountAfterSend.getCountOfSendingEmailVerificationEmail());
////        // 이메일 총 전송 횟수 6회로 증가
////        verify(emailService, times(6)).sendEmail(any(EmailMessage.class));
////    }
//}
