package portfolio2.module.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.post.service.process.EmailSendingProcessForPost;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_NEW_POST_URL;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_NEW_POST_FORM_VIEW_NAME;

@MockMvcTest
public class PostNewPostTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @AfterEach
    void afterEach(){
        notificationRepository.deleteAll();
        tagRepository.deleteAll();
        postRepository.deleteAll();
        accountRepository.deleteAll();
    }


    @DisplayName("글 작성 화면 보여주기")
    @SignUpAndLogInEmailVerified
    @Test
    void showPostNewPostView() throws Exception{
        mockMvc.perform(get(POST_NEW_POST_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postNewPostRequestDto"))
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력(모두 새로운 태그)")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithAllCurrentTag() throws Exception{

        String titleOfNewPost = "Test title";
        String contentOfNewPost = "<p>Test content</p>";
        String tagOfNewPost = "Test tag 1,Test tag 2,Test tag 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);

        assertEquals(titleOfNewPost, savedNewPostInDb.getTitle());
        assertEquals(contentOfNewPost, savedNewPostInDb.getContent());
        assertEquals(authorAccountInDb, savedNewPostInDb.getAuthor());

        Tag testTag1 = new Tag();
        testTag1.setTitle("Test tag 1");
        Tag testTag2 = new Tag();
        testTag2.setTitle("Test tag 2");

        String[] postedTag = tagOfNewPost.split(",");
        for(String tagTitle : postedTag){
            Tag containedTagInPost = tagRepository.findByTitle(tagTitle);
            assertNotNull(containedTagInPost);
            assertTrue(savedNewPostInDb.getCurrentTag().contains(containedTagInPost));
        }

        assertFalse(savedNewPostInDb.getCurrentTag().contains(testTag1));
        assertFalse(savedNewPostInDb.getCurrentTag().contains(testTag2));
        LocalDateTime firstWrittenDateTime = savedNewPostInDb.getFirstWrittenDateTime();
        LocalDateTime lastModifiedDateTime = savedNewPostInDb.getLastModifiedDateTime();
        assertNotNull(firstWrittenDateTime);
        assertEquals(firstWrittenDateTime, lastModifiedDateTime);
    }

    @DisplayName("글 작성 POST 요청 - 특수문자 테스트 - 모두 정상 입력(모두 새로운 태그)")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithSpecialCharacter() throws Exception{

        String titleOfNewPost = "Test 3#title";
        String contentOfNewPost = "<p>Test content</p>";
        String tagOfNewPost = "Test tag 1,Test tag 2,Test tag 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);

        assertEquals(titleOfNewPost, savedNewPostInDb.getTitle());
        assertEquals(contentOfNewPost, savedNewPostInDb.getContent());
        assertEquals(authorAccountInDb, savedNewPostInDb.getAuthor());

        Tag testTag1 = new Tag();
        testTag1.setTitle("Test tag 1");
        Tag testTag2 = new Tag();
        testTag2.setTitle("Test tag 2");

        String[] postedTag = tagOfNewPost.split(",");
        for(String tagTitle : postedTag){
            Tag containedTagInPost = tagRepository.findByTitle(tagTitle);
            assertNotNull(containedTagInPost);
            assertTrue(savedNewPostInDb.getCurrentTag().contains(containedTagInPost));
        }

        assertFalse(savedNewPostInDb.getCurrentTag().contains(testTag1));
        assertFalse(savedNewPostInDb.getCurrentTag().contains(testTag2));
        LocalDateTime firstWrittenDateTime = savedNewPostInDb.getFirstWrittenDateTime();
        LocalDateTime lastModifiedDateTime = savedNewPostInDb.getLastModifiedDateTime();
        assertNotNull(firstWrittenDateTime);
        assertEquals(firstWrittenDateTime, lastModifiedDateTime);
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력 - 일부 기존 존재하는 태그")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithExistingTag() throws Exception{
        Tag existingTag1 = new Tag();
        Tag existingTag2 = new Tag();
        existingTag1.setTitle("Tag 1");
        existingTag2.setTitle("Tag 2");
        tagRepository.save(existingTag1);
        tagRepository.save(existingTag2);

        String titleOfNewPost = "This is test title.";
        String contentOfNewPost = "<p>This is test content.</p>";
        String tagOfNewPost = "Test tag 1,Test tag 2,Test tag 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);

        assertEquals(titleOfNewPost, savedNewPostInDb.getTitle());
        assertEquals(contentOfNewPost, savedNewPostInDb.getContent());
        assertEquals(authorAccountInDb, savedNewPostInDb.getAuthor());

        String[] postedTag = tagOfNewPost.split(",");
        for(String tagTitle : postedTag){
            Tag containedTagInPost = tagRepository.findByTitle(tagTitle);
            assertNotNull(containedTagInPost);
            assertTrue(savedNewPostInDb.getCurrentTag().contains(containedTagInPost));
        }
        LocalDateTime firstWrittenDateTime = savedNewPostInDb.getFirstWrittenDateTime();
        LocalDateTime lastModifiedDateTime = savedNewPostInDb.getLastModifiedDateTime();
        assertNotNull(firstWrittenDateTime);
        assertEquals(firstWrittenDateTime, lastModifiedDateTime);
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력 - 태그 입력 안했을 때")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithEmptyTag() throws Exception{

        String titleOfNewPost = "This is test title.";
        String contentOfNewPost = "<p>This is test content.</p>";
        String tagOfNewPost = "";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);

        assertEquals(titleOfNewPost, savedNewPostInDb.getTitle());
        assertEquals(contentOfNewPost, savedNewPostInDb.getContent());
        assertEquals(authorAccountInDb, savedNewPostInDb.getAuthor());

        assertTrue(savedNewPostInDb.getCurrentTag().isEmpty());
    }

    @DisplayName("글 작성 POST 요청 - 정상 입력 - 내용 입력 안했을 때")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithEmptyContent() throws Exception{

        String titleOfNewPost = "This is test title.";
        String contentOfNewPost = "";
        String tagOfNewPost = "Test tag 1,Test tag 2,Test tag 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasNoErrors())
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);

        assertEquals(titleOfNewPost, savedNewPostInDb.getTitle());
        assertEquals(contentOfNewPost, savedNewPostInDb.getContent());
        assertEquals(authorAccountInDb, savedNewPostInDb.getAuthor());

        Tag testTag1 = new Tag();
        testTag1.setTitle("Test tag 1");
        Tag testTag2 = new Tag();
        testTag2.setTitle("Test tag 2");

        String[] postedTag = tagOfNewPost.split(",");
        for(String tagTitle : postedTag){
            Tag containedTagInPost = tagRepository.findByTitle(tagTitle);
            assertNotNull(containedTagInPost);
            assertTrue(savedNewPostInDb.getCurrentTag().contains(containedTagInPost));
        }

        assertFalse(savedNewPostInDb.getCurrentTag().contains(testTag1));
        assertFalse(savedNewPostInDb.getCurrentTag().contains(testTag2));
        LocalDateTime firstWrittenDateTime = savedNewPostInDb.getFirstWrittenDateTime();
        LocalDateTime lastModifiedDateTime = savedNewPostInDb.getLastModifiedDateTime();
        assertNotNull(firstWrittenDateTime);
        assertEquals(firstWrittenDateTime, lastModifiedDateTime);
    }

    @DisplayName("글 작성 POST 요청 - 입력 에러 - 제목 입력 안했을 때")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithEmptyTitle() throws Exception{

        String titleOfNewPost = "";
        String contentOfNewPost = "<p>This is test content.</p>";
        String tagOfNewPost = "Test tag 1,Test tag 2,Test tag 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postNewPostRequestDto",
                        "title",
                        "emptyTitle"

                ))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postNewPostRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);
        assertNull(savedNewPostInDb);
    }

    @DisplayName("글 작성 POST 요청 - 입력 에러 - 스페이스 외 공백문자 제목")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithWhiteSpaceTitle() throws Exception{

        String titleOfNewPost = "aslkd\tjwA";
        String contentOfNewPost = "<p>This is test content.</p>";
        String tagOfNewPost = "Test tag 1,Test tag 2,Test tag 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postNewPostRequestDto",
                        "title",
                        "invalidTitle"

                ))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postNewPostRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);
        assertNull(savedNewPostInDb);
    }

    @DisplayName("글 작성 POST 요청 - 입력 에러 - 태그 형식 에러")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithInvalidTagTitleOnPost() throws Exception{

        String titleOfNewPost = "aslkdjwA";
        String contentOfNewPost = "<p>This is test content.</p>";
        String tagOfNewPost = "Test ta\tg 1,Test tag 2,Test tag 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postNewPostRequestDto",
                        "tagTitleOnPost",
                        "invalidFormatTagTitleOnPost"

                ))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);
        assertNull(savedNewPostInDb);

        String[] tagArray = tagOfNewPost.split(",");
        for(String tagTitle : tagArray){
            Tag foundTagInDb = tagRepository.findByTitle(tagTitle);
            assertNull(foundTagInDb);
        }
    }

    @DisplayName("글 작성 POST 요청 - 입력 에러 - 제목과 내용 모두 입력 안했을 때")
    @SignUpAndLogInEmailVerified
    @Test
    void postNewPostWithEmptyTitleAndContent() throws Exception{

        String titleOfNewPost = "";
        String contentOfNewPost = "";
        String tagOfNewPost = "";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postNewPostRequestDto",
                        "title",
                        "emptyTitle"

                ))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postNewPostRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);
        assertNull(savedNewPostInDb);
    }

}
