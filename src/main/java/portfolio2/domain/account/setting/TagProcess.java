package portfolio2.domain.account.setting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.tag.Tag;
import portfolio2.domain.tag.TagRepository;
import portfolio2.dto.account.TagUpdateRequestDto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class TagProcess {

    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;

    public List<String> getInterestTagOfAccount(Account sessionAccount) {
        Account accountInDb = accountRepository.findByUserId(sessionAccount.getUserId());
        return accountInDb.getInterestTag().stream().map(Tag::getTitle).collect(Collectors.toList());
    }

    public Tag makeNewTagAndSaveIfNotExists(String tagTitle){
        Tag newTag = new Tag();
        newTag.setTitle(tagTitle);

        Tag tagInDb = tagRepository.findByTitle(tagTitle);

        return Objects.requireNonNullElseGet(tagInDb, () -> tagRepository.save(newTag));
    }

    public void addInterestTagToAccountIfNotHas(Account sessionAccount, TagUpdateRequestDto tagUpdateRequestDto) {
        Account accountInDb = accountRepository.findByUserId(sessionAccount.getUserId());
        Tag newTagInDb = this.makeNewTagAndSaveIfNotExists(tagUpdateRequestDto.getTagTitle());
        accountInDb.getInterestTag().add(newTagInDb);
    }

    public boolean removeInterestTagFromAccount(Account sessionAccount, TagUpdateRequestDto tagUpdateRequestDto) {
        Account accountInDb = accountRepository.findByUserId(sessionAccount.getUserId());
        Tag tagInDb = tagRepository.findByTitle(tagUpdateRequestDto.getTagTitle());
        if (accountInDb == null)
            return false;
        if (tagInDb == null)
            return false;
        accountInDb.getInterestTag().remove(tagInDb);
        return true;
    }
}
