package portfolio2.module.post;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PostRepositoryForSearch {

    List<Post> findByKeyword(String keyword);
}
