package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.domain.account.profile.ProfileSearchProcess;
import portfolio2.domain.account.profile.ProfileUpdateProcess;
import portfolio2.dto.request.account.profile.update.ProfileUpdateRequestDto;

@RequiredArgsConstructor
@Transactional
@Service
public class ProfileViewService {

    private final ProfileSearchProcess profileSearchProcess;

    public Account findUser(String userIdToSearch) {
        return profileSearchProcess.searchProfile(userIdToSearch);
    }
}
