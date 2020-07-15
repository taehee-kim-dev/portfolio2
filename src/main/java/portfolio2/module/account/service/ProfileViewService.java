package portfolio2.module.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.service.process.ProfileSearchProcess;

@RequiredArgsConstructor
@Transactional
@Service
public class ProfileViewService {

    private final ProfileSearchProcess profileSearchProcess;

    public Account findUser(String userIdToSearch) {
        return profileSearchProcess.searchProfile(userIdToSearch);
    }
}
