package portfolio2.domain.account.setting;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.setting.update.NotificationUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class NotificationUpdateProcess {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public Account updateNotification(Account sessionAccount, NotificationUpdateRequestDto notificationUpdateRequestDto) {
        modelMapper.map(notificationUpdateRequestDto, sessionAccount);
        return accountRepository.save(sessionAccount);
    }
}
