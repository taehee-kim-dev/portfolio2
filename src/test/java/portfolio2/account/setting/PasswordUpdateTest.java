package portfolio2.account.setting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.testaccountinfo.SignUpAndLoggedIn;
import portfolio2.account.testaccountinfo.TestAccountInfo;
import portfolio2.controller.ex.AccountSettingController;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class PasswordUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("비밀번호 변경 화면 보여주기")
    @SignUpAndLoggedIn
    @Test
    void showPasswordSettingView() throws Exception{

        mockMvc.perform(get(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME));
    }

    @DisplayName("비밀번호 변경하기 - 모두 정상입력")
    @SignUpAndLoggedIn
    @Test
    void updatePasswordSuccess() throws Exception{

        String newPassword = "newPassword";
        String newPasswordConfirm = "newPassword";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attribute("message", "비밀번호 변경이 완료되었습니다."));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertFalse(passwordEncoder.matches(TestAccountInfo.CORRECT_TEST_PASSWORD, updatedAccount.getPassword()));
        assertTrue(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));

    }

    @DisplayName("비밀번호 변경하기 - 너무 짧은 길이 에러")
    @SignUpAndLoggedIn
    @Test
    void updatePasswordTooShortError() throws Exception{

        String newPassword = "newPass";
        String newPasswordConfirm = "newPass";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("passwordUpdateRequestDto", "newPassword", "tooShortNewPassword"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertTrue(passwordEncoder.matches(TestAccountInfo.CORRECT_TEST_PASSWORD, updatedAccount.getPassword()));
        assertFalse(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));
    }

    @DisplayName("비밀번호 변경하기 - 너무 긴 길이 에러")
    @SignUpAndLoggedIn
    @Test
    void updatePasswordTooLongError() throws Exception{

        String newPassword = "newPasswordnewPasswordnewPassword";
        String newPasswordConfirm = "newPasswordnewPasswordnewPassword";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("passwordUpdateRequestDto", "newPassword", "tooLongNewPassword"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertTrue(passwordEncoder.matches(TestAccountInfo.CORRECT_TEST_PASSWORD, updatedAccount.getPassword()));
        assertFalse(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));
    }

    @DisplayName("비밀번호 변경하기 - 공백 포함 에러")
    @SignUpAndLoggedIn
    @Test
    void updatePasswordInvalidFormatError() throws Exception{

        String newPassword = "new Password";
        String newPasswordConfirm = "new Password";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("passwordUpdateRequestDto", "newPassword", "invalidFormatNewPassword"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertTrue(passwordEncoder.matches(TestAccountInfo.CORRECT_TEST_PASSWORD, updatedAccount.getPassword()));
        assertFalse(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));
    }

    @DisplayName("비밀번호 변경하기 - 비밀번호 확인 불일치 에러")
    @SignUpAndLoggedIn
    @Test
    void updatePasswordNotSamePasswordError() throws Exception{

        String newPassword = "newPassword";
        String newPasswordConfirm = "notSameNewPassword";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("passwordUpdateRequestDto", "newPasswordConfirm", "notSamePassword"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertTrue(passwordEncoder.matches(TestAccountInfo.CORRECT_TEST_PASSWORD, updatedAccount.getPassword()));
        assertFalse(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));
    }

    @DisplayName("비밀번호 변경하기 - 너무 짧은 길이, 비밀번호 확인 불일치 에러")
    @SignUpAndLoggedIn
    @Test
    void updatePasswordTooShortPasswordAndNotSamePasswordError() throws Exception{

        String newPassword = "newPass";
        String newPasswordConfirm = "new Pass";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("passwordUpdateRequestDto", "newPassword", "tooShortNewPassword"))
                .andExpect(model().attributeHasFieldErrorCode
                        ("passwordUpdateRequestDto", "newPasswordConfirm", "notSamePassword"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertTrue(passwordEncoder.matches(TestAccountInfo.CORRECT_TEST_PASSWORD, updatedAccount.getPassword()));
        assertFalse(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));
    }
}
