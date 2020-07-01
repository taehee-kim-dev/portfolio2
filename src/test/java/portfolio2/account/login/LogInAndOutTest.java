package portfolio2.account.login;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.testaccountinfo.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.CustomPrincipal;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.account.AccountService;
import portfolio2.service.account.SignUpService;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.testaccountinfo.TestAccountInfo.*;
import static portfolio2.config.UrlAndViewName.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LogInAndOutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LogInProcess logInProcess;

    @Autowired
    private SignUpAndLogInWithAccount1Process signUpAndLogInWithAccount1Process;

    @Autowired
    private SignUpAndLogOutWithAccount1Process signUpAndLogOutWithAccount1Process;

    @Autowired
    private SignUpAndLogOutWithAccount2Process signUpAndLogOutWithAccount2Process;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("로그인 화면 보여주기 - 비로그인 상태")
    @Test
    void showLogInPageWithoutLogIn() throws Exception{
        mockMvc.perform(get(LOGIN_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name(LOGIN_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그인 화면 보여주기 - 로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void showLogInPageWithLogIn() throws Exception{
        mockMvc.perform(get(LOGIN_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("올바른 아이디, 비밀번호로 로그인 성공")
    @SignUpAndLoggedIn
    @Test
    void logInSuccessWithCorrectIdAndPassword() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("올바른 인증된 이메일, 비밀번호로 로그인 성공 - 비로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void logInSuccessWithCorrectVerifiedEmailAndPassword() throws Exception {

        Account signedUpAccountInDb = accountRepository.findByUserId(TestAccountInfo.TEST_USER_ID);

        signedUpAccountInDb.setVerifiedEmail(signedUpAccountInDb.getEmailWaitingToBeVerified());
        signedUpAccountInDb.setEmailVerified(true);
        signedUpAccountInDb.setEmailWaitingToBeVerified(null);

        accountRepository.save(signedUpAccountInDb);

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("올바른 아이디, 비밀번호로 로그인 시도 - 로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void logInWithCorrectIdAndPasswordWithLogIn() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        String testUserId1 = "testUserId1";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(testUserId1)
                .nickname("testNickname1")
                .email("test1@email.com")
                .password("testPassword")
                .build();

        signUpService.signUp(signUpRequestDto);

        Account signedUpTestUserIdAccount = accountRepository.findByUserId(TEST_USER_ID);
        Account signedUpTestUserId1Account = accountRepository.findByUserId(testUserId1);

        assertNotNull(signedUpTestUserIdAccount);
        assertNotNull(signedUpTestUserId1Account);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL));
    }

    @DisplayName("틀린 아이디, 올바른 비밀번호로 로그인 실패")
    @SignUpAndLoggedIn
    @Test
    void logInFailureWithIncorrectUserIdAndCorrectPassword() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", "IncorrectUserId")
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("틀린 인증된 이메일, 올바른 비밀번호로 로그인 실패")
    @SignUpAndLoggedIn
    @Test
    void logInFailureWithIncorrectVerifiedEmailAndCorrectPassword() throws Exception {

        Account signedUpAccountInDb = accountRepository.findByUserId(TestAccountInfo.TEST_USER_ID);

        signedUpAccountInDb.setVerifiedEmail(signedUpAccountInDb.getEmailWaitingToBeVerified());
        signedUpAccountInDb.setEmailVerified(true);
        signedUpAccountInDb.setEmailWaitingToBeVerified(null);

        accountRepository.save(signedUpAccountInDb);

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", "IncorrectVerifiedEmail")
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증되지 않은 올바른 이메일, 올바른 비밀번호로 로그인 실패")
    @SignUpAndLoggedIn
    @Test
    void logInFailureWithCorrectUnverifiedEmailAndCorrectPassword() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }


    @DisplayName("올바른 아이디, 틀린 비밀번호로 로그인 실패")
    @SignUpAndLoggedIn
    @Test
    void logInFailureWithCorrectIdAndIncorrectPassword() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", "IncorrectPassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("올바른 인증된 이메일, 틀린 비밀번호로 로그인 실패")
    @SignUpAndLoggedIn
    @Test
    void logInFailureWithCorrectEmailAndIncorrectPassword() throws Exception {

        Account signedUpAccountInDb = accountRepository.findByUserId(TestAccountInfo.TEST_USER_ID);

        signedUpAccountInDb.setVerifiedEmail(signedUpAccountInDb.getEmailWaitingToBeVerified());
        signedUpAccountInDb.setEmailVerified(true);
        signedUpAccountInDb.setEmailWaitingToBeVerified(null);

        accountRepository.save(signedUpAccountInDb);

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", "IncorrectPassword")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그아웃 테스트")
    @SignUpAndLoggedIn
    @Test
    void logout() throws Exception {

        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(unauthenticated());
    }



    @DisplayName("인증 상태 테스트1")
    @SignUpAndLoggedIn
    @Test
    void authenticatedTest1() throws Exception {

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("인증 상태 테스트2")
    @Test
    void authenticatedTest2() throws Exception {

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TEST_USER_ID)
                .nickname(TEST_NICKNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        signUpService.signUp(signUpRequestDto);

        Authentication authentication1
                = SecurityContextHolder.getContext().getAuthentication();

//        System.out.println("*** 테스트 출력1 ***");
//        System.out.println(authentication1);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Authentication authentication2
                = SecurityContextHolder.getContext().getAuthentication();

//        System.out.println("*** 테스트 출력2 ***");
//        System.out.println(authentication2);
    }

    @DisplayName("비인증 상태 테스트1")
    @SignUpAndLoggedIn
    @Test
    void unauthenticatedTest1() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    @DisplayName("비인증 상태 테스트2")
    @Test
    void unauthenticatedTest2() throws Exception {

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TEST_USER_ID)
                .nickname(TEST_NICKNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        signUpService.signUp(signUpRequestDto);

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    @DisplayName("Account1로 로그인하고, Account2로 로그인하면 Account2로 로그인됨")
    @Test
    void duplicateLogInTest() throws Exception{
        // 두 계정 회원가입 후 로그아웃
        signUpAndLogOutWithAccount1Process.signUpAndLogOut();
        signUpAndLogOutWithAccount2Process.signUpAndLogOut();

        // 로그아웃 상태 확인
        Authentication authentication1
                = SecurityContextHolder.getContext().getAuthentication();

//        System.out.println("*** 테스트 출력1 ***");
//        System.out.println(authentication1);

        assertNull(authentication1);

        // Account1로 로그인 후 Account2로 로그인하면 Account2로 로그인 안됨
        logInProcess.logIn(TEST_USER_ID_1);
        logInProcess.logIn(TEST_USER_ID_2);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 두 계정 존재 확인
        Account account1 = accountRepository.findByUserId(TEST_USER_ID_1);
        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
        assertNotNull(account1);
        assertNotNull(account2);
    }

    @DisplayName("Account1, Account2로 회원가입하고 모두 로그아웃 후 Accout2로 로그인")
    @Test
    void signUpAndLogOutAndLogInTest() throws Exception{
        // 두 계정 회원가입 후 로그아웃
        signUpAndLogOutWithAccount1Process.signUpAndLogOut();
        signUpAndLogOutWithAccount2Process.signUpAndLogOut();

        // 로그아웃 상태 확인
        Authentication authentication1
                = SecurityContextHolder.getContext().getAuthentication();

//        System.out.println("*** 테스트 출력1 ***");
//        System.out.println(authentication1);

        assertNull(authentication1);

        // Account2로 로그인
        logInProcess.logIn(TEST_USER_ID_2);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        // 두 계정 존재 확인
        Account account1 = accountRepository.findByUserId(TEST_USER_ID_1);
        Account account2 = accountRepository.findByUserId(TEST_USER_ID_2);
        assertNotNull(account1);
        assertNotNull(account2);
    }

    @DisplayName("로그인 상태 테스트")
    @Test
    void logIn() throws Exception{
        signUpAndLogInWithAccount1Process.signUpAndLogIn();
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(TEST_USER_ID_1, customPrincipal.getSessionAccount().getUserId());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID_1));
    }

    @DisplayName("로그인 후 로그아웃 상태 테스트")
    @Test
    void logOut() throws Exception{
        signUpAndLogInWithAccount1Process.signUpAndLogIn();
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(TEST_USER_ID_1, customPrincipal.getSessionAccount().getUserId());

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    @DisplayName("로그아웃 상태 테스트")
    @Test
    void logOutTest() throws Exception{
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

}
