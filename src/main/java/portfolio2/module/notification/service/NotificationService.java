package portfolio2.module.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.notification.dto.NotificationDeleteRequestDto;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<Notification> ringBellCheck(Account sessionAccount, Pageable pageable) {
        Page<Notification> allNotificationPage = notificationRepository.findByAccount(sessionAccount, pageable);
        allNotificationPage.forEach(notification -> notification.setRingBellChecked(true));
        return allNotificationPage;
    }

    public void linkVisitCheck(Notification notification) {
        notification.setLinkVisited(true);
    }

    public Page<Notification> getLinkUnvisitedNotification(Account sessionAccount, Pageable pageable) {
        return notificationRepository.findWithPageableByAccountAndLinkVisited(sessionAccount, false, pageable);
    }

    public Page<Notification> getLinkVisitedNotification(Account sessionAccount, Pageable pageable) {
        return notificationRepository.findWithPageableByAccountAndLinkVisited(sessionAccount, true, pageable);
    }

    public void changeAllToLinkVisited(Account sessionAccount) {
        List<Notification> linkUnvisitedNotification
                = notificationRepository.findNotWithPageableByAccountAndLinkVisited(sessionAccount, false);
        linkUnvisitedNotification.forEach(notification -> notification.setLinkVisited(true));
    }

    public void deleteAllLinkVisited(Account sessionAccount) {
        notificationRepository.deleteAllByAccountAndLinkVisited(sessionAccount, true);
    }

    public void deleteNotification(NotificationDeleteRequestDto notificationDeleteRequestDto) {
        notificationRepository.deleteById(notificationDeleteRequestDto.getNotificationIdToDelete());
    }
}
