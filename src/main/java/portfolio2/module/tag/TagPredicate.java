package portfolio2.module.tag;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import portfolio2.module.account.QAccount;

import java.util.List;
import java.util.Set;

public class TagPredicate {

    public static Predicate findAllTagByAccountInterestTagAndTagOfNewPost(List<Tag> accountInterestTag, List<Tag> tagOfNewPost){
        QTag tag = QTag.tag;
        return tag.in(accountInterestTag).and(tag.in(tagOfNewPost));
    }

    public static Predicate findOnlyNewTagOfUpdatedPost(List<Tag> newTagOfUpdatedPost, List<Tag> beforeTagOfUpdatedPost) {
        QTag tag = QTag.tag;
        return tag.in(newTagOfUpdatedPost).and(tag.notIn(beforeTagOfUpdatedPost));
    }

    public static Predicate findAllTagOfOnlyNewTagOfUpdatedPostAndInterestTagOfAccount(List<Tag> onlyNewTagOfUpdatedPost, List<Tag> interestTagOfAccount) {
        QTag tag = QTag.tag;
        return tag.in(onlyNewTagOfUpdatedPost).and(tag.in(interestTagOfAccount));
    }
}
