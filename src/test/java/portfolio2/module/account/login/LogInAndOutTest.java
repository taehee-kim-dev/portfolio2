package portfolio2.module.account.login;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerified;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailNotVerifiedProcessForTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.*;
import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.LOGIN_URL;
import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.LOGIN_VIEW_NAME;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.HOME_URL;

/*
* ** 최종 결론 **
* 로그인은 무조건 SecurityContextHolder를 사용한 코드 직접 작성으로
* 로그인 인증 검증은 무조건 mockMvc로
* 로그아웃은 상관없으나 편의상 SecurityContextHolder를 사용한 코드 직접 작성으로
*
* 즉, 로그인, 로그아웃은 무조건 SecurityContextHolder를 사용한 코드 직접 작성으로
* 로그인 인증 검증은 무조건 mockMvc로.
*
* 코드로 이어서 중복 로그인하면 마지막 계정으로 인증정보 안바뀜.
* 즉, 코드로 로그인을 이후 인증 검증을 하되, 중간에 다른 계정으로 로그인 불가능.
* 단, 로그아웃 하고 새로 로그인한 상태로 mockMvc 검증을 하면 가능함.
* 한 번 mockMvc를 지나가면, 해당 인증 내용으로 고정됨.
*
* 최종 결론 : 로그인, 로그아웃은 SecurityContextHolder를 사용한 코드 직접 작성으로 하되,
* 검증은 맨 마지막에 mockMvc로 한다.
* */

@MockMvcTest
public class LogInAndOutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogInEmailNotVerifiedProcessForTest signUpAndLogInEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEmailNotVerifiedProcessForTest;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("테스트용")
    @Test
    void test() throws Exception{
        Account account = accountRepository.findById(1L).orElse(null);
        assertNull(account);
    }

    @DisplayName("로그인 화면 보여주기 - 비로그인 상태")
    @Test
    void showLogInPageWithoutLogIn() throws Exception{
        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        mockMvc.perform(get(LOGIN_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그인 화면 보여주기 - 로그인 상태")
    @SignUpAndLogInEmailNotVerified
    @Test
    void showLogInPageWithLogIn() throws Exception{
        mockMvc.perform(get(LOGIN_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("올바른 아이디, 비밀번호로 로그인 시도 - 로그인 상태")
    @SignUpAndLogInEmailNotVerified
    @Test
    void logInWithCorrectIdAndPasswordWithLogIn() throws Exception {

        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

//        System.out.println("***1");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

//        System.out.println("***2");
//        System.out.println(SecurityContextHolder.getContext());

        assertTrue(accountRepository.existsByUserId(TEST_USER_ID));
        assertTrue(accountRepository.existsByUserId(TEST_USER_ID_2));

        // 결론 : 로그인은 직접 작성한 코드로, 인증 검증은 mockMvc로.
    }

    @DisplayName("올바른 아이디, 비밀번호로 로그인 성공")
    @Test
    void logInSuccessWithCorrectIdAndPassword() throws Exception {

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("올바른 아이디, 틀린 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithCorrectIdAndIncorrectPassword() throws Exception {

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", "incorrectPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("올바른 인증된 이메일, 비밀번호로 로그인 성공")
    @Test
    void logInSuccessWithCorrectVerifiedEmailAndPassword() throws Exception {

        Account signedUpAccountInDb = signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        signedUpAccountInDb.setVerifiedEmail(signedUpAccountInDb.getEmailWaitingToBeVerified());
        accountRepository.save(signedUpAccountInDb);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("올바른 인증된 이메일, 틀린 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithCorrectEmailAndIncorrectPassword() throws Exception {

        Account signedUpAccountInDb = signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        signedUpAccountInDb.setVerifiedEmail(signedUpAccountInDb.getEmailWaitingToBeVerified());

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", "IncorrectPassword")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("틀린 아이디, 올바른 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithIncorrectUserIdAndCorrectPassword() throws Exception {

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(LOGIN_URL)
                .param("username", "incorrectUserId")
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("틀린 아이디, 틀린 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithIncorrectUserIdAndIncorrectPassword() throws Exception {

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(LOGIN_URL)
                .param("username", "incorrectUserId")
                .param("password", "incorrectPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("틀린 인증된 이메일, 올바른 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithIncorrectVerifiedEmailAndCorrectPassword() throws Exception {

        Account signedUpAccountInDb = signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        signedUpAccountInDb.setVerifiedEmail(signedUpAccountInDb.getEmailWaitingToBeVerified());

        mockMvc.perform(post(LOGIN_URL)
                .param("username", "incorrect@email.com")
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("틀린 인증된 이메일, 틀린 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithIncorrectVerifiedEmailAndIncorrectPassword() throws Exception {
        Account signedUpAccountInDb = signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        signedUpAccountInDb.setVerifiedEmail(signedUpAccountInDb.getEmailWaitingToBeVerified());

        mockMvc.perform(post(LOGIN_URL)
                .param("username", "incorrect@email.com")
                .param("password", "incorretPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증되지 않은 올바른 이메일, 올바른 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithCorrectUnverifiedEmailAndCorrectPassword() throws Exception {

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증되지 않은 올바른 이메일, 틀린 비밀번호로 로그인 실패")
    @Test
    void logInFailureWithCorrectUnverifiedEmailAndIncorrectPassword() throws Exception {

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", "incorrectPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그아웃 테스트")
    @SignUpAndLogInEmailNotVerified
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(unauthenticated());
    }
}
