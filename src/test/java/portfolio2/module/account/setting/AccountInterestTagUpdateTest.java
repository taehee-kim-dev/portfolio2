package portfolio2.module.account.setting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.SignUpAndLogInEmailNotVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;
import portfolio2.module.account.dto.request.TagUpdateRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.ACCOUNT_SETTING_TAG_URL;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.ACCOUNT_SETTING_TAG_VIEW_NAME;
import static portfolio2.module.main.config.VariableNameAboutMain.SESSION_ACCOUNT;


@MockMvcTest
public class AccountInterestTagUpdateTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository tagRepository;

    @AfterEach
    void afterEach(){
        tagRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("계정 관심 태그 설정 화면 보여주기")
    @SignUpAndLogInEmailNotVerified
    @Test
    void showAccountTagSettingView() throws Exception{

        mockMvc.perform(get(ACCOUNT_SETTING_TAG_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("tag"))
                .andExpect(view().name(ACCOUNT_SETTING_TAG_VIEW_NAME));
    }

    @DisplayName("태그 추가하기 - 완전히 새로운 태그")
    @SignUpAndLogInEmailNotVerified
    @Test
    void addNewTag() throws Exception{

        String newTagTitleToAdd = "newTagTitleToAdd";

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(newTagTitleToAdd);

        mockMvc.perform(post(ACCOUNT_SETTING_TAG_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagUpdateRequestDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);

        Tag newAddedTag = tagRepository.findByTitle(newTagTitleToAdd);

        assertNotNull(newAddedTag);

        assertTrue(updatedAccount.getInterestTag().contains(newAddedTag));
    }

    @DisplayName("태그 추가하기 - 이미 존재하는 태그 - 사용자는 갖고있지 않음.")
    @SignUpAndLogInEmailNotVerified
    @Test
    void addTagExistingInTagRepository() throws Exception{

        String newTagTitleToAdd = "newTagTitleToAdd";

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(newTagTitleToAdd);

        Tag existingTag = new Tag();
        existingTag.setTitle(newTagTitleToAdd);
        Tag existingTagInDb = tagRepository.save(existingTag);

        mockMvc.perform(post(ACCOUNT_SETTING_TAG_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagUpdateRequestDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertTrue(updatedAccount.getInterestTag().contains(existingTagInDb));
    }

    @DisplayName("태그 추가하기 - 이미 존재하는 태그 - 이미 사용자가 갖고있는 태그")
    @SignUpAndLogInEmailNotVerified
    @Test
    void addTagExistingInTagRepositoryAndAccount() throws Exception{

        String newTagTitleToAdd = "newTagTitleToAdd";

        Tag existingTag = new Tag();
        existingTag.setTitle(newTagTitleToAdd);
        Tag existingTagInDb = tagRepository.save(existingTag);

        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);
        existingAccount.getInterestTag().add(existingTagInDb);
        accountRepository.save(existingAccount);

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(newTagTitleToAdd);

        mockMvc.perform(post(ACCOUNT_SETTING_TAG_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagUpdateRequestDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account updatedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertTrue(updatedAccount.getInterestTag().contains(existingTagInDb));
    }

    @DisplayName("태그 추가하기 - 태그 정규식 에러")
    @SignUpAndLogInEmailNotVerified
    @Test
    void invalidTagFormatAddError() throws Exception{

        String newTagTitleToAdd = "newTag\tTitleToAdd";

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(newTagTitleToAdd);

        mockMvc.perform(post(ACCOUNT_SETTING_TAG_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagUpdateRequestDto))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Tag tag = tagRepository.findByTitle(newTagTitleToAdd);
        assertNull(tag);
    }


    @DisplayName("태그 삭제하기 - 존재하는 태그 정상 삭제")
    @SignUpAndLogInEmailNotVerified
    @Test
    void removeTag() throws Exception{

        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);

        String tagTitleToRemove = "tagTitleToRemove";

        Tag existingTagToRemove = new Tag();
        existingTagToRemove.setTitle(tagTitleToRemove);

        tagRepository.save(existingTagToRemove);

        existingAccount.getInterestTag().add(existingTagToRemove);

        accountRepository.save(existingAccount);

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(tagTitleToRemove);

        mockMvc.perform(post(ACCOUNT_SETTING_TAG_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagUpdateRequestDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account tagAddedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertFalse(tagAddedAccount.getInterestTag().contains(existingTagToRemove));

        Tag removedTag = tagRepository.findByTitle(tagTitleToRemove);
        assertNotNull(removedTag);
    }

    @DisplayName("존재하지 않는 태그 삭제하기 오류")
    @SignUpAndLogInEmailNotVerified
    @Test
    void removeNotExistingTag() throws Exception{

        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);

        String existingTagTitle = "existingTagTitle";
        Tag existingTag = new Tag();
        existingTag.setTitle(existingTagTitle);
        Tag existingTagInDb = tagRepository.save(existingTag);

        existingAccount.getInterestTag().add(existingTag);
        accountRepository.save(existingAccount);

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(existingTagTitle);

        mockMvc.perform(post(ACCOUNT_SETTING_TAG_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tagTitle\":\"notExistingTagTitle\"}")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account tagNotRemovedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertTrue(tagNotRemovedAccount.getInterestTag().contains(existingTagInDb));

        Tag notRemovedTag = tagRepository.findByTitle(existingTagTitle);
        assertNotNull(notRemovedTag);
    }

    @DisplayName("태그 정규식에 어긋나는 태그 삭제 요청 오륲")
    @SignUpAndLogInEmailNotVerified
    @Test
    void removeInvalidFormatTagTitleError() throws Exception{

        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);

        String existingTagTitle = "existing\nTagTitle";
        Tag existingTag = new Tag();
        existingTag.setTitle(existingTagTitle);
        Tag existingTagInDb = tagRepository.save(existingTag);

        existingAccount.getInterestTag().add(existingTag);
        accountRepository.save(existingAccount);

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(existingTagTitle);

        mockMvc.perform(post(ACCOUNT_SETTING_TAG_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tagTitle\":\"" + existingTagTitle + "\"}")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account tagNotRemovedAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertTrue(tagNotRemovedAccount.getInterestTag().contains(existingTagInDb));

        Tag notRemovedTag = tagRepository.findByTitle(existingTagTitle);
        assertNotNull(notRemovedTag);
    }
}
