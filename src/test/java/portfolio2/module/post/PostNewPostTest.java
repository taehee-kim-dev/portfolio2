package portfolio2.module.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.config.SignUpAndLoggedInEmailVerified;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_NEW_POST_URL;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_NEW_POST_FORM_VIEW_NAME;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class PostNewPostTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;


    @DisplayName("글 작성 화면 보여주기")
    @SignUpAndLoggedInEmailVerified
    @Test
    void showPostNewPostView() throws Exception{
        mockMvc.perform(get(POST_NEW_POST_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postRequestDto"))
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력(모두 새로운 태그)")
    @SignUpAndLoggedInEmailVerified
    @Test
    void postNewPostWithAllNewTag() throws Exception{

        String titleOfNewPost = "테스트 제목.";
        String contentOfNewPost = "<p>테스트 내용.</p>";
        String tagOfNewPost = "테스트 태그 1,테스트 태그 2,테스트 태그 3";

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
        testTag1.setTitle("테스트 태그 1");
        Tag testTag2 = new Tag();
        testTag2.setTitle("테스트 태그 2");

        String[] postedTag = tagOfNewPost.split(",");
        for(String tagTitle : postedTag){
            Tag containedTagInPost = tagRepository.findByTitle(tagTitle);
            assertNotNull(containedTagInPost);
            assertTrue(savedNewPostInDb.getTag().contains(containedTagInPost));
        }

        assertFalse(savedNewPostInDb.getTag().contains(testTag1));
        assertFalse(savedNewPostInDb.getTag().contains(testTag2));
        LocalDateTime firstWrittenTime = savedNewPostInDb.getFirstWrittenTime();
        LocalDateTime lastModifiedTime = savedNewPostInDb.getLastModifiedTime();
        assertNotNull(firstWrittenTime);
        assertEquals(firstWrittenTime, lastModifiedTime);
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력 - 일부 기존 존재하는 태그")
    @SignUpAndLoggedInEmailVerified
    @Test
    void postNewPostWithExistingTag() throws Exception{
        Tag existingTag1 = new Tag();
        Tag existingTag2 = new Tag();
        existingTag1.setTitle("태그 1");
        existingTag2.setTitle("태그 2");
        tagRepository.save(existingTag1);
        tagRepository.save(existingTag2);

        String titleOfNewPost = "테스트 제목 입니다.";
        String contentOfNewPost = "<p>테스트 내용 입니다.</p>";
        String tagOfNewPost = "태그 1,태그 2,태그 3";

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
            assertTrue(savedNewPostInDb.getTag().contains(containedTagInPost));
        }
        LocalDateTime firstWrittenTime = savedNewPostInDb.getFirstWrittenTime();
        LocalDateTime lastModifiedTime = savedNewPostInDb.getLastModifiedTime();
        assertNotNull(firstWrittenTime);
        assertEquals(firstWrittenTime, lastModifiedTime);
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력 - 태그 입력 안했을 때")
    @SignUpAndLoggedInEmailVerified
    @Test
    void postNewPostWithEmptyTag() throws Exception{

        String titleOfNewPost = "테스트 제목 입니다.";
        String contentOfNewPost = "<p>테스트 내용 입니다.</p>";
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

        assertTrue(savedNewPostInDb.getTag().isEmpty());
    }

    @DisplayName("글 작성 POST 요청 - 입력 에러 - 제목 입력 안했을 때")
    @SignUpAndLoggedInEmailVerified
    @Test
    void postNewPostWithEmptyTitle() throws Exception{

        String titleOfNewPost = "";
        String contentOfNewPost = "<p>테스트 내용 입니다.</p>";
        String tagOfNewPost = "태그 1,태그 2,태그 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postRequestDto",
                        "title",
                        "emptyTitle"

                ))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);
        assertNull(savedNewPostInDb);
    }

    @DisplayName("글 작성 POST 요청 - 입력 에러 - 내용 입력 안했을 때")
    @SignUpAndLoggedInEmailVerified
    @Test
    void postNewPostWithEmptyContent() throws Exception{

        String titleOfNewPost = "테스트 내용 입니다.";
        String contentOfNewPost = "";
        String tagOfNewPost = "태그 1,태그 2,태그 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postRequestDto",
                        "content",
                        "emptyContent"

                ))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);
        assertNull(savedNewPostInDb);
    }

    @DisplayName("글 작성 POST 요청 - 입력 에러 - 제목과 내용 모두 입력 안했을 때")
    @SignUpAndLoggedInEmailVerified
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
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postRequestDto",
                        "title",
                        "emptyTitle"

                ))
                .andExpect(model().attributeHasFieldErrorCode(
                        "postRequestDto",
                        "content",
                        "emptyContent"

                ))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postRequestDto"))
                .andExpect(status().isOk())
                .andExpect(view().name(POST_NEW_POST_FORM_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccountInDb = accountRepository.findByUserId(TEST_USER_ID);
        Post savedNewPostInDb = postRepository.findByAuthor(authorAccountInDb);
        assertNull(savedNewPostInDb);
    }

}
