package portfolio2.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUserId(String email);

    Account findByUserId(String userId);

    boolean existsByNickname(String nickname);

    boolean existsByVerifiedEmail(String email);

    Account findByVerifiedEmail(String email);

    boolean existsByEmailWaitingToBeVerified(String email);
}
