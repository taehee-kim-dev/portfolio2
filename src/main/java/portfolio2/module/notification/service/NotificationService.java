package portfolio2.module.notification.service;

import lombok.RequiredArgsConstructor;
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

    public void deleteNotification(NotificationDeleteRequestDto notificationDeleteRequestDto) {
        notificationRepository.deleteById(notificationDeleteRequestDto.getNotificationIdToDelete());
    }

    public List<Notification> ringBellCheck(Account sessionAccount) {
        List<Notification> allNotification = notificationRepository.findAllByAccount_UserId(sessionAccount.getUserId());
        allNotification.forEach(notification -> {
            notification.setRingBellChecked(true);
        });
        return allNotification;
    }
}
