package portfolio2.module.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.CustomPrincipal;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.post.dto.PostDeleteRequestDto;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.HOME_URL;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.*;

/**
 * - 게시글 삭제
 * 정상 삭제 - 게시글만 삭제, 작성 계정, 태그는 남아있음.
 * 존재하지 않는 게시물 에러
 * 글 삭제 권한 없는 사용자 요청 에러
 *
 */

@MockMvcTest
public class PostDeleteTest extends ContainerBaseTest {

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

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private PostService postService;

    private final String POST_TEST_TITLE = "Test title.";
    private final String POST_TEST_CONTENT = "Test content.";
    private final List<String> POST_TEST_TAG_STRING_LIST = List.of("test tagTitle 1", "test tagTitle 2", "test tagTitle 3");
    private Long savedPostId;

    @BeforeEach
    void beforeEach(){
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
        postNewPostRequestDto.setTitle(POST_TEST_TITLE);
        postNewPostRequestDto.setContent(POST_TEST_CONTENT);
        postNewPostRequestDto.setTagTitleOnPost(String.join(",", POST_TEST_TAG_STRING_LIST));
        CustomPrincipal customPrincipal = (CustomPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account sessionAccount = customPrincipal.getSessionAccount();
        Post savedPost = postService.saveNewPostWithTag(sessionAccount, postNewPostRequestDto);
        this.savedPostId = savedPost.getId();
    }

    @AfterEach
    void afterEach(){
        notificationRepository.deleteAll();
        tagRepository.deleteAll();
        postRepository.deleteAll();
        accountRepository.deleteAll();
    }


    @DisplayName("정상 삭제 - 작성 계정, 태그는 남아있음.")
    @Test
    void deleteSuccess() throws Exception{
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        PostDeleteRequestDto postDeleteRequestDto = new PostDeleteRequestDto();
        postDeleteRequestDto.setPostIdToDelete(savedPostId);

        mockMvc.perform(post(POST_DELETE_URL)
                        .param("postIdToDelete", String.valueOf(postDeleteRequestDto.getPostIdToDelete()))
                        .with(csrf()))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist(ERROR_TITLE))
                .andExpect(model().attributeDoesNotExist(ERROR_CONTENT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(HOME_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Optional<Post> deletedPost = postRepository.findById(savedPostId);
        assertTrue(deletedPost.isEmpty());

        Account authorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(authorAccount);

        POST_TEST_TAG_STRING_LIST.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNotNull(tagInDb);
        });
    }

    @DisplayName("존재하지 않는 게시물 에러")
    @Test
    void postNotFoundError() throws Exception{
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        PostDeleteRequestDto postDeleteRequestDto = new PostDeleteRequestDto();
        postDeleteRequestDto.setPostIdToDelete(savedPostId + 1);

        mockMvc.perform(post(POST_DELETE_URL)
                .param("postIdToDelete", String.valueOf(postDeleteRequestDto.getPostIdToDelete()))
                .with(csrf()))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute(ERROR_TITLE, "게시물 삭제 에러"))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Account authorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(authorAccount);

        Post notDeletedPost = postRepository.findById(savedPostId).orElse(null);
        assertNotNull(notDeletedPost);
        assertEquals(POST_TEST_TITLE, notDeletedPost.getTitle());
        assertEquals(POST_TEST_CONTENT, notDeletedPost.getContent());
        POST_TEST_TAG_STRING_LIST.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNotNull(tagInDb);
            assertTrue(notDeletedPost.getCurrentTag().contains(tagInDb));
        });
        assertTrue(notDeletedPost.getBeforeTag().isEmpty());
    }

    @DisplayName("작성자가 아닌 계정 에러")
    @Test
    void notAuthorError() throws Exception{
        logInAndOutProcessForTest.logOut();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));
        PostDeleteRequestDto postDeleteRequestDto = new PostDeleteRequestDto();
        postDeleteRequestDto.setPostIdToDelete(savedPostId);

        mockMvc.perform(post(POST_DELETE_URL)
                .param("postIdToDelete", String.valueOf(postDeleteRequestDto.getPostIdToDelete()))
                .with(csrf()))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute(ERROR_TITLE, "글 삭제 권한 없음"))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        Account authorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(authorAccount);

        Post notDeletedPost = postRepository.findById(savedPostId).orElse(null);
        assertNotNull(notDeletedPost);
        assertEquals(POST_TEST_TITLE, notDeletedPost.getTitle());
        assertEquals(POST_TEST_CONTENT, notDeletedPost.getContent());
        POST_TEST_TAG_STRING_LIST.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNotNull(tagInDb);
            assertTrue(notDeletedPost.getCurrentTag().contains(tagInDb));
        });
        assertTrue(notDeletedPost.getBeforeTag().isEmpty());
    }

}
