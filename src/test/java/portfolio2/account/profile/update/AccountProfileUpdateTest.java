package portfolio2.account.profile.update;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.account.config.SignUpAndLogOutProcessForTest;
import portfolio2.account.config.SignUpAndLoggedIn;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.account.SignUpRequestDto;
import portfolio2.dto.account.profile.update.ProfileUpdateRequestDto;
import portfolio2.mail.EmailMessage;
import portfolio2.mail.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.account.config.TestAccountInfo.*;
import static portfolio2.config.StaticFinalName.SESSION_ACCOUNT;
import static portfolio2.config.UrlAndViewName.*;

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
    @SignUpAndLoggedIn
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
    @SignUpAndLoggedIn
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
    @SignUpAndLoggedIn
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
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotEquals(sampleBio, updatedAccount.getBio());
        assertNotEquals(sampleLocation, updatedAccount.getLocation());
        assertNotEquals(sampleOccupation, updatedAccount.getOccupation());
        assertNotEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }

    @DisplayName("프로필 업데이트 - 너무 긴 지역 에러")
    @SignUpAndLoggedIn
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
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotEquals(sampleBio, updatedAccount.getBio());
        assertNotEquals(sampleLocation, updatedAccount.getLocation());
        assertNotEquals(sampleOccupation, updatedAccount.getOccupation());
        assertNotEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }

    @DisplayName("프로필 업데이트 - 너무 긴 직업 에러")
    @SignUpAndLoggedIn
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
                .andExpect(view().name(ACCOUNT_SETTING_PROFILE_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotEquals(sampleBio, updatedAccount.getBio());
        assertNotEquals(sampleLocation, updatedAccount.getLocation());
        assertNotEquals(sampleOccupation, updatedAccount.getOccupation());
        assertNotEquals(sampleProfileImage, updatedAccount.getProfileImage());
    }
}
