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
import portfolio2.mail.EmailMessage;
import portfolio2.mail.EmailService;

import java.time.LocalDateTime;

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
import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.config.UrlAndViewName.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class EmailVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogOutProcessForTest signUpAndLogOutProcessForTest;

    @Autowired
    private SignUpAndLogInProcessForTest signUpAndLogInProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @MockBean
    private EmailService emailService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 정상 링크

    @DisplayName("이메일 인증 - 정상 링크 - 로그아웃 상태 - 처음 인증 안 된 상태")
    @SignUpAndLoggedIn
    @Test
    void validLinkNotLoggedInNotFirstVerified() throws Exception{

        // 로그아웃
        logInAndOutProcessForTest.logOut();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        // 이메일 인증 링크
        String emailVerificationLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(emailVerificationLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());

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
        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        // 인증 이메일 전송 횟수 1회
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
        // 이메일 전송 1회
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }

    @DisplayName("이메일 인증 - 정상 링크 - 로그아웃 상태 - 처음 인증 된 상태")
    @SignUpAndLoggedIn
    @Test
    void validLinkNotLoggedInFirstVerified() throws Exception{

        // 로그아웃
        logInAndOutProcessForTest.logOut();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
        // 처음 인증 설정
        accountInDbToBeEmailVerified.setEmailFirstVerified(true);
        accountRepository.save(accountInDbToBeEmailVerified);

        // 이메일 인증 링크
        String emailVerificationLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(emailVerificationLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());

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
        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        // 인증 이메일 전송 횟수 1회
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
        // 이메일 전송 1회
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }


    @DisplayName("이메일 인증 - 정상 링크 - 본인 계정으로 로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void validLinkLoggedInByOwnAccount() throws Exception{

        // 유효 링크 찾기
        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
        // 처음 인증된 상태
        accountInDbToBeEmailVerified.setEmailFirstVerified(true);
        LocalDateTime beforeTokenGeneratedAt
                = accountInDbToBeEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt();
        // 상태값 저장
        accountRepository.save(accountInDbToBeEmailVerified);

        // 이후에 링크 발급받은 상태
        // 이메일 인증 링크
        String validLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(validLink))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
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
        assertEquals(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt(),
                beforeTokenGeneratedAt);
        // 인증 이메일 전송 횟수 1회
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
        // 이메일 전송 1회
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }

    @SignUpAndLoggedIn
    @DisplayName("이메일 인증 - 정상 링크 - 로그아웃 이후 다른 계정으로 회원가입 후 로그인 상태")
    @Test
    void logOutByOwnAccountAndLogInByNotOwnAccount() throws Exception{

        // 로그아웃
        logInAndOutProcessForTest.logOut();
        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        // 다른 새로운 계정으로 회원가입 후 로그인
        signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        // 로그인된 계정 확인
        logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2);

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
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_EMAIL, accountEmailVerified.getVerifiedEmail());

        assertTrue(accountEmailVerified.isEmailVerified());
        assertTrue(accountEmailVerified.isEmailFirstVerified());

        assertNull(accountEmailVerified.getEmailVerificationToken());
        assertNull(accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());

        // 나중에 회원가입하고 로그인했던 계정 존재 확인
        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
        assertNotNull(account2);
    }

    // 잘못된 링크 - 로그아웃 상태

    @DisplayName("이메일 인증 -  이메일이 틀린 경우 - 로그아웃 상태")
    @Test
    void inValidEmailWithLogOut() throws Exception{

        // 회원가입 후 로그아웃
        Account accountInDbToBeEmailVerified = signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        // 링크
        String invalidEmailLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + "notValid@email.com" +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(unauthenticated());

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }


    @DisplayName("이메일 인증 - 토큰 불일치 - 로그아웃 상태")
    @Test
    void inValidTokenWithLogOut() throws Exception{

        // 회원가입 후 로그아웃
        Account accountInDbToBeEmailVerified = signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        String invalidTokenLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken() + 'd';

        // 유효 링크 인증
        mockMvc.perform(get(invalidTokenLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(unauthenticated());

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }


    @DisplayName("이메일 인증 - 이상한 링크 - 로그아웃 상태")
    @Test
    void inValidLinkWithLogOut() throws Exception{

        // 회원가입 후 로그아웃
        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        String invalidLink = CHECK_EMAIL_VERIFICATION_LINK_URL + "?inval id";

        // 유효 링크 인증
        mockMvc.perform(get(invalidLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(unauthenticated());

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }

    // 내 계정으로 로그인 상태

    @DisplayName("이메일 인증 -  이메일이 틀린 경우 - 내 계정으로 로그인 상태")
    @Test
    void inValidEmailWithLogInByOwnAccount() throws Exception{

        Account accountInDbToBeEmailVerified = signUpAndLogInProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        // 링크
        String invalidEmailLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + "notValid@email.com" +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }


    @DisplayName("이메일 인증 - 토큰 불일치 - 내 계정으로 로그인 상태")
    @Test
    void inValidTokenWithLogInByOwnAccount() throws Exception{

        Account accountInDbToBeEmailVerified = signUpAndLogInProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        String invalidTokenLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken() + 'd';

        // 유효 링크 인증
        mockMvc.perform(get(invalidTokenLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }


    @DisplayName("이메일 인증 - 이상한 링크 - 내 계정으로 로그인 상태")
    @Test
    void inValidLinkWithLogInByOwnAccount() throws Exception{

        signUpAndLogInProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        String invalidLink = CHECK_EMAIL_VERIFICATION_LINK_URL + "?invalid";

        // 유효 링크 인증
        mockMvc.perform(get(invalidLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }


    // 다른 계정으로 로그인 상태

    @DisplayName("이메일 인증 -  이메일이 틀린 경우 - 다른 계정으로 로그인 상태")
    @Test
    void inValidEmailWithLogInByNotOwnAccount() throws Exception{

        signUpAndLogInProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Account accountInDbToBeEmailVerified
                = signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        // 링크
        String invalidEmailLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + "notValid@email.com" +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }


    @DisplayName("이메일 인증 - 토큰 불일치 - 다른 계정으로 로그인 상태")
    @Test
    void inValidTokenWithLogInByNotOwnAccount() throws Exception{

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        Account accountInDbToBeEmailVerified
                = signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        String invalidTokenLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken() + 'd';

        // 유효 링크 인증
        mockMvc.perform(get(invalidTokenLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }


    @DisplayName("이메일 인증 - 이상한 링크 - 다른 계정으로 로그인 상태")
    @Test
    void inValidLinkWithLogInByNotOwnAccount() throws Exception{

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        String invalidLink = CHECK_EMAIL_VERIFICATION_LINK_URL + "?in-valid";

        // 유효 링크 인증
        mockMvc.perform(get(invalidLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("invalidLinkError"))
                .andExpect(view().name(EMAIL_VERIFICATION_RESULT_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 이메일 인증 확인
        Account accountEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        assertNull(accountEmailVerified.getVerifiedEmail());

        assertFalse(accountEmailVerified.isEmailVerified());
        assertFalse(accountEmailVerified.isEmailFirstVerified());

        assertNotNull(accountEmailVerified.getEmailVerificationToken());
        assertEquals(TEST_EMAIL, accountEmailVerified.getEmailWaitingToBeVerified());

        assertNotNull(accountEmailVerified.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountEmailVerified.getCountOfSendingEmailVerificationEmail());
    }

}
