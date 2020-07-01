//package portfolio2.service.account;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import portfolio2.domain.account.Account;
//import portfolio2.domain.account.EmailVerificationProcess;
//import portfolio2.domain.account.LogInOrSessionUpdateProcess;
//
//@RequiredArgsConstructor
//@Service
//public class EmailVerificationService {
//
//    private EmailVerificationProcess emailVerificationProcess;
//    private LogInOrSessionUpdateProcess logInOrSessionUpdateProcess;
//
//    public boolean checkEmailVerificationLink(String email, String token) {
//        // 맞는 링크인지 확인
//        // 아니면 바로 false return
//        if(!emailVerificationProcess.isValidLink(email, token))
//            return false;
//        // 맞다면, 이메일 인증 처리하고 해당 계정 객체 반환받음
//        Account emailVerifiedAccountInDb = emailVerificationProcess.verifyEmail();
//        // 로그인 유무와 관계없이 무조건 현재 인증 링크에 해당하는 계정으로 로그인
//        logInOrSessionUpdateProcess.loginOrSessionUpdate(emailVerifiedAccountInDb);
//        // EmailVerificationProcess 필드값 초기화
//        emailVerificationProcess.clearField();
//        // return true
//        return true;
//    }
//}
