package portfolio2.account.setting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.account.testaccountinfo.SignUpAndLoggedIn;
import portfolio2.account.testaccountinfo.TestAccountInfo;
import portfolio2.controller.account.AccountSettingController;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.tag.TagRepository;
import portfolio2.dto.account.TagUpdateRequestDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class TagUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("태그 설정 화면 보여주기")
    @SignUpAndLoggedIn
    @Test
    void showTagSettingView() throws Exception{

        mockMvc.perform(get(AccountSettingController.ACCOUNT_SETTING_TAG_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("sessionAccount"))
                .andExpect(model().attributeExists("tag"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(view().name(AccountSettingController.ACCOUNT_SETTING_TAG_VIEW_NAME));

    }

    @DisplayName("태그 추가하기")
    @SignUpAndLoggedIn
    @Test
    void addTag() throws Exception{

        String newTagtitleToAdd = "newTagtitleToAdd";

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(newTagtitleToAdd);

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_TAG_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagUpdateRequestDto))
                .with(csrf()))
                .andExpect(status().isOk());

        Account updatedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        Tag newAddedTag = tagRepository.findByTitle(newTagtitleToAdd);

        assertNotNull(newAddedTag);

        assertTrue(updatedAccount.getTag().contains(newAddedTag));
    }


    @DisplayName("태그 삭제하기")
    @SignUpAndLoggedIn
    @Test
    void removeTag() throws Exception{

        Account existingAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        String tagtitleToRemove = "tagtitleToRemove";

        Tag existingTagToRemove = Tag.builder()
                .title(tagtitleToRemove).build();

        tagRepository.save(existingTagToRemove);

        existingAccount.getTag().add(existingTagToRemove);

        accountRepository.save(existingAccount);

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        tagUpdateRequestDto.setTagTitle(tagtitleToRemove);

        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_TAG_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagUpdateRequestDto))
                .with(csrf()))
                .andExpect(status().isOk());

        Account tagAddedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertFalse(tagAddedAccount.getTag().contains(existingTagToRemove));

        Tag removedTag = tagRepository.findByTitle(tagtitleToRemove);

        assertNotNull(removedTag);
    }

    @DisplayName("존재하지 않는 태그 삭제하기 오류")
    @SignUpAndLoggedIn
    @Test
    void removeNotExistingTag() throws Exception{



        Account existingAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);



        String existingTagTitle = "existingTagTitle";

        Tag existingTag = Tag.builder()
                .title(existingTagTitle).build();

        tagRepository.save(existingTag);



        existingAccount.getTag().add(existingTag);

        accountRepository.save(existingAccount);




        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();

        tagUpdateRequestDto.setTagTitle(existingTagTitle);



        mockMvc.perform(post(AccountSettingController.ACCOUNT_SETTING_TAG_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tagTitle\":\"notExistingTagTitle\"}")
                .with(csrf()))
                .andExpect(status().isBadRequest());


        Account tagNotRemovedAccount = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);

        assertTrue(tagNotRemovedAccount.getTag().contains(existingTag));

        Tag notRemovedTag = tagRepository.findByTitle(existingTagTitle);

        assertNotNull(notRemovedTag);
    }
}
