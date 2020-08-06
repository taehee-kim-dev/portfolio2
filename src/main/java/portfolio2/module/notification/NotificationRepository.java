package portfolio2.module.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import portfolio2.module.account.Account;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Long countByAccountAndRingBellChecked(Account account, boolean ringBellChecked);

    Long countByAccount(Account account);

    Long countByAccountAndLinkVisited(Account account, boolean linkVisited);

    @EntityGraph(value = "Notification.withCommonTag", type = EntityGraph.EntityGraphType.LOAD)
    List<Notification> findByAccount(Account account);

    @EntityGraph(value = "Notification.withCommonTag", type = EntityGraph.EntityGraphType.LOAD)
    Page<Notification> findWithPageableByAccount(Account account, Pageable pageable);

    @EntityGraph(value = "Notification.withCommonTag", type = EntityGraph.EntityGraphType.LOAD)
    Page<Notification> findWithPageableByAccountAndLinkVisited(Account account, boolean linkVisited, Pageable pageable);

    @EntityGraph(value = "Notification.withCommonTag", type = EntityGraph.EntityGraphType.LOAD)
    List<Notification> findNotWithPageableByAccountAndLinkVisited(Account account, boolean linkVisited);

    void deleteAllByAccountAndLinkVisited(Account account, boolean linkVisited);

    Notification findByAccount_UserId(String userId);
}
