package portfolio2.module.account.email;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailNotVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLoggedInEmailNotVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.infra.email.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.*;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@MockMvcTest
public class ShowPasswordUpdatePageTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEMailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogInEmailNotVerifiedProcessForTest signUpAndLogInEmailNotVerifiedProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @MockBean
    private EmailService emailService;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 정상 링크

    @DisplayName("비밀번호 변경 페이지 보여주기 - 정상 링크 - 로그아웃 상태 - 인증된 이메일")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void validLinkNotLoggedIn() throws Exception{

        // 로그아웃
        logInAndOutProcessForTest.logOut();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        beforeAccount.generateShowPasswordUpdatePageToken();
        accountRepository.save(beforeAccount);

        Account accountInDbToShowPasswordUpdatePage = accountRepository.findByUserId(TEST_USER_ID);

        // 이메일 인증 링크
        String validShowPasswordUpdatePageLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + accountInDbToShowPasswordUpdatePage.getVerifiedEmail() +
                "&token=" + accountInDbToShowPasswordUpdatePage.getShowPasswordUpdatePageToken();

        // 유효 링크 인증
        mockMvc.perform(get(validShowPasswordUpdatePageLink))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(ERROR_TITLE))
                .andExpect(model().attributeDoesNotExist(ERROR_CONTENT))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 비밀번호 변경 페이지 보여주기 토큰 삭제 확인
        Account showedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(showedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    @DisplayName("비밀번호 변경 페이지 보여주기 - 정상 링크 - 본인 계정으로 로그인 상태")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void validLinkLoggedInByOwnAccount() throws Exception{

        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        beforeAccount.generateShowPasswordUpdatePageToken();
        accountRepository.save(beforeAccount);

        Account accountInDbToShowPasswordUpdatePage = accountRepository.findByUserId(TEST_USER_ID);

        // 이메일 인증 링크
        String validShowPasswordUpdatePageLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + accountInDbToShowPasswordUpdatePage.getVerifiedEmail() +
                "&token=" + accountInDbToShowPasswordUpdatePage.getShowPasswordUpdatePageToken();

        // 유효 링크 인증
        mockMvc.perform(get(validShowPasswordUpdatePageLink))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(ERROR_TITLE))
                .andExpect(model().attributeDoesNotExist(ERROR_CONTENT))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 비밀번호 변경 페이지 보여주기 토큰 삭제 확인
        Account showedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(showedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }

    @SignUpAndLoggedInEmailNotVerified
    @DisplayName("이메일 인증 - 정상 링크 - 로그아웃 이후 다른 계정으로 회원가입 후 로그인 상태")
    @Test
    void logOutByOwnAccountAndLogInByNotOwnAccount() throws Exception{

        // 로그아웃
        logInAndOutProcessForTest.logOut();
        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        beforeAccount.generateShowPasswordUpdatePageToken();
        accountRepository.save(beforeAccount);

        // 다른 새로운 계정으로 회원가입 후 로그인
        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        // 로그인된 계정 확인
        logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2);

        Account accountInDbToShowPasswordUpdatePage = accountRepository.findByUserId(TEST_USER_ID);

        // 이메일 인증 링크
        String validShowPasswordUpdatePageLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + accountInDbToShowPasswordUpdatePage.getVerifiedEmail() +
                "&token=" + accountInDbToShowPasswordUpdatePage.getShowPasswordUpdatePageToken();

        // 유효 링크 인증
        mockMvc.perform(get(validShowPasswordUpdatePageLink))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(ERROR_TITLE))
                .andExpect(model().attributeDoesNotExist(ERROR_CONTENT))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 비밀번호 변경 페이지 보여주기 토큰 삭제 확인
        Account showedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(showedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());

        // 나중에 회원가입하고 로그인했던 계정 존재 확인
        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
        assertNotNull(account2);
    }

    // 잘못된 링크 - 로그아웃 상태

    @DisplayName("이메일 인증 -  이메일이 틀린 경우 - 로그아웃 상태")
    @Test
    void inValidEmailWithLogOut() throws Exception{

        // 회원가입 후 로그아웃
        Account accountInDbToShowPasswordUpdatePage = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        Account accountToFindToken = accountRepository.findByUserId(TEST_USER_ID);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + "notValid@email.com" +
                "&token=" + accountToFindToken.getShowPasswordUpdatePageToken();

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(unauthenticated());

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    @DisplayName("이메일 인증 - 토큰 불일치 - 로그아웃 상태")
    @Test
    void inValidTokenWithLogOut() throws Exception{

        // 회원가입 후 로그아웃
        Account accountInDbToShowPasswordUpdatePage = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        Account accountToFindToken = accountRepository.findByUserId(TEST_USER_ID);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + accountToFindToken.getVerifiedEmail() +
                "&token=" + accountToFindToken.getShowPasswordUpdatePageToken() + 'd';

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(unauthenticated());

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    @DisplayName("이메일 인증 - 이상한 링크 - 로그아웃 상태")
    @Test
    void inValidLinkWithLogOut() throws Exception{

        // 회원가입 후 로그아웃
        Account accountInDbToShowPasswordUpdatePage = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        // 로그아웃 확인
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL + "?in valid";

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(unauthenticated());

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }

    // 내 계정으로 로그인 상태

    @DisplayName("이메일 인증 -  이메일이 틀린 경우 - 내 계정으로 로그인 상태")
    @Test
    void inValidEmailWithLogInByOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage = signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        Account accountToFindToken = accountRepository.findByUserId(TEST_USER_ID);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + "notValid@email.com" +
                "&token=" + accountToFindToken.getShowPasswordUpdatePageToken();

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    @DisplayName("이메일 인증 - 토큰 불일치 - 내 계정으로 로그인 상태")
    @Test
    void inValidTokenWithLogInByOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage = signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        Account accountToFindToken = accountRepository.findByUserId(TEST_USER_ID);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + accountToFindToken.getVerifiedEmail() +
                "&token=" + accountToFindToken.getShowPasswordUpdatePageToken() + 'd';

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    @DisplayName("이메일 인증 - 이상한 링크 - 내 계정으로 로그인 상태")
    @Test
    void inValidLinkWithLogInByOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage = signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL + "?in valid";

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    // 다른 계정으로 로그인 상태

    @DisplayName("이메일 인증 -  이메일이 틀린 경우 - 다른 계정으로 로그인 상태")
    @Test
    void inValidEmailWithLogInByNotOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage
                = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        Account accountToFindToken = accountRepository.findByUserId(TEST_USER_ID);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + "notValid@email.com" +
                "&token=" + accountToFindToken.getShowPasswordUpdatePageToken();

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    @DisplayName("이메일 인증 - 토큰 불일치 - 다른 계정으로 로그인 상태")
    @Test
    void inValidTokenWithLogInByNotOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage
                = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        Account accountToFindToken = accountRepository.findByUserId(TEST_USER_ID);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL +
                "?email=" + accountToFindToken.getVerifiedEmail() +
                "&token=" + accountToFindToken.getShowPasswordUpdatePageToken() + 'd';

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }


    @DisplayName("이메일 인증 - 이상한 링크 - 다른 계정으로 로그인 상태")
    @Test
    void inValidLinkWithLogInByNotOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage
                = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        // 링크
        String invalidEmailLink = CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL + "?in-valid";

        // 유효 링크 인증
        mockMvc.perform(get(invalidEmailLink))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("이메일 인증 - email parameter가 null - 다른 계정으로 로그인 상태")
    @Test
    void emailParameterNullWithLogInByNotOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage
                = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        // 유효 링크 인증
        mockMvc.perform(get(CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL)
                .param("email", (String)null)
                .param("token", accountInDbToShowPasswordUpdatePage.getShowPasswordUpdatePageToken()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("이메일 인증 - token parameter가 null - 다른 계정으로 로그인 상태")
    @Test
    void tokenParameterNullWithLogInByNotOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage
                = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        // 유효 링크 인증
        mockMvc.perform(get(CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL)
                .param("email", accountInDbToShowPasswordUpdatePage.getVerifiedEmail())
                .param("token", (String)null))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("이메일 인증 - email, token parameter가 null - 다른 계정으로 로그인 상태")
    @Test
    void emailTokenParameterNullWithLogInByNotOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage
                = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.generateShowPasswordUpdatePageToken();
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        // 유효 링크 인증
        mockMvc.perform(get(CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL)
                .param("email", (String)null)
                .param("token", (String)null))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("이메일 인증 - 이메일에 해당하는 계정은 있지만, 해당 계정의 토큰값이 null - 다른 계정으로 로그인 상태")
    @Test
    void accountTokenNullWithLogInByNotOwnAccount() throws Exception{

        Account accountInDbToShowPasswordUpdatePage
                = signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        accountInDbToShowPasswordUpdatePage.setVerifiedEmail(accountInDbToShowPasswordUpdatePage.getEmailWaitingToBeVerified());
        accountInDbToShowPasswordUpdatePage.setEmailVerified(true);
        accountInDbToShowPasswordUpdatePage.setShowPasswordUpdatePageToken(null);
        accountRepository.save(accountInDbToShowPasswordUpdatePage);

        // 유효 링크 인증
        mockMvc.perform(get(CHECK_SHOW_PASSWORD_UPDATE_PAGE_LINK_URL)
                .param("email", accountInDbToShowPasswordUpdatePage.getVerifiedEmail())
                .param("token", "abcde"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 토큰 확인
        Account notShowedPasswordUpdatePageAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(notShowedPasswordUpdatePageAccount.getShowPasswordUpdatePageToken());
    }

}
