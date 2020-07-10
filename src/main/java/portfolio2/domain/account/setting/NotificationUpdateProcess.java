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
        if(!accountToUpdate.isEmailVerified()){
            accountToUpdate.setNotificationLikeOnMyPostByEmail(false);
            accountToUpdate.setNotificationLikeOnMyReplyByEmail(false);
            accountToUpdate.setNotificationReplyOnMyPostByEmail(false);
            accountToUpdate.setNotificationReplyOnMyReplyByEmail(false);
            accountToUpdate.setNotificationNewPostWithMyTagByEmail(false);
        }
        return accountToUpdate;
    }
}
