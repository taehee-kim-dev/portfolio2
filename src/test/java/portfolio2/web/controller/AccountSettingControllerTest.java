package portfolio2.web.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.WithAccount;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.web.controller.account.AccountSettingController;

import static org.junit.jupiter.api.Assertions.*;
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
    AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @WithAccount("testUserId")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void showProfileUpdateForm() throws Exception{
        mockMvc.perform(get(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"));
    }

    @WithAccount("testUserId")
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

    @WithAccount("testUserId")
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
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByUserId("testUserId");
        assertNull(account.getBio());
    }
}