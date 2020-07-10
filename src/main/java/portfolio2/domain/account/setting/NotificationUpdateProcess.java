package portfolio2.domain.account.setting;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.AccountRepository;
import portfolio2.dto.request.account.setting.NotificationUpdateRequestDto;

@RequiredArgsConstructor
@Component
public class NotificationUpdateProcess {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public Account updateNotification(Account sessionAccount, NotificationUpdateRequestDto notificationUpdateRequestDto) {
        Account accountToUpdate = accountRepository.findByUserId(sessionAccount.getUserId());
        modelMapper.map(notificationUpdateRequestDto, accountToUpdate);
        return accountToUpdate;
    }
    // TODO: 인증된 이메일 없으면 이메일 알림 받을 수 없음.
}
