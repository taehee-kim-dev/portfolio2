package portfolio2.module.main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerified;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailNotVerifiedProcessForTest;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.main.config.StaticVariableNamesAboutMain.*;

/*
* ** 최종 결론 **
* 로그인은 무조건 SecurityContextHolder를 사용한 코드 직접 작성으로
* 로그인 인증 검증은 무조건 mockMvc로
* 로그아웃은 상관없으나 편의상 SecurityContextHolder를 사용한 코드 직접 작성으로
*
* 즉, 로그인, 로그아웃은 무조건 SecurityContextHolder를 사용한 코드 직접 작성으로
* 로그인 인증 검증은 무조건 mockMvc로.
*
* 코드로 이어서 중복 로그인하면 마지막 계정으로 인증정보 안바뀜.
* 즉, 코드로 로그인을 이후 인증 검증을 하되, 중간에 다른 계정으로 로그인 불가능.
* 단, 로그아웃 하고 새로 로그인한 상태로 mockMvc 검증을 하면 가능함.
* 한 번 mockMvc를 지나가면, 해당 인증 내용으로 고정됨.
*
* 최종 결론 : 로그인, 로그아웃은 SecurityContextHolder를 사용한 코드 직접 작성으로 하되,
* 검증은 맨 마지막에 mockMvc로 한다.
* */

@MockMvcTest
public class HomeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogInEmailNotVerifiedProcessForTest signUpAndLogInEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEmailNotVerifiedProcessForTest;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("홈 화면 보여주기 - 로그아웃 상태")
    @Test
    void showHomeLoggedOut() throws Exception{
        signUpAndLogOutEmailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postList"))
                .andExpect(view().name(HOME_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("홈 화면 보여주기 - 로그인 상태")
    @SignUpAndLogInEmailNotVerified
    @Test
    void showHomeLoggedIn() throws Exception{
        mockMvc.perform(get(HOME_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postList"))
                .andExpect(view().name(HOME_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

}
