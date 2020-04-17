package portfolio2.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import portfolio2.domain.account.Account;

import javax.validation.Valid;

@NoArgsConstructor
@Data
public class ProfileDto {

    @Length(max = 35)
    private String bio;

    @Length(max = 20)
    private String occupation;

    @Length(max = 20)
    private String location;

    public ProfileDto(Account account){
        this.bio = account.getBio();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
    }
}
