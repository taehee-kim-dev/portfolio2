package portfolio2.module.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.notification.Notification;
import portfolio2.module.notification.NotificationRepository;
import portfolio2.module.notification.dto.request.NotificationDeleteRequestDto;
import portfolio2.module.notification.dto.response.EachNotificationCountResponseDto;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    public Page<Notification> ringBellCheck(Account sessionAccount, Pageable pageable) {
        Account account = accountRepository.findByUserId(sessionAccount.getUserId());
        List<Notification> notifications = notificationRepository.findByAccount(account);
        notifications.forEach(notification -> notification.setRingBellChecked(true));
        return notificationRepository.findWithPageableByAccount(account, pageable);
    }

    public void linkVisitCheck(Notification notification) {
        notification.setLinkVisited(true);
    }

    public Page<Notification> getLinkUnvisitedNotification(Account sessionAccount, Pageable pageable) {
        Account account = accountRepository.findByUserId(sessionAccount.getUserId());
        return notificationRepository.findWithPageableByAccountAndLinkVisited(account, false, pageable);
    }

    public Page<Notification> getLinkVisitedNotification(Account sessionAccount, Pageable pageable) {
        Account account = accountRepository.findByUserId(sessionAccount.getUserId());
        return notificationRepository.findWithPageableByAccountAndLinkVisited(account, true, pageable);
    }

    public void changeAllToLinkVisited(Account sessionAccount) {
        Account account = accountRepository.findByUserId(sessionAccount.getUserId());
        List<Notification> linkUnvisitedNotification
                = notificationRepository.findNotWithPageableByAccountAndLinkVisited(account, false);
        linkUnvisitedNotification.forEach(notification -> notification.setLinkVisited(true));
    }

    public void deleteAllLinkVisited(Account sessionAccount) {
        Account account = accountRepository.findByUserId(sessionAccount.getUserId());
        notificationRepository.deleteAllByAccountAndLinkVisited(account, true);
    }

    public void deleteNotification(NotificationDeleteRequestDto notificationDeleteRequestDto) {
        notificationRepository.deleteById(notificationDeleteRequestDto.getNotificationIdToDelete());
    }

    public EachNotificationCountResponseDto getEachNotificationCount(Account sessionAccount) {
        Account account = accountRepository.findByUserId(sessionAccount.getUserId());
        Long totalNotificationCount = notificationRepository.countByAccount(account);
        Long linkUnvisitedNotificationCount = notificationRepository.countByAccountAndLinkVisited(account, false);
        long linkVisitedNotificationCount = totalNotificationCount - linkUnvisitedNotificationCount;
        EachNotificationCountResponseDto eachNotificationCountResponseDto = new EachNotificationCountResponseDto();
        eachNotificationCountResponseDto.setTotalNotificationCount(totalNotificationCount);
        eachNotificationCountResponseDto.setLinkUnvisitedNotificationCount(linkUnvisitedNotificationCount);
        eachNotificationCountResponseDto.setLinkVisitedNotificationCount(linkVisitedNotificationCount);
        return eachNotificationCountResponseDto;
    }
}
