package portfolio2.module.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import portfolio2.module.account.Account;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Long countByAccountAndChecked(Account sessionAccount, boolean isChecked);
}
