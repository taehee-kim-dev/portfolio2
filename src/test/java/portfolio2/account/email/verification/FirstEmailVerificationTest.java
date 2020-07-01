package portfolio2.account.email.verification;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.CustomPrincipal;
import portfolio2.mail.EmailMessage;
import portfolio2.mail.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.*;
import static portfolio2.config.UrlAndViewName.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class FirstEmailVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OnlySignUpProcessForTest onlySignUpProcessForTest;

    @Autowired
    private SignUpAndLogInProcessForTest signUpAndLogInProcessForTest;

    @MockBean
    private EmailService emailService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 정상 링크

    @DisplayName("이메일 인증 - 정상 링크 - 처음 회원가입 시 - 비로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void emailVerificationTestWithValidLinkWhenFirstSignUpNotLoggedIn() throws Exception{

        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
        
        // 로그아웃
        mockMvc.perform(get("/logout"));
        
        // 이메일 인증 링크
        String emailVerificationLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(emailVerificationLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
        
        // 인증 대기 이메일 인증된 이메일로 변경됨
        assertEquals(TEST_EMAIL, accountEmailVerified.getVerifiedEmail());
        // 이메일 인증됨으로 바뀜
        assertTrue(accountEmailVerified.isEmailVerified());
        // 이메일 처음 인증됨 설정
        assertTrue(accountEmailVerified.isEmailFirstVerified());
        // 이메일 인증 토큰 null로 됨
        assertNull(accountEmailVerified.getEmailVerificationToken());
        // 인증 대기 이메일 null로 됨
        assertNull(accountEmailVerified.getEmailWaitingToBeVerified());
        // 토큰 발행 시간 존재
        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
        // 인증 이메일 전송 횟수 1회
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
        // 이메일 전송 1회
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }

    @DisplayName("이메일 인증 - 정상 링크 - 처음 회원가입 시 - 본인 계정으로 로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void emailVerificationTestWithValidLinkWhenFirstSignUpLoggedInByOwnAccount() throws Exception{

        // 유효 링크 찾기
        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        // 이메일 인증 링크
        String validLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(validLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        // 인증 대기 이메일 인증된 이메일로 변경됨
        assertEquals(TEST_EMAIL, accountEmailVerified.getVerifiedEmail());
        // 이메일 인증됨으로 바뀜
        assertTrue(accountEmailVerified.isEmailVerified());
        // 이메일 처음 인증됨 설정
        assertTrue(accountEmailVerified.isEmailFirstVerified());
        // 이메일 인증 토큰 null로 됨
        assertNull(accountEmailVerified.getEmailVerificationToken());
        // 인증 대기 이메일 null로 됨
        assertNull(accountEmailVerified.getEmailWaitingToBeVerified());
        // 토큰 발행 시간 존재
        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
        // 인증 이메일 전송 횟수 1회
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
        // 이메일 전송 1회
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }

    @SignUpAndLoggedIn
    @DisplayName("이메일 인증 - 정상 링크 - 처음 회원가입 시 - 로그아웃 이후 다른 계정으로 회원가입 후 로그인 상태")
    @Test
    void logOutByOwnAccountAndLogInByNotOwnAccount() throws Exception{

        mockMvc.perform(get("/logout"));

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 다른 새로운 계정으로 회원가입 후 로그인
        signUpAndLogInProcessForTest.signUpAndLogIn(2);

        // 로그인된 계정 확인
        CustomPrincipal customPrincipal
                = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        assertEquals(TEST_USER_ID_2, customPrincipal.getSessionAccount().getUserId());


        // 유효 링크 찾기
        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        String validLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(validLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_EMAIL, accountEmailVerified.getVerifiedEmail());

        assertTrue(accountEmailVerified.isEmailVerified());
        assertTrue(accountEmailVerified.isEmailFirstVerified());

        assertNull(accountEmailVerified.getEmailVerificationToken());
        assertNull(accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());

        // 나중에 회원가입하고 로그인했던 계정 존재 확인
        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
        assertNotNull(account2);
    }

    @SignUpAndLoggedIn
    @DisplayName("이메일 인증 - 정상 링크 - 처음 회원가입 시 - 로그인된 상태에서 다른 계정으로 회원가입 후 로그인 상태")
    @Test
    void logInByOwnAccountAndLogInByNotOwnAccount() throws Exception{

        // 로그인 상태

        // 다른 새로운 계정으로 회원가입 후 로그인
        signUpAndLogInProcessForTest.signUpAndLogIn(2);

        // 로그인된 계정 확인
        CustomPrincipal customPrincipal
                = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        assertEquals(TEST_USER_ID_2, customPrincipal.getSessionAccount().getUserId());


        // 유효 링크 찾기
        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        String validLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(validLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_EMAIL, accountEmailVerified.getVerifiedEmail());

        assertTrue(accountEmailVerified.isEmailVerified());
        assertTrue(accountEmailVerified.isEmailFirstVerified());

        assertNull(accountEmailVerified.getEmailVerificationToken());
        assertNull(accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());

        // 나중에 회원가입하고 로그인했던 계정 존재 확인
        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
        assertNotNull(account2);
    }

    // 잘못된 링크 - 로그아웃 상태

    @DisplayName("이메일 인증 - 대기중이지 않은 이메일일 경우 - 로그아웃 상태")
    @Test
    void inValidEmailWithLogOut() throws Exception{

        Account accountInDbToBeEmailVerified = onlySignUpProcessForTest.signUpDefault();

        // 로그아웃 상태 확인
        Authentication authentication1
                = SecurityContextHolder.getContext().getAuthentication();

        assertNull(authentication1);

        // 링크
        String invalidEmailLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + "notValid@email.com" +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 토큰 불일치 - 로그아웃 상태")
//    @Test
//    void inValidTokenWithLogOut() throws Exception{
//
//        // 로그아웃
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        // 로그아웃 상태 확인
//        Authentication authentication1
//                = SecurityContextHolder.getContext().getAuthentication();
//
//        assertNull(authentication1);
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidTokenLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
//                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
//                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken() + 'd';
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidTokenLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(unauthenticated());
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//    }
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 이상한 링크 - 로그아웃 상태")
//    @Test
//    void inValidLinkWithLogOut() throws Exception{
//
//        // 로그아웃
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        // 로그아웃 상태 확인
//        Authentication authentication1
//                = SecurityContextHolder.getContext().getAuthentication();
//
//        assertNull(authentication1);
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidLink = CHECK_EMAIL_VERIFICATION_LINK_URL + "?invalid";
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(unauthenticated());
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//    }
//
//    // 잘못된 링크 - 내 계정으로 로그인 상태
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 대기중이지 않은 이메일일 경우 - 내 계정으로 로그인 상태")
//    @Test
//    void inValidEmailWithLogInByOwnAccount() throws Exception{
//
//        // 내 계정으로 로그인 상태 확인
//        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        assertEquals(TEST_USER_ID, customPrincipal.getSessionAccount().getUserId());
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidEmailLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
//                "?email=" + "notValid@email.com" +
//                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidEmailLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//    }
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 토큰 불일치 - 내 계정으로 로그인 상태")
//    @Test
//    void inValidTokenWithLogInByOwnAccount() throws Exception{
//
//        // 내 계정으로 로그인 상태 확인
//        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        assertEquals(TEST_USER_ID, customPrincipal.getSessionAccount().getUserId());
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidTokenLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
//                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
//                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken() + 'd';
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidTokenLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//    }
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 이상한 링크 - 내 계정으로 로그인 상태")
//    @Test
//    void inValidLinkWithLogInByOwnAccount() throws Exception{
//
//        // 내 계정으로 로그인 상태 확인
//        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        assertEquals(TEST_USER_ID, customPrincipal.getSessionAccount().getUserId());
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidLink = CHECK_EMAIL_VERIFICATION_LINK_URL + "?invalid";
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(authenticated().withUsername(TEST_USER_ID));
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//    }
//
//    // 잘못된 링크 - 다른 계정으로 로그인 상태
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 대기중이지 않은 이메일일 경우 - 다른 계정으로 로그인 상태")
//    @Test
//    void inValidEmailWithLogInByNotOwnAccount() throws Exception{
//
//        // 로그아웃 후 로그아웃 상태 확인
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//
//        // 다른 계정으로 회원가입 후 로그인
//        signUpAndLogInWithAccount2Process.signUpAndLogIn();
//
//        // 내 다른 계정으로 로그인 상태 확인
//        CustomPrincipal customPrincipal
//                = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        assertEquals(TEST_USER_ID_2, customPrincipal.getSessionAccount().getUserId());
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidEmailLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
//                "?email=" + "notValid@email.com" +
//                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidEmailLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(authenticated().withUsername(TEST_USER_ID_2));
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//
//        // 다른계정 회원가입 상태 확인
//        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
//        assertNotNull(account2);
//        assertNull(account2.getVerifiedEmail());
//        assertFalse(account2.isEmailVerified());
//    }
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 토큰 불일치 - 다른 계정으로 로그인 상태")
//    @Test
//    void inValidTokenWithLogInByNotOwnAccount() throws Exception{
//
//        // 로그아웃 후 로그아웃 상태 확인
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//
//        // 다른 계정으로 회원가입 후 로그인
//        signUpAndLogInWithAccount2Process.signUpAndLogIn();
//
//        // 내 다른 계정으로 로그인 상태 확인
//        CustomPrincipal customPrincipal
//                = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        assertEquals(TEST_USER_ID_2, customPrincipal.getSessionAccount().getUserId());
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidTokenLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
//                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
//                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken() + 'd';
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidTokenLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(authenticated().withUsername(TEST_USER_ID_2));
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//
//        // 다른계정 회원가입 상태 확인
//        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
//        assertNotNull(account2);
//        assertNull(account2.getVerifiedEmail());
//        assertFalse(account2.isEmailVerified());
//    }
//
//    @SignUpAndLoggedIn
//    @DisplayName("이메일 인증 - 이상한 링크 - 다른 계정으로 로그인 상태")
//    @Test
//    void inValidLinkWithLogInByNotOwnAccount() throws Exception{
//
//        // 로그아웃 후 로그아웃 상태 확인
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//
//        // 다른 계정으로 회원가입 후 로그인
//        signUpAndLogInWithAccount2Process.signUpAndLogIn();
//
//        // 내 다른 계정으로 로그인 상태 확인
//        CustomPrincipal customPrincipal
//                = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        assertEquals(TEST_USER_ID_2, customPrincipal.getSessionAccount().getUserId());
//
//        // 유효 링크 찾기
//        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        String invalidLink = CHECK_EMAIL_VERIFICATION_LINK_URL + "?invalid";
//
//        // 유효 링크 인증
//        mockMvc.perform(get(invalidLink))
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors())
//                .andExpect(model().attributeExists("invalidLinkError"))
//                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
//                .andExpect(authenticated().withUsername(TEST_USER_ID_2));
//
//        // 이메일 인증 확인
//        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
//
//        assertNull(accountEmailVerified.getVerifiedEmail());
//
//        assertFalse(accountEmailVerified.isEmailVerified());
//        assertFalse(accountEmailVerified.isEmailFirstVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationToken());
//        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());
//
//        assertNotNull(accountEmailVerified.getEmailVerificationTokenFirstGeneratedAt());
//        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
//
//        // 다른계정 회원가입 상태 확인
//        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
//        assertNotNull(account2);
//        assertNull(account2.getVerifiedEmail());
//        assertFalse(account2.isEmailVerified());
//    }

}
