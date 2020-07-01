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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.testaccountinfo.SignUpAndLoggedIn;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.mail.EmailMessage;
import portfolio2.mail.EmailService;
import portfolio2.service.account.SignUpService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.testaccountinfo.TestAccountInfo.*;
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
    private SignUpService signUpService;

    @MockBean
    private EmailService emailService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("이메일 인증 - 정상 링크 - 처음 회원가입 시 - 비로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void emailVerificationTestWithValidLinkWhenFirstSignUpNotLoggedIn() throws Exception{

        // 로그아웃
        SecurityContextHolder.getContext().setAuthentication(null);

        // 유효 링크 찾기
        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        String validLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();
        System.out.println(validLink);

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

        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }

    @DisplayName("이메일 인증 - 정상 링크 - 처음 회원가입 시 - 본인 계정으로 로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void emailVerificationTestWithValidLinkWhenFirstSignUpLoggedInByOwnAccount() throws Exception{

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

        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }

    @DisplayName("이메일 인증 - 정상 링크 - 처음 회원가입 시 - 다른 계정으로 로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void emailVerificationTestWithValidLinkWhenFirstSignUpLoggedInByNotOwnAccount() throws Exception{

        // 로그아웃
        SecurityContextHolder.getContext().setAuthentication(null);

        // 다른 새로운 계정으로 회원가입
        String testUserId2 = "testUserId2";

        SignUpRequestDto signUpRequestDtoForNewAccount = SignUpRequestDto.builder()
                .userId(testUserId2)
                .nickname("testNickname2")
                .email("test2@email.com")
                .password("testPassword2")
                .build();





        // 유효 링크 찾기
        Account accountInDbToBeEmailVerified = accountRepository.findByUserId(TEST_USER_ID);

        String validLink = CHECK_EMAIL_VERIFICATION_LINK_URL +
                "?email=" + accountInDbToBeEmailVerified.getEmailWaitingToBeVerified() +
                "&token=" + accountInDbToBeEmailVerified.getEmailVerificationToken();
        System.out.println(validLink);

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

        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));
    }

}
