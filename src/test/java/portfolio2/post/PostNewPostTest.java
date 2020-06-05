package portfolio2.post;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.account.testaccountinfo.SignUpAndLoggedIn;
import portfolio2.account.testaccountinfo.TestAccountInfo;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.post.Post;
import portfolio2.domain.post.PostRepository;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.tag.TagRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private final String SESSION_ACCOUNT = "sessionAccount";

    private final String POST_NEW_POST_URL = "/postNewPost";
    private final String POST_NEW_POST_VIEW_FOLDER = "post/form";

    @DisplayName("글 작성 화면 보여주기")
    @SignUpAndLoggedIn
    @Test
    void showPostNewPostView() throws Exception{
        mockMvc.perform(get(POST_NEW_POST_URL))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postNewPostRequestDto"))
                .andExpect(view().name(POST_NEW_POST_VIEW_FOLDER + "/post-new-post-form"));
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력(모두 새로운 태그)")
    @SignUpAndLoggedIn
    @Test
    void postNewPost() throws Exception{

        String titleOfNewPost = "테스트 제목 입니다.";
        String contentOfNewPost = "<p>테스트 내용 입니다.</p>";
        String tagOfNewPost = "테스트 태그 1,테스트 태그 2,테스트 태그 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors());

        Account author = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);
        Post newPost = postRepository.findByAuthor(author);

        assertEquals(newPost.getTitle(), titleOfNewPost);
        assertEquals(newPost.getContent(), contentOfNewPost);

        String[] postedTag = tagOfNewPost.split(",");
        for(String tagTitle : postedTag){
            Tag containedTagInPost = tagRepository.findByTitle(tagTitle);
            assertNotNull(containedTagInPost);
            assertTrue(newPost.getTag().contains(containedTagInPost));
        }
    }

    @DisplayName("글 작성 POST 요청 - 모두 정상 입력(일부 기존 존재하는 태그)")
    @SignUpAndLoggedIn
    @Test
    void postNewPostWithExistingTag() throws Exception{

        tagRepository.save(Tag.builder().title("태그 1").build());
        tagRepository.save(Tag.builder().title("태그 2").build());

        String titleOfNewPost = "테스트 제목 입니다.";
        String contentOfNewPost = "<p>테스트 내용 입니다.</p>";
        String tagOfNewPost = "태그 1,태그 2,태그 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors());

        Account author = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);
        Post newPost = postRepository.findByAuthor(author);

        assertEquals(newPost.getTitle(), titleOfNewPost);
        assertEquals(newPost.getContent(), contentOfNewPost);

        String[] postedTag = tagOfNewPost.split(",");
        for(String tagTitle : postedTag){
            Tag containedTagInPost = tagRepository.findByTitle(tagTitle);
            assertNotNull(containedTagInPost);
            assertTrue(newPost.getTag().contains(containedTagInPost));
        }
    }

    @DisplayName("글 작성 POST 요청 - 너무 짧은 제목 에러")
    @SignUpAndLoggedIn
    @Test
    void postNewPostTooShortTitle() throws Exception{

        tagRepository.save(Tag.builder().title("태그 1").build());
        tagRepository.save(Tag.builder().title("태그 2").build());

        String titleOfNewPost = "짧은제목";
        String contentOfNewPost = "<p>테스트 내용 입니다.</p>";
        String tagOfNewPost = "태그 1,태그 2,태그 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("postNewPostRequestDto", "title", "tooShortPostTitle"))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postNewPostRequestDto"))
                .andExpect(view().name(POST_NEW_POST_VIEW_FOLDER + "/post-new-post-form"));


        Account author = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);
        Post newPost = postRepository.findByAuthor(author);
        assertNull(newPost);
    }

    @DisplayName("글 작성 POST 요청 - 너무 긴 제목 에러")
    @SignUpAndLoggedIn
    @Test
    void postNewPostTooLongTitle() throws Exception{

        List<Post> existingPostInDb = postRepository.findAll();
        List<Tag> existingTagInDb = tagRepository.findAll();
        List<Account> existingAccountInDb = accountRepository.findAll();

        assertTrue(existingPostInDb.isEmpty());
        assertTrue(existingTagInDb.isEmpty());
        assertFalse(existingAccountInDb.isEmpty());
        assertNotNull(accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID));

        assertEquals(accountRepository.count(), 1);
        assertEquals(postRepository.count(), 0);
        assertEquals(tagRepository.count(), 0);

        tagRepository.save(Tag.builder().title("태그 1").build());
        tagRepository.save(Tag.builder().title("태그 2").build());

        String titleOfNewPost = "긴 테스트 제목 입니다.긴 테스트 제목 입니다.긴 테스트 제목 입니다." +
                "긴 테스트 제목 입니다.긴 테스트 제목 입니다.긴 테스트 제목 입니다.긴 테스트 제목 입니다.긴 테스트 제목 입니다." +
                "긴 테스트 제목 입니다.긴 테스트 제목 입니다.긴 테스트 제목 입니다.긴 테스트 제목 입니다.";
        String contentOfNewPost = "<p>테스트 내용 입니다.</p>";
        String tagOfNewPost = "태그 1,태그 2,태그 3";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode
                        ("postNewPostRequestDto", "title", "tooLongPostTitle"))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("postNewPostRequestDto"))
                .andExpect(view().name(POST_NEW_POST_VIEW_FOLDER + "/post-new-post-form"));


        Account author = accountRepository.findByUserId(TestAccountInfo.CORRECT_TEST_USER_ID);
        Post newPost = postRepository.findByAuthor(author);
        assertNull(newPost);
    }
}
