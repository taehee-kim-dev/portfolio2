package portfolio2.account.login;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.*;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.*;
import static portfolio2.config.UrlAndViewName.*;

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

@SpringBootTest
@AutoConfigureMockMvc
public class LogInAndOutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SignUpAndLogInProcessForTest signUpAndLogInProcessForTest;

    @Autowired
    private SignUpAndLogOutProcessForTest signUpAndLogOutProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

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

    @DisplayName("올바른 아이디, 비밀번호로 로그인 시도 - 로그인 상태")
    @SignUpAndLoggedIn
    @Test
    void logInWithCorrectIdAndPasswordWithLogIn() throws Exception {

        signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

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

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

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

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

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

        Account signedUpAccountInDb = signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

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

        Account signedUpAccountInDb = signUpAndLogOutProcessForTest.signUpAndLogOutDefault();
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

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

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

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

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

        Account signedUpAccountInDb = signUpAndLogOutProcessForTest.signUpAndLogOutDefault();
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
        Account signedUpAccountInDb = signUpAndLogOutProcessForTest.signUpAndLogOutDefault();
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

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

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

        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_EMAIL)
                .param("password", "incorrectPassword")
                .with(csrf()))
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

    @DisplayName("아무것도 없는 상태 출력")
    @Test
    void nobodyPrint() throws Exception {

//        System.out.println("***");
//        System.out.println(SecurityContextHolder.getContext());
    }

    @DisplayName("로그아웃 상태 출력")
    @SignUpAndLoggedIn
    @Test
    void logoutPrint() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(unauthenticated());

//        System.out.println("***");
//        System.out.println(SecurityContextHolder.getContext());
    }

    // 로그인 상태 출력

    @DisplayName("로그인 상태 출력")
    @Test
    void logInPring() throws Exception{

        Account account1 = new Account();
        account1.setUserId(TEST_USER_ID);
        account1.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        accountRepository.save(account1);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***");
//        System.out.println(SecurityContextHolder.getContext());
        // 결론 : 한 mockMvc 테스트 안에서만 검증된다.
        // 그 다음에 출력하는것은 의미없다.
    }

    @DisplayName("로그인 후 홈으로 갔을 때 상태 출력")
    @Test
    void logInAndHomePrint() throws Exception{

        Account account1 = new Account();
        account1.setUserId(TEST_USER_ID);
        account1.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        accountRepository.save(account1);

        mockMvc.perform(post(LOGIN_URL)
                .param("username", TEST_USER_ID)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***1");
//        System.out.println(SecurityContextHolder.getContext());



        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***2");
//        System.out.println(SecurityContextHolder.getContext());
        // 결론 : 한 mockMvc 테스트 안에서만 검증된다.
        // 그 다음에 출력하는것은 의미없다.
    }

    @DisplayName("직접 코드로 로그인 후 홈으로 갔을 때 상태 출력")
    @Test
    void logInByCodingAndHomePrint() throws Exception{

        signUpAndLogInProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isSomeoneLoggedIn());
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

//        System.out.println("***1");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***2");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***3");
//        System.out.println(SecurityContextHolder.getContext());


        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***4");
//        System.out.println(SecurityContextHolder.getContext());

        // 결론 : 직접 코드로 로그인하면 여러개의 연속된 mockMvc에서 정상적으로 인증된다.
        // 하지만, 직접 작성한 코드 검증으로는 검증이 안된다.
    }

    @DisplayName("모두 코드로 연속으로 이어서 로그인 하면, 마지막 인증된 계정은 마지막에 로그인한 계정")
    @Test
    void duplicateLogInInRow() throws Exception{
        signUpAndLogInProcessForTest.signUpAndLogInDefault();

        assertTrue(logInAndOutProcessForTest.isSomeoneLoggedIn());
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // logInAndOutProcess.logOut();

//        System.out.println("***1");
//        System.out.println(SecurityContextHolder.getContext());
        signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());


//        System.out.println("***2");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***3");
//        System.out.println(SecurityContextHolder.getContext());

        // 코드로 작성한 로그인들 중간중간은 코드로 검증 된다.
        // 하지만, mockMvc를 넘어가면 코드로 검증이 안 된다.

        // mockMvc로 로그인 -> mockMvc를 넘어가면 무조건 인증정보 사라짐. mockMvc 검증이든, 직접 코드 작성 검증이든 인증정보 사라짐.
        // 직접 코드로 로그인 -> mockMvc를 넘어가도 mockMvc에서는 인증정보 안사라짐. 하지만, 직접 코드 작성 검증에서는 사라짐.


        /*
        * 최종 결론 :
        * 로그인은 무조건 직접 작성 코드(SecurityContextHolder 사용) 으로.
        * 로그인 인증 상태 검증은 무조건 mockMvc 검증으로.
        * */

    }

    @DisplayName("직접 코드로 로그인 후 홈으로 갔을 때 상태 출력 - 어노테이션 검증")
    @SignUpAndLoggedIn
    @Test
    void logInByCodingAndHomePrintWithAnnotation() throws Exception{

        assertTrue(logInAndOutProcessForTest.isSomeoneLoggedIn());
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

//        System.out.println("***1 존재함");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***2 존재하지 않음");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***3 존재하지 않음");
//        System.out.println(SecurityContextHolder.getContext());


        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***4 존재하지 않음");
//        System.out.println(SecurityContextHolder.getContext());

        // 결론 : 직접 코드로 로그인하면 여러개의 연속된 mockMvc에서 정상적으로 인증된다.
        // 하지만, 직접 작성한 코드 검증으로는 검증이 안된다.
    }

    @DisplayName("모두 코드로 연속으로 이어서 로그인 하면, 마지막 인증된 계정은 마지막에 로그인한 계정 - 어노테이션 검증")
    @SignUpAndLoggedIn
    @Test
    void duplicateLogInInRowWithAnnotaion() throws Exception{

        assertTrue(logInAndOutProcessForTest.isSomeoneLoggedIn());
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

//        System.out.println("***1 존재함");
//        System.out.println(SecurityContextHolder.getContext());

        signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        assertTrue(logInAndOutProcessForTest.isSomeoneLoggedIn());
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

//        System.out.println("***2 2로 존재함");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("***3 존재하지 않음");
//        System.out.println(SecurityContextHolder.getContext());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        // 코드로 작성한 로그인들 중간중간은 코드로 검증 된다.
        // 하지만, mockMvc를 넘어가면 코드로 검증이 안 된다.

        // mockMvc로 로그인 -> mockMvc를 넘어가면 무조건 인증정보 사라짐. mockMvc 검증이든, 직접 코드 작성 검증이든 인증정보 사라짐.
        // 직접 코드로 로그인 -> mockMvc를 넘어가도 mockMvc에서는 인증정보 안사라짐. 하지만, 직접 코드 작성 검증에서는 사라짐.
        // mockMvc 전에는 직접 코드 작성 검증으로 검증됨.


        /*
         * 최종 결론 :
         * 로그인은 무조건 직접 작성 코드(SecurityContextHolder 사용) 으로.
         * 로그인 인증 상태 검증은 무조건 mockMvc 검증으로.
         * */

    }

    @DisplayName("직접 코드 작성으로 로그아웃, 코드와 mockMvc 모두로 검증.")
    @SignUpAndLoggedIn
    @Test
    void codeLogOutAndTestWithCodeAndMockMvc() throws Exception{

        assertTrue(logInAndOutProcessForTest.isSomeoneLoggedIn());
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        logInAndOutProcessForTest.logOut();

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("*** 존재하지 않음");
//        System.out.println(SecurityContextHolder.getContext());
    }

    @DisplayName("mockMvc로 로그아웃, 코드와 mockMvc 모두로 검증.")
    @SignUpAndLoggedIn
    @Test
    void mockMvcLogOutAndTestWithCodeAndMockMvc() throws Exception{

        assertTrue(logInAndOutProcessForTest.isSomeoneLoggedIn());
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(unauthenticated());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("*** 존재하지 않음");
//        System.out.println(SecurityContextHolder.getContext());

        /*
        * 결론 : 코드로 로그아웃 하나, mockMvc로 로그아웃 하나 둘다 정상 검증.
        * */
    }

    @DisplayName("로그인 안하고 로그아웃")
    @Test
    void logOutWitNotLoggedIn() throws Exception{

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(unauthenticated());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

//        System.out.println("*** 존재하지 않음");
//        System.out.println(SecurityContextHolder.getContext());
    }

    @DisplayName("모두 직접 작성 코드로 테스트")
    @Test
    void onlyCodeTest() throws Exception{
        signUpAndLogInProcessForTest.signUpAndLogInDefault();
        logInAndOutProcessForTest.logOut();
        signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));
    }

    @DisplayName("로그인 로그아웃은 직접 작성 코드로, 검증만 마지막에 mockMvc로")
    @SignUpAndLoggedIn
    @Test
    void logInAndOutWithCodeAndValidationWithMockMvc() throws Exception{
        // logInAndOutProcess.logOut();
        signUpAndLogInProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));
    }
}
