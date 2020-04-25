package portfolio2.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUserId(String email);

    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    Account findByEmail(String email);

    Account findByUserId(String userId);

    Account findByNickname(String nickname);
}
