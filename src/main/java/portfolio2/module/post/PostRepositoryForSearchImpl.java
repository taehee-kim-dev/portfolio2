package portfolio2.module.post;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import portfolio2.module.account.QAccount;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static portfolio2.module.account.QAccount.*;
import static portfolio2.module.post.QPost.post;

public class PostRepositoryForSearchImpl implements PostRepositoryForSearch{

    private final JPAQueryFactory queryFactory;

    public PostRepositoryForSearchImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 꼭 Page<PostResponseDto> 등으로 반환해야 한다. Entity 그대로 반환하면 절대 안됨.
     * */
    @Override
    public Page<Post> findByKeyword(String keyword, Pageable pageable) {
        QueryResults<Post> results = queryFactory
                .select(post)
                .distinct()
                .from(post)
                .leftJoin(post.author, account)
                .where(
                        post.author.userId.containsIgnoreCase(keyword)
                        .or(post.author.nickname.containsIgnoreCase(keyword))
                        .or(post.title.containsIgnoreCase(keyword))
                        .or(post.content.containsIgnoreCase(keyword))
                        .or(post.currentTag.any().title.containsIgnoreCase(keyword))
                )
                .orderBy(post.firstWrittenDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
