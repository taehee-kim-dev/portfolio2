package portfolio2.module.post;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.account.dto.request.TagUpdateRequestDto;
import portfolio2.module.account.service.AccountSettingService;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.post.service.PostType;
import portfolio2.module.post.service.process.EmailSendingProcessForPost;
import portfolio2.module.tag.Tag;
import portfolio2.module.tag.TagRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static portfolio2.module.account.config.TestAccountInfo.*;
import static portfolio2.module.post.controller.config.StaticVariableNamesAboutPost.POST_NEW_POST_URL;

@MockMvcTest
public class PostNewPostNotificationTest {

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

    private final List<String> TEST_USER_ID_TAG = List.of("tag 1.", "tag 2.", "tag 3.", "tag 4.", "tag 5.");
    private final List<String> TEST_USER_ID_1_TAG = List.of("tag 11.", "tag 22.", "tag 33.", "tag 44.", "tag 55.");
    private final List<String> TEST_USER_ID_2_TAG = List.of("tag 111.", "tag 222.", "tag 333.", "tag 444.", "tag 555.");
    private Long savedPostId;

    private void timeVerificationOfSendingEmailNotification(int time){
                verify(emailSendingProcessForPost, times(time)).sendNotificationEmailForPostWithInterestTag(
                        any(PostType.class), any(Account.class), any(Post.class), anyIterable());
    }

    private void webNotificationVerification(String testUserId, List<String> tagStringList){
        Notification notification = notificationRepository.findByAccount_UserId(testUserId);
        if(tagStringList.size() == 0){
            assertNull(notification);
        }else{
            assertNotNull(notification);
            assertEquals(tagStringList.size(), notification.getCommonTag().size());
            tagStringList.forEach(tagTitle -> {
                Tag tag = tagRepository.findByTitle(tagTitle);
                assertTrue(notification.getCommonTag().contains(tag));
            });
        }
    }

    @AfterEach
    void afterEach(){
        tagRepository.deleteAll();
        postRepository.deleteAll();
        notificationRepository.deleteAll();
        accountRepository.deleteAll();
    }


    @BeforeEach
    void beforeEach(){
        signUpAndLogInEmailVerifiedProcessForTest.signUpAndLogInDefault();
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));
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

    @DisplayName("본인 계정에는 이메일과 웹 알림이 가지 않음")
    @Test
    void authorAccountNotNotified() throws Exception{
        String titleOfNewPost = "Test title";
        String contentOfNewPost = "Test content";
        String tagOfNewPost = "tag 1.,tag 2.";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()));

        this.timeVerificationOfSendingEmailNotification(0);
        this.webNotificationVerification(TEST_USER_ID, new LinkedList<>());
    }

    @DisplayName("새 게시글의 태그를 관심태그로 갖고있는 계정에게 이메일과 웹 알림이 전송됨.")
    @Test
    void emailAndWebNotification() throws Exception{
        String titleOfNewPost = "Test title";
        String contentOfNewPost = "Test content";
        String tagOfNewPost = "tag 1.,tag 2.,tag 11.,tag 22.,tag 111.,tag 222.";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()));

        this.timeVerificationOfSendingEmailNotification(2);
        this.webNotificationVerification(TEST_USER_ID_1, List.of("tag 11.", "tag 22."));
        this.webNotificationVerification(TEST_USER_ID_2, List.of("tag 111.", "tag 222."));
    }

    @DisplayName("새 게시글의 태그를 관심태그로 갖고있는 계정에게 이메일과 웹 알림이 전송됨. - 알림 설정한 계정에게만")
    @Test
    void emailAndWebNotificationWhenNotificationSettingIsTrue() throws Exception{

        Account accountTestUserId1 = accountRepository.findByUserId(TEST_USER_ID_1);
        Account accountTestUserId2 = accountRepository.findByUserId(TEST_USER_ID_2);
        accountTestUserId1.setNotificationNewPostWithMyInterestTagByEmail(false);
        accountTestUserId2.setNotificationNewPostWithMyInterestTagByWeb(false);

        String titleOfNewPost = "Test title";
        String contentOfNewPost = "Test content";
        String tagOfNewPost = "tag 1.,tag 2.,tag 11.,tag 22.,tag 111.,tag 222.";

        mockMvc.perform(post(POST_NEW_POST_URL)
                .param("title", titleOfNewPost)
                .param("content", contentOfNewPost)
                .param("tagTitleOnPost", tagOfNewPost)
                .with(csrf()));

        this.timeVerificationOfSendingEmailNotification(1);
        this.webNotificationVerification(TEST_USER_ID_1, List.of("tag 11.", "tag 22."));
        this.webNotificationVerification(TEST_USER_ID_2, new ArrayList<>());
    }


}
