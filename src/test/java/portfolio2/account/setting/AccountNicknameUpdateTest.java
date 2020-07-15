package portfolio2.account.setting;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.account.config.SignUpAndLoggedInEmailNotVerified;
import portfolio2.account.config.SignUpAndLoggedInEmailVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.service.process.EmailSendingProcess;
import portfolio2.module.account.dto.request.AccountNicknameUpdateRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.*;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class AccountNicknameUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @MockBean
    private EmailSendingProcess emailSendingProcess;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 모두 무조건 로그인 상태여야 함

    @DisplayName("모두 정상 입력 - 이메일 인증 안된 상태")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void successWithEmailNotVerified() throws Exception{
        AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto
                = new AccountNicknameUpdateRequestDto();
        String newNickname = "newNickname";
        accountNicknameUpdateRequestDto.setNickname(newNickname);

        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                        .param("nickname", accountNicknameUpdateRequestDto.getNickname())
                        .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        verify(emailSendingProcess, times(0)).sendNicknameUpdateNotificationEmail(any(Account.class));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(newNickname, updatedAccount.getNickname());
        assertNull(updatedAccount.getNicknameBeforeUpdate());
        assertNull(updatedAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("모두 정상 입력 - 이메일 인증 된 상태")
    @SignUpAndLoggedInEmailVerified
    @Test
    void successWithEmailVerified() throws Exception{
        AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto
                = new AccountNicknameUpdateRequestDto();
        String newNickname = "newNickname";
        accountNicknameUpdateRequestDto.setNickname(newNickname);

        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", accountNicknameUpdateRequestDto.getNickname())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        verify(emailSendingProcess, times(1)).sendNicknameUpdateNotificationEmail(any(Account.class));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(newNickname, updatedAccount.getNickname());
        assertNull(updatedAccount.getNicknameBeforeUpdate());
        assertNotNull(updatedAccount.getShowPasswordUpdatePageToken());
    }

    // 입력 에러

    @DisplayName("입력 에러 - 너무 짧은 닉네임 - 이메일 인증 된 상태")
    @SignUpAndLoggedInEmailVerified
    @Test
    void tooShortNicknameError() throws Exception{
        AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto
                = new AccountNicknameUpdateRequestDto();
        String newNickname = "ab";
        accountNicknameUpdateRequestDto.setNickname(newNickname);

        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", accountNicknameUpdateRequestDto.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "accountNicknameUpdateRequestDto",
                        "nickname",
                        "tooShortNickname"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        verify(emailSendingProcess, times(0)).sendNicknameUpdateNotificationEmail(any(Account.class));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_NICKNAME, notUpdatedAccount.getNickname());
        assertNotEquals(newNickname, notUpdatedAccount.getNickname());
        assertNull(notUpdatedAccount.getNicknameBeforeUpdate());
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("입력 에러 - 너무 긴 닉네임 - 이메일 인증 된 상태")
    @SignUpAndLoggedInEmailVerified
    @Test
    void tooLongNicknameError() throws Exception{
        AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto
                = new AccountNicknameUpdateRequestDto();
        String newNickname = "tooLongNewNickna";
        accountNicknameUpdateRequestDto.setNickname(newNickname);

        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", accountNicknameUpdateRequestDto.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "accountNicknameUpdateRequestDto",
                        "nickname",
                        "tooLongNickname"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        verify(emailSendingProcess, times(0)).sendNicknameUpdateNotificationEmail(any(Account.class));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_NICKNAME, notUpdatedAccount.getNickname());
        assertNotEquals(newNickname, notUpdatedAccount.getNickname());
        assertNull(notUpdatedAccount.getNicknameBeforeUpdate());
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("입력 에러 - 형식에 맞지않는 닉네임 - 이메일 인증 된 상태")
    @SignUpAndLoggedInEmailVerified
    @Test
    void invalidFormatNicknameError() throws Exception{
        AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto
                = new AccountNicknameUpdateRequestDto();
        String newNickname = "newNick%name";
        accountNicknameUpdateRequestDto.setNickname(newNickname);

        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", accountNicknameUpdateRequestDto.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "accountNicknameUpdateRequestDto",
                        "nickname",
                        "invalidFormatNickname"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        verify(emailSendingProcess, times(0)).sendNicknameUpdateNotificationEmail(any(Account.class));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_NICKNAME, notUpdatedAccount.getNickname());
        assertNotEquals(newNickname, notUpdatedAccount.getNickname());
        assertNull(notUpdatedAccount.getNicknameBeforeUpdate());
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("입력 에러 - 이미 존재하는 닉네임 - 이메일 인증 된 상태")
    @Test
    void nicknameAlreadyExistsError() throws Exception{
        signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutNotDefaultWith(TEST_USER_ID_2);
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();

        AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto
                = new AccountNicknameUpdateRequestDto();
        String newNickname = TEST_NICKNAME_2;
        accountNicknameUpdateRequestDto.setNickname(newNickname);

        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_NICKNAME_URL)
                .param("nickname", accountNicknameUpdateRequestDto.getNickname())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "accountNicknameUpdateRequestDto",
                        "nickname",
                        "nicknameAlreadyExists"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        verify(emailSendingProcess, times(0)).sendNicknameUpdateNotificationEmail(any(Account.class));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_NICKNAME, notUpdatedAccount.getNickname());
        assertNotEquals(newNickname, notUpdatedAccount.getNickname());
        assertNull(notUpdatedAccount.getNicknameBeforeUpdate());
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }
}
