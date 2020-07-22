package portfolio2.module.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio2.module.account.Account;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAccountOrderByCreatedDateTimeDesc(Account sessionAccount);
    List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account sessionAccount, boolean isChecked);

    Notification findNotificationByAccount_UserId(String userId);
}
