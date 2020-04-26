package portfolio2.signup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.WithAccount;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpEmailCheckTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    private final String TEST_USER_ID = "testUserId";

    @DisplayName("회원가입 이메일 인증 - 유효한 링크 - 로그인 상태")
    @WithAccount(TEST_USER_ID)
    @Test
    void validEmailCheckLinkLoggedIn() throws Exception {

        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);

        mockMvc.perform(get("/check-email-token")
                .param("token", existingAccount.getEmailCheckToken())
                .param("email", existingAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(authenticated());

        Account newAccountInDbAfterCompleteSignUp = accountRepository.findByUserId(TEST_USER_ID);

        assertTrue(newAccountInDbAfterCompleteSignUp.isEmailVerified());

    }

    @DisplayName("회원가입 이메일 인증 - 유효한 링크 - 비로그인 상태")
    @WithAccount(TEST_USER_ID)
    @Test
    void validEmailCheckLinkNotLoggedIn() throws Exception {

        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);

        // logout
        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(get("/check-email-token")
                .param("token", existingAccount.getEmailCheckToken())
                .param("email", existingAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(authenticated());

        Account newAccountInDbAfterCompleteSignUp = accountRepository.findByUserId(TEST_USER_ID);

        assertTrue(newAccountInDbAfterCompleteSignUp.isEmailVerified());

    }


    @DisplayName("회원가입 이메일 인증 - 유효하지 않은 토큰")
    @Test
    void invalidEmailCheckToken() throws Exception {

        Account newAccountToSignUp = Account.builder()
                .userId("testUserId")
                .nickname("testNickname")
                .email("test@email.com")
                .password("testPassword")
                .build();

        mockMvc.perform(post("/sign-up")
                .param("userId", newAccountToSignUp.getUserId())
                .param("nickname", newAccountToSignUp.getNickname())
                .param("email", newAccountToSignUp.getEmail())
                .param("password", newAccountToSignUp.getPassword())
                .with(csrf()));

        Account newAccountInDb = accountRepository.findByUserId(newAccountToSignUp.getUserId());

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccountInDb.getEmailCheckToken() + "d")
                .param("email", newAccountInDb.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "invalidToken"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeDoesNotExist("nickname"))
                .andExpect(unauthenticated());

        Account newAccountInDbAfterCompleteSignUp = accountRepository.findByUserId(newAccountToSignUp.getUserId());

        assertFalse(newAccountInDbAfterCompleteSignUp.isEmailVerified());

    }

    @DisplayName("회원가입 이메일 인증 - 유효하지 않은 이메일")
    @Test
    void invalidEmailCheckEmail() throws Exception {

        Account newAccountToSignUp = Account.builder()
                .userId("testUserId")
                .nickname("testNickname")
                .email("test@email.com")
                .password("testPassword")
                .build();

        mockMvc.perform(post("/sign-up")
                .param("userId", newAccountToSignUp.getUserId())
                .param("nickname", newAccountToSignUp.getNickname())
                .param("email", newAccountToSignUp.getEmail())
                .param("password", newAccountToSignUp.getPassword())
                .with(csrf()));

        Account newAccountInDb = accountRepository.findByUserId(newAccountToSignUp.getUserId());

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccountInDb.getEmailCheckToken())
                .param("email", newAccountInDb.getEmail() + "d"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "invalidEmail"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeDoesNotExist("nickname"))
                .andExpect(unauthenticated());

        Account newAccountInDbAfterCompleteSignUp = accountRepository.findByUserId(newAccountToSignUp.getUserId());

        assertFalse(newAccountInDbAfterCompleteSignUp.isEmailVerified());

    }





}
