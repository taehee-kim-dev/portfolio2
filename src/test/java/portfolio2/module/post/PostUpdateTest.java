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
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.dto.PostUpdateRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.ERROR_VIEW_NAME;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.*;

/**
 * - 게시글 수정
 * 정상 업데이트 - 제목, 내용, 태그 모두 변경
 * 존재하지 않는 게시물 에러
 * 글 수정 권한 없는 사용자 요청 에러
 *
 */

@MockMvcTest
public class PostUpdateTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

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
        tagRepository.deleteAll();
        postRepository.deleteAll();
        accountRepository.deleteAll();
    }


    @DisplayName("정상 업데이트 - 제목, 내용, 태그 변경")
    @Test
    void updateSuccess() throws Exception{
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        String newTitle = "new title.";
        String newContent = "new Content.";
        List<String> newTagStringList = List.of("new tagTitle 1", "new tagTitle 2", "new tagTitle 3");
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto();
        postUpdateRequestDto.setPostIdToUpdate(savedPostId);
        postUpdateRequestDto.setTitle(newTitle);
        postUpdateRequestDto.setContent(newContent);
        postUpdateRequestDto.setTagTitleOnPost(String.join(",", newTagStringList));

        mockMvc.perform(post(POST_UPDATE_URL)
                        .param("postIdToUpdate", String.valueOf(postUpdateRequestDto.getPostIdToUpdate()))
                        .param("title", postUpdateRequestDto.getTitle())
                        .param("content", postUpdateRequestDto.getContent())
                        .param("tagTitleOnPost", postUpdateRequestDto.getTagTitleOnPost())
                        .with(csrf()))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeDoesNotExist("errorTitle"))
                .andExpect(model().attributeDoesNotExist("errorContent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(POST_VIEW_URL + '/' + savedPostId))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Post updatedPost = postRepository.findById(savedPostId).orElse(null);
        assertEquals(newTitle, updatedPost.getTitle());
        assertEquals(newContent, updatedPost.getContent());
        newTagStringList.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNotNull(tagInDb);
            assertTrue(updatedPost.getCurrentTag().contains(tagInDb));
        });
        POST_TEST_TAG_STRING_LIST.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNotNull(tagInDb);
            assertTrue(updatedPost.getBeforeTag().contains(tagInDb));
        });
    }


    @DisplayName("존재하지 않는 게시물 에러")
    @Test
    void postNotFoundError() throws Exception{
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        String newTitle = "new title.";
        String newContent = "new Content.";
        List<String> newTagStringList = List.of("new tagTitle 1", "new tagTitle 2", "new tagTitle 3");
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto();
        postUpdateRequestDto.setPostIdToUpdate(savedPostId + 1);
        postUpdateRequestDto.setTitle(newTitle);
        postUpdateRequestDto.setContent(newContent);
        postUpdateRequestDto.setTagTitleOnPost(String.join(",", newTagStringList));

        mockMvc.perform(post(POST_UPDATE_URL)
                .param("postIdToUpdate", String.valueOf(postUpdateRequestDto.getPostIdToUpdate()))
                .param("title", postUpdateRequestDto.getTitle())
                .param("content", postUpdateRequestDto.getContent())
                .param("tagTitleOnPost", postUpdateRequestDto.getTagTitleOnPost())
                .with(csrf()))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("errorTitle", "게시물 수정 에러"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Post updatedPost = postRepository.findById(savedPostId).orElse(null);
        assertEquals(POST_TEST_TITLE, updatedPost.getTitle());
        assertEquals(POST_TEST_CONTENT, updatedPost.getContent());
        newTagStringList.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNull(tagInDb);
        });
        POST_TEST_TAG_STRING_LIST.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNotNull(tagInDb);
            assertTrue(updatedPost.getCurrentTag().contains(tagInDb));
        });
        assertTrue(updatedPost.getBeforeTag().isEmpty());
    }


    @DisplayName("작성자가 아닌 계정 에러")
    @Test
    void notAuthorError() throws Exception{
        logInAndOutProcessForTest.logOut();
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));
        String newTitle = "new title.";
        String newContent = "new Content.";
        List<String> newTagStringList = List.of("new tagTitle 1", "new tagTitle 2", "new tagTitle 3");
        PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto();
        postUpdateRequestDto.setPostIdToUpdate(savedPostId);
        postUpdateRequestDto.setTitle(newTitle);
        postUpdateRequestDto.setContent(newContent);
        postUpdateRequestDto.setTagTitleOnPost(String.join(",", newTagStringList));

        mockMvc.perform(post(POST_UPDATE_URL)
                .param("postIdToUpdate", String.valueOf(postUpdateRequestDto.getPostIdToUpdate()))
                .param("title", postUpdateRequestDto.getTitle())
                .param("content", postUpdateRequestDto.getContent())
                .param("tagTitleOnPost", postUpdateRequestDto.getTagTitleOnPost())
                .with(csrf()))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attribute("errorTitle", "글 수정 권한 없음"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        Post updatedPost = postRepository.findById(savedPostId).orElse(null);
        assertEquals(POST_TEST_TITLE, updatedPost.getTitle());
        assertEquals(POST_TEST_CONTENT, updatedPost.getContent());
        newTagStringList.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNull(tagInDb);
        });
        POST_TEST_TAG_STRING_LIST.forEach(tagTitle -> {
            Tag tagInDb = tagRepository.findByTitle(tagTitle);
            assertNotNull(tagInDb);
            assertTrue(updatedPost.getCurrentTag().contains(tagInDb));
        });
        assertTrue(updatedPost.getBeforeTag().isEmpty());
    }
}
