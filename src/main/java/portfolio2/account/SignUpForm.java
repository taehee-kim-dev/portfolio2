package portfolio2.account;

import lombok.Data;


/*
    @Data 어노테이션은 @Getter, @Setter, @RequiredArgsConstructor,
    @ToString, @EqualsAndHashCode을
    한꺼번에 설정해주는 매우 유용한 어노테이션
* */
@Data
public class SignUpForm {

    private String nickname;

    private String email;

    private String password;
}
