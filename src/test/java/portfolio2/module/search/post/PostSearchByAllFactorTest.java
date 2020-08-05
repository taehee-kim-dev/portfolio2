package portfolio2.module.search.post;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.search.service.SearchService;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.main.config.VariableNameAboutMain.SESSION_ACCOUNT;
import static portfolio2.module.search.controller.config.UrlAndViewNameAboutSearch.SEARCH_POST_RESULT_VIEW_NAME;
import static portfolio2.module.search.controller.config.UrlAndViewNameAboutSearch.SEARCH_POST_URL;

/**
 * 제목, 내용, 태그로 찾아지는지?
 * 최신순으로 정렬되어있는지?
 * */


@MockMvcTest
public class PostSearchByAllFactorTest extends ContainerBaseTest {

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
    private SearchService searchService;

    @Autowired
    private PostService postService;

    private final String KEY_WORD_TO_SEARCH_39 = "testKeyword";

    PageRequest page0 = PageRequest.of(0, 15, Sort.Direction.DESC, "firstWrittenDateTime");
    PageRequest page1 = PageRequest.of(1, 15, Sort.Direction.DESC, "firstWrittenDateTime");
    PageRequest page2 = PageRequest.of(2, 15, Sort.Direction.DESC, "firstWrittenDateTime");

    @AfterEach
    void afterEach(){
        postRepository.deleteAll();
        tagRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @BeforeEach
    void beforeEach(){
        Account account2 = signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        // 제목에 키워드 포함 13개의 글
        for(long i = 13; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();

            String randomValueForTitle = RandomString.make(5);
            postNewPostRequestDto.setTitle(KEY_WORD_TO_SEARCH_39 + randomValueForTitle);

            postNewPostRequestDto.setContent("Test content not contains keyword");
            postNewPostRequestDto.setTagTitleOnPost("Tag1" + ',' + "Tag2");

            postService.saveNewPostWithTag(account2, postNewPostRequestDto);
        }

        // 내용에 키워드 포함 13개의 글
        for(long i = 13; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");

            String randomValueForContent = RandomString.make(5);
            postNewPostRequestDto.setContent(KEY_WORD_TO_SEARCH_39 + randomValueForContent);

            postNewPostRequestDto.setTagTitleOnPost("Tag3" + ',' + "Tag4");

            postService.saveNewPostWithTag(account2, postNewPostRequestDto);
        }

        // 태그에 키워드 포함 13개의 글
        for(long i = 13; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");
            postNewPostRequestDto.setContent("Test content not contains keyword");

            String randomValueForTag1 = RandomString.make(3);
            String randomValueForTag2 = RandomString.make(3);
            postNewPostRequestDto.setTagTitleOnPost(KEY_WORD_TO_SEARCH_39 + randomValueForTag1
                    + ',' + KEY_WORD_TO_SEARCH_39 + randomValueForTag2);

            postService.saveNewPostWithTag(account2, postNewPostRequestDto);
        }

        // 어느 키워드도 포함되지 않는 13개의 글
        for(long i = 13; i >= 1; i--){
            PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
            postNewPostRequestDto.setTitle("Test title not contains keyword");
            postNewPostRequestDto.setContent("Test content not contains keyword");
            postNewPostRequestDto.setTagTitleOnPost("Tag5" + ',' + "Tag6");

            postService.saveNewPostWithTag(account2, postNewPostRequestDto);
        }

        logInAndOutProcessForTest.logOut();
    }

    @DisplayName("Post검색 - 비로그인 상태 - 존재하는 키워드로 검색 - Controller단")
    @Test
    void searchPostNotLoggedInWithKeywordController() throws Exception{
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SEARCH_POST_URL)
                        .param("keyword", KEY_WORD_TO_SEARCH_39))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("Post검색 - 비로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단")
    @Test
    void searchPostNotLoggedInWithNotKeywordController() throws Exception{
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "ASgvcb"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하는 키워드로 검색 - Controller단")
    @Test
    void searchPostLoggedInWithKeywordController() throws Exception{
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", KEY_WORD_TO_SEARCH_39))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단")
    @Test
    void searchPostLoggedInWithNotKeywordController() throws Exception{
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(SEARCH_POST_URL)
                .param("keyword", "XCNhgf"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("postPage"))
                .andExpect(status().isOk())
                .andExpect(view().name(SEARCH_POST_RESULT_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("Post검색 - 존재하는 키워드로 검색 - 39개의 결과 - Service단")
    @Test
    void searchPostWithKeywordService() throws Exception{
        Page<Post> page0Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_39, page0);
        Page<Post> page1Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_39, page1);
        Page<Post> page2Elements = searchService.findPostByKeyword(KEY_WORD_TO_SEARCH_39, page2);

        assertEquals(39L, page0Elements.getTotalElements());

        List<Post> page0List = page0Elements.getContent();
        assertEquals(15, page0List.size());

        List<Post> page1List = page1Elements.getContent();
        assertEquals(15, page1List.size());

        List<Post> page2List = page2Elements.getContent();
        assertEquals(9, page2List.size());

        assertFalse(page2Elements.hasNext());

        LocalDateTime timeOfBeforePost = LocalDateTime.MAX;

        for (Post post : page0List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_39));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }

        for (Post post : page1List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_39));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }

        for (Post post : page2List) {
            assertTrue(isPostHasKeyword(post, KEY_WORD_TO_SEARCH_39));
            LocalDateTime firstWrittenDateTimeOfCurrentPost = post.getFirstWrittenDateTime();
            assertTrue(timeOfBeforePost.isAfter(firstWrittenDateTimeOfCurrentPost));
            timeOfBeforePost = firstWrittenDateTimeOfCurrentPost;
        }
    }

    private boolean isPostHasKeyword(Post post, String keyword) {
        return post.getTitle().contains(keyword)
                || post.getContent().contains(keyword)
                || isCurrentTagTitleOfPostHasKeyword(post, keyword);
    }

    private boolean isCurrentTagTitleOfPostHasKeyword(Post post, String keyword) {
        for (Tag tag : post.getCurrentTag()){
            if(tag.getTitle().contains(keyword))
                return true;
        }
        return false;
    }



    @DisplayName("Post검색 - 존재하지 않는 키워드로 검색 - Service단")
    @Test
    void searchPostWithNotKeywordService() throws Exception{
        String notKeyWord = "notKeyword";
        Page<Post> page0Elements = searchService.findPostByKeyword(notKeyWord, page0);

        assertEquals(0L, page0Elements.getTotalElements());

        List<Post> page0List = page0Elements.getContent();
        assertEquals(0, page0List.size());
        assertTrue(page0Elements.isEmpty());

        assertFalse(page0Elements.hasNext());
    }
}
