package portfolio2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
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

    @DisplayName("회원가입 이메일 인증 - 입력값 정상")
    @Test
    void validEmailCheckToken() throws Exception {

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
                .param("email", newAccountInDb.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(authenticated());

        Account newAccountInDbAfterCompleteSignUp = accountRepository.findByUserId(newAccountToSignUp.getUserId());

        assertTrue(newAccountInDbAfterCompleteSignUp.isEmailVerified());

    }
}
