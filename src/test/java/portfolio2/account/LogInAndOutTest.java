package portfolio2.account;

import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.TestAccountInfo;
import portfolio2.SignUpAndLoggedIn;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.SignUpRequestDto;
import portfolio2.service.AccountService;

import javax.servlet.http.HttpSession;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LogInAndOutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("올바른 아이디, 비밀번호로 로그인 성공")
    @SignUpAndLoggedIn
    @Test
    void logInWithCorrectIdAndPassword() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post("/login")
                .param("username", TestAccountInfo.CORRECT_TEST_USER_ID)
                .param("password", TestAccountInfo.CORRECT_TEST_PASSWORD)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(TestAccountInfo.CORRECT_TEST_USER_ID));
    }

    @DisplayName("올바른 이메일, 비밀번호로 로그인 성공")
    @SignUpAndLoggedIn
    @Test
    void logInWithCorrectEmailAndPassword() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post("/login")
                .param("username", TestAccountInfo.CORRECT_TEST_EMAIL)
                .param("password", TestAccountInfo.CORRECT_TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(TestAccountInfo.CORRECT_TEST_USER_ID));
    }


}
