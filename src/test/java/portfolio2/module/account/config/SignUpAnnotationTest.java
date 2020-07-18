package portfolio2.module.account.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.service.process.EmailSendingProcessForAccount;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static portfolio2.module.account.config.TestAccountInfo.*;

@MockMvcTest
public class SignUpAnnotationTest extends ContainerBaseTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @MockBean
    private EmailSendingProcessForAccount emailSendingProcessForAccount;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("회원가입 후 이메일 인증 안됨 테스트")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void signUpAndEmailNotVerified(){
        Account signedUpAccount = accountRepository.findByUserId(TEST_USER_ID);
        // 이메일 인증 이메일 1회 발송 확인
        verify(emailSendingProcessForAccount, times(1)).sendEmailVerificationEmail(any(Account.class));

        // 아이디, 닉네임 확인
        assertEquals(TEST_USER_ID, signedUpAccount.getUserId());
        assertEquals(TEST_NICKNAME, signedUpAccount.getNickname());

        // 인증된 메일 null 확인
        assertNull(signedUpAccount.getVerifiedEmail());

        // 비밀번호 암호화 확인
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, signedUpAccount.getPassword()));

        // 인증 대기 이메일 값 일치 확인
        assertEquals(TEST_EMAIL, signedUpAccount.getEmailWaitingToBeVerified());
        // 이메일 처음 인증 상태 false 확인
        assertFalse(signedUpAccount.isEmailFirstVerified());
        // 이메일 인증 상태 false 확인
        assertFalse(signedUpAccount.isEmailVerified());
        // 이메일 인증 토큰 값 존재 확인
        assertNotNull(signedUpAccount.getEmailVerificationToken());
        // 이메일 인증 토큰 생성 시간 존재 확인
        assertNotNull(signedUpAccount.getFirstCountOfSendingEmailVerificationEmailSetDateTime());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, signedUpAccount.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(signedUpAccount.getSignUpDateTime());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(signedUpAccount.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(signedUpAccount.getBio());
        assertNull(signedUpAccount.getOccupation());
        assertNull(signedUpAccount.getLocation());
        assertNull(signedUpAccount.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationCommentOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationCommentOnMyCommentByWeb());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationLikeOnMyCommentByWeb());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyInterestTagByWeb());

        // 이메일 알림 값 모두 false 확인
        assertFalse(signedUpAccount.isNotificationCommentOnMyPostByEmail());
        assertFalse(signedUpAccount.isNotificationCommentOnMyCommentByEmail());

        assertFalse(signedUpAccount.isNotificationLikeOnMyPostByEmail());
        assertFalse(signedUpAccount.isNotificationLikeOnMyCommentByEmail());

        assertFalse(signedUpAccount.isNotificationNewPostWithMyInterestTagByEmail());

        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(signedUpAccount.getInterestTag());

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 후 이메일 인증 됨 테스트")
    @SignUpAndLoggedInEmailVerified
    @Test
    void signUpAndEmailVerified(){
        Account signedUpAccount = accountRepository.findByUserId(TEST_USER_ID);
        // 이메일 인증 이메일 1회 발송 확인
        verify(emailSendingProcessForAccount, times(1)).sendEmailVerificationEmail(any(Account.class));

        // 아이디, 닉네임 확인
        assertEquals(TEST_USER_ID, signedUpAccount.getUserId());
        assertEquals(TEST_NICKNAME, signedUpAccount.getNickname());

        // 인증된 메일 null 아님 확인
        assertNotNull(signedUpAccount.getVerifiedEmail());

        // 비밀번호 암호화 확인
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, signedUpAccount.getPassword()));

        // 인증 대기 이메일 값 null 확인
        assertNull(signedUpAccount.getEmailWaitingToBeVerified());
        // 이메일 처음 인증 상태 true 확인
        assertTrue(signedUpAccount.isEmailFirstVerified());
        // 이메일 인증 상태 true 확인
        assertTrue(signedUpAccount.isEmailVerified());
        // 이메일 인증 토큰 값 null 확인
        assertNull(signedUpAccount.getEmailVerificationToken());
        // 이메일 인증 토큰 생성 시간 존재 확인
        assertNotNull(signedUpAccount.getFirstCountOfSendingEmailVerificationEmailSetDateTime());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, signedUpAccount.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(signedUpAccount.getSignUpDateTime());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(signedUpAccount.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(signedUpAccount.getBio());
        assertNull(signedUpAccount.getOccupation());
        assertNull(signedUpAccount.getLocation());
        assertNull(signedUpAccount.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationCommentOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationCommentOnMyCommentByWeb());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationLikeOnMyCommentByWeb());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyInterestTagByWeb());

        // 이메일 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationCommentOnMyPostByEmail());
        assertTrue(signedUpAccount.isNotificationCommentOnMyCommentByEmail());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByEmail());
        assertTrue(signedUpAccount.isNotificationLikeOnMyCommentByEmail());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyInterestTagByEmail());

        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(signedUpAccount.getInterestTag());

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
    }
}
