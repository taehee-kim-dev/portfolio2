package portfolio2.module.account.dto.response;

import lombok.Data;
import portfolio2.module.account.Account;

@Data
public class EmailVerificationResponseDto {
    private boolean emailVerifiedAccountLoggedIn;
    private Account updatedSessionAccount;
    private Account emailVerifiedAccountInDb;
}
