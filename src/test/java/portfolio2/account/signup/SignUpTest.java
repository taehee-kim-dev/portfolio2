package portfolio2.account.signup;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.testaccountinfo.SignUpAndLoggedIn;
import portfolio2.account.testaccountinfo.TestAccountInfo;
import portfolio2.controller.account.SignUpController;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.CustomPrincipal;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.mail.EmailMessage;
import portfolio2.mail.EmailService;
import portfolio2.service.account.SignUpService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class SignUpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @SignUpAndLoggedIn
    @DisplayName("SecurityContextHolder 확인")
    @Test
    void testSecurityContextHolder() throws Exception{

        CustomPrincipal customPrincipal
                = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("*** 테스트 출력 ***");
        log.info(customPrincipal.getSessionAccount().getUserId());
    }

    @DisplayName("회원가입 화면 보여주기 - 비로그인 상태")
    @Test
    void showSignUpPageWithoutLogIn() throws Exception{
        mockMvc.perform(get(SignUpController.SIGN_UP_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name(SignUpController.SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @SignUpAndLoggedIn
    @DisplayName("회원가입 화면 보여주기 - 로그인 상태")
    @Test
    void showSignUpPageWithLogIn() throws Exception{
        mockMvc.perform(get(SignUpController.SIGN_UP_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(TestAccountInfo.TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 모든 필드 정상 - 비로그인 상태")
    @Test
    void allValidFieldsSignUpWithoutLogIn() throws Exception{

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TestAccountInfo.TEST_USER_ID)
                .nickname(TestAccountInfo.TEST_NICKNAME)
                .email(TestAccountInfo.TEST_EMAIL)
                .password(TestAccountInfo.TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/sign-up")
                .param("userId", signUpRequestDto.getUserId())
                .param("nickname", signUpRequestDto.getNickname())
                .param("email", signUpRequestDto.getEmail())
                .param("password", signUpRequestDto.getPassword())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("email"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/email-verification-request"))
                .andExpect(authenticated().withUsername(TestAccountInfo.TEST_USER_ID));

        then(emailService).should().sendEmail(any(EmailMessage.class));

        Account newAccountInDb = accountRepository.findByUserId(signUpRequestDto.getUserId());

        assertEquals(newAccountInDb.getUserId(), signUpRequestDto.getUserId());
        assertEquals(newAccountInDb.getNickname(), signUpRequestDto.getNickname());
        assertNull(newAccountInDb.getVerifiedEmail());
        assertTrue(passwordEncoder.matches(signUpRequestDto.getPassword(), newAccountInDb.getPassword()));

        assertEquals(newAccountInDb.getEmailWaitingToBeVerified(), signUpRequestDto.getEmail());
        assertFalse(newAccountInDb.isEmailVerified());
        assertNotNull(newAccountInDb.getEmailVerificationToken());
        assertNotNull(newAccountInDb.getEmailVerificationTokenFirstGeneratedAt());
        assertEquals(newAccountInDb.getCountOfSendingEmailVerificationEmail(), 1);
        assertNotNull(newAccountInDb.getJoinedAt());

        assertNull(newAccountInDb.getFindPasswordToken());
        assertNull(newAccountInDb.getFindPasswordTokenFirstGeneratedAt());
        assertEquals(newAccountInDb.getCountOfSendingFindPasswordEmail(), 0);

        assertNull(newAccountInDb.getBio());
        assertNull(newAccountInDb.getOccupation());
        assertNull(newAccountInDb.getLocation());
        assertNull(newAccountInDb.getProfileImage());


        assertTrue(newAccountInDb.isNotificationReplyOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationReplyOnMyReplyByWeb());

        assertTrue(newAccountInDb.isNotificationLikeOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationLikeOnMyReplyByWeb());

        assertTrue(newAccountInDb.isNotificationNewPostWithMyTagByWeb());


        assertFalse(newAccountInDb.isNotificationReplyOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationReplyOnMyReplyByEmail());

        assertFalse(newAccountInDb.isNotificationLikeOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationLikeOnMyReplyByEmail());

        assertFalse(newAccountInDb.isNotificationNewPostWithMyTagByEmail());


        assertNotNull(newAccountInDb.getTag());
        assertNotNull(newAccountInDb.getPost());
    }

    @SignUpAndLoggedIn
    @DisplayName("회원가입 POST 요청 - 모든 필드 정상 - 로그인 상태")
    @Test
    void allValidFieldsSignUpWithLogIn() throws Exception{

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId("testUserId2")
                .nickname("testNickname2")
                .email("test2@email.com")
                .password(TestAccountInfo.TEST_PASSWORD)
                .build();

        mockMvc.perform(post("/sign-up")
                .param("userId", signUpRequestDto.getUserId())
                .param("nickname", signUpRequestDto.getNickname())
                .param("email", signUpRequestDto.getEmail())
                .param("password", signUpRequestDto.getPassword())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername(TestAccountInfo.TEST_USER_ID));

        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));

        Account newAccountInDb = accountRepository.findByUserId(signUpRequestDto.getUserId());
        assertNull(newAccountInDb);
    }

//
//
//    // userId errors.
//    @DisplayName("회원가입 POST 요청 - 너무 짧은 userId 에러")
//    @Test
//    void signUpTooShortIdError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "ab")
//                .param("nickname", "testNickname")
//                .param("email", "test@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "userId",
//                        "tooShortUserId"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 너무 긴 userId 에러")
//    @Test
//    void signUpTooLongIdError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "abcdeabcdeabcdeabcdeab")
//                .param("nickname", "testNickname")
//                .param("email", "test@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "userId",
//                        "tooLongUserId"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 userId 에러")
//    @Test
//    void signUpInvalidFormatIdError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "sdf df")
//                .param("nickname", "testNickname")
//                .param("email", "test@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "userId",
//                        "invalidFormatUserId"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 이미 존재하는 userId 에러")
//    @Test
//    void signUpIdAlreadyExistsError() throws Exception{
//
//        Account existingAccount = accountRepository.save(Account.builder()
//                .userId("testUserId")
//                .email("test@email.com")
//                .nickname("testNickname")
//                .build());
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", existingAccount.getUserId())
//                .param("nickname", "testNickname1")
//                .param("email", "test1@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "userId",
//                        "userIdAlreadyExists"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//
//
//    // nickname errors.
//    @DisplayName("회원가입 POST 요청 - 너무 짧은 nickname 에러")
//    @Test
//    void signUpTooShortNicknameError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId")
//                .param("nickname", "ab")
//                .param("email", "test@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "nickname",
//                        "tooShortNickname"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 너무 긴 nickname 에러")
//    @Test
//    void signUpTooLongNicknameError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId")
//                .param("nickname", "testNicknametestNicknametestNickname")
//                .param("email", "test@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "nickname",
//                        "tooLongNickname"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 nickname 에러")
//    @Test
//    void signUpInvalidFormatNicknameError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId")
//                .param("nickname", "testNi ckname")
//                .param("email", "test@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "nickname",
//                        "invalidFormatNickname"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 이미 존재하는 nickname 에러")
//    @Test
//    void signUpNicknameAlreadyExistsError() throws Exception{
//
//        Account existingAccount = accountRepository.save(Account.builder()
//                .userId("testUserId")
//                .email("test@email.com")
//                .nickname("testNickname")
//                .build());
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId1")
//                .param("nickname", existingAccount.getNickname())
//                .param("email", "test1@email.com")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "nickname",
//                        "nicknameAlreadyExists"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//
//
//    // email errors.
//    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 email 에러")
//    @Test
//    void signUpInvalidFormatEmailError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId")
//                .param("nickname", "testNickname")
//                .param("email", "test@email")
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "email",
//                        "invalidFormatEmail"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 이미 존재하는 email 에러")
//    @Test
//    void signUpEmailAlreadyExistsError() throws Exception{
//
//        Account existingAccount = accountRepository.save(Account.builder()
//                .userId("testUserId")
//                .email("test@email.com")
//                .nickname("testNickname")
//                .build());
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId1")
//                .param("nickname", "testNickname")
//                .param("email", existingAccount.getEmail())
//                .param("password", "12345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "email",
//                        "emailAlreadyExists"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//
//    // password errors.
//    @DisplayName("회원가입 POST 요청 - 너무 짧은 password 에러")
//    @Test
//    void signUpTooShortPasswordError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId")
//                .param("nickname", "testNickname")
//                .param("email", "test@email.com")
//                .param("password", "1234567")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "password",
//                        "tooShortPassword"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 너무 긴 password 에러")
//    @Test
//    void signUpTooLongPasswordError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId")
//                .param("nickname", "testNickname")
//                .param("email", "test@email.com")
//                .param("password", "12345678123456781234567812345678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "password",
//                        "tooLongPassword"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }
//
//    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 password 에러")
//    @Test
//    void signUpInvalidFormatPasswordError() throws Exception{
//
//        mockMvc.perform(post("/sign-up")
//                .param("userId", "testUserId")
//                .param("nickname", "testNickname")
//                .param("email", "test@email.com")
//                .param("password", "1234 5678")
//                .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(model().hasErrors())
//                .andExpect(model().attributeHasFieldErrorCode(
//                        "signUpRequestDto",
//                        "password",
//                        "invalidFormatPassword"))
//                .andExpect(model().attributeExists("signUpRequestDto"))
//                .andExpect(view().name("account/sign-up"))
//                .andExpect(unauthenticated());
//    }


}
