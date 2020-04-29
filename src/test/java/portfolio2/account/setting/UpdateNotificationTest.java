package portfolio2.account.setting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.SignUpAndLoggedIn;
import portfolio2.TestAccountInfo;
import portfolio2.controller.account.AccountSettingController;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.service.AccountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UpdateNotificationTest {

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

    @DisplayName("닉네임 변경하기 - 정상입력")
    @SignUpAndLoggedIn
    @Test
    void updateNicknameSuccess() throws Exception{

        String newNickname = "newNickname";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attribute("message", "닉네임 변경이 완료되었습니다."));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertNotEquals(TestAccountInfo.CORRECT_TEST_NICKNAME, updatedAccount.getNickname());
        assertEquals(newNickname, updatedAccount.getNickname());
    }

    @DisplayName("닉네임 변경하기 - 너무 짧은 길이 에러")
    @SignUpAndLoggedIn
    @Test
    void updateNicknameTooShortError() throws Exception{

        String newNickname = "aa";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("accountNicknameUpdateRequestDto", "nickname", "tooShortNickname"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(TestAccountInfo.CORRECT_TEST_NICKNAME, updatedAccount.getNickname());
        assertNotEquals(newNickname, updatedAccount.getNickname());
    }

    @DisplayName("닉네임 변경하기 - 너무 긴 길이 에러")
    @SignUpAndLoggedIn
    @Test
    void updateNicknameTooLongError() throws Exception{

        String newNickname = "newNicknamenewNicknamenewNickname";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("accountNicknameUpdateRequestDto", "nickname", "tooLongNickname"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(TestAccountInfo.CORRECT_TEST_NICKNAME, updatedAccount.getNickname());
        assertNotEquals(newNickname, updatedAccount.getNickname());
    }

    @DisplayName("닉네임 변경하기 - 맞지 않는 형식 에러")
    @SignUpAndLoggedIn
    @Test
    void updateNicknameInvalidFormatError() throws Exception{

        String newNickname = "newNickname!";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("accountNicknameUpdateRequestDto", "nickname", "invalidFormatNickname"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(TestAccountInfo.CORRECT_TEST_NICKNAME, updatedAccount.getNickname());
        assertNotEquals(newNickname, updatedAccount.getNickname());
    }

    @DisplayName("닉네임 변경하기 - 이미 존재하는 닉네임 에러")
    @SignUpAndLoggedIn
    @Test
    void updateNicknameAlreadyExistsError() throws Exception{

        String existingNickname = "existing";

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId("testUserId2")
                .password("testPassword")
                .email("test2@email.con")
                .nickname(existingNickname)
                .build();

        accountService.saveNewAccount(signUpRequestDto);

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", existingNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("accountNicknameUpdateRequestDto", "nickname", "nicknameAlreadyExists"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(TestAccountInfo.CORRECT_TEST_NICKNAME, updatedAccount.getNickname());
        assertNotEquals(existingNickname, updatedAccount.getNickname());
    }
}
