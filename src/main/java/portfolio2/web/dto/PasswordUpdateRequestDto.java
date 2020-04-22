package portfolio2.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio2.domain.account.Account;

@NoArgsConstructor
@Data
public class PasswordUpdateRequestDto {

    private String newPassword;

    private String newPasswordConfirm;
}
