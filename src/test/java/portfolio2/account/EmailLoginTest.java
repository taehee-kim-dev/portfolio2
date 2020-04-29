package portfolio2.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.SignUpAndLoggedIn;
import portfolio2.TestAccountInfo;
import portfolio2.controller.account.AccountSettingController;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.account.SignUpRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
public class EmailLoginTest {

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

    @DisplayName("이메일 로그인 링크 전송 화면 보여주기")
    @Test
    void showSendingLoginLinkEmailView() throws Exception{

        mockMvc.perform(get("/email-login"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sendEmailLoginLinkRequestDto"))
                .andExpect(view().name("account/email-login"));

    }

    @DisplayName("이메일 로그인 링크 이메일 전송 요청 - 정상 이메일, 1회 요청")
    @SignUpAndLoggedIn
    @Test
    void sendLoginLinkToValidEmailOneTimeSuccess() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        Account existingAccountBeforeSendEmail = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(0, existingAccountBeforeSendEmail.getSendLoginEmailCount());
        assertNull(existingAccountBeforeSendEmail.getEmailLoginToken());
        assertNull(existingAccountBeforeSendEmail.getEmailLoginTokenFirstGeneratedAt());

        mockMvc.perform(post("/email-login")
                .param("email", TestAccountInfo.CORRECT_TEST_EMAIL)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/email-login"))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attribute("successMessage", "로그인 링크를 이메일로 발송했습니다."))
                .andExpect(unauthenticated());

        Account existingAccountAfterSendEmail = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(1, existingAccountAfterSendEmail.getSendLoginEmailCount());
        assertNotNull(existingAccountAfterSendEmail.getEmailLoginToken());
        assertNotNull(existingAccountAfterSendEmail.getEmailLoginTokenFirstGeneratedAt());

        verify(javaMailSender, times(2)).send(any(SimpleMailMessage.class));

    }

    @DisplayName("이메일 로그인 링크 이메일 전송 요청 - 1회이상 연속 요청")
    @SignUpAndLoggedIn
    @Test
    void sendLoginLinkToValidEmailRepeatedly() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        Account existingAccountBeforeSendEmail1 = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(0, existingAccountBeforeSendEmail1.getSendLoginEmailCount());
        assertNull(existingAccountBeforeSendEmail1.getEmailLoginToken());
        assertNull(existingAccountBeforeSendEmail1.getEmailLoginTokenFirstGeneratedAt());

        String beforeEmailLoginToken = existingAccountBeforeSendEmail1.getEmailLoginToken();
        LocalDateTime beforeEmailLoginTokenFirstGeneratedAt
                = existingAccountBeforeSendEmail1.getEmailLoginTokenFirstGeneratedAt();


        // Up to 3 times in 12 hours.

        for(int time = 1; time <= 3; time++){

            mockMvc.perform(post("/email-login")
                    .param("email", TestAccountInfo.CORRECT_TEST_EMAIL)
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/email-login"))
                    .andExpect(model().hasNoErrors())
                    .andExpect(flash().attribute("successMessage", "로그인 링크를 이메일로 발송했습니다."))
                    .andExpect(unauthenticated());

            Account existingAccountAfterSendEmail1 = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

            assertEquals(time, existingAccountAfterSendEmail1.getSendLoginEmailCount());
            assertNotEquals(beforeEmailLoginToken, existingAccountAfterSendEmail1.getEmailLoginToken());

            if(beforeEmailLoginTokenFirstGeneratedAt != null){
                assertEquals(existingAccountAfterSendEmail1.getEmailLoginTokenFirstGeneratedAt(),
                        beforeEmailLoginTokenFirstGeneratedAt);
            }

            verify(javaMailSender, times(1 + time)).send(any(SimpleMailMessage.class));

            beforeEmailLoginToken = existingAccountAfterSendEmail1.getEmailLoginToken();
            beforeEmailLoginTokenFirstGeneratedAt = existingAccountAfterSendEmail1.getEmailLoginTokenFirstGeneratedAt();
        }


        // Over 3 times in 12 hours.

        mockMvc.perform(post("/email-login")
                .param("email", TestAccountInfo.CORRECT_TEST_EMAIL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-login"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("emailCannotSendError", "로그인 링크 이메일은 12시간동안 3번만 보낼 수 있습니다."))
                .andExpect(unauthenticated());

        Account existingAccountAfterSendEmail = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(3, existingAccountAfterSendEmail.getSendLoginEmailCount());
        assertEquals(beforeEmailLoginToken, existingAccountAfterSendEmail.getEmailLoginToken());


        assertEquals(existingAccountAfterSendEmail.getEmailLoginTokenFirstGeneratedAt(),
                    beforeEmailLoginTokenFirstGeneratedAt);


        verify(javaMailSender, times(4)).send(any(SimpleMailMessage.class));



        Account existingAccountBeforeSendEmail2 = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);
        LocalDateTime timeBefore = existingAccountBeforeSendEmail2.getEmailLoginTokenFirstGeneratedAt();
        existingAccountBeforeSendEmail2.setEmailLoginTokenFirstGeneratedAt(timeBefore.minusHours(12));
        accountRepository.save(existingAccountBeforeSendEmail2);

        // Over 3 times after 12 hours.

        for(int time = 1; time <= 3; time++){

            mockMvc.perform(post("/email-login")
                    .param("email", TestAccountInfo.CORRECT_TEST_EMAIL)
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/email-login"))
                    .andExpect(model().hasNoErrors())
                    .andExpect(flash().attribute("successMessage", "로그인 링크를 이메일로 발송했습니다."))
                    .andExpect(unauthenticated());

            Account existingAccountAfterSendEmail2 = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

            assertEquals(time, existingAccountAfterSendEmail2.getSendLoginEmailCount());
            assertNotEquals(beforeEmailLoginToken, existingAccountAfterSendEmail2.getEmailLoginToken());

            if(time == 1){
                assertNotEquals(existingAccountAfterSendEmail2.getEmailLoginTokenFirstGeneratedAt(),
                        beforeEmailLoginTokenFirstGeneratedAt);
            }else{
                assertEquals(existingAccountAfterSendEmail2.getEmailLoginTokenFirstGeneratedAt(),
                        beforeEmailLoginTokenFirstGeneratedAt);
            }

            verify(javaMailSender, times(4 + time)).send(any(SimpleMailMessage.class));

            beforeEmailLoginToken = existingAccountAfterSendEmail2.getEmailLoginToken();
            beforeEmailLoginTokenFirstGeneratedAt = existingAccountAfterSendEmail2.getEmailLoginTokenFirstGeneratedAt();
        }

    }

    @DisplayName("로그인 링크 이메일 전송 요청 - 가입되지 않은 이메일 에러")
    @SignUpAndLoggedIn
    @Test
    void sendLoginLinkToNotExistingEmail() throws Exception {

        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(post("/email-login")
                .param("email", "incorrect@email.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-login"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("notExistingEmailError", "가입되지 않은 이메일 입니다."))
                .andExpect(unauthenticated());

    }

}
