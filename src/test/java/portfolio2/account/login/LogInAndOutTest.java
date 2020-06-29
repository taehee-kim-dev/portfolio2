//package portfolio2.account.login;
//
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import portfolio2.account.testaccountinfo.TestAccountInfo;
//import portfolio2.account.testaccountinfo.SignUpAndLoggedIn;
//import portfolio2.domain.account.AccountRepository;
//import portfolio2.dto.account.SignUpRequestDto;
//import portfolio2.service.account.AccountService;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
//import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class LogInAndOutTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @AfterEach
//    void afterEach(){
//        accountRepository.deleteAll();
//    }
//
//    @DisplayName("올바른 아이디, 비밀번호로 로그인 성공")
//    @SignUpAndLoggedIn
//    @Test
//    void logInSuccessWithCorrectIdAndPassword() throws Exception {
//
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        mockMvc.perform(post("/login")
//                .param("username", TestAccountInfo.CORRECT_TEST_USER_ID)
//                .param("password", TestAccountInfo.CORRECT_TEST_PASSWORD)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/"))
//                .andExpect(authenticated().withUsername(TestAccountInfo.CORRECT_TEST_USER_ID));
//    }
//
//    @DisplayName("올바른 이메일, 비밀번호로 로그인 성공")
//    @SignUpAndLoggedIn
//    @Test
//    void logInSuccessWithCorrectEmailAndPassword() throws Exception {
//
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        mockMvc.perform(post("/login")
//                .param("username", TestAccountInfo.CORRECT_TEST_EMAIL)
//                .param("password", TestAccountInfo.CORRECT_TEST_PASSWORD)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/"))
//                .andExpect(authenticated().withUsername(TestAccountInfo.CORRECT_TEST_USER_ID));
//    }
//
//    @DisplayName("틀린 아이디 또는 이메일, 올바른 비밀번호로 로그인 실패")
//    @SignUpAndLoggedIn
//    @Test
//    void logInFailureWithIncorrectIdOrEmailAndCorrectPassword() throws Exception {
//
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        mockMvc.perform(post("/login")
//                .param("username", "IncorrectIdOrEmail")
//                .param("password", TestAccountInfo.CORRECT_TEST_PASSWORD)
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login?error"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("올바른 아이디, 틀린 비밀번호로 로그인 실패")
//    @SignUpAndLoggedIn
//    @Test
//    void logInFailureWithCorrectIdAndIncorrectPassword() throws Exception {
//
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        mockMvc.perform(post("/login")
//                .param("username", TestAccountInfo.CORRECT_TEST_USER_ID)
//                .param("password", "IncorrectPassword")
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login?error"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("올바른 이메일, 틀린 비밀번호로 로그인 실패")
//    @SignUpAndLoggedIn
//    @Test
//    void logInFailureWithCorrectEmailAndIncorrectPassword() throws Exception {
//
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        mockMvc.perform(post("/login")
//                .param("username", TestAccountInfo.CORRECT_TEST_EMAIL)
//                .param("password", "IncorrectPassword")
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login?error"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("로그아웃 테스트")
//    @SignUpAndLoggedIn
//    @Test
//    void logout() throws Exception {
//
//        mockMvc.perform(post("/logout")
//                .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("인증 상태 테스트")
//    @SignUpAndLoggedIn
//    @Test
//    void authenticatedTest() throws Exception {
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                .andExpect(authenticated());
//    }
//
//    @DisplayName("비인증 상태 테스트1")
//    @SignUpAndLoggedIn
//    @Test
//    void unauthenticatedTest1() throws Exception {
//
//        SecurityContextHolder.getContext().setAuthentication(null);
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("비인증 상태 테스트2")
//    @Test
//    void unauthenticatedTest2() throws Exception {
//
//        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
//                .userId("testUserId2")
//                .nickname("testNickname2")
//                .email("test2@email.com")
//                .password("testPassword2")
//                .build();
//
//        // accountService.saveNewAccount(signUpRequestDto2);
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                .andExpect(unauthenticated());
//    }
//
//}
