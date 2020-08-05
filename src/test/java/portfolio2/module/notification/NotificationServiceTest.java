package portfolio2.module.notification;

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
import portfolio2.module.notification.dto.response.EachNotificationCountResponseDto;
import portfolio2.module.notification.service.NotificationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;

@MockMvcTest
public class NotificationServiceTest extends ContainerBaseTest {

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
    private NotificationService notificationService;

    @AfterEach
    void afterEach(){
        notificationRepository.deleteAll();
        accountRepository.deleteAll();
    }

    PageRequest page0 = PageRequest.of(0, 3, Sort.Direction.DESC, "createdDateTime");
    PageRequest page1 = PageRequest.of(1, 3, Sort.Direction.DESC, "createdDateTime");
    PageRequest page2 = PageRequest.of(2, 3, Sort.Direction.DESC, "createdDateTime");
    PageRequest page3 = PageRequest.of(3, 3, Sort.Direction.DESC, "createdDateTime");

    Account account;

    @BeforeEach
    void beforeEach(){
        account = signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();
        for (long i = 5; i > 0; i--){
            String randomValue = RandomString.make(5);
            Notification notification = new Notification();
            notification.setAccount(account);
            notification.setTitle(randomValue);
            notification.setLink("test-link");
            notification.setLinkVisited(false);
            notification.setCreatedDateTime(LocalDateTime.now().minusHours(10L + i));
            notificationRepository.save(notification);
        }
        for (long i = 5; i > 0; i--){
            String randomValue = RandomString.make(5);
            Notification notification = new Notification();
            notification.setAccount(account);
            notification.setTitle(randomValue);
            notification.setLink("test-link");
            notification.setLinkVisited(true);
            notification.setCreatedDateTime(LocalDateTime.now().minusHours(i));
            notificationRepository.save(notification);
        }
    }

    @DisplayName("모든 알림 - 날짜 순서대로 가져오는지")
    @Test
    void allNotificationDateTimeDescTest(){
        Page<Notification> page0Elements = notificationService.ringBellCheck(account, page0);
        Page<Notification> page1Elements = notificationService.ringBellCheck(account, page1);
        Page<Notification> page2Elements = notificationService.ringBellCheck(account, page2);
        Page<Notification> page3Elements = notificationService.ringBellCheck(account, page3);
        assertEquals(10L, page0Elements.getTotalElements());

        List<Notification> page0List = page0Elements.getContent();
        assertEquals(3, page0List.size());

        List<Notification> page1List = page1Elements.getContent();
        assertEquals(3, page1List.size());

        List<Notification> page2List = page2Elements.getContent();
        assertEquals(3, page2List.size());

        List<Notification> page3List = page3Elements.getContent();
        assertEquals(1, page3List.size());

        assertFalse(page3Elements.hasNext());

        LocalDateTime timeOfBeforeNotification = LocalDateTime.MAX;

        for (Notification notification : page0List) {
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }

        for (Notification notification : page1List) {
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }

        for (Notification notification : page2List) {
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }

        for (Notification notification : page3List) {
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }

        EachNotificationCountResponseDto eachNotificationCountResponseDto
                = notificationService.getEachNotificationCount(account);
        assertEquals(10L, eachNotificationCountResponseDto.getTotalNotificationCount());
        assertEquals(5L, eachNotificationCountResponseDto.getLinkUnvisitedNotificationCount());
        assertEquals(5L, eachNotificationCountResponseDto.getLinkVisitedNotificationCount());
    }


    @DisplayName("링크 방문하지 않은 알림 - 날짜 순서대로 가져오는지")
    @Test
    void linkUnvisitedNotificationDateTimeDescTest(){
        Page<Notification> page0Elements = notificationService.getLinkUnvisitedNotification(account, page0);
        Page<Notification> page1Elements = notificationService.getLinkUnvisitedNotification(account, page1);

        assertEquals(5L, page0Elements.getTotalElements());

        List<Notification> page0List = page0Elements.getContent();
        assertEquals(3, page0List.size());

        List<Notification> page1List = page1Elements.getContent();
        assertEquals(2, page1List.size());

        assertFalse(page1Elements.hasNext());

        LocalDateTime timeOfBeforeNotification = LocalDateTime.MAX;

        for (Notification notification : page0List) {
            assertFalse(notification.isLinkVisited());
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }

        for (Notification notification : page1List) {
            assertFalse(notification.isLinkVisited());
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }
    }


    @DisplayName("링크 방문한 알림 - 날짜 순서대로 가져오는지")
    @Test
    void linkVisitedNotificationDateTimeDescTest(){
        Page<Notification> page0Elements = notificationService.getLinkVisitedNotification(account, page0);
        Page<Notification> page1Elements = notificationService.getLinkVisitedNotification(account, page1);

        assertEquals(5L, page0Elements.getTotalElements());

        List<Notification> page0List = page0Elements.getContent();
        assertEquals(3, page0List.size());

        List<Notification> page1List = page1Elements.getContent();
        assertEquals(2, page1List.size());

        assertFalse(page1Elements.hasNext());

        LocalDateTime timeOfBeforeNotification = LocalDateTime.MAX;

        for (Notification notification : page0List) {
            assertTrue(notification.isLinkVisited());
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }

        for (Notification notification : page1List) {
            assertTrue(notification.isLinkVisited());
            LocalDateTime createdDateTimeOfCurrentNotification = notification.getCreatedDateTime();
            assertTrue(timeOfBeforeNotification.isAfter(createdDateTimeOfCurrentNotification));
            timeOfBeforeNotification = createdDateTimeOfCurrentNotification;
        }
    }

}
