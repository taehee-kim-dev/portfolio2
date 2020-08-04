package portfolio2.module.post;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import portfolio2.module.account.QAccount;

public class PostRepositoryForSearchImpl extends QuerydslRepositorySupport implements PostRepositoryForSearch{

    public PostRepositoryForSearchImpl() {
        super(Post.class);
    }

    @Override
    public Page<Post> findByKeyword(String keyword, Pageable pageable) {
        QPost post = QPost.post;
        final JPQLQuery<Post> query = from(post).where(post.title.containsIgnoreCase(keyword)
                .or(post.content.containsIgnoreCase(keyword))
                .or(post.currentTag.any().title.containsIgnoreCase(keyword)))
                .leftJoin(post.author, QAccount.account).fetchJoin()
                .distinct();
        final JPQLQuery<Post> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        final QueryResults<Post> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
