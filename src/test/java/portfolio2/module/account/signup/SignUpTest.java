package portfolio2.module.account.signup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.*;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.SignUpRequestDto;
import portfolio2.infra.email.EmailMessage;
import portfolio2.infra.email.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.*;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@MockMvcTest
public class SignUpTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEmailNotVerifiedProcessForTest;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("회원가입 화면 보여주기 - 비로그인 상태")
    @Test
    void showSignUpPageWithoutLogIn() throws Exception{
        mockMvc.perform(get(SIGN_UP_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 화면 보여주기 - 로그인 상태")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void showSignUpPageWithLogIn() throws Exception{
        mockMvc.perform(get(SIGN_UP_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 모든 필드 정상 - 비로그인 상태1")
    @Test
    void allValidFieldsSignUpWithoutLogIn1() throws Exception{

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TEST_USER_ID)
                .nickname(TEST_NICKNAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", signUpRequestDto.getUserId())
                .param("nickname", signUpRequestDto.getNickname())
                .param("email", signUpRequestDto.getEmail())
                .param("password", signUpRequestDto.getPassword())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(view().name(EMAIL_VERIFICATION_REQUEST_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
        
        // 이메일 인증 이메일 1회 발송 확인
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));


        Account newAccountInDb = accountRepository.findByUserId(signUpRequestDto.getUserId());
        
        // 아이디, 닉네임 확인
        assertEquals(signUpRequestDto.getUserId(), newAccountInDb.getUserId());
        assertEquals(signUpRequestDto.getNickname(), newAccountInDb.getNickname());
        
        // 인증된 메일 null 확인
        assertNull(newAccountInDb.getVerifiedEmail());
        
        // 비밀번호 암호화 확인
        assertTrue(passwordEncoder.matches(signUpRequestDto.getPassword(), newAccountInDb.getPassword()));
        
        // 인증 대기 이메일 값 일치 확인
        assertEquals(signUpRequestDto.getEmail(), newAccountInDb.getEmailWaitingToBeVerified());
        // 이메일 처음 인증 상태 false 확인
        assertFalse(newAccountInDb.isEmailFirstVerified());
        // 이메일 인증 상태 false 확인
        assertFalse(newAccountInDb.isEmailVerified());
        // 이메일 인증 토큰 값 존재 확인
        assertNotNull(newAccountInDb.getEmailVerificationToken());
        // 이메일 인증 토큰 생성 시간 존재 확인
        assertNotNull(newAccountInDb.getFirstCountOfSendingEmailVerificationEmailSetDateTime());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, newAccountInDb.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(newAccountInDb.getSignUpDateTime());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(newAccountInDb.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(newAccountInDb.getBio());
        assertNull(newAccountInDb.getOccupation());
        assertNull(newAccountInDb.getLocation());
        assertNull(newAccountInDb.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(newAccountInDb.isNotificationCommentOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationCommentOnMyCommentByWeb());

        assertTrue(newAccountInDb.isNotificationLikeOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationLikeOnMyCommentByWeb());

        assertTrue(newAccountInDb.isNotificationNewPostWithMyInterestTagByWeb());

        // 이메일 알림 값 모두 false 확인
        assertFalse(newAccountInDb.isNotificationCommentOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationCommentOnMyCommentByEmail());

        assertFalse(newAccountInDb.isNotificationLikeOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationLikeOnMyCommentByEmail());

        assertFalse(newAccountInDb.isNotificationNewPostWithMyInterestTagByEmail());

        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(newAccountInDb.getInterestTag());
    }

    @DisplayName("회원가입 POST 요청 - 모든 필드 정상 - 비로그인 상태2")
    @Test
    void allValidFieldsSignUpWithoutLogIn2() throws Exception{

        final String userId = "user_-id.";
        final String nickname = "aA_-.";
        final String password = "aA._-%$&^%{}32094";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(userId)
                .nickname(nickname)
                .email(TEST_EMAIL)
                .password(password)
                .build();

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", userId)
                .param("nickname", nickname)
                .param("email", signUpRequestDto.getEmail())
                .param("password", signUpRequestDto.getPassword())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(view().name(EMAIL_VERIFICATION_REQUEST_VIEW_NAME))
                .andExpect(authenticated().withUsername(userId));

        // 이메일 인증 이메일 1회 발송 확인
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));


        Account newAccountInDb = accountRepository.findByUserId(signUpRequestDto.getUserId());

        // 아이디, 닉네임 확인
        assertEquals(signUpRequestDto.getUserId(), newAccountInDb.getUserId());
        assertEquals(signUpRequestDto.getNickname(), newAccountInDb.getNickname());

        // 인증된 메일 null 확인
        assertNull(newAccountInDb.getVerifiedEmail());

        // 비밀번호 암호화 확인
        assertTrue(passwordEncoder.matches(signUpRequestDto.getPassword(), newAccountInDb.getPassword()));

        // 인증 대기 이메일 값 일치 확인
        assertEquals(signUpRequestDto.getEmail(), newAccountInDb.getEmailWaitingToBeVerified());
        // 이메일 처음 인증 상태 false 확인
        assertFalse(newAccountInDb.isEmailFirstVerified());
        // 이메일 인증 상태 false 확인
        assertFalse(newAccountInDb.isEmailVerified());
        // 이메일 인증 토큰 값 존재 확인
        assertNotNull(newAccountInDb.getEmailVerificationToken());
        // 이메일 인증 토큰 생성 시간 존재 확인
        assertNotNull(newAccountInDb.getFirstCountOfSendingEmailVerificationEmailSetDateTime());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, newAccountInDb.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(newAccountInDb.getSignUpDateTime());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(newAccountInDb.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(newAccountInDb.getBio());
        assertNull(newAccountInDb.getOccupation());
        assertNull(newAccountInDb.getLocation());
        assertNull(newAccountInDb.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(newAccountInDb.isNotificationCommentOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationCommentOnMyCommentByWeb());

        assertTrue(newAccountInDb.isNotificationLikeOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationLikeOnMyCommentByWeb());

        assertTrue(newAccountInDb.isNotificationNewPostWithMyInterestTagByWeb());
        assertTrue(newAccountInDb.isNotificationMyInterestTagAddedToExistingPostByWeb());

        // 이메일 알림 값 모두 false 확인
        assertFalse(newAccountInDb.isNotificationCommentOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationCommentOnMyCommentByEmail());

        assertFalse(newAccountInDb.isNotificationLikeOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationLikeOnMyCommentByEmail());

        assertFalse(newAccountInDb.isNotificationNewPostWithMyInterestTagByEmail());
        assertFalse(newAccountInDb.isNotificationMyInterestTagAddedToExistingPostByEmail());
        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(newAccountInDb.getInterestTag());
    }

    @DisplayName("회원가입 POST 요청 - 모든 필드 정상 - 비로그인 상태3")
    @Test
    void allValidFieldsSignUpWithoutLogIn3() throws Exception{

        final String userId = "user_-id1.";
        final String nickname = "aA2_-.";
        final String password = "aAㄱㅏ5캌`-=[]\\;',./~!@#$%^&*()_+{}|:\"<>?";
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(userId)
                .nickname(nickname)
                .email(TEST_EMAIL)
                .password(password)
                .build();

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", userId)
                .param("nickname", nickname)
                .param("email", signUpRequestDto.getEmail())
                .param("password", signUpRequestDto.getPassword())
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(view().name(EMAIL_VERIFICATION_REQUEST_VIEW_NAME))
                .andExpect(authenticated().withUsername(userId));

        // 이메일 인증 이메일 1회 발송 확인
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));


        Account newAccountInDb = accountRepository.findByUserId(signUpRequestDto.getUserId());

        // 아이디, 닉네임 확인
        assertEquals(signUpRequestDto.getUserId(), newAccountInDb.getUserId());
        assertEquals(signUpRequestDto.getNickname(), newAccountInDb.getNickname());

        // 인증된 메일 null 확인
        assertNull(newAccountInDb.getVerifiedEmail());

        // 비밀번호 암호화 확인
        assertTrue(passwordEncoder.matches(signUpRequestDto.getPassword(), newAccountInDb.getPassword()));

        // 인증 대기 이메일 값 일치 확인
        assertEquals(signUpRequestDto.getEmail(), newAccountInDb.getEmailWaitingToBeVerified());
        // 이메일 처음 인증 상태 false 확인
        assertFalse(newAccountInDb.isEmailFirstVerified());
        // 이메일 인증 상태 false 확인
        assertFalse(newAccountInDb.isEmailVerified());
        // 이메일 인증 토큰 값 존재 확인
        assertNotNull(newAccountInDb.getEmailVerificationToken());
        // 이메일 인증 토큰 생성 시간 존재 확인
        assertNotNull(newAccountInDb.getFirstCountOfSendingEmailVerificationEmailSetDateTime());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, newAccountInDb.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(newAccountInDb.getSignUpDateTime());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(newAccountInDb.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(newAccountInDb.getBio());
        assertNull(newAccountInDb.getOccupation());
        assertNull(newAccountInDb.getLocation());
        assertNull(newAccountInDb.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(newAccountInDb.isNotificationCommentOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationCommentOnMyCommentByWeb());

        assertTrue(newAccountInDb.isNotificationLikeOnMyPostByWeb());
        assertTrue(newAccountInDb.isNotificationLikeOnMyCommentByWeb());

        assertTrue(newAccountInDb.isNotificationNewPostWithMyInterestTagByWeb());
        assertTrue(newAccountInDb.isNotificationMyInterestTagAddedToExistingPostByWeb());

        // 이메일 알림 값 모두 false 확인
        assertFalse(newAccountInDb.isNotificationCommentOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationCommentOnMyCommentByEmail());

        assertFalse(newAccountInDb.isNotificationLikeOnMyPostByEmail());
        assertFalse(newAccountInDb.isNotificationLikeOnMyCommentByEmail());

        assertFalse(newAccountInDb.isNotificationNewPostWithMyInterestTagByEmail());
        assertFalse(newAccountInDb.isNotificationMyInterestTagAddedToExistingPostByEmail());
        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(newAccountInDb.getInterestTag());
    }

    @DisplayName("회원가입 POST 요청 - 모든 필드 정상 - 로그인 상태")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void allValidFieldsSignUpWithLogIn() throws Exception{

        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId(TEST_USER_ID_2)
                .nickname(TEST_NICKNAME_2)
                .email(TEST_EMAIL_2)
                .password(TEST_PASSWORD_2)
                .build();

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", signUpRequestDto.getUserId())
                .param("nickname", signUpRequestDto.getNickname())
                .param("email", signUpRequestDto.getEmail())
                .param("password", signUpRequestDto.getPassword())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        // 이메일 인증 이메일 총 1회만 전송됨
        verify(emailService, times(1)).sendEmail(any(EmailMessage.class));

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID_2));
    }


    // userId errors.
    @DisplayName("회원가입 POST 요청 - 너무 짧은 userId 에러")
    @Test
    void signUpTooShortUserIdError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "ab")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 너무 긴 userId 에러")
    @Test
    void signUpTooLongUserIdError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "abcdeabcdeabcdeabcdea")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }


    @DisplayName("회원가입 POST 요청 - 대문자 userId 에러")
    @Test
    void signUpInvalidUpperCaseOfUserIdError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "useridA")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 한글조합문자 userId 에러")
    @Test
    void signUpInvalidKoreanOfUserIdError1() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "userId핳")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 한글자음 userId 에러")
    @Test
    void signUpInvalidKoreanOfUserIdError2() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "userIdㅈ")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 한글모음 userId 에러")
    @Test
    void signUpInvalidKoreanOfUserIdError3() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "userIdㅡ")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 공백문자 userId 에러1")
    @Test
    void signUpInvalidWhiteSpaceOfUserIdError1() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "sdf df")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 공백문자 userId 에러2")
    @Test
    void signUpInvalidWhiteSpaceOfUserIdError2() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "sdf\tdf")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 공백문자 userId 에러2")
    @Test
    void signUpInvalidWhiteSpaceOfUserIdError3() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "sdf\ndf")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }

    @DisplayName("회원가입 POST 요청 - 특수문자 userId 에러")
    @Test
    void signUpInvalidSpecialCharacterOfUserIdError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "sdf%df")
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        // 가입되지 않음 확인
        assertFalse(accountRepository.existsByNickname(TEST_NICKNAME));
    }


    @DisplayName("회원가입 POST 요청 - 이미 존재하는 userId 에러")
    @Test
    void signUpUserIdAlreadyExistsError() throws Exception{

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
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
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertTrue(accountRepository.existsByUserId(TEST_USER_ID));
        assertFalse(accountRepository.existsByNickname("testNickname1"));
    }


    // nickname errors.

    @DisplayName("회원가입 POST 요청 - 너무 짧은 nickname 에러")
    @Test
    void signUpTooShortNicknameError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "a")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 너무 긴 nickname 에러")
    @Test
    void signUpTooLongNicknameError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "abcdeabcdeabcdeabcdea")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }



    @DisplayName("회원가입 POST 요청 - 한글자음 nickname 에러")
    @Test
    void signUpKoreanNicknameError1() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "testNiㅁckname")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 한글모음 nickname 에러")
    @Test
    void signUpKoreanNicknameError2() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "testNiㅏckname")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 공백문자 nickname 에러1")
    @Test
    void signUpWhiteSpaceNicknameError1() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "testNi ckname")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 공백문자 nickname 에러2")
    @Test
    void signUpWhiteSpaceNicknameError2() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "testNickname\t")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 공백문자 nickname 에러3")
    @Test
    void signUpWhiteSpaceNicknameError3() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "testNickname\n")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 특수문자 nickname 에러")
    @Test
    void signUpSpecialCharacterNicknameError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", "testNi&ckname")
                .param("email", TEST_EMAIL)
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "nickname",
                        "invalidFormatNickname"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 이미 존재하는 nickname 에러")
    @Test
    void signUpNicknameAlreadyExistsError() throws Exception{

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "testUserId1")
                .param("nickname", TEST_NICKNAME)
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
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertTrue(accountRepository.existsByUserId(TEST_USER_ID));
        assertFalse(accountRepository.existsByUserId("test1@email.com"));
    }


    // email errors.

    @DisplayName("회원가입 POST 요청 - 형식에 맞지 않는 email 에러")
    @Test
    void signUpInvalidFormatEmailError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", TEST_NICKNAME)
                .param("email", "test@email")
                .param("password", TEST_PASSWORD)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "email",
                        "invalidFormatEmail"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 인증 대기중인 이메일로 존재하는 email 성공")
    @Test
    void signUpEmailAlreadyExistsAsEmailWaitingToBeVerifiedError() throws Exception{

        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(TEST_EMAIL, existingAccount.getEmailWaitingToBeVerified());
        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID_1)
                .param("nickname", "testNickname1")
                .param("email", TEST_EMAIL)
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("email"))
                .andExpect(view().name(EMAIL_VERIFICATION_REQUEST_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID_1));

        assertTrue(accountRepository.existsByUserId(TEST_USER_ID));
        assertTrue(accountRepository.existsByUserId(TEST_USER_ID_1));


    }

    @DisplayName("회원가입 POST 요청 - 이미 인증된 이메일로 존재하는 email 에러")
    @Test
    void signUpEmailAlreadyExistsAsVerifiedEmailError() throws Exception{

        Account existingAccount = signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        existingAccount.setVerifiedEmail(TEST_EMAIL);
        accountRepository.save(existingAccount);

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID_1)
                .param("nickname", "testNickname1")
                .param("email", TEST_EMAIL)
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "email",
                        "emailAlreadyExists"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertTrue(accountRepository.existsByUserId(TEST_USER_ID));
        assertFalse(accountRepository.existsByUserId(TEST_USER_ID_1));
    }


    // password errors.

    @DisplayName("회원가입 POST 요청 - 너무 짧은 password 에러")
    @Test
    void signUpTooShortPasswordError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", "1234567")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "invalidFormatPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 너무 긴 password 에러")
    @Test
    void signUpTooLongPasswordError() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", "123456789012345678901234567890123456789012345678901")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "invalidFormatPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 공백 password 에러1")
    @Test
    void signUpInvalidWhiteSpacePasswordError1() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", "1234 5678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "invalidFormatPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 공백 password2 에러")
    @Test
    void signUpInvalidWhiteSpacePasswordError2() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", "12345678\t")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "invalidFormatPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 POST 요청 - 공백 password 에러3")
    @Test
    void signUpInvalidWhiteSpacePasswordError3() throws Exception{

        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", TEST_USER_ID)
                .param("nickname", TEST_NICKNAME)
                .param("email", TEST_EMAIL)
                .param("password", "12345678\n")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "invalidFormatPassword"))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

    @DisplayName("입력 에러 모두 각각 출력")
    @Test
    void displayAllErrorCodes() throws Exception{
        mockMvc.perform(post(SIGN_UP_URL)
                .param("userId", "a a")
                .param("nickname", TEST_NICKNAME)
                .param("email", "asdfasdf@email")
                .param("password", "12345 67812345678123456781234567")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "userId",
                        "invalidFormatUserId"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "email",
                        "invalidFormatEmail"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "signUpRequestDto",
                        "password",
                        "invalidFormatPassword"))
                .andExpect(model().attributeErrorCount("signUpRequestDto", 3))
                .andExpect(model().attributeExists("signUpRequestDto"))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(view().name(SIGN_UP_VIEW_NAME))
                .andExpect(unauthenticated());

        assertFalse(accountRepository.existsByUserId(TEST_USER_ID));
    }

}
