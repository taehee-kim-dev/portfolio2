package portfolio2.dto;

import lombok.*;


/*
    @Data 어노테이션은 @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode을
    한꺼번에 설정해주는 매우 유용한 어노테이션
* */
@Data
@NoArgsConstructor
public class SignUpRequestDto {

    private String userId;

    private String nickname;

    private String email;

    private String password;

    @Builder
    public SignUpRequestDto(String userId, String nickname, String email, String password) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}