package portfolio2.module.account.setting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerified;
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
import static portfolio2.module.account.config.TestAccountInfo.*;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

@MockMvcTest
public class AccountProfileUpdateTest extends ContainerBaseTest {

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
    @SignUpAndLogInEmailNotVerified
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

    @DisplayName("프로필 업데이트 - 모두 정상 입력1")
    @SignUpAndLogInEmailNotVerified
    @Test
    void updateProfileSuccess1() throws Exception{
        String sampleBio = "aB3^ ";
        String sampleLocation = "aB3^ ";
        String sampleOccupation = "aB3^ ";
        String sampleProfileImage = "aB3^ ";

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

    @DisplayName("프로필 업데이트 - 모두 정상 입력2")
    @SignUpAndLogInEmailNotVerified
    @Test
    void updateProfileSuccess2() throws Exception{
        String sampleBio = "sS %^&$#@#$";
        String sampleLocation = "sA 29#$%";
        String sampleOccupation = "sA 29#$%";
        String sampleProfileImage = "sA 29#$%";

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
    @SignUpAndLogInEmailNotVerified
    @Test
    void tooLongBioError() throws Exception{
        String sampleBio = "abcdeabcdeabcdeabcdeabcdeabcdea";
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
                        "invalidBio"))
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

    @DisplayName("프로필 업데이트 - 자기소개 스페이스 외의 공백문자 에러")
    @SignUpAndLogInEmailNotVerified
    @Test
    void whiteSpaceErrorOfBio() throws Exception{
        String sampleBio = "asdfa\tsdf";
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
                        "invalidBio"))
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
    @SignUpAndLogInEmailNotVerified
    @Test
    void tooLongOccupationError() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "sampleLocation";
        String sampleOccupation = "abcdeabcdeabcdea";
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
                        "invalidOccupation"))
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

    @DisplayName("프로필 업데이트 - 스페이스 외의 공백문자 직업 에러")
    @SignUpAndLogInEmailNotVerified
    @Test
    void whiteSpaceErrorOfOccupation() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "sampleLocation";
        String sampleOccupation = "abcde\ncdea";
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
                        "invalidOccupation"))
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
    @SignUpAndLogInEmailNotVerified
    @Test
    void tooLongLocationError() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "abcdeabcdeabcdea";
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
                        "invalidLocation"))
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

    @DisplayName("프로필 업데이트 - 스페이스 외의 공백문자 지역 에러")
    @SignUpAndLogInEmailNotVerified
    @Test
    void whiteSpaceErrorOfLocation() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "as\tgbc";
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
                        "invalidLocation"))
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
    @SignUpAndLogInEmailNotVerified
    @Test
    void showAllErrorCodes() throws Exception{
        String sampleBio = "sampleBio";
        String sampleLocation = "abcdeabcdeabcdea";
        String sampleOccupation = "abcdeabcdeabcdea";
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
                        "invalidOccupation"))
                .andExpect(model().attributeHasFieldErrorCode(
                        "profileUpdateRequestDto",
                        "location",
                        "invalidLocation"))
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
