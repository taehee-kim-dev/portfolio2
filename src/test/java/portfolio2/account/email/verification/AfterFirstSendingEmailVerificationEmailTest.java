//package portfolio2.account.email.verification;
//
//import lombok.extern.slf4j.Slf4j;
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
//public class AfterFirstSendingEmailVerificationEmailTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private SignUpAndLogInWithAccount1Process signUpAndLogInWithAccount1Process;
//
//    @Autowired
//    private SignUpAndLogOutWithAccount1Process signUpAndLogOutWithAccount1Process;
//
//    @Autowired
//    private SignUpAndLogInWithAccount2Process signUpAndLogInWithAccount2Process;
//
//    @Autowired
//    private SignUpAndLogOutWithAccount2Process signUpAndLogOutWithAccount2Process;
//
//    @Autowired
//    private LogInProcess logInProcess;
//
//    @MockBean
//    private EmailService emailService;
//
//    @AfterEach
//    void afterEach(){
//        accountRepository.deleteAll();
//    }
//
//    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증한 적 없음1회 - 보내기 가능 상태 - 본인 계정으로 로그인 상태")
//    @SignUpAndLoggedIn
//    @Test
//    void sendEmailVerificationEmailWithLogInByOwnAccount() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
//        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
//
//        String emailToSendEmail = "emailToSend@email.com";
//
//        // 인증 이메일 보내기
//        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                .param("email", emailToSendEmail)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
//        assertFalse(accountAfterSend.isEmailVerified());
//        assertNull(accountAfterSend.getVerifiedEmail());
//
//        assertNotNull(accountAfterSend.getEmailVerificationToken());
//        assertEquals(emailToSendEmail, accountAfterSend.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(2, accountAfterSend.getCountOfSendingEmailVerificationEmail());
//
//        verify(emailService, times(2)).sendEmail(any(EmailMessage.class));
//    }
//
//    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증 한 적 없음 - 5회 - 보내기 가능 상태 - 본인 계정으로 로그인 상태")
//    @SignUpAndLoggedIn
//    @Test
//    void sendEmailVerificationEmailFiveTimes() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
//        String firstToken = accountBeforeSend.getEmailVerificationToken();
//        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
//
//        String emailToSendEmail = "emailToSend@email.com";
//
//        for(int time = 1; time <= 4; time++){
//            // 인증 이메일 보내기
//            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                    .param("email", emailToSendEmail)
//                    .with(csrf()))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                    .andExpect(authenticated().withUsername(TEST_USER_ID));
//        }
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
//        assertFalse(accountAfterSend.isEmailVerified());
//        assertNull(accountAfterSend.getVerifiedEmail());
//
//        assertNotNull(accountAfterSend.getEmailVerificationToken());
//        assertEquals(emailToSendEmail, accountAfterSend.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(5, accountAfterSend.getCountOfSendingEmailVerificationEmail());
//
//        assertNotEquals(firstToken, accountAfterSend.getEmailVerificationToken());
//
//        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
//    }
//
//    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증 한 적 없음 - 연속 6번 째부터 보내기 불가능 - 본인 계정으로 로그인 상태")
//    @SignUpAndLoggedIn
//    @Test
//    void sendEmailVerificationEmailSixTimesInRow() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
//        String firstToken = accountBeforeSend.getEmailVerificationToken();
//        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
//
//        String emailToSendEmail = "emailToSend@email.com";
//
//        for(int time = 1; time <= 4; time++){
//            // 인증 이메일 보내기
//            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                    .param("email", emailToSendEmail)
//                    .with(csrf()))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                    .andExpect(authenticated().withUsername(TEST_USER_ID));
//        }
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSendFiveTimes = accountRepository.findByUserId(TEST_USER_ID);
//        assertFalse(accountAfterSendFiveTimes.isEmailVerified());
//        assertNull(accountAfterSendFiveTimes.getVerifiedEmail());
//
//        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationToken());
//        assertEquals(emailToSendEmail, accountAfterSendFiveTimes.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(5, accountAfterSendFiveTimes.getCountOfSendingEmailVerificationEmail());
//
//        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
//
//        String fifthToken = accountAfterSendFiveTimes.getEmailVerificationToken();
//
//        assertNotEquals(firstToken, fifthToken);
//
//        // 5회 전송 완료
//
//        // 인증 이메일 연속으로 6번 째 보내기 - 불가능
//        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                .param("email", emailToSendEmail)
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(view().name(CANNOT_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME))
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("cannotSendError"))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSendSixTimesInRow = accountRepository.findByUserId(TEST_USER_ID);
//        assertFalse(accountAfterSendSixTimesInRow.isEmailVerified());
//        assertNull(accountAfterSendSixTimesInRow.getVerifiedEmail());
//
//        assertNotNull(accountAfterSendSixTimesInRow.getEmailVerificationToken());
//        assertEquals(emailToSendEmail, accountAfterSendSixTimesInRow.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountAfterSendSixTimesInRow.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(5, accountAfterSendSixTimesInRow.getCountOfSendingEmailVerificationEmail());
//        assertFalse(accountAfterSendSixTimesInRow.canSendEmailVerificationEmail());
//
//        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
//
//        assertEquals(fifthToken, accountAfterSendSixTimesInRow.getEmailVerificationToken());
//    }
//
//    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증 한 적 없음 - 12시간 후 보내기 가능 - 본인 계정으로 로그인 상태")
//    @SignUpAndLoggedIn
//    @Test
//    void sendEmailVerificationEmailSixTimesAfter12HoursFromFirstTime() throws Exception{
//
//        // 이메일 인증 이메일 보낸 전 계정 상태 확인
//        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
//        String firstToken = accountBeforeSend.getEmailVerificationToken();
//        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
//        LocalDateTime firstTime = accountBeforeSend.getEmailVerificationTokenFirstGeneratedAt();
//
//        String emailToSendEmail = "emailToSend@email.com";
//
//        for(int time = 1; time <= 4; time++){
//            // 인증 이메일 보내기
//            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                    .param("email", emailToSendEmail)
//                    .with(csrf()))
//                    .andExpect(status().is3xxRedirection())
//                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                    .andExpect(authenticated().withUsername(TEST_USER_ID));
//        }
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSendFiveTimes = accountRepository.findByUserId(TEST_USER_ID);
//        assertFalse(accountAfterSendFiveTimes.isEmailVerified());
//        assertNull(accountAfterSendFiveTimes.getVerifiedEmail());
//
//        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationToken());
//        assertEquals(emailToSendEmail, accountAfterSendFiveTimes.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(5, accountAfterSendFiveTimes.getCountOfSendingEmailVerificationEmail());
//
//        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
//
//        // 5회 전송 완료
//
//        String fifthToken = accountAfterSendFiveTimes.getEmailVerificationToken();
//        assertNotEquals(firstToken, fifthToken);
//
//        // 12시간 후로 설정
//
//        LocalDateTime after12Hours = firstTime.minusHours(12).minusMinutes(1);
//        accountAfterSendFiveTimes.setEmailVerificationTokenFirstGeneratedAt(after12Hours);
//
//        accountRepository.save(accountAfterSendFiveTimes);
//
//        // 인증 이메일 보내기
//        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
//                .param("email", emailToSendEmail)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
//                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 이메일 보낸 후 계정 상태 확인
//        Account accountAfterSendSixthTimes = accountRepository.findByUserId(TEST_USER_ID);
//        assertFalse(accountAfterSendSixthTimes.isEmailVerified());
//        assertNull(accountAfterSendSixthTimes.getVerifiedEmail());
//
//        assertNotNull(accountAfterSendSixthTimes.getEmailVerificationToken());
//        assertEquals(emailToSendEmail, accountAfterSendSixthTimes.getEmailWaitingToBeVerified());
//
//        assertNotEquals(after12Hours, accountAfterSendSixthTimes.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountAfterSendSixthTimes.getCountOfSendingEmailVerificationEmail());
//        assertTrue(accountAfterSendSixthTimes.canSendEmailVerificationEmail());
//
//        verify(emailService, times(6)).sendEmail(any(EmailMessage.class));
//        assertNotEquals(fifthToken, accountAfterSendSixthTimes.getEmailVerificationToken());
//    }
//
//    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증한 적 없음1회 - 보내기 가능 상태 - 본인 계정으로 로그인 상태")
//    @SignUpAndLoggedIn
//    @Test
//    void sendEmailVerificationEmailAfterFirstVerification() throws Exception{
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String validLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
//                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
//                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();
//        System.out.println(validLink);
//
//        // 유효 링크 인증
//        mockMvc.perform(get(validLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("sessionAccount"))
//                .andExpect(model().attributeExists("nickname"))
//                .andExpect(model().attributeExists("userId"))
//                .andExpect(model().attributeExists("email"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//        assertEquals(TEST_EMAIL, accountEmailVerified.getVerifiedEmail());
//
//        assertTrue(accountEmailVerified.isEmailVerified());
//        assertTrue(accountEmailVerified.isEmailFirstVerified());
//
//        assertNull(accountEmailVerified.getEmailVerificationToken());
//        assertNull(accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//
//        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
//
//        mockMvc.perform(get(HOME_URL))
//                .andExpect(status().isOk())
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
////
////
////        // 새로운 이메일 인증 이메일 보낸 전 계정 상태 확인
////        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
////        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
////
////        String emailToSendEmail = TEST_EMAIL;
////
////        // 인증 이메일 보내기
////        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                .param("email", emailToSendEmail)
////                .with(csrf()))
////                .andExpect(status().is3xxRedirection())
////                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                .andExpect(authenticated().withUsername(TEST_USER_ID));
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
////        assertFalse(accountAfterSend.isEmailVerified());
////        assertNull(accountAfterSend.getVerifiedEmail());
////
////        assertNotNull(accountAfterSend.getEmailVerificationToken());
////        assertEquals(emailToSendEmail, accountAfterSend.getEmailWaitingToBeVerified());
////
////        assertNotNull(accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
////        assertEquals(2, accountAfterSend.getCountOfSendingEmailVerificationEmail());
////
////        verify(emailService, times(2)).sendEmail(any(EmailMessage.class));
//    }
//
////    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증 한 적 없음 - 5회 - 보내기 가능 상태 - 본인 계정으로 로그인 상태")
////    @SignUpAndLoggedIn
////    @Test
////    void sendEmailVerificationEmailFiveTimes() throws Exception{
////
////        // 이메일 인증 이메일 보낸 전 계정 상태 확인
////        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
////        String firstToken = accountBeforeSend.getEmailVerificationToken();
////        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
////
////        String emailToSendEmail = "emailToSend@email.com";
////
////        for(int time = 1; time <= 4; time++){
////            // 인증 이메일 보내기
////            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                    .param("email", emailToSendEmail)
////                    .with(csrf()))
////                    .andExpect(status().is3xxRedirection())
////                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                    .andExpect(authenticated().withUsername(TEST_USER_ID));
////        }
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSend = accountRepository.findByUserId(TEST_USER_ID);
////        assertFalse(accountAfterSend.isEmailVerified());
////        assertNull(accountAfterSend.getVerifiedEmail());
////
////        assertNotNull(accountAfterSend.getEmailVerificationToken());
////        assertEquals(emailToSendEmail, accountAfterSend.getEmailWaitingToBeVerified());
////
////        assertNotNull(accountAfterSend.getEmailVerificationTokenFirstGeneratedAt());
////        assertEquals(5, accountAfterSend.getCountOfSendingEmailVerificationEmail());
////
////        assertNotEquals(firstToken, accountAfterSend.getEmailVerificationToken());
////
////        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
////    }
////
////    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증 한 적 없음 - 연속 6번 째부터 보내기 불가능 - 본인 계정으로 로그인 상태")
////    @SignUpAndLoggedIn
////    @Test
////    void sendEmailVerificationEmailSixTimesInRow() throws Exception{
////
////        // 이메일 인증 이메일 보낸 전 계정 상태 확인
////        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
////        String firstToken = accountBeforeSend.getEmailVerificationToken();
////        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
////
////        String emailToSendEmail = "emailToSend@email.com";
////
////        for(int time = 1; time <= 4; time++){
////            // 인증 이메일 보내기
////            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                    .param("email", emailToSendEmail)
////                    .with(csrf()))
////                    .andExpect(status().is3xxRedirection())
////                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                    .andExpect(authenticated().withUsername(TEST_USER_ID));
////        }
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSendFiveTimes = accountRepository.findByUserId(TEST_USER_ID);
////        assertFalse(accountAfterSendFiveTimes.isEmailVerified());
////        assertNull(accountAfterSendFiveTimes.getVerifiedEmail());
////
////        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationToken());
////        assertEquals(emailToSendEmail, accountAfterSendFiveTimes.getEmailWaitingToBeVerified());
////
////        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationTokenFirstGeneratedAt());
////        assertEquals(5, accountAfterSendFiveTimes.getCountOfSendingEmailVerificationEmail());
////
////        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
////
////        String fifthToken = accountAfterSendFiveTimes.getEmailVerificationToken();
////
////        assertNotEquals(firstToken, fifthToken);
////
////        // 5회 전송 완료
////
////        // 인증 이메일 연속으로 6번 째 보내기 - 불가능
////        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                .param("email", emailToSendEmail)
////                .with(csrf()))
////                .andExpect(status().isOk())
////                .andExpect(view().name(CANNOT_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME))
////                .andExpect(model().hasNoErrors())
////                .andExpect(model().attributeExists("cannotSendError"))
////                .andExpect(authenticated().withUsername(TEST_USER_ID));
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSendSixTimesInRow = accountRepository.findByUserId(TEST_USER_ID);
////        assertFalse(accountAfterSendSixTimesInRow.isEmailVerified());
////        assertNull(accountAfterSendSixTimesInRow.getVerifiedEmail());
////
////        assertNotNull(accountAfterSendSixTimesInRow.getEmailVerificationToken());
////        assertEquals(emailToSendEmail, accountAfterSendSixTimesInRow.getEmailWaitingToBeVerified());
////
////        assertNotNull(accountAfterSendSixTimesInRow.getEmailVerificationTokenFirstGeneratedAt());
////        assertEquals(5, accountAfterSendSixTimesInRow.getCountOfSendingEmailVerificationEmail());
////        assertFalse(accountAfterSendSixTimesInRow.canSendEmailVerificationEmail());
////
////        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
////
////        assertEquals(fifthToken, accountAfterSendSixTimesInRow.getEmailVerificationToken());
////    }
////
////    @DisplayName("이메일 인증 이메일 보내기 - 처음 인증 한 적 없음 - 12시간 후 보내기 가능 - 본인 계정으로 로그인 상태")
////    @SignUpAndLoggedIn
////    @Test
////    void sendEmailVerificationEmailSixTimesAfter12HoursFromFirstTime() throws Exception{
////
////        // 이메일 인증 이메일 보낸 전 계정 상태 확인
////        Account accountBeforeSend = accountRepository.findByUserId(TEST_USER_ID);
////        String firstToken = accountBeforeSend.getEmailVerificationToken();
////        assertEquals(1, accountBeforeSend.getCountOfSendingEmailVerificationEmail());
////        LocalDateTime firstTime = accountBeforeSend.getEmailVerificationTokenFirstGeneratedAt();
////
////        String emailToSendEmail = "emailToSend@email.com";
////
////        for(int time = 1; time <= 4; time++){
////            // 인증 이메일 보내기
////            mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                    .param("email", emailToSendEmail)
////                    .with(csrf()))
////                    .andExpect(status().is3xxRedirection())
////                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                    .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                    .andExpect(authenticated().withUsername(TEST_USER_ID));
////        }
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSendFiveTimes = accountRepository.findByUserId(TEST_USER_ID);
////        assertFalse(accountAfterSendFiveTimes.isEmailVerified());
////        assertNull(accountAfterSendFiveTimes.getVerifiedEmail());
////
////        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationToken());
////        assertEquals(emailToSendEmail, accountAfterSendFiveTimes.getEmailWaitingToBeVerified());
////
////        assertNotNull(accountAfterSendFiveTimes.getEmailVerificationTokenFirstGeneratedAt());
////        assertEquals(5, accountAfterSendFiveTimes.getCountOfSendingEmailVerificationEmail());
////
////        verify(emailService, times(5)).sendEmail(any(EmailMessage.class));
////
////        // 5회 전송 완료
////
////        String fifthToken = accountAfterSendFiveTimes.getEmailVerificationToken();
////        assertNotEquals(firstToken, fifthToken);
////
////        // 12시간 후로 설정
////
////        LocalDateTime after12Hours = firstTime.minusHours(12).minusMinutes(1);
////        accountAfterSendFiveTimes.setEmailVerificationTokenFirstGeneratedAt(after12Hours);
////
////        accountRepository.save(accountAfterSendFiveTimes);
////
////        // 인증 이메일 보내기
////        mockMvc.perform(post(AFTER_FIRST_SEND_EMAIL_VERIFICATION_EMAIL_URL)
////                .param("email", emailToSendEmail)
////                .with(csrf()))
////                .andExpect(status().is3xxRedirection())
////                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
////                .andExpect(flash().attribute("message", "인증 이메일을 보냈습니다."))
////                .andExpect(authenticated().withUsername(TEST_USER_ID));
////
////        // 이메일 인증 이메일 보낸 후 계정 상태 확인
////        Account accountAfterSendSixthTimes = accountRepository.findByUserId(TEST_USER_ID);
////        assertFalse(accountAfterSendSixthTimes.isEmailVerified());
////        assertNull(accountAfterSendSixthTimes.getVerifiedEmail());
////
////        assertNotNull(accountAfterSendSixthTimes.getEmailVerificationToken());
////        assertEquals(emailToSendEmail, accountAfterSendSixthTimes.getEmailWaitingToBeVerified());
////
////        assertNotEquals(after12Hours, accountAfterSendSixthTimes.getEmailVerificationTokenFirstGeneratedAt());
////        assertEquals(1, accountAfterSendSixthTimes.getCountOfSendingEmailVerificationEmail());
////        assertTrue(accountAfterSendSixthTimes.canSendEmailVerificationEmail());
////
////        verify(emailService, times(6)).sendEmail(any(EmailMessage.class));
////        assertNotEquals(fifthToken, accountAfterSendSixthTimes.getEmailVerificationToken());
////    }
//
//}
