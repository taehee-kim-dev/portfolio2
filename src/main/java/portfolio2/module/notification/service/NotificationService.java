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

    private final NotificationRepository notificationRepository;

    public List<Notification> ringBellCheck(Account sessionAccount) {
        List<Notification> allNotification = notificationRepository.findByAccountOrderByCreatedDateTimeDesc(sessionAccount);
        allNotification.forEach(notification -> notification.setRingBellChecked(true));
        return allNotification;
    }

    public void linkVisitCheck(Notification notification) {
        notification.setLinkVisited(true);
    }

    public List<Notification> getLinkUnvisitedNotification(Account sessionAccount) {
        return notificationRepository.findByAccountAndLinkVisitedOrderByCreatedDateTimeDesc(sessionAccount, false);
    }

    public List<Notification> getLinkVisitedNotification(Account sessionAccount) {
        return notificationRepository.findByAccountAndLinkVisitedOrderByCreatedDateTimeDesc(sessionAccount, true);
    }

    public void changeAllToLinkVisited(Account sessionAccount) {
        List<Notification> linkUnvisitedNotification = this.getLinkUnvisitedNotification(sessionAccount);
        linkUnvisitedNotification.forEach(notification -> notification.setLinkVisited(true));
    }

    public void deleteAllLinkVisited(Account sessionAccount) {
        notificationRepository.deleteAllByAccountAndLinkVisited(sessionAccount, true);
    }

    public void deleteNotification(NotificationDeleteRequestDto notificationDeleteRequestDto) {
        notificationRepository.deleteById(notificationDeleteRequestDto.getNotificationIdToDelete());
    }
}
