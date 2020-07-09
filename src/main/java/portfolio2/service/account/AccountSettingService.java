package portfolio2.service.account;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.account.Account;
import portfolio2.domain.account.LogInOrSessionUpdateProcess;
import portfolio2.domain.account.setting.NotificationUpdateProcess;
import portfolio2.domain.account.setting.ProfileUpdateProcess;
import portfolio2.dto.request.account.setting.update.NotificationUpdateRequestDto;
import portfolio2.dto.request.account.setting.update.ProfileUpdateRequestDto;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountSettingService {

    private final ProfileUpdateProcess profileUpdateProcess;
    private final NotificationUpdateProcess notificationUpdateProcess;

    private final LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;

    public void updateProfileAndSession(Account sessionAccount, ProfileUpdateRequestDto profileUpdateRequestDto) {
        // 프로필 업데이트
        Account updatedAccount
                = profileUpdateProcess.updateProfile(sessionAccount, profileUpdateRequestDto);
        // 세션 업데이트
        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }

    public void updateNotificationAndSession(Account sessionAccount, NotificationUpdateRequestDto notificationUpdateRequestDto) {
        Account updatedAccount
                = notificationUpdateProcess.updateNotification(sessionAccount, notificationUpdateRequestDto);

        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }
}
