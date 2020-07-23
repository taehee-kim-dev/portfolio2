package portfolio2.module.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
    boolean existsByUserId(String email);

    Account findByUserId(String userId);

    boolean existsByNickname(String nickname);

    boolean existsByVerifiedEmail(String email);

    Account findByVerifiedEmail(String email);

    boolean existsByEmailWaitingToBeVerified(String email);

    Account findByUserIdAndEmailWaitingToBeVerified(String userId, String email);

    List<Account> findAllByEmailWaitingToBeVerified(String email);
}
