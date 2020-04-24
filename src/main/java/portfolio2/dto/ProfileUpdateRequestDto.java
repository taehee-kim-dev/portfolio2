package portfolio2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio2.domain.account.Account;

@Data
public class ProfileUpdateRequestDto {

    private String bio;

    private String occupation;

    private String location;

    private String profileImage;
}
