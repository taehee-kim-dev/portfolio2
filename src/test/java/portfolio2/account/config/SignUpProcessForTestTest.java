package portfolio2.account.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.email.EmailSendingProcess;
import portfolio2.domain.post.PostRepository;
import portfolio2.domain.tag.TagRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static portfolio2.account.config.TestAccountInfo.*;

@Slf4j
@SpringBootTest
public class SignUpProcessForTestTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogInEmailNotVerifiedProcessForTest signUpAndLogInEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEmailNotVerifiedProcessForTest;

    @Autowired

    @MockBean
    private EmailSendingProcess emailSendingProcess;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("회원가입 후 로그인 이메일 인증 안됨 테스트")
    @Test
    void signUpAndLoggedInEmailNotVerified(){
        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInDefault();

        Account signedUpAccount = accountRepository.findByUserId(TEST_USER_ID);
        // 이메일 인증 이메일 1회 발송 확인
        verify(emailSendingProcess, times(1)).sendEmailVerificationEmail(any(Account.class));

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
        assertNotNull(signedUpAccount.getFirstCountOfSendingEmailVerificationEmailSetAt());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, signedUpAccount.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(signedUpAccount.getJoinedAt());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(signedUpAccount.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(signedUpAccount.getBio());
        assertNull(signedUpAccount.getOccupation());
        assertNull(signedUpAccount.getLocation());
        assertNull(signedUpAccount.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationReplyOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationReplyOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationLikeOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyTagByWeb());

        // 이메일 알림 값 모두 false 확인
        assertFalse(signedUpAccount.isNotificationReplyOnMyPostByEmail());
        assertFalse(signedUpAccount.isNotificationReplyOnMyReplyByEmail());

        assertFalse(signedUpAccount.isNotificationLikeOnMyPostByEmail());
        assertFalse(signedUpAccount.isNotificationLikeOnMyReplyByEmail());

        assertFalse(signedUpAccount.isNotificationNewPostWithMyTagByEmail());

        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(signedUpAccount.getInterestTag());
        assertNotNull(signedUpAccount.getPost());

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 후 로그아웃 이메일 인증 안됨 테스트")
    @Test
    void signUpAndLoggedOutEmailNotVerified(){
        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();

        Account signedUpAccount = accountRepository.findByUserId(TEST_USER_ID);
        // 이메일 인증 이메일 1회 발송 확인
        verify(emailSendingProcess, times(1)).sendEmailVerificationEmail(any(Account.class));

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
        assertNotNull(signedUpAccount.getFirstCountOfSendingEmailVerificationEmailSetAt());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, signedUpAccount.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(signedUpAccount.getJoinedAt());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(signedUpAccount.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(signedUpAccount.getBio());
        assertNull(signedUpAccount.getOccupation());
        assertNull(signedUpAccount.getLocation());
        assertNull(signedUpAccount.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationReplyOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationReplyOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationLikeOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyTagByWeb());

        // 이메일 알림 값 모두 false 확인
        assertFalse(signedUpAccount.isNotificationReplyOnMyPostByEmail());
        assertFalse(signedUpAccount.isNotificationReplyOnMyReplyByEmail());

        assertFalse(signedUpAccount.isNotificationLikeOnMyPostByEmail());
        assertFalse(signedUpAccount.isNotificationLikeOnMyReplyByEmail());

        assertFalse(signedUpAccount.isNotificationNewPostWithMyTagByEmail());

        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(signedUpAccount.getInterestTag());
        assertNotNull(signedUpAccount.getPost());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());
    }

    @DisplayName("회원가입 후 로그인 이메일 인증 됨 테스트")
    @Test
    void signUpAndLoggedInEmailVerified(){
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();

        Account signedUpAccount = accountRepository.findByUserId(TEST_USER_ID);
        // 이메일 인증 이메일 1회 발송 확인
        verify(emailSendingProcess, times(1)).sendEmailVerificationEmail(any(Account.class));

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
        assertNotNull(signedUpAccount.getFirstCountOfSendingEmailVerificationEmailSetAt());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, signedUpAccount.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(signedUpAccount.getJoinedAt());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(signedUpAccount.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(signedUpAccount.getBio());
        assertNull(signedUpAccount.getOccupation());
        assertNull(signedUpAccount.getLocation());
        assertNull(signedUpAccount.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationReplyOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationReplyOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationLikeOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyTagByWeb());

        // 이메일 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationReplyOnMyPostByEmail());
        assertTrue(signedUpAccount.isNotificationReplyOnMyReplyByEmail());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByEmail());
        assertTrue(signedUpAccount.isNotificationLikeOnMyReplyByEmail());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyTagByEmail());

        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(signedUpAccount.getInterestTag());
        assertNotNull(signedUpAccount.getPost());

        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
    }

    @DisplayName("회원가입 후 로그아웃 이메일 인증 됨 테스트")
    @Test
    void signUpAndLoggedOutEmailVerified(){
        signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();

        Account signedUpAccount = accountRepository.findByUserId(TEST_USER_ID);
        // 이메일 인증 이메일 1회 발송 확인
        verify(emailSendingProcess, times(1)).sendEmailVerificationEmail(any(Account.class));

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
        assertNotNull(signedUpAccount.getFirstCountOfSendingEmailVerificationEmailSetAt());
        // 이메일 인증 발송 횟수 1 확인
        assertEquals(1, signedUpAccount.getCountOfSendingEmailVerificationEmail());
        // 회원 가입 시간 존재 확인
        assertNotNull(signedUpAccount.getJoinedAt());

        // 비밀번호 찾기 토큰 null 확인
        assertNull(signedUpAccount.getShowPasswordUpdatePageToken());


        // 프로필 값 모두 null 확인
        assertNull(signedUpAccount.getBio());
        assertNull(signedUpAccount.getOccupation());
        assertNull(signedUpAccount.getLocation());
        assertNull(signedUpAccount.getProfileImage());

        // Web 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationReplyOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationReplyOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByWeb());
        assertTrue(signedUpAccount.isNotificationLikeOnMyReplyByWeb());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyTagByWeb());

        // 이메일 알림 값 모두 true 확인
        assertTrue(signedUpAccount.isNotificationReplyOnMyPostByEmail());
        assertTrue(signedUpAccount.isNotificationReplyOnMyReplyByEmail());

        assertTrue(signedUpAccount.isNotificationLikeOnMyPostByEmail());
        assertTrue(signedUpAccount.isNotificationLikeOnMyReplyByEmail());

        assertTrue(signedUpAccount.isNotificationNewPostWithMyTagByEmail());

        // 태그, 포스트 초기 값 존재 확인
        assertNotNull(signedUpAccount.getInterestTag());
        assertNotNull(signedUpAccount.getPost());

        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());
    }
}
