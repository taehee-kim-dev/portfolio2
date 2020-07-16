package portfolio2.module.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long>, QuerydslPredicateExecutor<Tag> {
    Tag findByTitle(String title);
}
