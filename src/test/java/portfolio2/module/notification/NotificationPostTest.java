package portfolio2.module.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import portfolio2.infra.ContainerBaseTest;
import portfolio2.infra.MockMvcTest;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.config.LogInAndOutProcessForTest;
import portfolio2.module.account.config.SignUpAndLogInEmailVerifiedProcessForTest;
import portfolio2.module.account.config.SignUpAndLogOutEmailVerifiedProcessForTest;
import portfolio2.module.notification.dto.request.NotificationDeleteRequestDto;
import portfolio2.module.tag.TagRepository;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.notification.controller.config.UrlAndViewNameAboutNotification.*;

@MockMvcTest
public class NotificationPostTest extends ContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SignUpAndLogInEmailVerifiedProcessForTest signUpAndLogInEmailVerifiedProcessForTest;

    @Autowired
    private SignUpAndLogOutEmailVerifiedProcessForTest signUpAndLogOutEmailVerifiedProcessForTest;

    @Autowired
    private ObjectMapper objectMapper;

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
    private Long idOfThirdNotificationForAccount = -1L;
    private Long idOfFourthNotificationForAccount2 = -1L;
    private Long idOfFifthNotificationForAccount2 = -1L;


    @BeforeEach
    void beforeEach(){
        Account account = signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();
        Account account2 = signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutNotDefaultWith(TEST_USER_ID_2);

        Notification firstNotificationForAccount = new Notification();
        Notification secondNotificationForAccount = new Notification();
        Notification thirdNotificationForAccount = new Notification();
        Notification fourthNotificationForAccount2 = new Notification();
        Notification fifthNotificationForAccount2 = new Notification();

        firstNotificationForAccount.setAccount(account);
        idOfFirstNotificationForAccount
                = notificationRepository.save(firstNotificationForAccount).getId();

        secondNotificationForAccount.setAccount(account);
        idOfSecondNotificationForAccount
                = notificationRepository.save(secondNotificationForAccount).getId();

        thirdNotificationForAccount.setAccount(account);
        idOfThirdNotificationForAccount
                = notificationRepository.save(thirdNotificationForAccount).getId();

        fourthNotificationForAccount2.setAccount(account2);
        idOfFourthNotificationForAccount2
                = notificationRepository.save(fourthNotificationForAccount2).getId();

        fifthNotificationForAccount2.setAccount(account2);
        idOfFifthNotificationForAccount2
                = notificationRepository.save(fifthNotificationForAccount2).getId();
    }

    @DisplayName("안읽은 알림 모두 읽은 알림으로 전환 - 전체 알림 리스트 뷰에서 요청")
    @Test
    void changeAllToLinkVisitedFromAllList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2 = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification4 = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5 = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1);
        assertNotNull(notification2);
        assertNotNull(notification4);
        assertNotNull(notification5);

        assertFalse(notification1.isLinkVisited());
        assertFalse(notification2.isLinkVisited());
        assertFalse(notification4.isLinkVisited());
        assertFalse(notification5.isLinkVisited());

        mockMvc.perform(post(CHANGE_ALL_LINK_UNVISITED_NOTIFICATION_TO_VISITED_URL)
                .param("currentUrl", ALL_NOTIFICATION_LIST_URL)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ALL_NOTIFICATION_LIST_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1After);
        assertNotNull(notification2After);
        assertNotNull(notification4After);
        assertNotNull(notification5After);

        assertTrue(notification1After.isLinkVisited());
        assertTrue(notification2After.isLinkVisited());
        assertFalse(notification4After.isLinkVisited());
        assertFalse(notification5After.isLinkVisited());
    }

    @DisplayName("읽은 알림 모두 삭제 - 전체 알림 리스트 뷰에서 요청")
    @Test
    void deleteAllLinkVisitedFromAllList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1ForSetting = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2ForSetting = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        assertNotNull(notification1ForSetting);
        assertNotNull(notification2ForSetting);
        notification1ForSetting.setLinkVisited(true);
        notification2ForSetting.setLinkVisited(true);
        notificationRepository.save(notification1ForSetting);
        notificationRepository.save(notification2ForSetting);

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2 = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3 = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4 = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5 = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1);
        assertNotNull(notification2);
        assertNotNull(notification3);

        assertNotNull(notification4);
        assertNotNull(notification5);

        assertTrue(notification1.isLinkVisited());
        assertTrue(notification2.isLinkVisited());
        assertFalse(notification3.isLinkVisited());

        assertFalse(notification4.isLinkVisited());
        assertFalse(notification5.isLinkVisited());

        mockMvc.perform(post(DELETE_ALL_LINK_VISITED_NOTIFICATION_URL)
                .param("currentUrl", ALL_NOTIFICATION_LIST_URL)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ALL_NOTIFICATION_LIST_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3After = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNull(notification1After);
        assertNull(notification2After);

        assertNotNull(notification3After);
        assertNotNull(notification4After);
        assertNotNull(notification5After);

        assertFalse(notification3After.isLinkVisited());
        assertFalse(notification4After.isLinkVisited());
        assertFalse(notification5After.isLinkVisited());
    }

    @DisplayName("안읽은 알림 모두 읽은 알림으로 전환 - 안읽은 알림 리스트 뷰에서 요청")
    @Test
    void changeAllToLinkVisitedFromLinkUnvisitedList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2 = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification4 = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5 = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1);
        assertNotNull(notification2);
        assertNotNull(notification4);
        assertNotNull(notification5);

        assertFalse(notification1.isLinkVisited());
        assertFalse(notification2.isLinkVisited());
        assertFalse(notification4.isLinkVisited());
        assertFalse(notification5.isLinkVisited());

        mockMvc.perform(post(CHANGE_ALL_LINK_UNVISITED_NOTIFICATION_TO_VISITED_URL)
                .param("currentUrl", LINK_UNVISITED_NOTIFICATION_LIST_URL)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LINK_UNVISITED_NOTIFICATION_LIST_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1After);
        assertNotNull(notification2After);
        assertNotNull(notification4After);
        assertNotNull(notification5After);

        assertTrue(notification1After.isLinkVisited());
        assertTrue(notification2After.isLinkVisited());
        assertFalse(notification4After.isLinkVisited());
        assertFalse(notification5After.isLinkVisited());
    }

    @DisplayName("읽은 알림 모두 삭제 - 안읽은 알림 리스트 뷰에서 요청")
    @Test
    void deleteAllLinkVisitedFromLinkUnvisitedList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1ForSetting = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2ForSetting = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        assertNotNull(notification1ForSetting);
        assertNotNull(notification2ForSetting);
        notification1ForSetting.setLinkVisited(true);
        notification2ForSetting.setLinkVisited(true);
        notificationRepository.save(notification1ForSetting);
        notificationRepository.save(notification2ForSetting);

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2 = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3 = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4 = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5 = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1);
        assertNotNull(notification2);
        assertNotNull(notification3);

        assertNotNull(notification4);
        assertNotNull(notification5);

        assertTrue(notification1.isLinkVisited());
        assertTrue(notification2.isLinkVisited());
        assertFalse(notification3.isLinkVisited());

        assertFalse(notification4.isLinkVisited());
        assertFalse(notification5.isLinkVisited());

        mockMvc.perform(post(DELETE_ALL_LINK_VISITED_NOTIFICATION_URL)
                .param("currentUrl", LINK_UNVISITED_NOTIFICATION_LIST_URL)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LINK_UNVISITED_NOTIFICATION_LIST_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3After = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNull(notification1After);
        assertNull(notification2After);

        assertNotNull(notification3After);
        assertNotNull(notification4After);
        assertNotNull(notification5After);

        assertFalse(notification3After.isLinkVisited());
        assertFalse(notification4After.isLinkVisited());
        assertFalse(notification5After.isLinkVisited());
    }

    @DisplayName("안읽은 알림 모두 읽은 알림으로 전환 - 읽은 알림 리스트 뷰에서 요청")
    @Test
    void changeAllToLinkVisitedFromLinkVisitedList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2 = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification4 = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5 = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1);
        assertNotNull(notification2);
        assertNotNull(notification4);
        assertNotNull(notification5);

        assertFalse(notification1.isLinkVisited());
        assertFalse(notification2.isLinkVisited());
        assertFalse(notification4.isLinkVisited());
        assertFalse(notification5.isLinkVisited());

        mockMvc.perform(post(CHANGE_ALL_LINK_UNVISITED_NOTIFICATION_TO_VISITED_URL)
                .param("currentUrl", LINK_VISITED_NOTIFICATION_LIST_URL)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LINK_VISITED_NOTIFICATION_LIST_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1After);
        assertNotNull(notification2After);
        assertNotNull(notification4After);
        assertNotNull(notification5After);

        assertTrue(notification1After.isLinkVisited());
        assertTrue(notification2After.isLinkVisited());
        assertFalse(notification4After.isLinkVisited());
        assertFalse(notification5After.isLinkVisited());
    }

    @DisplayName("읽은 알림 모두 삭제 - 읽은 알림 리스트 뷰에서 요청")
    @Test
    void deleteAllLinkVisitedFromLinkVisitedList() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Notification notification1ForSetting = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2ForSetting = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        assertNotNull(notification1ForSetting);
        assertNotNull(notification2ForSetting);
        notification1ForSetting.setLinkVisited(true);
        notification2ForSetting.setLinkVisited(true);
        notificationRepository.save(notification1ForSetting);
        notificationRepository.save(notification2ForSetting);

        Notification notification1 = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2 = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3 = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4 = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5 = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1);
        assertNotNull(notification2);
        assertNotNull(notification3);

        assertNotNull(notification4);
        assertNotNull(notification5);

        assertTrue(notification1.isLinkVisited());
        assertTrue(notification2.isLinkVisited());
        assertFalse(notification3.isLinkVisited());

        assertFalse(notification4.isLinkVisited());
        assertFalse(notification5.isLinkVisited());

        mockMvc.perform(post(DELETE_ALL_LINK_VISITED_NOTIFICATION_URL)
                .param("currentUrl", LINK_VISITED_NOTIFICATION_LIST_URL)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LINK_VISITED_NOTIFICATION_LIST_URL))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3After = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNull(notification1After);
        assertNull(notification2After);

        assertNotNull(notification3After);
        assertNotNull(notification4After);
        assertNotNull(notification5After);

        assertFalse(notification3After.isLinkVisited());
        assertFalse(notification4After.isLinkVisited());
        assertFalse(notification5After.isLinkVisited());
    }

    @DisplayName("알림 삭제 - 본인 - 존재하는 알림 정상 삭제")
    @Test
    void deleteNotificationSuccess() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        NotificationDeleteRequestDto notificationDeleteRequestDto = new NotificationDeleteRequestDto();
        notificationDeleteRequestDto.setNotificationIdToDelete(idOfFirstNotificationForAccount);

        mockMvc.perform(post(NOTIFICATION_DELETE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificationDeleteRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3After = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNull(notification1After);
        assertNotNull(notification2After);
        assertNotNull(notification3After);

        assertNotNull(notification4After);
        assertNotNull(notification5After);
    }

    @DisplayName("알림 삭제 - 존재하지 않는 알림 에러")
    @Test
    void deleteNotificationNotFoundError() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID));

        Random random = new Random();
        Long notExistingNotificationIdToDelete = random.nextLong();
        while(notExistingNotificationIdToDelete < 1L
                || notExistingNotificationIdToDelete.equals(idOfFirstNotificationForAccount)
                || notExistingNotificationIdToDelete.equals(idOfSecondNotificationForAccount)
                || notExistingNotificationIdToDelete.equals(idOfThirdNotificationForAccount)
                || notExistingNotificationIdToDelete.equals(idOfFourthNotificationForAccount2)
                || notExistingNotificationIdToDelete.equals(idOfFifthNotificationForAccount2)){
            notExistingNotificationIdToDelete = random.nextLong();
        }

        NotificationDeleteRequestDto notificationDeleteRequestDto = new NotificationDeleteRequestDto();
        notificationDeleteRequestDto.setNotificationIdToDelete(idOfFirstNotificationForAccount);

        mockMvc.perform(post(NOTIFICATION_DELETE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notExistingNotificationIdToDelete))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(authenticated().withUsername(TEST_USER_ID));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3After = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1After);
        assertNotNull(notification2After);
        assertNotNull(notification3After);

        assertNotNull(notification4After);
        assertNotNull(notification5After);
    }

    @DisplayName("알림 삭제 - 본인이 아닌 계정 - 에러 - 존재하는 알림")
    @Test
    void deleteNotificationNotOwnerError() throws Exception{
        logInAndOutProcessForTest.logIn(TEST_USER_ID_2);
        assertTrue(logInAndOutProcessForTest.isLoggedInByUserId(TEST_USER_ID_2));

        NotificationDeleteRequestDto notificationDeleteRequestDto = new NotificationDeleteRequestDto();
        notificationDeleteRequestDto.setNotificationIdToDelete(idOfFirstNotificationForAccount);

        mockMvc.perform(post(NOTIFICATION_DELETE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDeleteRequestDto))
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(authenticated().withUsername(TEST_USER_ID_2));

        Notification notification1After = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        Notification notification2After = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification notification3After = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);

        Notification notification4After = notificationRepository.findById(idOfFourthNotificationForAccount2).orElse(null);
        Notification notification5After = notificationRepository.findById(idOfFifthNotificationForAccount2).orElse(null);

        assertNotNull(notification1After);
        assertNotNull(notification2After);
        assertNotNull(notification3After);

        assertNotNull(notification4After);
        assertNotNull(notification5After);
    }
}
