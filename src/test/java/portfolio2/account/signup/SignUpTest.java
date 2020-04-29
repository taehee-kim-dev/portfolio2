package portfolio2.account.signup;

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
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("회원가입 화면 보여주기")
    @Test
    void showSignUpPage() throws Exception{
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"));
    }

    @DisplayName("회원가입 POST 요청 - 모든 필드 정상")
    @Test
    void allValidFieldsSignUp() throws Exception{

        Account newAccountToSignUp = Account.builder()
                .userId("testUserId")
                .nickname("testNickname")
                .email("test@email.com")
                .password("testPassword")
                .build();

        mockMvc.perform(post("/sign-up")
                .param("userId", newAccountToSignUp.getUserId())
                .param("nickname", newAccountToSignUp.getNickname())
                .param("email", newAccountToSignUp.getEmail())
                .param("password", newAccountToSignUp.getPassword())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());

        then(javaMailSender).should().send(any(SimpleMailMessage.class));

        Account newAccountInDb = accountRepository.findByUserId(newAccountToSignUp.getUserId());

        assertEquals(newAccountToSignUp.getUserId(), newAccountInDb.getUserId());
        assertEquals(newAccountToSignUp.getNickname(), newAccountInDb.getNickname());
        assertEquals(newAccountToSignUp.getEmail(), newAccountInDb.getEmail());
        assertTrue(passwordEncoder.matches(newAccountToSignUp.getPassword(), newAccountInDb.getPassword()));

        assertEquals(newAccountInDb.isEmailVerified(), false);
        assertNotNull(newAccountInDb.getEmailCheckToken());
        assertNotNull(newAccountInDb.getEmailCheckTokenFirstGeneratedAt());
        assertEquals(newAccountInDb.getSendCheckEmailCount(), 1);



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

    }


    // userId errors.
    @DisplayName("회원가입 POST 요청 - 너무 짧은 userId 에러")
    @Test
    void signUpTooShortIdError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "ab")
                .param("nickname", "testNickname")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "tooShortUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 너무 긴 userId 에러")
    @Test
    void signUpTooLongIdError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "abcdeabcdeabcdeabcdeab")
                .param("nickname", "testNickname")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "tooLongUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 userId 에러")
    @Test
    void signUpInvalidFormatIdError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "sdf df")
                .param("nickname", "testNickname")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 이미 존재하는 userId 에러")
    @Test
    void signUpIdAlreadyExistsError() throws Exception{

        Account existingAccount = accountRepository.save(Account.builder()
                .userId("testUserId")
                .email("test@email.com")
                .nickname("testNickname")
                .build());

        mockMvc.perform(post("/sign-up")
                .param("userId", existingAccount.getUserId())
                .param("nickname", "testNickname1")
                .param("email", "test1@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "userIdAlreadyExists"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }



    // nickname errors.
    @DisplayName("회원가입 POST 요청 - 너무 짧은 nickname 에러")
    @Test
    void signUpTooShortNicknameError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId")
                .param("nickname", "ab")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "tooShortNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 너무 긴 nickname 에러")
    @Test
    void signUpTooLongNicknameError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId")
                .param("nickname", "testNicknametestNicknametestNickname")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "tooLongNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 nickname 에러")
    @Test
    void signUpInvalidFormatNicknameError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId")
                .param("nickname", "testNi ckname")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 이미 존재하는 nickname 에러")
    @Test
    void signUpNicknameAlreadyExistsError() throws Exception{

        Account existingAccount = accountRepository.save(Account.builder()
                .userId("testUserId")
                .email("test@email.com")
                .nickname("testNickname")
                .build());

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId1")
                .param("nickname", existingAccount.getNickname())
                .param("email", "test1@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "nicknameAlreadyExists"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }



    // email errors.
    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 email 에러")
    @Test
    void signUpInvalidFormatEmailError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId")
                .param("nickname", "testNickname")
                .param("email", "test@email")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "email",
                        "invalidFormatEmail"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 이미 존재하는 email 에러")
    @Test
    void signUpEmailAlreadyExistsError() throws Exception{

        Account existingAccount = accountRepository.save(Account.builder()
                .userId("testUserId")
                .email("test@email.com")
                .nickname("testNickname")
                .build());

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId1")
                .param("nickname", "testNickname")
                .param("email", existingAccount.getEmail())
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "email",
                        "emailAlreadyExists"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }


    // password errors.
    @DisplayName("회원가입 POST 요청 - 너무 짧은 password 에러")
    @Test
    void signUpTooShortPasswordError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId")
                .param("nickname", "testNickname")
                .param("email", "test@email.com")
                .param("password", "1234567")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "tooShortPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 너무 긴 password 에러")
    @Test
    void signUpTooLongPasswordError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId")
                .param("nickname", "testNickname")
                .param("email", "test@email.com")
                .param("password", "12345678123456781234567812345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "tooLongPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 password 에러")
    @Test
    void signUpInvalidFormatPasswordError() throws Exception{

        mockMvc.perform(post("/sign-up")
                .param("userId", "testUserId")
                .param("nickname", "testNickname")
                .param("email", "test@email.com")
                .param("password", "1234 5678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "invalidFormatPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }


}
