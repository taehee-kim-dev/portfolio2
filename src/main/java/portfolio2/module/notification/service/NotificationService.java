package portfolio2.module.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.notification.dto.NotificationDeleteRequestDto;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    public List<Notification> ringBellCheck(Account sessionAccount) {
        Account account = accountRepository.findByUserId(sessionAccount.getUserId());
        List<Notification> allNotification = notificationRepository.findByAccount(account);
        allNotification.forEach(notification -> {
            notification.setRingBellChecked(true);
        });
        return allNotification;
    }

    public void linkVisitCheck(Notification notification) {
        notification.setLinkVisited(true);
    }

    public List<Notification> getLinkUnvisitedNotification(Account sessionAccount) {
        return notificationRepository.findByAccountAndLinkVisited(sessionAccount, false);
    }

    public List<Notification> getLinkVisitedNotification(Account sessionAccount) {
        return notificationRepository.findByAccountAndLinkVisited(sessionAccount, true);
    }

    public void deleteNotification(NotificationDeleteRequestDto notificationDeleteRequestDto) {
        notificationRepository.deleteById(notificationDeleteRequestDto.getNotificationIdToDelete());
    }
}
