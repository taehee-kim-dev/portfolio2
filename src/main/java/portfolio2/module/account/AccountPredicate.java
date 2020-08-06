package portfolio2.module.account;

import com.querydsl.core.types.Predicate;
import portfolio2.module.tag.Tag;

import java.util.List;

public class AccountPredicate {

    public static Predicate findAccountByTagOfNewPost(List<Tag> currentTagOfNewPost){
        QAccount accountToFind = QAccount.account;
        return accountToFind.interestTag.any().in(currentTagOfNewPost);
    }

    public static Predicate findAccountByOnlyNewTagOfUpdatedPost(List<Tag> onlyNewTag) {
        QAccount accountToFind = QAccount.account;
        return accountToFind.interestTag.any().in(onlyNewTag);
    }
}
