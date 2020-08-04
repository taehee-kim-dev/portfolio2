package portfolio2.module.notification;

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
import portfolio2.module.post.PostRepository;
import portfolio2.module.tag.TagRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.notification.controller.config.UrlAndViewNameAboutNotification.*;

@MockMvcTest
public class NotificationGetTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private LogInAndOutProcessForTest logInAndOutProcessForTest;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TagRepository tagRepository;

    @AfterEach
    void afterEach(){
        notificationRepository.deleteAll();
        tagRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private Long idOfFirstNotificationForAccount = -1L;
    private Long idOfSecondNotificationForAccount = -1L;
    private Long idOfThirdNotificationForAccount2 = -1L;
    private Long idOfFourthNotificationForAccount2 = -1L;

    private String LinkForFirstNotificationForAccount = "firstLink";


    @BeforeEach
    void beforeEach(){
        Account account = signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();
        Account account2 = signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutNotDefaultWith(TEST_USER_ID_2);

        Notification firstNotificationForAccount = new Notification();
        Notification secondNotificationForAccount = new Notification();
        Notification thirdNotificationForAccount2 = new Notification();
        Notification fourthNotificationForAccount2 = new Notification();

        firstNotificationForAccount.setAccount(account);
        firstNotificationForAccount.setLink(LinkForFirstNotificationForAccount);
        idOfFirstNotificationForAccount
                = notificationRepository.save(firstNotificationForAccount).getId();

        secondNotificationForAccount.setAccount(account);
        idOfSecondNotificationForAccount
                = notificationRepository.save(secondNotificationForAccount).getId();

        thirdNotificationForAccount2.setAccount(account2);
        idOfThirdNotificationForAccount2
                = notificationRepository.save(thirdNotificationForAccount2).getId();

        fourthNotificationForAccount2.setAccount(account2);
        idOfFourthNotificationForAccount2
                = notificationRepository.save(fourthNotificationForAccount2).getId();
    }

    @DisplayName("모든 알림 목록 보여줄 때, 모든 알림의 ringBellCheck값 false에서 true로 바뀜")
    @Test
    void showAllNotificationList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2 = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        assertNotNull(notification1);
        assertNotNull(notification2);
        assertFalse(notification1.isRingBellChecked());
        assertFalse(notification2.isRingBellChecked());

        mockMvc.perform(get(ALL_NOTIFICATION_LIST_URL))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("allNotificationPage"))
                .andExpect(status().isOk())
                .andExpect(view().name(ALL_NOTIFICATION_LIST_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        assertNotNull(notification1After);
        assertNotNull(notification2After);
        assertTrue(notification1After.isRingBellChecked());
        assertTrue(notification2After.isRingBellChecked());
    }

    @DisplayName("링크를 통해 보지 않은 알림 목록 보여주기")
    @Test
    void showLinkUnvisitedNotificationList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(LINK_UNVISITED_NOTIFICATION_LIST_URL))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("linkUnvisitedNotificationPage"))
                .andExpect(status().isOk())
                .andExpect(view().name(LINK_UNVISITED_NOTIFICATION_LIST_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("링크를 통해 본 알림 목록 보여주기")
    @Test
    void showLinkVisitedNotificationList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        mockMvc.perform(get(LINK_VISITED_NOTIFICATION_LIST_URL))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists("linkVisitedNotificationPage"))
                .andExpect(status().isOk())
                .andExpect(view().name(LINK_VISITED_NOTIFICATION_LIST_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID));
    }

    @DisplayName("링크를 통해 해당 컨텐츠 확인 - 알림 대상에 해당하는 계정으로")
    @Test
    void linkVisit() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        assertNotNull(notification1);
        assertFalse(notification1.isLinkVisited());

        mockMvc.perform(get(NOTIFICATION_LINK_VISIT_URL + '/' + idOfFirstNotificationForAccount))
                .andExpect(model().attributeDoesNotExist(SESSION_ACCOUNT))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(notification1.getLink()))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        assertNotNull(notification1After);
        assertTrue(notification1After.isLinkVisited());
    }

    @DisplayName("링크를 통해 해당 컨텐츠 확인 에러 - 알림 대상에 해당하지 않는 계정으로")
    @Test
    void linkVisitNotOwnerAccount() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID_2);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        assertNotNull(notification1);
        assertFalse(notification1.isLinkVisited());

        mockMvc.perform(get(NOTIFICATION_LINK_VISIT_URL + '/' + idOfFirstNotificationForAccount))
                .andExpect(model().attributeExists(SESSION_ACCOUNT))
                .andExpect(model().attributeExists(ERROR_TITLE))
                .andExpect(model().attributeExists(ERROR_CONTENT))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW_NAME))
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        assertNotNull(notification1After);
        assertFalse(notification1After.isLinkVisited());
    }

}
