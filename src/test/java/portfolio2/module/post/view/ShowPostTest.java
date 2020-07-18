package portfolio2.module.post.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.post.Post;
import portfolio2.module.post.PostRepository;
import portfolio2.module.tag.TagRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.account.controller.config.UrlAndViewNameAboutAccount.ERROR_VIEW_NAME;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.*;

@MockMvcTest
public class ShowPostTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @BeforeEach
    void beforeEach(){
        signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();
        Account existingAccount = accountRepository.findByUserId(TEST_USER_ID);
        Post newPost = new Post();
        newPost.setAuthor(existingAccount);
        newPost.setTitle("This is test title.");
        newPost.setContent("This is test content.");
        LocalDateTime firstWrittenTime = LocalDateTime.now();
        newPost.setFirstWrittenDateTime(firstWrittenTime);
        newPost.setLastModifiedDateTime(firstWrittenTime);
        postRepository.save(newPost);
    }

    @AfterEach
    void afterEach(){
        List<Account> allAccount = accountRepository.findAll();
        for(Account account : allAccount){
            account.getInterestTag().clear();
            accountRepository.save(account);
        }
        List<Post> allPost = postRepository.findAll();
        for(Post post : allPost){
            post.setAuthor(null);
            post.getCurrentTag().clear();
            postRepository.save(post);
        }
        postRepository.deleteAll();
        tagRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("글 보여주기 - 로그인 안 한 상태")
    @Test
    void showPost() throws Exception{
        Account existingAuthorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(existingAuthorAccount);
        Post existingPost = postRepository.findByAuthor(existingAuthorAccount);
        assertNotNull(existingPost);
        mockMvc.perform(get(POST_VIEW_URL + '/' + existingPost.getId()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("isAuthor", false))
                .andExpect(model().attributeExists("firstWrittenTime"))
                .andExpect(model().attributeExists("tagOnPost"))
                .andExpect(view().name(POST_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("글 보여주기 - 작성자 계정으로 로그인 한 상태")
    @Test
    void showPostWithAuthorAccount() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        Account existingAuthorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(existingAuthorAccount);
        Post existingPost = postRepository.findByAuthor(existingAuthorAccount);
        assertNotNull(existingPost);
        mockMvc.perform(get(POST_VIEW_URL + '/' + existingPost.getId()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("isAuthor", true))
                .andExpect(model().attributeExists("firstWrittenTime"))
                .andExpect(model().attributeExists("tagOnPost"))
                .andExpect(view().name(POST_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("글 보여주기 - 작성자가 아닌 계정으로 로그인 한 상태")
    @Test
    void showPostWithNotAuthorAccount() throws Exception{
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);
        Account existingAuthorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(existingAuthorAccount);
        Post existingPost = postRepository.findByAuthor(existingAuthorAccount);
        assertNotNull(existingPost);
        mockMvc.perform(get(POST_VIEW_URL + '/' + existingPost.getId()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("isAuthor", false))
                .andExpect(model().attributeExists("firstWrittenTime"))
                .andExpect(model().attributeExists("tagOnPost"))
                .andExpect(view().name(POST_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));
    }

    @DisplayName("존재하지 않는 게시글 조회 - 로그아웃 상태")
    @Test
    void showNotExistingPostErrorLoggedOut() throws Exception{
        Account existingAuthorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(existingAuthorAccount);
        Post existingPost = postRepository.findByAuthor(existingAuthorAccount);
        assertNotNull(existingPost);
        mockMvc.perform(get(POST_VIEW_URL + '/' + existingPost.getId() + 1))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("errorTitle"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(model().attributeDoesNotExist(("post")))
                .andExpect(model().attributeDoesNotExist("isAuthor"))
                .andExpect(model().attributeDoesNotExist("firstWrittenTime"))
                .andExpect(model().attributeDoesNotExist("tagOnPost"))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(unauthenticated());
    }

    @DisplayName("존재하지 않는 게시글 조회 - 로그인 상태")
    @Test
    void showNotExistingPostErrorLoggedIn() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        Account existingAuthorAccount = accountRepository.findByUserId(TEST_USER_ID);
        assertNotNull(existingAuthorAccount);
        Post existingPost = postRepository.findByAuthor(existingAuthorAccount);
        assertNotNull(existingPost);
        mockMvc.perform(get(POST_VIEW_URL + '/' + existingPost.getId() + 1))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("errorTitle"))
                .andExpect(model().attributeExists("errorContent"))
                .andExpect(model().attributeDoesNotExist(("post")))
                .andExpect(model().attributeDoesNotExist("isAuthor"))
                .andExpect(model().attributeDoesNotExist("firstWrittenTime"))
                .andExpect(model().attributeDoesNotExist("tagOnPost"))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

}
