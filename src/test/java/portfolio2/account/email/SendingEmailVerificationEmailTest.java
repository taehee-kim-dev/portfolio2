package portfolio2.account.email;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.*;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.service.process.EmailSendingProcess;
import portfolio2.module.account.dto.request.AccountEmailUpdateRequestDto;

import java.time.LocalDateTime;

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
public class SendingEmailVerificationEmailTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogInEmailNotVerifiedProcessForTest signUpAndLogInEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @MockBean
    private EmailSendingProcess emailSendingProcess;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 무조건 로그인 상태에서만 보낼 수 밖에 없다.

    @DisplayName("기존에 이메일 인증 안된 상태 - 기존 이메일로 재전송 - 모든 입력 정상 - 1회 성공")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void notVerifiedEmailSendAgainSuccess() throws Exception{
        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime initialTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String initialToken = beforeTry.getEmailVerificationToken();

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        accountEmailUpdateRequestDto.setEmail(TEST_EMAIL);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                        .param("email", accountEmailUpdateRequestDto.getEmail())
                        .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSent.getVerifiedEmail());
        assertEquals(TEST_EMAIL, accountAfterEmailSent.getEmailWaitingToBeVerified());
        assertFalse(accountAfterEmailSent.isEmailVerified());
        assertNotEquals(initialToken, accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(initialTime, accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(2, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(2)).sendEmailVerificationEmail(any(Account.class));
    }

    @DisplayName("기존에 이메일 인증 안된 상태 - 새로운 이메일로 전송 - 모든 입력 정상 - 1회 성공")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void notVerifiedEmailSendToNewEmailSuccess() throws Exception{
        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime initialTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String initialToken = beforeTry.getEmailVerificationToken();

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        String newEmail = "new@email.com";
        accountEmailUpdateRequestDto.setEmail(newEmail);

        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto.getEmail())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSent.getVerifiedEmail());
        assertEquals(newEmail, accountAfterEmailSent.getEmailWaitingToBeVerified());
        assertFalse(accountAfterEmailSent.isEmailVerified());
        assertNotEquals(initialToken, accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(initialTime, accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(2, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(2)).sendEmailVerificationEmail(any(Account.class));
    }

    @DisplayName("기존에 이메일 인증 된 상태 - 새로운 이메일로 인증 이메일 전송 - 모든 입력 정상 - 1회 성공")
    @SignUpAndLoggedInEmailVerified
    @Test
    void verifiedEmailSendAgainSuccess() throws Exception{
        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime initialTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String initialToken = beforeTry.getEmailVerificationToken();

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        String newEmail = "new@email.com";
        accountEmailUpdateRequestDto.setEmail(newEmail);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto.getEmail())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSent.getVerifiedEmail());
        assertEquals(newEmail, accountAfterEmailSent.getEmailWaitingToBeVerified());
        assertFalse(accountAfterEmailSent.isEmailVerified());
        assertNotEquals(initialToken, accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(initialTime, accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(2, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(2)).sendEmailVerificationEmail(any(Account.class));
    }

    // 입력 에러

    @DisplayName("형식에 맞지 않는 이메일")
    @SignUpAndLoggedInEmailVerified
    @Test
    void invalidFormatNewEmail() throws Exception{
        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime initialTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String initialToken = beforeTry.getEmailVerificationToken();

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        String newEmail = "new@email";
        accountEmailUpdateRequestDto.setEmail(newEmail);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto.getEmail())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "accountEmailUpdateRequestDto",
                        "email",
                        "invalidFormatEmail"
                ))
                .andExpect(model().attributeErrorCount("accountEmailUpdateRequestDto", 1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_EMAIL, accountAfterEmailSent.getVerifiedEmail());
        assertNull(accountAfterEmailSent.getEmailWaitingToBeVerified());
        assertTrue(accountAfterEmailSent.isEmailVerified());
        assertNull(accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(initialTime, accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(1)).sendEmailVerificationEmail(any(Account.class));
    }

    @DisplayName("이미 사용중인 이메일 - 본인이 인증한 이메일")
    @SignUpAndLoggedInEmailVerified
    @Test
    void newEmailAlreadyExistsByOwnAccount() throws Exception{
        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime initialTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String initialToken = beforeTry.getEmailVerificationToken();
        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        String newEmail = TEST_EMAIL;
        accountEmailUpdateRequestDto.setEmail(newEmail);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto.getEmail())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "accountEmailUpdateRequestDto",
                        "email",
                        "emailAlreadyExists"
                ))
                .andExpect(model().attributeErrorCount("accountEmailUpdateRequestDto", 1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_EMAIL, accountAfterEmailSent.getVerifiedEmail());
        assertNull(accountAfterEmailSent.getEmailWaitingToBeVerified());
        assertTrue(accountAfterEmailSent.isEmailVerified());
        assertNull(accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(initialTime, accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(1)).sendEmailVerificationEmail(any(Account.class));
    }

    @DisplayName("이미 사용중인 이메일 - 다른 사람이 이미 인증한 이메일")
    @Test
    void newEmailAlreadyExistsByNotOwnAccount() throws Exception{
        signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutNotDefaultWith(TEST_USER_ID_2);
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();

        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime initialTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String initialToken = beforeTry.getEmailVerificationToken();

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        String newEmail = TEST_EMAIL_2;
        accountEmailUpdateRequestDto.setEmail(newEmail);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto.getEmail())
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "accountEmailUpdateRequestDto",
                        "email",
                        "emailAlreadyExists"
                ))
                .andExpect(model().attributeErrorCount("accountEmailUpdateRequestDto", 1))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeExists("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(ACCOUNT_SETTING_ACCOUNT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_EMAIL, accountAfterEmailSent.getVerifiedEmail());
        assertNull(accountAfterEmailSent.getEmailWaitingToBeVerified());
        assertTrue(accountAfterEmailSent.isEmailVerified());
        assertNull(accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(initialTime, accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(1, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(2)).sendEmailVerificationEmail(any(Account.class));
    }

    // 12시간동안 5회까지 성공
    @DisplayName("12시간동안 5회 전송 성공")
    @SignUpAndLoggedInEmailVerified
    @Test
    void successFor5TimesIn12Hours() throws Exception{
        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime beforeTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String beforeToken = beforeTry.getEmailVerificationToken();

        for(int time = 2; time <= 5; time++){
            AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
            String newEmail = "new" + time + "@email.com";
            accountEmailUpdateRequestDto.setEmail(newEmail);
            mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                    .param("email", accountEmailUpdateRequestDto.getEmail())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                    .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                    .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                    .andExpect(flash().attributeExists("message"))
                    .andExpect(flash().attributeCount(1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL));

            Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
            assertNull(accountAfterEmailSent.getVerifiedEmail());
            assertEquals(newEmail, accountAfterEmailSent.getEmailWaitingToBeVerified());
            assertFalse(accountAfterEmailSent.isEmailVerified());
            String afterToken = accountAfterEmailSent.getEmailVerificationToken();
            assertNotNull(afterToken);
            assertNotEquals(beforeToken, afterToken);
            LocalDateTime afterTime = accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt();
            assertNotNull(afterTime);
            assertEquals(beforeTime, afterTime);
            assertEquals(time, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

            verify(emailSendingProcess, times(time)).sendEmailVerificationEmail(any(Account.class));
            beforeTime = afterTime;
            beforeToken = afterToken;
        }
    }

    @DisplayName("12시간동안 6회 전송 실패")
    @SignUpAndLoggedInEmailVerified
    @Test
    void failFor6TimesIn12Hours() throws Exception{

        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime beforeTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String beforeToken = beforeTry.getEmailVerificationToken();

        for(int time = 2; time <= 5; time++){
            AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
            String newEmail = "new" + time + "@email.com";
            accountEmailUpdateRequestDto.setEmail(newEmail);
            mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                    .param("email", accountEmailUpdateRequestDto.getEmail())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                    .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                    .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                    .andExpect(flash().attributeExists("message"))
                    .andExpect(flash().attributeCount(1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL));

            Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
            assertNull(accountAfterEmailSent.getVerifiedEmail());
            assertEquals(newEmail, accountAfterEmailSent.getEmailWaitingToBeVerified());
            assertFalse(accountAfterEmailSent.isEmailVerified());
            String afterToken = accountAfterEmailSent.getEmailVerificationToken();
            assertNotNull(afterToken);
            assertNotEquals(beforeToken, afterToken);
            LocalDateTime afterTime = accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt();
            assertNotNull(afterTime);
            assertEquals(beforeTime, afterTime);
            assertEquals(time, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

            verify(emailSendingProcess, times(time)).sendEmailVerificationEmail(any(Account.class));
            beforeTime = afterTime;
            beforeToken = afterToken;
        }

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto = new AccountEmailUpdateRequestDto();
        String newEmail = "new" + 6 + "@email.com";
        accountEmailUpdateRequestDto.setEmail(newEmail);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto.getEmail())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("cannotSendError"))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(CANNOT_SEND_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME));

        Account accountAfterEmailSent = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSent.getVerifiedEmail());
        assertEquals("new5@email.com", accountAfterEmailSent.getEmailWaitingToBeVerified());
        assertFalse(accountAfterEmailSent.isEmailVerified());
        assertNotNull(accountAfterEmailSent.getEmailVerificationToken());
        assertNotNull(accountAfterEmailSent.getFirstCountOfSendingEmailVerificationEmailSetAt());
        assertEquals(5, accountAfterEmailSent.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(5)).sendEmailVerificationEmail(any(Account.class));
    }

    @DisplayName("12시간 후 10번 째 까지 전송 성공")
    @SignUpAndLoggedInEmailVerified
    @Test
    void successFor6TimesAfter12Hours() throws Exception {
        Account beforeTry = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime beforeTime = beforeTry.getFirstCountOfSendingEmailVerificationEmailSetAt();
        String beforeToken = beforeTry.getEmailVerificationToken();

        for (int time1 = 2; time1 <= 5; time1++) {
            AccountEmailUpdateRequestDto accountEmailUpdateRequestDto1 = new AccountEmailUpdateRequestDto();
            String newEmail1 = "new" + time1 + "@email.com";
            accountEmailUpdateRequestDto1.setEmail(newEmail1);
            mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                    .param("email", accountEmailUpdateRequestDto1.getEmail())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                    .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                    .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                    .andExpect(flash().attributeExists("message"))
                    .andExpect(flash().attributeCount(1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL));

            Account accountAfterEmailSent1 = accountRepository.findByUserId(TEST_USER_ID);
            assertNull(accountAfterEmailSent1.getVerifiedEmail());
            assertEquals(newEmail1, accountAfterEmailSent1.getEmailWaitingToBeVerified());
            assertFalse(accountAfterEmailSent1.isEmailVerified());
            String afterToken1 = accountAfterEmailSent1.getEmailVerificationToken();
            assertNotNull(afterToken1);
            assertNotEquals(beforeToken, afterToken1);
            LocalDateTime afterTime1 = accountAfterEmailSent1.getFirstCountOfSendingEmailVerificationEmailSetAt();
            assertNotNull(afterTime1);
            assertEquals(beforeTime, afterTime1);
            assertEquals(time1, accountAfterEmailSent1.getCountOfSendingEmailVerificationEmail());

            verify(emailSendingProcess, times(time1)).sendEmailVerificationEmail(any(Account.class));
            beforeTime = afterTime1;
            beforeToken = afterToken1;
        }

        Account accountToTimeSet = accountRepository.findByUserId(TEST_USER_ID);
        LocalDateTime fifthTime = accountToTimeSet.getFirstCountOfSendingEmailVerificationEmailSetAt();
        accountToTimeSet.setFirstCountOfSendingEmailVerificationEmailSetAt(fifthTime.minusHours(12).minusMinutes(1));
        accountRepository.save(accountToTimeSet);

        // 6번 째 전송
        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto2 = new AccountEmailUpdateRequestDto();
        String newEmail2 = "new6@email.com";
        accountEmailUpdateRequestDto2.setEmail(newEmail2);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto2.getEmail())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL));

        Account accountAfterEmailSent2 = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSent2.getVerifiedEmail());
        assertEquals(newEmail2, accountAfterEmailSent2.getEmailWaitingToBeVerified());
        assertFalse(accountAfterEmailSent2.isEmailVerified());
        String afterToken2 = accountAfterEmailSent2.getEmailVerificationToken();
        assertNotNull(afterToken2);
        assertNotEquals(beforeToken, afterToken2);
        LocalDateTime afterTime2 = accountAfterEmailSent2.getFirstCountOfSendingEmailVerificationEmailSetAt();
        assertNotNull(afterTime2);
        assertNotEquals(beforeTime, afterTime2);
        assertEquals(1, accountAfterEmailSent2.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(6)).sendEmailVerificationEmail(any(Account.class));
        beforeTime = afterTime2;
        beforeToken = afterToken2;

        for (int time3 = 7; time3 <= 10; time3++) {
            AccountEmailUpdateRequestDto accountEmailUpdateRequestDto3 = new AccountEmailUpdateRequestDto();
            String newEmail3 = "new" + time3 + "@email.com";
            accountEmailUpdateRequestDto3.setEmail(newEmail3);
            mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                    .param("email", accountEmailUpdateRequestDto3.getEmail())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                    .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                    .andExpect(model().attributeDoesNotExist("accountEmailUpdateRequestDto"))
                    .andExpect(flash().attributeExists("message"))
                    .andExpect(flash().attributeCount(1))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl(ACCOUNT_SETTING_ACCOUNT_URL));

            Account accountAfterEmailSent3 = accountRepository.findByUserId(TEST_USER_ID);
            assertNull(accountAfterEmailSent3.getVerifiedEmail());
            assertEquals(newEmail3, accountAfterEmailSent3.getEmailWaitingToBeVerified());
            assertFalse(accountAfterEmailSent3.isEmailVerified());
            String afterToken3 = accountAfterEmailSent3.getEmailVerificationToken();
            assertNotNull(afterToken3);
            assertNotEquals(beforeToken, afterToken3);
            LocalDateTime afterTime3 = accountAfterEmailSent3.getFirstCountOfSendingEmailVerificationEmailSetAt();
            assertNotNull(afterTime3);
            assertEquals(beforeTime, afterTime3);
            assertEquals(time3 - 5, accountAfterEmailSent3.getCountOfSendingEmailVerificationEmail());

            verify(emailSendingProcess, times(time3)).sendEmailVerificationEmail(any(Account.class));
            beforeTime = afterTime3;
            beforeToken = afterToken3;
        }

        AccountEmailUpdateRequestDto accountEmailUpdateRequestDto4 = new AccountEmailUpdateRequestDto();
        String newEmail4 = "new" + 11 + "@email.com";
        accountEmailUpdateRequestDto4.setEmail(newEmail4);
        mockMvc.perform(post(ACCOUNT_SETTING_ACCOUNT_EMAIL_URL)
                .param("email", accountEmailUpdateRequestDto4.getEmail())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("cannotSendError"))
                .andExpect(model().attributeDoesNotExist("accountNicknameUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(status().isOk())
                .andExpect(view().name(CANNOT_SEND_EMAIL_VERIFICATION_EMAIL_ERROR_VIEW_NAME));

        Account accountAfterEmailSent4 = accountRepository.findByUserId(TEST_USER_ID);
        assertNull(accountAfterEmailSent4.getVerifiedEmail());
        assertEquals("new10@email.com", accountAfterEmailSent4.getEmailWaitingToBeVerified());
        assertFalse(accountAfterEmailSent4.isEmailVerified());
        String afterToken4 = accountAfterEmailSent4.getEmailVerificationToken();
        assertNotNull(afterToken4);
        assertEquals(beforeToken, afterToken4);
        LocalDateTime afterTime4 = accountAfterEmailSent4.getFirstCountOfSendingEmailVerificationEmailSetAt();
        assertNotNull(afterTime4);
        assertEquals(beforeTime, afterTime4);
        assertEquals(5, accountAfterEmailSent4.getCountOfSendingEmailVerificationEmail());

        verify(emailSendingProcess, times(10)).sendEmailVerificationEmail(any(Account.class));
    }
}
