package portfolio2.domain.account.setting;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.setting.update.ProfileUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class ProfileUpdateProcess {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public Account updateProfile(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        modelMapper.map(profileUpdateRequestDto, sessionAccount);
        return accountRepository.save(sessionAccount);
    }
}
