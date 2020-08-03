package portfolio2.module.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;

@Transactional(readOnly=true)
public interface PostRepository extends JpaRepository<Post, Long> , PostRepositoryForSearch{
    Post findByAuthor(Account authorAccount);

    Post findPostByAuthor_UserId(String userId);
}
