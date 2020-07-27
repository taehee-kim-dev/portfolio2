package portfolio2.module.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio2.module.account.Account;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Long countByAccountAndRingBellCheckedOrderByCreatedDateTimeDesc(Account sessionAccount, boolean ringBellChecked);
    List<Notification> findAllByAccount_UserId(String userID);
    Notification findByAccount_UserId(String userId);
}
