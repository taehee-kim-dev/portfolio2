package portfolio2.module.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import portfolio2.module.account.Account;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countByAccountAndRingBellChecked(Account sessionAccount, boolean ringBellChecked);

    @EntityGraph(value = "Notification.withCommonTag", type = EntityGraph.EntityGraphType.LOAD)
    Page<Notification> findByAccount(Account sessionAccount, Pageable pageable);

    @EntityGraph(value = "Notification.withCommonTag", type = EntityGraph.EntityGraphType.LOAD)
    Page<Notification> findWithPageableByAccountAndLinkVisited(Account sessionAccount, boolean linkVisited, Pageable pageable);

    @EntityGraph(value = "Notification.withCommonTag", type = EntityGraph.EntityGraphType.LOAD)
    List<Notification> findNotWithPageableByAccountAndLinkVisited(Account sessionAccount, boolean linkVisited);

    void deleteAllByAccountAndLinkVisited(Account sessionAccount, boolean linkVisited);

    Notification findByAccount_UserId(String userId);
}
