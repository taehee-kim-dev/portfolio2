package portfolio2.account.profile;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.LogInAndOutProcessForTest;
import portfolio2.account.config.SignUpAndLogInProcessForTest;
import portfolio2.account.config.SignUpAndLogOutProcessForTest;
import portfolio2.account.config.SignUpAndLoggedIn;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.config.UrlAndViewName.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class ProfileShowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogInProcessForTest signUpAndLogInProcessForTest;

    @Autowired
    private SignUpAndLogOutProcessForTest signUpAndLogOutProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }


    @DisplayName("비로그인 상태에서 프로필 조회 - 존재하는 사용자의 프로필")
    @Test
    void searchExistingProfileNotLoggedIn() throws Exception{
        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SHOW_PROFILE_URL + '/' + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("searchedAccount"))
                .andExpect(model().attribute("isOwner", false))
                .andExpect(view().name(SHOW_PROFILE_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("비로그인 상태에서 프로필 조회 - 존재하지 않는 사용자의 프로필")
    @Test
    void searchNotExistingProfileNotLoggedIn() throws Exception{
        signUpAndLogOutProcessForTest.signUpAndLogOutDefault();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SHOW_PROFILE_URL + '/' + TEST_USER_ID_2))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("notFoundError"))
                .andExpect(model().attributeDoesNotExist("searchedAccount"))
                .andExpect(model().attributeDoesNotExist("isOwner"))
                .andExpect(view().name(SHOW_PROFILE_NOT_FOUND_ERORR_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그인 상태에서 본인 프로필 조회 - 존재하는 사용자의 프로필")
    @Test
    void searchOwnProfileLoggedIn() throws Exception{
        signUpAndLogInProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SHOW_PROFILE_URL + '/' + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("searchedAccount"))
                .andExpect(model().attribute("isOwner", true))
                .andExpect(view().name(SHOW_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("로그인 상태에서 다른 사람의 프로필 조회 - 존재하는 사용자의 프로필")
    @Test
    void searchOtherExistingProfileLoggedIn() throws Exception{
        signUpAndLogOutProcessForTest.signUpAndLogOutNotDefaultWith(TEST_USER_ID_2);
        signUpAndLogInProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SHOW_PROFILE_URL + '/' + TEST_USER_ID_2))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("searchedAccount"))
                .andExpect(model().attribute("isOwner", false))
                .andExpect(view().name(SHOW_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("로그인 상태에서 프로필 조회 - 존재하지 않는 사용자의 프로필")
    @Test
    void searchNotExistingProfileLoggedIn() throws Exception{
        signUpAndLogInProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SHOW_PROFILE_URL + '/' + TEST_USER_ID_2))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("notFoundError"))
                .andExpect(model().attributeDoesNotExist("searchedAccount"))
                .andExpect(model().attributeDoesNotExist("isOwner"))
                .andExpect(view().name(SHOW_PROFILE_NOT_FOUND_ERORR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }
}
