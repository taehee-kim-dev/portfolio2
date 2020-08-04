package portfolio2.module.main.search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.main.service.MainService;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
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
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;

/**
 * 제목, 내용, 태그로 찾아지는지?
 * 최신순으로 정렬되어있는지?
 * */


@MockMvcTest
public class PostSearchTest  extends ContainerBaseTest {

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
    private MainService mainService;

    private final String KEY_WORD_TO_SEARCH_3 = "testTest";

    private final LocalDateTime STANDARD_TIME = LocalDateTime.now();

    private Long post1Id;
    private Long post2Id;
    private Long post3Id;
    private Long post4Id;
    private Long post5Id;

    @AfterEach
    void afterEach(){
        postRepository.deleteAll();
        tagRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @BeforeEach
    void beforeEach(){
        Account account2 = signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);

        Post post1 = new Post();
        post1.setAuthor(account2);
        post1.setTitle(KEY_WORD_TO_SEARCH_3);
        post1.setContent("blahBlah1");
        post1.setFirstWrittenDateTime(STANDARD_TIME.minusMinutes(100));
        post1.setLastModifiedDateTime(STANDARD_TIME.minusMinutes(100));
        post1Id = postRepository.save(post1).getId();

        Post post2 = new Post();
        post2.setAuthor(account2);
        post2.setTitle("asfewf");
        post2.setContent("testtEst");
        post2.setFirstWrittenDateTime(STANDARD_TIME.minusMinutes(80));
        post2.setLastModifiedDateTime(STANDARD_TIME.minusMinutes(80));
        post2Id = postRepository.save(post2).getId();

        Post post3 = new Post();
        post3.setAuthor(account2);
        post3.setTitle("zvzsfrvb");
        post3.setContent("wvSWV");
        post3.setFirstWrittenDateTime(STANDARD_TIME.minusMinutes(70));
        post3.setLastModifiedDateTime(STANDARD_TIME.minusMinutes(70));
        Tag tagForPost3 = new Tag();
        tagForPost3.setTitle("TestteST");
        post3.getCurrentTag().add(tagRepository.save(tagForPost3));
        post3Id = postRepository.save(post3).getId();

        Post post4 = new Post();
        post4.setAuthor(account2);
        post4.setTitle("cGMHG");
        post4.setContent("RHTH");
        post4.setFirstWrittenDateTime(STANDARD_TIME.minusMinutes(40));
        post4.setLastModifiedDateTime(STANDARD_TIME.minusMinutes(40));
        Tag tagForPost4_1 = new Tag();
        tagForPost4_1.setTitle("Asvfds");
        Tag tagForPost4_2 = new Tag();
        tagForPost4_2.setTitle("Assdbf");
        post4.getCurrentTag().add(tagRepository.save(tagForPost4_1));
        post4.getCurrentTag().add(tagRepository.save(tagForPost4_2));
        post4Id = postRepository.save(post4).getId();

        Post post5 = new Post();
        post5.setAuthor(account2);
        post5.setTitle("XVCNM");
        post5.setContent("ERT");
        post5.setFirstWrittenDateTime(STANDARD_TIME.minusMinutes(20));
        post5.setLastModifiedDateTime(STANDARD_TIME.minusMinutes(20));
        Tag tagForPost5_1 = new Tag();
        tagForPost5_1.setTitle("abet");
        Tag tagForPost5_2 = new Tag();
        tagForPost5_2.setTitle("sergv");
        post5.getCurrentTag().add(tagRepository.save(tagForPost5_1));
        post5.getCurrentTag().add(tagRepository.save(tagForPost5_2));
        post5Id = postRepository.save(post5).getId();

        logInAndOutProcessForTest.logOut();
    }

    @DisplayName("Post검색 - 비로그인 상태 - 존재하는 키워드로 검색 - Controller단")
    @Test
    void searchPostNotLoggedInWithKeywordController() throws Exception{
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get("/search/post")
                        .param("keyword", KEY_WORD_TO_SEARCH_3))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("searchedPost"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Post검색 - 비로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단")
    @Test
    void searchPostNotLoggedInWithNotKeywordController() throws Exception{
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        mockMvc.perform(get("/search/post")
                .param("keyword", "ASgvcb"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("searchedPost"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(unauthenticated());
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하는 키워드로 검색 - Controller단")
    @Test
    void searchPostLoggedInWithKeywordController() throws Exception{
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get("/search/post")
                .param("keyword", KEY_WORD_TO_SEARCH_3))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("searchedPost"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("Post검색 - 로그인 상태 - 존재하지 않는 키워드로 검색 - Controller단")
    @Test
    void searchPostLoggedInWithNotKeywordController() throws Exception{
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get("/search/post")
                .param("keyword", "XCNhgf"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("keyword"))
                .andExpect(model().attributeExists("searchedPost"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("Post검색 - 존재하는 키워드로 검색 - Service단")
    @Test
    void searchPostWithKeywordService() throws Exception{
        assertFalse(logInAndOutProcessForTest.isSomeoneLoggedIn());

        List<Post> searchedPost = mainService.findPostByKeyword(KEY_WORD_TO_SEARCH_3, pageable);
        assertEquals(3, searchedPost.size());
        Post searchedPost3 = searchedPost.get(0);
        Post searchedPost2 = searchedPost.get(1);
        Post searchedPost1 = searchedPost.get(2);
        assertEquals(post3Id, searchedPost3.getId());
        assertEquals(post2Id, searchedPost2.getId());
        assertEquals(post1Id, searchedPost1.getId());
        assertTrue(searchedPost2.getFirstWrittenDateTime().isBefore(searchedPost3.getFirstWrittenDateTime()));
        assertTrue(searchedPost1.getFirstWrittenDateTime().isBefore(searchedPost2.getFirstWrittenDateTime()));
    }

    @DisplayName("Post검색 - 존재하지 않는 키워드로 검색 - Service단")
    @Test
    void searchPostWithNotKeywordService() throws Exception{
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        List<Post> searchedPost = mainService.findPostByKeyword("xfnfhn", pageable);
        assertTrue(searchedPost.isEmpty());
    }
}
