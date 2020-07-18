package portfolio2.module.account.service.process;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import portfolio2.module.account.Account;
import portfolio2.module.account.AccountRepository;
import portfolio2.module.account.dto.request.NotificationUpdateRequestDto;

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
            accountToUpdate.setNotificationLikeOnMyCommentByEmail(false);
            accountToUpdate.setNotificationCommentOnMyPostByEmail(false);
            accountToUpdate.setNotificationCommentOnMyCommentByEmail(false);
            accountToUpdate.setNotificationNewPostWithMyInterestTagByEmail(false);
            accountToUpdate.setNotificationMyInterestTagAddedToExistingPostByEmail(false);
        }
        return accountToUpdate;
    }
}
