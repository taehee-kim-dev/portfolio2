package portfolio2.module.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PostRepositoryForSearch {
    Page<Post> findByKeyword(String keyword, Pageable pageable);
}
