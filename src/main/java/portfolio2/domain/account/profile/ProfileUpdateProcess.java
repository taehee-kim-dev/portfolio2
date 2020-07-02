package portfolio2.domain.account.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.account.profile.update.ProfileUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class ProfileUpdateProcess {

    private final AccountRepository accountRepository;

    public Account updateProfile(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        Account accountToUpdate = accountRepository.findByUserId(sessionAccount.getUserId());
        accountToUpdate.setBio(profileUpdateRequestDto.getBio());
        accountToUpdate.setOccupation(profileUpdateRequestDto.getOccupation());
        accountToUpdate.setLocation(profileUpdateRequestDto.getLocation());
        accountToUpdate.setProfileImage(profileUpdateRequestDto.getProfileImage());
        return accountToUpdate;
    }
}
