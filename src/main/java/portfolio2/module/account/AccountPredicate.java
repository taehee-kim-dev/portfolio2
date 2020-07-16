package portfolio2.module.account;

import com.querydsl.core.types.Predicate;
import portfolio2.module.tag.Tag;

import java.util.Set;

public class AccountPredicate {

    public static Predicate findByTag(Set<Tag> postTag){
        QAccount accountToFind = QAccount.account;
        return accountToFind.interestTag.any().in(postTag);
    }
}
