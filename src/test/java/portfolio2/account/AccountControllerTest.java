package portfolio2.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.service.AccountService;
import portfolio2.web.dto.SignUpRequestDto;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
    @SpringBootTest 에는 MockMvc가 없다.
    @AutoConfigureMockMvc를 추가해주면 사용할 수 있다.
* */
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("회원가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpRequestDto"));
    }

    @DisplayName("회원 가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {

        mockMvc.perform(post("/sign-up")
                .param("userId", "test Id")
                .param("nickname", "testNi ckname")
                .param("email", "test@e mail.com")
                .param("password", "12345 678")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {

        mockMvc.perform(post("/sign-up")
                .param("userId", "testId")
                .param("nickname", "testNickname")
                .param("email", "test@email.com")
                .param("password", "12345678")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("testId"));

        Account account = accountRepository.findByUserId("testId");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "12345678");
        assertNotNull(account.getEmailCheckToken());
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    void checkEmailtoken_with_wrong_input() throws Exception{
        mockMvc.perform(get("/check-email-token")
                .param("token", "asdfasdf")
                .param("email", "email@email.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(unauthenticated());
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    void checkEmailtoken() throws Exception{

        Account account = Account.builder()
                .userId("testUserId")
                .nickname("testNickname")
                .email("test@email.com")
                .password("12345678")
                .build();

        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(view().name("account/checked-email"))
                .andExpect(authenticated().withUsername("testUserId"));
    }

    @DisplayName("인증 메일 12시간당 5번 전송 제한 테스트")
    @Test
    void sendCheckEmailFiveTimesPer12Hours() throws Exception{

        // 회원가입 요청 폼 객체
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .userId("testUserId")
                .nickname("testNickname")
                .email("test@email.com")
                .password("12345678")
                .build();

        // 회원가입 등록
        accountService.processNewAccount(signUpRequestDto);

        // 회원가입한 계정 가져옴
        Account newAccount = accountRepository.findByUserId("testUserId");

        // 현재까지 이메일 전송 횟수
        int countSendEmail = newAccount.getSendCheckEmailCount();
        // 처음 이메일을 보낸 시간
        LocalDateTime firstTime = newAccount.getEmailCheckTokenFirstGeneratedAt();

        // 이메일 확인 토큰이 존재하고
        assertNotNull(newAccount.getEmailCheckToken());
        // 처음 이메일을 보낸 시간이 존재하고
        assertNotNull(newAccount.getEmailCheckTokenFirstGeneratedAt());
        // 이메일을 전송한 횟수가 1회이다.
        assertTrue(countSendEmail == 1);
        
        // 2번째부터 5번째까지 전송
        for(int i = 2; i <= 5; i++){
            // 5번째 까지는 보낼 수 있음.
            assertTrue(newAccount.canSendConfirmEmail());
            accountService.sendSignUpConfirmEmail(newAccount);
            // 이메일 전송 횟수 별도 카운트
            countSendEmail++;
        }

        // 계정에 저장된 이메일 전송 횟수와, 별도로 센 이메일 전송 횟수가 같다.
        assertEquals(newAccount.getSendCheckEmailCount(), countSendEmail);
        // 처음 이메일을 보낸 시간이 5번째 까지 유지된다.
        assertEquals(newAccount.getEmailCheckTokenFirstGeneratedAt(), firstTime);

        // 6번째 이메일을 12시간 내에 보내면, 이메일을 보낼 수 있음 체크 함수의 반환값이 false이다.
        assertFalse(newAccount.canSendConfirmEmail());
        // 처음 이메일을 보낸 시간을 현재 시간보다 12시간 이전으로 설정한다.
        newAccount.setEmailCheckTokenFirstGeneratedAt(firstTime.minusHours(12));
        System.out.println(newAccount.getEmailCheckTokenFirstGeneratedAt());
        // 12시간 이후에는 6번째 이메일을 보낼 수 있다.
        assertTrue(newAccount.canSendConfirmEmail());
        // 6번째 이메일을 보낸다.
        accountService.sendSignUpConfirmEmail(newAccount);
        // 12시간 후에 6번째 이메일을 보내면, 인증 이메일 전송 횟수가 1로 초기화된다.
        assertEquals(newAccount.getSendCheckEmailCount(), 1);
        // 처음 이메일을 보낸 시간 또한 현재 시간으로 초기화 된다.
        assertTrue(newAccount.getEmailCheckTokenFirstGeneratedAt().isAfter(firstTime.minusHours(12)));
        System.out.println(newAccount.getEmailCheckTokenFirstGeneratedAt());

    }
}