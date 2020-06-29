package portfolio2.account.setting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.testaccountinfo.SignUpAndLoggedIn;
import portfolio2.account.testaccountinfo.TestAccountInfo;
import portfolio2.controller.ex.AccountSettingController;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.service.AccountService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class NotificationUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("알림 설정 화면 보여주기")
    @SignUpAndLoggedIn
    @Test
    void showNotificationSettingView() throws Exception{

        mockMvc.perform(get(AccountSettingController.ACCOUNT_SETTING_NOTIFICATION_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("notificationUpdateRequestDto"))
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_NOTIFICATION_VIEW_NAME));

    }

    @DisplayName("알림 설정 - 정상입력")
    @SignUpAndLoggedIn
    @Test
    void updateNotification() throws Exception{

        Account existingAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);


        // Notifications by web
        assertTrue(existingAccount.isNotificationReplyOnMyPostByWeb());
        assertTrue(existingAccount.isNotificationReplyOnMyReplyByWeb());

        assertTrue(existingAccount.isNotificationLikeOnMyPostByWeb());
        assertTrue(existingAccount.isNotificationLikeOnMyReplyByWeb());

        assertTrue(existingAccount.isNotificationNewPostWithMyTagByWeb());


        // Notifications by email
        assertFalse(existingAccount.isNotificationReplyOnMyPostByEmail());
        assertFalse(existingAccount.isNotificationReplyOnMyReplyByEmail());

        assertFalse(existingAccount.isNotificationLikeOnMyPostByEmail());
        assertFalse(existingAccount.isNotificationLikeOnMyReplyByEmail());

        assertFalse(existingAccount.isNotificationNewPostWithMyTagByEmail());

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_NOTIFICATION_URL)
                .param("notificationLikeOnMyPostByWeb", "false")
                .param("notificationLikeOnMyReplyByWeb", "false")
                .param("notificationReplyOnMyPostByWeb", "false")
                .param("notificationReplyOnMyReplyByWeb", "false")
                .param("notificationNewPostWithMyTagByWeb", "false")
                .param("notificationLikeOnMyPostByEmail", "true")
                .param("notificationLikeOnMyReplyByEmail", "true")
                .param("notificationReplyOnMyPostByEmail", "true")
                .param("notificationReplyOnMyReplyByEmail", "true")
                .param("notificationNewPostWithMyTagByEmail", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(AccountSettingController.ACCOUNT_SETTING_NOTIFICATION_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attribute("message", "알림설정이 저장되었습니다."));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);


        // Notifications by web
        assertFalse(updatedAccount.isNotificationReplyOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationReplyOnMyReplyByWeb());

        assertFalse(updatedAccount.isNotificationLikeOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationLikeOnMyReplyByWeb());

        assertFalse(updatedAccount.isNotificationNewPostWithMyTagByWeb());


        // Notifications by email
        assertTrue(updatedAccount.isNotificationReplyOnMyPostByEmail());
        assertTrue(updatedAccount.isNotificationReplyOnMyReplyByEmail());

        assertTrue(updatedAccount.isNotificationLikeOnMyPostByEmail());
        assertTrue(updatedAccount.isNotificationLikeOnMyReplyByEmail());

        assertTrue(updatedAccount.isNotificationNewPostWithMyTagByEmail());


    }

}
