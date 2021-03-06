package portfolio2.module.account.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.Account;
import portfolio2.module.account.dto.request.*;
import portfolio2.module.account.service.process.*;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountSettingService {

    private final ProfileUpdateProcess profileUpdateProcess;
    private final NotificationUpdateProcess notificationUpdateProcess;
    private final TagUpdateProcess tagUpdateProcess;
    private final PasswordUpdateProcess passwordUpdateProcess;
    private final NicknameUpdateProcess nicknameUpdateProcess;
    private final EmailUpdateProcess emailUpdateProcess;
    private final SendingEmailVerificationEmailProcess sendingEmailVerificationEmailProcess;


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

    public List<String> getInterestTagOfAccount(Account sessionAccount) {
        return tagUpdateProcess.getInterestTagOfAccount(sessionAccount);
    }

    public void addInterestTagToAccount(Account sessionAccount, TagUpdateRequestDto tagUpdateRequestDto) {
        tagUpdateProcess.addInterestTagToAccountIfNotHas(sessionAccount, tagUpdateRequestDto);
    }

    public boolean removeTagFromAccount(Account sessionAccount, TagUpdateRequestDto tagUpdateRequestDto) {
        return tagUpdateProcess.removeInterestTagFromAccount(sessionAccount, tagUpdateRequestDto);
    }

    public void updatePasswordAndSession(Account sessionAccount, PasswordUpdateRequestDto passwordUpdateRequestDto) {
        Account updatedAccount = passwordUpdateProcess.updatePassword(sessionAccount, passwordUpdateRequestDto);
        // 인증된 이메일이 있다면, 비밀번호 변경 알림 이메일 발송
        if (updatedAccount.isEmailVerified()){
            updatedAccount
                    = passwordUpdateProcess.sendPasswordUpdateNotificationEmail(updatedAccount);
        }
        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }

    public void updateAccountNicknameAndSession(Account sessionAccount, AccountNicknameUpdateRequestDto accountNicknameUpdateRequestDto) {
        Account updatedAccount = nicknameUpdateProcess.updateNickname(sessionAccount, accountNicknameUpdateRequestDto);
        // 인증된 이메일이 있다면, 닉네임 변경 알림 이메일 발송
        if (updatedAccount.isEmailVerified()){
            updatedAccount
                    = nicknameUpdateProcess.sendNicknameUpdateNotificationEmail(updatedAccount);
        }
        updatedAccount.setNicknameBeforeUpdate(null);
        logInOrSessionUpdateProcess.loginOrSessionUpdate(updatedAccount);
    }

    public boolean canSendEmailVerificationEmail(Account sessionAccount) {
        return sendingEmailVerificationEmailProcess.canSendEmailVerificationEmail(sessionAccount);
    }

    public void updateAccountEmailAndSession(Account sessionAccount, AccountEmailUpdateRequestDto accountEmailUpdateRequestDto) {
        Account emailSentAccount = emailUpdateProcess.updateEmail(sessionAccount, accountEmailUpdateRequestDto);
        logInOrSessionUpdateProcess.loginOrSessionUpdate(emailSentAccount);
    }
}
