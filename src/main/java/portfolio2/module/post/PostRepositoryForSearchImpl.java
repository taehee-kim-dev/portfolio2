package portfolio2.module.post;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class PostRepositoryForSearchImpl extends QuerydslRepositorySupport implements PostRepositoryForSearch{

    public PostRepositoryForSearchImpl() {
        super(Post.class);
    }

    @Override
    public List<Post> findByKeyword(String keyword) {
        QPost post = QPost.post;
        final JPQLQuery<Post> query = from(post).where(post.title.containsIgnoreCase(keyword)
                .or(post.content.containsIgnoreCase(keyword))
                .or(post.currentTag.any().title.containsIgnoreCase(keyword)))
                .orderBy(post.firstWrittenDateTime.desc());
        return query.fetch();
    }
}
