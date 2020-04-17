package portfolio2.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import portfolio2.domain.account.Account;

@NoArgsConstructor
@Data
public class ProfileUpdateRequestDto {

    private String bio;

    private String occupation;

    private String location;

    public ProfileUpdateRequestDto(Account account){
        this.bio = account.getBio();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
