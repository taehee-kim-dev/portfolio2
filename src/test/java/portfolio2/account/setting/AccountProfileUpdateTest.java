package portfolio2.account.setting;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.SignUpAndLoggedInEmailNotVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.*;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class AccountProfileUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // 모두 무조건 로그인 상태여야 함

    @DisplayName("프로필 수정 화면 보여주기")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void showProfileUpdatePage() throws Exception{
        mockMvc.perform(get(ACCOUNT_SETTING_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    // 모두 정상 입력

    @DisplayName("프로필 업데이트 - 모두 정상 입력")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void updateProfileSuccess() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "sampleLocation";
        String sampleOccupation = "sampleOccupation";
        String sampleProfileImage = "sampleProfileImage";

        mockMvc.perform(post(ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", sampleBio)
                .param("location", sampleLocation)
                .param("occupation", sampleOccupation)
                .param("profileImage", sampleProfileImage)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeCount(1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ACCOUNT_SETTING_PROFILE_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertEquals(sampleBio, updatedAccount.getBio());
        assertEquals(sampleLocation, updatedAccount.getLocation());
        assertEquals(sampleOccupation, updatedAccount.getOccupation());
        assertEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }

    // 입력 에러

    @DisplayName("프로필 업데이트 - 너무 긴 자기소개 에러")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void tooLongBioError() throws Exception{
        String sampleBio = "sampleBiosampleBiosampleBiosampleBiosampleBio";
        String sampleLocation = "sampleLocation";
        String sampleOccupation = "sampleOccupation";
        String sampleProfileImage = "sampleProfileImage";

        mockMvc.perform(post(ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", sampleBio)
                .param("location", sampleLocation)
                .param("occupation", sampleOccupation)
                .param("profileImage", sampleProfileImage)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "profileUpdateRequestDto",
                        "bio",
                        "tooLongBio"))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotEquals(sampleBio, updatedAccount.getBio());
        assertNotEquals(sampleLocation, updatedAccount.getLocation());
        assertNotEquals(sampleOccupation, updatedAccount.getOccupation());
        assertNotEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }

    @DisplayName("프로필 업데이트 - 너무 긴 지역 에러")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void tooLongLocationError() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "sampleLocationsampleLocationsampleLocation";
        String sampleOccupation = "sampleOccupation";
        String sampleProfileImage = "sampleProfileImage";

        mockMvc.perform(post(ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", sampleBio)
                .param("location", sampleLocation)
                .param("occupation", sampleOccupation)
                .param("profileImage", sampleProfileImage)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "profileUpdateRequestDto",
                        "location",
                        "tooLongLocation"))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotEquals(sampleBio, updatedAccount.getBio());
        assertNotEquals(sampleLocation, updatedAccount.getLocation());
        assertNotEquals(sampleOccupation, updatedAccount.getOccupation());
        assertNotEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }

    @DisplayName("프로필 업데이트 - 너무 긴 직업 에러")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void tooLongOccupationError() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "sampleLocation";
        String sampleOccupation = "sampleOccupationsampleOccupation";
        String sampleProfileImage = "sampleProfileImage";

        mockMvc.perform(post(ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", sampleBio)
                .param("location", sampleLocation)
                .param("occupation", sampleOccupation)
                .param("profileImage", sampleProfileImage)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "profileUpdateRequestDto",
                        "occupation",
                        "tooLongOccupation"))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(flash().attributeCount(0))
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotEquals(sampleBio, updatedAccount.getBio());
        assertNotEquals(sampleLocation, updatedAccount.getLocation());
        assertNotEquals(sampleOccupation, updatedAccount.getOccupation());
        assertNotEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }

    @DisplayName("입력값 에러 각각 모두 출력")
    @SignUpAndLoggedInEmailNotVerified
    @Test
    void showAllErrorCodes() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "sampleLocationsampleLocationsampleLocation";
        String sampleOccupation = "sampleOccupationsampleOccupation";
        String sampleProfileImage = "sampleProfileImage";

        mockMvc.perform(post(ACCOUNT_SETTING_PROFILE_URL)
                .param("bio", sampleBio)
                .param("location", sampleLocation)
                .param("occupation", sampleOccupation)
                .param("profileImage", sampleProfileImage)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode(
                        "profileUpdateRequestDto",
                        "location",
                        "tooLongLocation"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "profileUpdateRequestDto",
                        "occupation",
                        "tooLongOccupation"))
                .andExpect(model().attributeErrorCount("profileUpdateRequestDto", 2))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("profileUpdateRequestDto"))
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(flash().attributeCount(0))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotEquals(sampleBio, updatedAccount.getBio());
        assertNotEquals(sampleLocation, updatedAccount.getLocation());
        assertNotEquals(sampleOccupation, updatedAccount.getOccupation());
        assertNotEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }
}
