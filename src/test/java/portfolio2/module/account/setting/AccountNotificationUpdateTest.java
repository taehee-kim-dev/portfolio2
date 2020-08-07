package portfolio2.module.account.setting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.controller.config.StaticVariableNamesAboutAccount.*;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.SESSION_ACCOUNT;

@MockMvcTest
public class AccountNotificationUpdateTest extends ContainerBaseTest {

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
    @SignUpAndLogInEmailNotVerified
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
    @SignUpAndLogInEmailNotVerified
    @Test
    void updateProfileSuccessEmailNotVerified() throws Exception{

        mockMvc.perform(post(ACCOUNT_SETTING_NOTIFICATION_URL)
                .param("notificationLikeOnMyPostByWeb", String.valueOf(false))
                .param("notificationLikeOnMyCommentByWeb", String.valueOf(false))
                .param("notificationCommentOnMyPostByWeb", String.valueOf(false))
                .param("notificationCommentOnMyCommentByWeb", String.valueOf(false))
                .param("notificationNewPostWithMyInterestTagByWeb", String.valueOf(false))
                .param("notificationMyInterestTagAddedToExistingPostByWeb", String.valueOf(false))
                .param("notificationLikeOnMyPostByEmail", String.valueOf(true))
                .param("notificationLikeOnMyCommentByEmail", String.valueOf(true))
                .param("notificationCommentOnMyPostByEmail", String.valueOf(true))
                .param("notificationCommentOnMyCommentByEmail", String.valueOf(true))
                .param("notificationNewPostWithMyInterestTagByEmail", String.valueOf(true))
                .param("notificationMyInterestTagAddedToExistingPostByEmail", String.valueOf(true))
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_NOTIFICATION_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(updatedAccount.isNotificationLikeOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationLikeOnMyCommentByWeb());

        assertFalse(updatedAccount.isNotificationCommentOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationCommentOnMyCommentByWeb());

        assertFalse(updatedAccount.isNotificationNewPostWithMyInterestTagByWeb());
        assertFalse(updatedAccount.isNotificationMyInterestTagAddedToExistingPostByWeb());

        assertFalse(updatedAccount.isNotificationLikeOnMyPostByEmail());
        assertFalse(updatedAccount.isNotificationLikeOnMyCommentByEmail());

        assertFalse(updatedAccount.isNotificationCommentOnMyPostByEmail());
        assertFalse(updatedAccount.isNotificationCommentOnMyCommentByEmail());

        assertFalse(updatedAccount.isNotificationNewPostWithMyInterestTagByEmail());
        assertFalse(updatedAccount.isNotificationMyInterestTagAddedToExistingPostByEmail());
    }

    @DisplayName("알림설정 - 모두 정상 입력 - 이메일이 인증된 상태")
    @SignUpAndLogInEmailNotVerified
    @Test
    void updateProfileSuccessEmailVerified() throws Exception{

        Account accountToUpdate = accountRepository.findByUserId(TEST_USER_ID);
        accountToUpdate.setEmailVerified(true);
        accountRepository.save(accountToUpdate);

        mockMvc.perform(post(ACCOUNT_SETTING_NOTIFICATION_URL)
                .param("notificationLikeOnMyPostByWeb", String.valueOf(false))
                .param("notificationLikeOnMyCommentByWeb", String.valueOf(false))
                .param("notificationCommentOnMyPostByWeb", String.valueOf(false))
                .param("notificationCommentOnMyCommentByWeb", String.valueOf(false))
                .param("notificationNewPostWithMyInterestTagByWeb", String.valueOf(false))
                .param("notificationMyInterestTagAddedToExistingPostByWeb", String.valueOf(false))
                .param("notificationLikeOnMyPostByEmail", String.valueOf(true))
                .param("notificationLikeOnMyCommentByEmail", String.valueOf(true))
                .param("notificationCommentOnMyPostByEmail", String.valueOf(true))
                .param("notificationCommentOnMyCommentByEmail", String.valueOf(true))
                .param("notificationNewPostWithMyInterestTagByEmail", String.valueOf(true))
                .param("notificationMyInterestTagAddedToExistingPostByEmail", String.valueOf(true))
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_NOTIFICATION_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(updatedAccount.isNotificationLikeOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationLikeOnMyCommentByWeb());

        assertFalse(updatedAccount.isNotificationCommentOnMyPostByWeb());
        assertFalse(updatedAccount.isNotificationCommentOnMyCommentByWeb());

        assertFalse(updatedAccount.isNotificationNewPostWithMyInterestTagByWeb());
        assertFalse(updatedAccount.isNotificationMyInterestTagAddedToExistingPostByWeb());

        assertTrue(updatedAccount.isNotificationLikeOnMyPostByEmail());
        assertTrue(updatedAccount.isNotificationLikeOnMyCommentByEmail());

        assertTrue(updatedAccount.isNotificationCommentOnMyPostByEmail());
        assertTrue(updatedAccount.isNotificationCommentOnMyCommentByEmail());

        assertTrue(updatedAccount.isNotificationNewPostWithMyInterestTagByEmail());
        assertTrue(updatedAccount.isNotificationMyInterestTagAddedToExistingPostByEmail());

    }
}
