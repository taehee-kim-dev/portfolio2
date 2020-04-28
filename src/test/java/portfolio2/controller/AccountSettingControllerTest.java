package portfolio2.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.WithAccount;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.controller.account.AccountSettingController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountSettingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    private final String TEST_USER_ID = "testUserId";

    @WithAccount
    @DisplayName("프로필 수정하기 폼 보여주기")
    @Test
    void showProfileUpdateForm() throws Exception{
        mockMvc.perform(get(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"));
    }

    @WithAccount
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception{
        String bio = "짧은 소개를 정상적으로 수정하는 경우.";
        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", bio)
                .param("location", "")
                .param("occupation", "")
        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByUserId("testUserId");
        assertEquals(bio, account.getBio());
    }

    @WithAccount
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_with_error_input() throws Exception{
        String bio = "bio값을 35자 초과로 주는 경우.bio값을 35자 초과로 주는 경우.bio값을 35자 초과로 주는 경우.bio값을 35자 초과로 주는 경우.";
        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", bio)
                .param("location", "")
                .param("occupation", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/setting/profile"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByUserId("testUserId");
        assertNull(account.getBio());
    }


    @WithAccount
    @DisplayName("비밀번호 수정 뷰 보여주기")
    @Test
    void showUpdatePasswordView() throws Exception{
        mockMvc.perform(get(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(model().hasNoErrors());
    }


    @WithAccount
    @DisplayName("비밀번호 수정하기 - 입력값 정상")
    @Test
    void updatePassword() throws Exception{
        String newPassword = "123456789";
        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPassword)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account accountWithNewPassword = accountRepository.findByUserId(TEST_USER_ID);

        assertTrue(passwordEncoder.matches(newPassword, accountWithNewPassword.getPassword()));
    }

    @WithAccount
    @DisplayName("비밀번호 수정하기 - 입력값 오류")
    @Test
    void updatePasswordWithErrorInput1() throws Exception{
        String newPassword = "123456789";
        String newPasswordConfirm = "1234567899";
        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(model().hasErrors());

        Account accountWithOldPassword = accountRepository.findByUserId(TEST_USER_ID);

        assertFalse(passwordEncoder.matches(newPassword, accountWithOldPassword.getPassword()));
    }
}