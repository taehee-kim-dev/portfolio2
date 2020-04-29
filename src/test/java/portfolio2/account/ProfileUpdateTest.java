package portfolio2.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import portfolio2.SignUpAndLoggedIn;
import portfolio2.TestAccountInfo;
import portfolio2.controller.account.AccountSettingController;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ProfileUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("프로필 화면 보여주기 - 정상 userId")
    @SignUpAndLoggedIn
    @Test
    void showProfileView() throws Exception{

        mockMvc.perform(get("/account/profile/testUserId"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("accountInDb"))
                .andExpect(model().attributeExists("isOwner"))
                .andExpect(view().name("account/profile"));
    }

    @DisplayName("프로필 화면 보여주기 - 비정상 userId")
    @SignUpAndLoggedIn
    @Test
    void showProfileViewWithIncorrectUserId() throws Exception{

        String incorrectTestUserId = "IncorrectTestUserId";

        try{
            mockMvc.perform(get("/account/profile/" + incorrectTestUserId));
        }catch (Exception e){
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            assertEquals(incorrectTestUserId + "에 해당하는 사용자가 없습니다.", e.getCause().getMessage());
        }
    }

    @DisplayName("프로필 수정 화면 보여주기")
    @SignUpAndLoggedIn
    @Test
    void showProfileSettingView() throws Exception{

        mockMvc.perform(get(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PROFILE_VIEW_NAME));
    }

    @DisplayName("프로필 수정하기 - 모두 정상입력")
    @SignUpAndLoggedIn
    @Test
    void updateProfile() throws Exception{

        String newBio = "updatedBio";
        String newLocation = "updatedLocation";
        String newOccupation = "updatedOccupation";
        String newProfileImage = "updatedProfileImage";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", newBio)
                .param("location", newLocation)
                .param("occupation", newOccupation)
                .param("profileImage", newProfileImage)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attribute("message", "프로필 수정이 완료되었습니다."));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertEquals(newBio, updatedAccount.getBio());
        assertEquals(newLocation, updatedAccount.getLocation());
        assertEquals(newOccupation, updatedAccount.getOccupation());
        assertEquals(newProfileImage, updatedAccount.getProfileImage());

    }

    @DisplayName("프로필 한 줄 소개 수정하기 - 길이 초과 에러")
    @SignUpAndLoggedIn
    @Test
    void updateBioTooLongError() throws Exception{

        String newBio = "updatedBioupdatedBioupdatedBioupdatedBio";
        String newLocation = "updatedLocation";
        String newOccupation = "updatedOccupation";
        String newProfileImage = "updatedProfileImage";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", newBio)
                .param("location", newLocation)
                .param("occupation", newOccupation)
                .param("profileImage", newProfileImage)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("profileUpdateRequestDto", "bio", "tooLongBio"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertNotEquals(newBio, updatedAccount.getBio());
        assertNotEquals(newLocation, updatedAccount.getLocation());
        assertNotEquals(newOccupation, updatedAccount.getOccupation());
        assertNotEquals(newProfileImage, updatedAccount.getProfileImage());

        assertNull(updatedAccount.getBio());
        assertNull(updatedAccount.getLocation());
        assertNull(updatedAccount.getOccupation());
        assertNull(updatedAccount.getProfileImage());
    }

    @DisplayName("프로필 지역 수정하기 - 길이 초과 에러")
    @SignUpAndLoggedIn
    @Test
    void updateLocationTooLongError() throws Exception{

        String newBio = "updatedBio";
        String newLocation = "updatedLocationupdatedLocation";
        String newOccupation = "updatedOccupation";
        String newProfileImage = "updatedProfileImage";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", newBio)
                .param("location", newLocation)
                .param("occupation", newOccupation)
                .param("profileImage", newProfileImage)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("profileUpdateRequestDto", "location", "tooLongLocation"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertNotEquals(newBio, updatedAccount.getBio());
        assertNotEquals(newLocation, updatedAccount.getLocation());
        assertNotEquals(newOccupation, updatedAccount.getOccupation());
        assertNotEquals(newProfileImage, updatedAccount.getProfileImage());

        assertNull(updatedAccount.getBio());
        assertNull(updatedAccount.getLocation());
        assertNull(updatedAccount.getOccupation());
        assertNull(updatedAccount.getProfileImage());
    }

    @DisplayName("프로필 직업 수정하기 - 길이 초과 에러")
    @SignUpAndLoggedIn
    @Test
    void updateOccupationTooLongError() throws Exception{

        String newBio = "updatedBio";
        String newLocation = "updatedLocation";
        String newOccupation = "updatedOccupationupdatedOccupation";
        String newProfileImage = "updatedProfileImage";

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", newBio)
                .param("location", newLocation)
                .param("occupation", newOccupation)
                .param("profileImage", newProfileImage)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("profileUpdateRequestDto", "occupation", "tooLongOccupation"))
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("profileUpdateRequestDto"));

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertNotEquals(newBio, updatedAccount.getBio());
        assertNotEquals(newLocation, updatedAccount.getLocation());
        assertNotEquals(newOccupation, updatedAccount.getOccupation());
        assertNotEquals(newProfileImage, updatedAccount.getProfileImage());

        assertNull(updatedAccount.getBio());
        assertNull(updatedAccount.getLocation());
        assertNull(updatedAccount.getOccupation());
        assertNull(updatedAccount.getProfileImage());

    }
}
