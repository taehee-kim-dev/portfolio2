package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.dto.request.account.ShowPasswordUpdatePageRequestDto;

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
