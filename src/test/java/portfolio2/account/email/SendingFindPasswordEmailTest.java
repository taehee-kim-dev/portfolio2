package portfolio2.account.email;

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
import portfolio2.domain.email.EmailSendingProcess;
import portfolio2.email.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.*;
import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.controller.config.UrlAndViewName.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class SendingFindPasswordEmailTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogInEmailNotVerifiedProcessForTest signUpAndLogInEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @MockBean
    private EmailSendingProcess emailSendingProcess;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("비밀번호 찾기 페이지 보여주기 - 로그아웃 상태")
    @SignUpAndLoggedInEmailVerified
    @Test
    void showFindPasswordPageNotLoggedIn() throws Exception{
        // 로그아웃
        logInAndOutProcessForTest.logOut();
        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(FIND_PASSWORD_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("findPasswordRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(FIND_PASSWORD_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("비밀번호 찾기 페이지 보여주기 - 로그인 상태")
    @SignUpAndLoggedInEmailVerified
    @Test
    void showFindPasswordPageLoggedIn() throws Exception{
        // 로그인 확인
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(FIND_PASSWORD_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("findPasswordRequestDto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("비밀번호 찾기 메일 전송 - 정상입력 성공 - 이메일 인증된 상태 - 로그아웃 상태")
    @Test
    void success() throws Exception{
        signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();
        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(post(FIND_PASSWORD_URL)
                        .param("email", TEST_EMAIL)
                        .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(model().attributeExists("findPasswordRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(FIND_PASSWORD_VIEW_NAME))
                .andExpect(unauthenticated());

        Account accountAfterEmailSend = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(accountAfterEmailSend.getShowPasswordUpdatePageToken());

        verify(emailSendingProcess, times(1)).sendFindPasswordEmail(any(Account.class));
    }

    @DisplayName("비밀번호 찾기 메일 전송 - 정상입력 성공 - 이메일 인증된 상태 - 로그인 상태")
    @Test
    void redirectedToHomeWhenLoggedIn() throws Exception{
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        // 로그인 확인
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(post(FIND_PASSWORD_URL)
                .param("email", TEST_EMAIL)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("findPasswordRequestDto"))
                .andExpect(model().attributeDoesNotExist("successMessage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account accountAfterEmailSend = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSend.getShowPasswordUpdatePageToken());

        verify(emailSendingProcess, times(0)).sendFindPasswordEmail(any(Account.class));
    }

    // 입력값 오류

    @DisplayName("입력 오류 - 유효하지 않은 이메일 형식")
    @Test
    void invalidFormatEmail() throws Exception{
        signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(post(FIND_PASSWORD_URL)
                        .param("email", "test@email.")
                        .with(csrf()))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "findPasswordRequestDto",
                        "email",
                        "invalidFormatEmail"
                ))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("findPasswordRequestDto"))
                .andExpect(model().attributeDoesNotExist("successMessage"))
                .andExpect(status().isOk())
                .andExpect(view().name(FIND_PASSWORD_VIEW_NAME))
                .andExpect(unauthenticated());

        Account accountAfterEmailSend = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSend.getShowPasswordUpdatePageToken());

        verify(emailSendingProcess, times(0)).sendFindPasswordEmail(any(Account.class));
    }

    @DisplayName("입력 오류 - 존재하지 않거나 인증되지 않은 이메일")
    @Test
    void notExistingEmail() throws Exception{
        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(post(FIND_PASSWORD_URL)
                .param("email", TEST_EMAIL)
                .with(csrf()))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "findPasswordRequestDto",
                        "email",
                        "emailNotExists"
                ))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("findPasswordRequestDto"))
                .andExpect(model().attributeDoesNotExist("successMessage"))
                .andExpect(status().isOk())
                .andExpect(view().name(FIND_PASSWORD_VIEW_NAME))
                .andExpect(unauthenticated());

        Account accountAfterEmailSend = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSend.getShowPasswordUpdatePageToken());

        verify(emailSendingProcess, times(0)).sendFindPasswordEmail(any(Account.class));
    }
}
