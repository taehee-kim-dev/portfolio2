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
import portfolio2.module.notification.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID;
import static portfolio2.module.account.config.TestAccountInfo.TEST_USER_ID_2;
import static portfolio2.module.main.config.UrlAndViewNameAboutBasic.*;
import static portfolio2.module.main.config.VariableName.SESSION_ACCOUNT;
import static portfolio2.module.notification.controller.config.UrlAndViewNameAboutNotification.*;

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

    private Long idOfFirstNotificationForAccount = -1L;
    private Long idOfSecondNotificationForAccount = -1L;
    private Long idOfThirdNotificationForAccount = -1L;
    private Long idOfFourthNotificationForAccount = -1L;

    private Long idOfFirstNotificationForAccount2 = -1L;
    private Long idOfSecondNotificationForAccount2 = -1L;
    private Long idOfThirdNotificationForAccount2 = -1L;
    private Long idOfFourthNotificationForAccount2 = -1L;


    @BeforeEach
    void beforeEach(){
        Account account = signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutDefault();
        Account account2 = signUpAndLogOutEmailVerifiedProcessForTest.signUpAndLogOutNotDefaultWith(TEST_USER_ID_2);

        Notification firstNotificationForAccount = new Notification();
        firstNotificationForAccount.setAccount(account);
        firstNotificationForAccount.setCreatedDateTime(LocalDateTime.now().minusMinutes(100));
        idOfFirstNotificationForAccount
                = notificationRepository.save(firstNotificationForAccount).getId();

        Notification secondNotificationForAccount = new Notification();
        secondNotificationForAccount.setAccount(account);
        secondNotificationForAccount.setCreatedDateTime(LocalDateTime.now().minusMinutes(90));
        idOfSecondNotificationForAccount
                = notificationRepository.save(secondNotificationForAccount).getId();

        Notification thirdNotificationForAccount = new Notification();
        thirdNotificationForAccount.setAccount(account);
        thirdNotificationForAccount.setCreatedDateTime(LocalDateTime.now().minusMinutes(80));
        idOfThirdNotificationForAccount
                = notificationRepository.save(thirdNotificationForAccount).getId();

        Notification fourthNotificationForAccount = new Notification();
        fourthNotificationForAccount.setAccount(account);
        fourthNotificationForAccount.setCreatedDateTime(LocalDateTime.now().minusMinutes(70));
        idOfFourthNotificationForAccount
                = notificationRepository.save(fourthNotificationForAccount).getId();



        Notification firstNotificationForAccount2 = new Notification();
        firstNotificationForAccount2.setAccount(account2);
        firstNotificationForAccount2.setCreatedDateTime(LocalDateTime.now().minusMinutes(100));
        idOfFirstNotificationForAccount2
                = notificationRepository.save(firstNotificationForAccount2).getId();

        Notification secondNotificationForAccount2 = new Notification();
        secondNotificationForAccount2.setAccount(account2);
        secondNotificationForAccount2.setCreatedDateTime(LocalDateTime.now().minusMinutes(90));
        idOfSecondNotificationForAccount2
                = notificationRepository.save(secondNotificationForAccount2).getId();

        Notification thirdNotificationForAccount2 = new Notification();
        thirdNotificationForAccount2.setAccount(account2);
        thirdNotificationForAccount2.setCreatedDateTime(LocalDateTime.now().minusMinutes(80));
        idOfThirdNotificationForAccount2
                = notificationRepository.save(thirdNotificationForAccount2).getId();

        Notification fourthNotificationForAccount2 = new Notification();
        fourthNotificationForAccount2.setAccount(account2);
        fourthNotificationForAccount2.setCreatedDateTime(LocalDateTime.now().minusMinutes(70));
        idOfFourthNotificationForAccount2
                = notificationRepository.save(fourthNotificationForAccount2).getId();
    }

    // TODO : pageable 적용
    @DisplayName("모든 알림 - 날짜 순서대로 가져오는지")
    @Test
    void allNotificationDateTimeDescTest(){
        Account account = accountRepository.findByUserId(TEST_USER_ID);
        List<Notification> allNotification = notificationService.ringBellCheck(account);
        assertEquals(4, allNotification.size());
        Notification fourthNotification = allNotification.get(0);
        Notification thirdNotification = allNotification.get(1);
        Notification secondNotification = allNotification.get(2);
        Notification firstNotification = allNotification.get(3);
        assertEquals(idOfFourthNotificationForAccount, fourthNotification.getId());
        assertEquals(idOfThirdNotificationForAccount, thirdNotification.getId());
        assertEquals(idOfSecondNotificationForAccount, secondNotification.getId());
        assertEquals(idOfFirstNotificationForAccount, firstNotification.getId());
        assertTrue(thirdNotification.getCreatedDateTime().isBefore(fourthNotification.getCreatedDateTime()));
        assertTrue(secondNotification.getCreatedDateTime().isBefore(thirdNotification.getCreatedDateTime()));
        assertTrue(firstNotification.getCreatedDateTime().isBefore(secondNotification.getCreatedDateTime()));
    }

    // TODO : pageable 적용
    @DisplayName("링크 방문하지 않은 알림 - 날짜 순서대로 가져오는지")
    @Test
    void linkUnvisitedNotificationDateTimeDescTest(){
        Notification secondNotification = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        assertNotNull(secondNotification);
        secondNotification.setLinkVisited(true);
        notificationRepository.save(secondNotification);

        Account account = accountRepository.findByUserId(TEST_USER_ID);
        List<Notification> linkUnvisitedNotification = notificationService.getLinkUnvisitedNotification(account);
        assertEquals(3, linkUnvisitedNotification.size());
        Notification fourthNotification = linkUnvisitedNotification.get(0);
        Notification thirdNotification = linkUnvisitedNotification.get(1);
        Notification firstNotification = linkUnvisitedNotification.get(2);
        assertFalse(linkUnvisitedNotification.contains(secondNotification));
        assertEquals(idOfFourthNotificationForAccount, fourthNotification.getId());
        assertEquals(idOfThirdNotificationForAccount, thirdNotification.getId());
        assertEquals(idOfFirstNotificationForAccount, firstNotification.getId());
        assertTrue(thirdNotification.getCreatedDateTime().isBefore(fourthNotification.getCreatedDateTime()));
        assertTrue(firstNotification.getCreatedDateTime().isBefore(thirdNotification.getCreatedDateTime()));
    }

    // TODO : pageable 적용
    @DisplayName("링크 방문한 알림 - 날짜 순서대로 가져오는지")
    @Test
    void linkVisitedNotificationDateTimeDescTest(){
        Notification fourthNotification = notificationRepository.findById(idOfFourthNotificationForAccount).orElse(null);
        Notification thirdNotification = notificationRepository.findById(idOfThirdNotificationForAccount).orElse(null);
        Notification secondNotification = notificationRepository.findById(idOfSecondNotificationForAccount).orElse(null);
        Notification firstNotification = notificationRepository.findById(idOfFirstNotificationForAccount).orElse(null);
        assertNotNull(fourthNotification);
        assertNotNull(thirdNotification);
        assertNotNull(secondNotification);
        fourthNotification.setLinkVisited(true);
        thirdNotification.setLinkVisited(true);
        secondNotification.setLinkVisited(true);
        notificationRepository.save(fourthNotification);
        notificationRepository.save(thirdNotification);
        notificationRepository.save(secondNotification);

        Account account = accountRepository.findByUserId(TEST_USER_ID);
        List<Notification> linkVisitedNotification = notificationService.getLinkVisitedNotification(account);
        assertEquals(3, linkVisitedNotification.size());
        Notification foundFourthNotification = linkVisitedNotification.get(0);
        Notification foundThirdNotification = linkVisitedNotification.get(1);
        Notification foundSecondNotification = linkVisitedNotification.get(2);
        assertFalse(linkVisitedNotification.contains(firstNotification));
        assertEquals(idOfFourthNotificationForAccount, foundFourthNotification.getId());
        assertEquals(idOfThirdNotificationForAccount, foundThirdNotification.getId());
        assertEquals(idOfSecondNotificationForAccount, foundSecondNotification.getId());
        assertTrue(foundThirdNotification.getCreatedDateTime().isBefore(foundFourthNotification.getCreatedDateTime()));
        assertTrue(foundSecondNotification.getCreatedDateTime().isBefore(foundThirdNotification.getCreatedDateTime()));
    }

}
