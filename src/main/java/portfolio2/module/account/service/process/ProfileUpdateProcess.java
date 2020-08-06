package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.ProfileUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class ProfileUpdateProcess {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public Account updateProfile(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        Account accountToUpdate = accountRepository.findByUserId(sessionAccount.getUserId());
        modelMapper.map(profileUpdateRequestDto, accountToUpdate);
        return accountToUpdate;
    }
}
