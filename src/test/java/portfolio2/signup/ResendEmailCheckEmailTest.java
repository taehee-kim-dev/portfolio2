package portfolio2.signup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.WithAccount;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ResendEmailCheckEmailTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    private final String TEST_USER_ID = "testUserId";

    @DisplayName("이메일 인증 이메일 재전송 화면 보여주기")
    @WithAccount(TEST_USER_ID)
    @Test
    void showResendEmailCheckEmailPage() throws Exception {

        mockMvc.perform(get("/check-email"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name("account/check-email"));

    }

    @DisplayName("이메일 인증 이메일 재전송시 Account에 기록되는지 확인")
    @WithAccount(TEST_USER_ID)
    @Test
    void countSendingEmailCheckEmail() throws Exception {

        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(beforeAccount.getEmailCheckToken());
        assertNotNull(beforeAccount.getEmailCheckTokenFirstGeneratedAt());
        assertEquals(beforeAccount.getSendCheckEmailCount(), 1);

        mockMvc.perform(get("/resend-confirm-email"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist("sessionAccount"))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(model().attributeDoesNotExist("error"));


        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));

        Account afterAccount = accountRepository.findByUserId(TEST_USER_ID);

        assertNotEquals(beforeAccount.getEmailCheckToken(), afterAccount.getEmailCheckToken());
        assertEquals(afterAccount.getSendCheckEmailCount(), 2);

    }

    @DisplayName("이메일 인증 이메일 12시간내 5회 초과로 전송 시 오류 발생")
    @WithAccount(TEST_USER_ID)
    @Test
    void sendingEmailCheckEmailMoreThanFiveTimesError() throws Exception {

        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(beforeAccount.getEmailCheckToken());
        assertNotNull(beforeAccount.getEmailCheckTokenFirstGeneratedAt());
        assertEquals(beforeAccount.getSendCheckEmailCount(), 1);

        for(int i = 2; i <= 5; i++){
            mockMvc.perform(get("/resend-confirm-email"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attributeDoesNotExist("sessionAccount"))
                    .andExpect(model().attributeDoesNotExist("email"))
                    .andExpect(model().attributeDoesNotExist("error"));

            verify(javaMailSender, times(i)).send(any(SimpleMailMessage.class));
        }

        Account afterAccount = accountRepository.findByUserId(TEST_USER_ID);

        assertNotEquals(beforeAccount.getEmailCheckToken(), afterAccount.getEmailCheckToken());
        assertEquals(afterAccount.getSendCheckEmailCount(), 5);


        // 6th times in 12 hours

        mockMvc.perform(get("/resend-confirm-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/check-email"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("error"));

        Account lastAccount = accountRepository.findByUserId(TEST_USER_ID);

        assertEquals(afterAccount.getEmailCheckToken(), lastAccount.getEmailCheckToken());
        assertEquals(lastAccount.getSendCheckEmailCount(), 5);

    }

    @DisplayName("이메일 인증 이메일 12시간 이후 5회 초과로 전송 시 이메일 재발송")
    @WithAccount(TEST_USER_ID)
    @Test
    void vaildSendingEmailCheckEmailMoreThanFiveTimes() throws Exception {

        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(beforeAccount.getEmailCheckToken());
        assertNotNull(beforeAccount.getEmailCheckTokenFirstGeneratedAt());
        assertEquals(beforeAccount.getSendCheckEmailCount(), 1);

        for(int i = 2; i <= 5; i++){
            mockMvc.perform(get("/resend-confirm-email"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attributeDoesNotExist("sessionAccount"))
                    .andExpect(model().attributeDoesNotExist("email"))
                    .andExpect(model().attributeDoesNotExist("error"));

            verify(javaMailSender, times(i)).send(any(SimpleMailMessage.class));
        }

        Account afterAccount = accountRepository.findByUserId(TEST_USER_ID);

        assertNotEquals(beforeAccount.getEmailCheckToken(), afterAccount.getEmailCheckToken());
        assertEquals(afterAccount.getSendCheckEmailCount(), 5);


        // 6th times after 12 hours

        afterAccount.setEmailCheckTokenFirstGeneratedAt(LocalDateTime.now().minusHours(12));
        accountRepository.save(afterAccount);

        mockMvc.perform(get("/resend-confirm-email"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist("sessionAccount"))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(model().attributeDoesNotExist("error"));

        verify(javaMailSender, times(6)).send(any(SimpleMailMessage.class));

        Account lastAccount = accountRepository.findByUserId(TEST_USER_ID);

        assertNotEquals(afterAccount.getEmailCheckToken(), lastAccount.getEmailCheckToken());
        assertNotEquals(afterAccount.getEmailCheckTokenFirstGeneratedAt(), lastAccount.getEmailCheckTokenFirstGeneratedAt());
        assertEquals(lastAccount.getSendCheckEmailCount(), 1);

    }

}
