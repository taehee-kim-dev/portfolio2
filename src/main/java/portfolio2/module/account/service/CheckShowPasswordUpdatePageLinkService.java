package portfolio2.module.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.ShowPasswordUpdatePageRequestDto;
import portfolio2.module.account.service.process.LogInOrSessionUpdateProcess;

@Transactional
@RequiredArgsConstructor
@Service
public class CheckShowPasswordUpdatePageLinkService {

    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;
    private final AccountRepository accountRepository;

    public void login(ShowPasswordUpdatePageRequestDto showPasswordUpdatePageRequestDto) {
        Account accountToShowPasswordUpdatePage
                = accountRepository.findByVerifiedEmail(showPasswordUpdatePageRequestDto.getEmail());
        accountToShowPasswordUpdatePage.setShowPasswordUpdatePageToken(null);
        logInOrSessionUpdateProcess.loginOrSessionUpdate(accountToShowPasswordUpdatePage);
    }
}
