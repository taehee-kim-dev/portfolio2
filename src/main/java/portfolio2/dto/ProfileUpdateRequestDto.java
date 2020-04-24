package portfolio2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import portfolio2.domain.account.Account;

@NoArgsConstructor
@Data
public class ProfileUpdateRequestDto {

    private String bio;

    private String occupation;

    private String location;

    private String profileImage;

    public ProfileUpdateRequestDto(Account account){
        this.bio = account.getBio();
        this.occupation = account.getOccupation();
        this.location = account.getLocation();
        this.profileImage = account.getProfileImage();
    }
}
