package portfolio2.dto.response.account;

import lombok.Data;
import portfolio2.domain.account.Account;

@Data
public class EmailVerificationResponseDto {
    private boolean isEmailVerifiedAccountLoggedIn;
    private Account updatedSessionAccount;
    private Account emailVerifiedAccountInDb;
}
