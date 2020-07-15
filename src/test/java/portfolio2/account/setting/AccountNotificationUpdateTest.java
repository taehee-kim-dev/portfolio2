package portfolio2.account.setting;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.SignUpAndLoggedInEmailNotVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class AccountNotificationUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;


    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 모두 무조건 로그인 상태여야 함

    @DisplayName("알림설정 화면 보여주기")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void showNotificationUpdatePage() throws Exception{
        mockMvc.perform(get(ACCOUNT_SETTING_NOTIFICATION_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("notificationUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    // 모두 정상 입력

    @DisplayName("알림설정 - 모두 정상 입력 - 이메일이 인증되지 않은 상태")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void updateProfileSuccessEmailNotVerified() throws Exception{

        mockMvc.perform(post(ACCOUNT_SETTING_NOTIFICATION_URL)
                .param("notificationLikeOnMyPostByWeb", String.valueOf(false))
                .param("notificationLikeOnMyReplyByWeb", String.valueOf(false))
                .param("notificationReplyOnMyPostByWeb", String.valueOf(false))
                .param("notificationReplyOnMyReplyByWeb", String.valueOf(false))
                .param("notificationNewPostWithMyTagByWeb", String.valueOf(false))
                .param("notificationLikeOnMyPostByEmail", String.valueOf(true))
                .param("notificationLikeOnMyReplyByEmail", String.valueOf(true))
                .param("notificationReplyOnMyPostByEmail", String.valueOf(true))
                .param("notificationReplyOnMyReplyByEmail", String.valueOf(true))
                .param("notificationNewPostWithMyTagByEmail", String.valueOf(true))
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_NOTIFICATION_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(updatedAccount.isNotificationLikeOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationLikeOnMyReplyByWeb());

        assertFalse(updatedAccount.isNotificationReplyOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationReplyOnMyReplyByWeb());

        assertFalse(updatedAccount.isNotificationNewPostWithMyTagByWeb());

        assertFalse(updatedAccount.isNotificationLikeOnMyPostByEmail());
        assertFalse(updatedAccount.isNotificationLikeOnMyReplyByEmail());

        assertFalse(updatedAccount.isNotificationReplyOnMyPostByEmail());
        assertFalse(updatedAccount.isNotificationReplyOnMyReplyByEmail());

        assertFalse(updatedAccount.isNotificationNewPostWithMyTagByEmail());
    }

    @DisplayName("알림설정 - 모두 정상 입력 - 이메일이 인증된 상태")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void updateProfileSuccessEmailVerified() throws Exception{

        Account accountToUpdate = accountRepository.findByUserId(TEST_USER_ID);
        accountToUpdate.setEmailVerified(true);
        accountRepository.save(accountToUpdate);

        mockMvc.perform(post(ACCOUNT_SETTING_NOTIFICATION_URL)
                .param("notificationLikeOnMyPostByWeb", String.valueOf(false))
                .param("notificationLikeOnMyReplyByWeb", String.valueOf(false))
                .param("notificationReplyOnMyPostByWeb", String.valueOf(false))
                .param("notificationReplyOnMyReplyByWeb", String.valueOf(false))
                .param("notificationNewPostWithMyTagByWeb", String.valueOf(false))
                .param("notificationLikeOnMyPostByEmail", String.valueOf(true))
                .param("notificationLikeOnMyReplyByEmail", String.valueOf(true))
                .param("notificationReplyOnMyPostByEmail", String.valueOf(true))
                .param("notificationReplyOnMyReplyByEmail", String.valueOf(true))
                .param("notificationNewPostWithMyTagByEmail", String.valueOf(true))
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_NOTIFICATION_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(updatedAccount.isNotificationLikeOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationLikeOnMyReplyByWeb());

        assertFalse(updatedAccount.isNotificationReplyOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationReplyOnMyReplyByWeb());

        assertFalse(updatedAccount.isNotificationNewPostWithMyTagByWeb());

        assertTrue(updatedAccount.isNotificationLikeOnMyPostByEmail());
        assertTrue(updatedAccount.isNotificationLikeOnMyReplyByEmail());

        assertTrue(updatedAccount.isNotificationReplyOnMyPostByEmail());
        assertTrue(updatedAccount.isNotificationReplyOnMyReplyByEmail());

        assertTrue(updatedAccount.isNotificationNewPostWithMyTagByEmail());
    }
}
