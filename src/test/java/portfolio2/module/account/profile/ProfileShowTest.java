package portfolio2.module.account.profile;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailNotVerifiedProcessForTest;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.tag.TagRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@MockMvcTest
public class ProfileShowTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SignUpAndLogInEmailNotVerifiedProcessForTest signUpAndLogInEmailNotVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailNotVerifiedProcessForTest signUpAndLogOutEMailNotVerifiedProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @Autowired
    private TagRepository tagRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }


    @DisplayName("비로그인 상태에서 프로필 조회 - 존재하는 사용자의 프로필")
    @Test
    void searchExistingProfileNotLoggedIn() throws Exception{
        signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(PROFILE_VIEW_URL + '/' + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("searchedAccount"))
                .andExpect(model().attribute("isOwner", false))
                .andExpect(view().name(PROFILE_VIEW_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("비로그인 상태에서 프로필 조회 - 존재하지 않는 사용자의 프로필")
    @Test
    void searchNotExistingProfileNotLoggedIn() throws Exception{
        signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutDefault();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(PROFILE_VIEW_URL + '/' + TEST_USER_ID_2))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("errorTitle"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(model().attributeDoesNotExist("searchedAccount"))
                .andExpect(model().attributeDoesNotExist("isOwner"))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그인 상태에서 본인 프로필 조회 - 존재하는 사용자의 프로필")
    @Test
    void searchOwnProfileLoggedIn() throws Exception{
        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(PROFILE_VIEW_URL + '/' + TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("searchedAccount"))
                .andExpect(model().attribute("isOwner", true))
                .andExpect(view().name(PROFILE_VIEW_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("로그인 상태에서 다른 사람의 프로필 조회 - 존재하는 사용자의 프로필")
    @Test
    void searchOtherExistingProfileLoggedIn() throws Exception{
        signUpAndLogOutEMailNotVerifiedProcessForTest.signUpAndLogOutNotDefaultWith(TEST_USER_ID_2);
        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(PROFILE_VIEW_URL + '/' + TEST_USER_ID_2))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("searchedAccount"))
                .andExpect(model().attribute("isOwner", false))
                .andExpect(view().name(PROFILE_VIEW_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("로그인 상태에서 프로필 조회 - 존재하지 않는 사용자의 프로필")
    @Test
    void searchNotExistingProfileLoggedIn() throws Exception{
        signUpAndLogInEmailNotVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(PROFILE_VIEW_URL + '/' + TEST_USER_ID_2))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("errorTitle"))
                .andExpect(model().attributeDoesNotExist("searchedAccount"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(model().attributeDoesNotExist("isOwner"))
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }
}
