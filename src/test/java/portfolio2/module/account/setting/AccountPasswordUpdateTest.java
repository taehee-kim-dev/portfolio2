package portfolio2.module.account.setting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.SignUpAndLoggedInEmailNotVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.service.process.EmailSendingProcessForAccount;
import portfolio2.module.account.dto.request.PasswordUpdateRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_PASSWORD;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@MockMvcTest
public class AccountPasswordUpdateTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailSendingProcessForAccount emailSendingProcessForAccount;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 모두 무조건 로그인 상태여야 함

    @DisplayName("비밀번호 변경 화면 보여주기")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void showAccountPasswordUpdatePage() throws Exception{
        mockMvc.perform(get(ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    // 모두 정상 입력

    @DisplayName("비밀번호 변경 모두 정상 입력 - 인증된 이메일이 없는 경우")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void updatePasswordSuccessNotEmailVerified() throws Exception{
        String newPassword = "ChangedPassword";
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setNewPassword(newPassword);
        passwordUpdateRequestDto.setNewPasswordConfirm(newPassword);

        mockMvc.perform(post(ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", passwordUpdateRequestDto.getNewPassword())
                .param("newPasswordConfirm", passwordUpdateRequestDto.getNewPasswordConfirm())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertTrue(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));
        verify(emailSendingProcessForAccount, times(0))
                .sendPasswordUpdateNotificationEmail(any(Account.class));
        assertNull(updatedAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("비밀번호 변경 모두 정상 입력 - 인증된 이메일이 있는 경우")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void updatePasswordSuccessWithEmailVerified() throws Exception{
        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        accountRepository.save(beforeAccount);

        String newPassword = "ChangedPassword#!@$$^_)+";
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setNewPassword(newPassword);
        passwordUpdateRequestDto.setNewPasswordConfirm(newPassword);

        mockMvc.perform(post(ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", passwordUpdateRequestDto.getNewPassword())
                .param("newPasswordConfirm", passwordUpdateRequestDto.getNewPasswordConfirm())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_PASSWORD_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertTrue(passwordEncoder.matches(newPassword, updatedAccount.getPassword()));
        verify(emailSendingProcessForAccount, times(1))
                .sendPasswordUpdateNotificationEmail(any(Account.class));
        assertNotNull(updatedAccount.getShowPasswordUpdatePageToken());
    }

    // 비정상 입력 - 모두 이메일 인증 된 상태

    // 비밀번호 확인은 모두 맞는 상태

    @DisplayName("너무 짧은 비밀번호 - 비밀번호 확인 일치")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void tooShortNewPasswordAndCorrectNewPasswordConfirmError() throws Exception{
        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        accountRepository.save(beforeAccount);

        String newPassword = "1234567";
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setNewPassword(newPassword);
        passwordUpdateRequestDto.setNewPasswordConfirm(newPassword);

        mockMvc.perform(post(ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", passwordUpdateRequestDto.getNewPassword())
                .param("newPasswordConfirm", passwordUpdateRequestDto.getNewPasswordConfirm())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                       "passwordUpdateRequestDto",
                        "newPassword",
                        "invalidFormatNewPassword"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(flash().attributeCount(0))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(passwordEncoder.matches(newPassword, notUpdatedAccount.getPassword()));
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, notUpdatedAccount.getPassword()));
        verify(emailSendingProcessForAccount, times(0))
                .sendPasswordUpdateNotificationEmail(any(Account.class));
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("너무 긴 비밀번호 - 비밀번호 확인 일치")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void tooLongNewPasswordAndCorrectNewPasswordConfirmError() throws Exception{
        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        accountRepository.save(beforeAccount);

        String newPassword = "abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdeabcdea";
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setNewPassword(newPassword);
        passwordUpdateRequestDto.setNewPasswordConfirm(newPassword);

        mockMvc.perform(post(ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", passwordUpdateRequestDto.getNewPassword())
                .param("newPasswordConfirm", passwordUpdateRequestDto.getNewPasswordConfirm())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "passwordUpdateRequestDto",
                        "newPassword",
                        "invalidFormatNewPassword"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(flash().attributeCount(0))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(passwordEncoder.matches(newPassword, notUpdatedAccount.getPassword()));
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, notUpdatedAccount.getPassword()));
        verify(emailSendingProcessForAccount, times(0))
                .sendPasswordUpdateNotificationEmail(any(Account.class));
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }

    @DisplayName("공백 포함 비밀번호 - 비밀번호 확인 일치")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void invalidWhiteSpaceNewPasswordAndCorrectNewPasswordConfirmError() throws Exception{
        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        accountRepository.save(beforeAccount);

        String newPassword = "updated Password";
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setNewPassword(newPassword);
        passwordUpdateRequestDto.setNewPasswordConfirm(newPassword);

        mockMvc.perform(post(ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", passwordUpdateRequestDto.getNewPassword())
                .param("newPasswordConfirm", passwordUpdateRequestDto.getNewPasswordConfirm())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "passwordUpdateRequestDto",
                        "newPassword",
                        "invalidFormatNewPassword"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(flash().attributeCount(0))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(passwordEncoder.matches(newPassword, notUpdatedAccount.getPassword()));
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, notUpdatedAccount.getPassword()));
        verify(emailSendingProcessForAccount, times(0))
                .sendPasswordUpdateNotificationEmail(any(Account.class));
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }

    // 확인 비밀번호만 불일치

    @DisplayName("정상 비밀번호 입력 - 확인 비밀번호 불일치")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void validNewPasswordAndIncorrectNewPasswordConfirmError() throws Exception{
        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        accountRepository.save(beforeAccount);

        String newPassword = "updatedPassword";
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setNewPassword(newPassword);
        passwordUpdateRequestDto.setNewPasswordConfirm(newPassword);

        mockMvc.perform(post(ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", passwordUpdateRequestDto.getNewPassword())
                .param("newPasswordConfirm", "updatedPassworda")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "passwordUpdateRequestDto",
                        "newPasswordConfirm",
                        "notSamePassword"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(flash().attributeCount(0))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(passwordEncoder.matches(newPassword, notUpdatedAccount.getPassword()));
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, notUpdatedAccount.getPassword()));
        verify(emailSendingProcessForAccount, times(0))
                .sendPasswordUpdateNotificationEmail(any(Account.class));
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }

    // 비정상 비밀번호 - 확인 비밀번호 불일치하면 비정상 비밀번호에 대한 에러문구만 띄움
    @DisplayName("공백 포함 비밀번호 - 비밀번호 확인 불일치")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void invalidFormatNewPasswordAndIncorrectNewPasswordConfirmError() throws Exception{
        Account beforeAccount = accountRepository.findByUserId(TEST_USER_ID);
        beforeAccount.setVerifiedEmail(beforeAccount.getEmailWaitingToBeVerified());
        beforeAccount.setEmailVerified(true);
        accountRepository.save(beforeAccount);

        String newPassword = "updated Password";
        PasswordUpdateRequestDto passwordUpdateRequestDto = new PasswordUpdateRequestDto();
        passwordUpdateRequestDto.setNewPassword(newPassword);
        passwordUpdateRequestDto.setNewPasswordConfirm(newPassword);

        mockMvc.perform(post(ACCOUNT_SETTING_PASSWORD_URL)
                .param("newPassword", passwordUpdateRequestDto.getNewPassword())
                .param("newPasswordConfirm", "incorrectPassword")
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "passwordUpdateRequestDto",
                        "newPassword",
                        "invalidFormatNewPassword"
                ))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("passwordUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PASSWORD_VIEW_NAME))
                .andExpect(status().isOk())
                .andExpect(flash().attributeCount(0))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account notUpdatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(passwordEncoder.matches(newPassword, notUpdatedAccount.getPassword()));
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, notUpdatedAccount.getPassword()));
        verify(emailSendingProcessForAccount, times(0))
                .sendPasswordUpdateNotificationEmail(any(Account.class));
        assertNull(notUpdatedAccount.getShowPasswordUpdatePageToken());
    }
}
