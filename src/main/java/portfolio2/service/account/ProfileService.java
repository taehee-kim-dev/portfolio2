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
public class ProfileService {

    private final ProfileSearchProcess profileSearchProcess;
    private final ProfileUpdateProcess profileUpdateProcess;
    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public Account findUser(String userIdToSearch) {
        return profileSearchProcess.searchProfile(userIdToSearch);
    }

    public void updateProfileAndSession(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        // 프로필 업데이트
        Account updatedAccount
                = profileUpdateProcess.updateProfile(sessionAccount, profileUpdateRequestDto);
        // 세션 업데이트
        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }
}
