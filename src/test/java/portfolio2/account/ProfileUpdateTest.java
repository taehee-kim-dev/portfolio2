package portfolio2.account;

import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import portfolio2.SignUpAndLoggedIn;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ProfileUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("프로필 화면 보여주기 - 정상 userId")
    @SignUpAndLoggedIn
    @Test
    void showProfileView() throws Exception{

        mockMvc.perform(get("/account/profile/testUserId"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("accountInDb"))
                .andExpect(model().attributeExists("isOwner"))
                .andExpect(view().name("account/profile"));
    }

    @DisplayName("프로필 화면 보여주기 - 비정상 userId")
    @SignUpAndLoggedIn
    @Test
    void showProfileViewWithIncorrectUserId() throws Exception{

        Throwable exception = assertThrows(NestedServletException.class,
                () -> {mockMvc.perform(get("/account/profile/IncorrectTestUserId"));},
                "IncorrectTestUserId에 해당하는 사용자가 없습니다.");
    }
}
