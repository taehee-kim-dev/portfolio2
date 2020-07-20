package portfolio2.module.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import portfolio2.module.account.dto.request.TagUpdateRequestDto;
import portfolio2.module.account.service.AccountSettingService;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.post.dto.PostNewPostRequestDto;
import portfolio2.module.post.service.PostService;
import portfolio2.module.post.service.PostType;
import portfolio2.module.post.service.process.EmailSendingProcessForPost;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static portfolio2.module.account.config.TestAccountInfo.*;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_NEW_POST_URL;
import static portfolio2.module.post.controller.config.UrlAndViewNameAboutPost.POST_UPDATE_URL;

@MockMvcTest
public class PostUpdateNotificationTest extends ContainerBaseTest {

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
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @MockBean
    EmailSendingProcessForPost emailSendingProcessForPost;

    @Autowired
    private AccountSettingService accountSettingService;

    @Autowired
    private PostService postService;

    private final List<String> TEST_USER_ID_TAG = List.of("tag 1.", "tag 2.", "tag 3.", "tag 4.", "tag 5.");
    private final List<String> TEST_USER_ID_1_TAG = List.of("tag 11.", "tag 22.", "tag 33.", "tag 44.", "tag 55.");
    private final List<String> TEST_USER_ID_2_TAG = List.of("tag 111.", "tag 222.", "tag 333.", "tag 444.", "tag 555.");
    private Long savedPostId;

    private void timeVerificationOfSendingEmailNotification(int time){
        verify(emailSendingProcessForPost, times(time)).sendNotificationEmailForPostWithInterestTag(
                any(PostType.class), any(Account.class), any(Post.class), anyIterable());
    }

    private void webNotificationVerification(String testUserId, List<String> tagStringList){
        Notification notification = notificationRepository.findNotificationByAccount_UserId(testUserId);
        assertNotNull(notification);
        tagStringList.forEach(tagTitle -> {
            Tag tag = tagRepository.findByTitle(tagTitle);
            assertTrue(notification.getCommonTag().contains(tag));
        });
    }

    @BeforeEach
    void beforeEach(){
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
        PostNewPostRequestDto postNewPostRequestDto = new PostNewPostRequestDto();
        postNewPostRequestDto.setTitle("Test title.");
        postNewPostRequestDto.setContent("Test content");
        postNewPostRequestDto.setTagTitleOnPost("tag 1,tag 11,tag 111");
        CustomPrincipal customPrincipal = (CustomPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account sessionAccount = customPrincipal.getSessionAccount();
        Post savedPost = postService.saveNewPostWithTag(sessionAccount, postNewPostRequestDto);
        this.savedPostId = savedPost.getId();

        TagUpdateRequestDto tagUpdateRequestDto = new TagUpdateRequestDto();
        TEST_USER_ID_TAG.forEach(tagTitle -> {
            tagUpdateRequestDto.setTagTitle(tagTitle);
            accountSettingService.addInterestTagToAccount(
                    logInAndOutProcessForTest.getSessionAccount(), tagUpdateRequestDto);
        });
        logInAndOutProcessForTest.logOut();
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_1);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_1));
        TEST_USER_ID_1_TAG.forEach(tagTitle -> {
            tagUpdateRequestDto.setTagTitle(tagTitle);
            accountSettingService.addInterestTagToAccount(
                    logInAndOutProcessForTest.getSessionAccount(), tagUpdateRequestDto);
        });
        logInAndOutProcessForTest.logOut();
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInNotDefaultWith(TEST_USER_ID_2);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));
        TEST_USER_ID_2_TAG.forEach(tagTitle -> {
            tagUpdateRequestDto.setTagTitle(tagTitle);
            accountSettingService.addInterestTagToAccount(
                    logInAndOutProcessForTest.getSessionAccount(), tagUpdateRequestDto);
        });
        logInAndOutProcessForTest.logOut();
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

    }

    @DisplayName("태그 저장 테스트")
    @Test
    void beforeEachTest(){
        Account account = accountRepository.findByUserId(TEST_USER_ID);
        TEST_USER_ID_TAG.forEach(tagTitle -> {
            Tag tag = tagRepository.findByTitle(tagTitle);
            assertNotNull(tag);
            assertTrue(account.getInterestTag().contains(tag));
        });
    }

    @AfterEach
    void afterEach(){
        tagRepository.deleteAll();
        postRepository.deleteAll();
        notificationRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @DisplayName("본인 계정에는 이메일과 웹 알림이 가지 않음")
    @Test
    void authorAccountNotNotified() throws Exception{
        String titleOfNewPost = "Test title";
        String contentOfNewPost = "Test content";
        String tagOfNewPost = "tag 1,tag 11,tag 2.,tag 22.,tag 222.";

        mockMvc.perform(post(POST_UPDATE_URL)
                .param("postIdToUpdate", String.valueOf(savedPostId))
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()));

        this.timeVerificationOfSendingEmailNotification(2);
        Notification notification = notificationRepository.findNotificationByAccount_UserId(TEST_USER_ID);
        assertNull(notification);
    }

    @DisplayName("수정된 게시물의 새로 추가된 태그를 관심태그로 갖고있는 계정에게 이메일과 웹 알림이 전송됨.")
    @Test
    void emailAndWebNotification() throws Exception{
        String titleOfNewPost = "Test title";
        String contentOfNewPost = "Test content";
        String tagOfNewPost = "tag 1,tag 11,tag 2.,tag 22.,tag 33.,tag 222.,tag 333.";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("postIdToUpdate", String.valueOf(savedPostId))
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()));

        this.timeVerificationOfSendingEmailNotification(2);
        this.webNotificationVerification(TEST_USER_ID_1, List.of("tag 22.", "tag 33."));
        this.webNotificationVerification(TEST_USER_ID_2, List.of("tag 222.", "tag 333."));
    }

    @DisplayName("수정된 게시물의 새로 추가된 태그를 관심태그로 갖고있는 계정에게 이메일과 웹 알림이 전송됨. - 알림 설정한 계정에게만")
    @Test
    void emailAndWebNotificationWhenNotificationSettingIsTrue() throws Exception{

        Account accountTestUserId1 = accountRepository.findByUserId(TEST_USER_ID_1);
        Account accountTestUserId2 = accountRepository.findByUserId(TEST_USER_ID_2);
        accountTestUserId1.setNotificationNewPostWithMyInterestTagByEmail(false);
        accountTestUserId2.setNotificationNewPostWithMyInterestTagByWeb(false);

        String titleOfNewPost = "Test title";
        String contentOfNewPost = "Test content";
        String tagOfNewPost = "tag 1,tag 11,tag 2.,tag 22.,tag 33.,tag 222.,tag 333.";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("postIdToUpdate", String.valueOf(savedPostId))
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()));

        this.timeVerificationOfSendingEmailNotification(1);
        this.webNotificationVerification(TEST_USER_ID_1, List.of("tag 22.", "tag 33."));
        Notification notificationForTestUserId2 = notificationRepository.findNotificationByAccount_UserId(TEST_USER_ID_2);
        assertNull(notificationForTestUserId2);
    }


}
