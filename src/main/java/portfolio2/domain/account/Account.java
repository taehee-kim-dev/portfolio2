package portfolio2.domain.account;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


/*
    @EqualsAndHashCode(of = "id") :
    연관관계가 복잡해질 때,
    서로 다른 연관관계를 순환참조를 하느라 무한루프가 발생하고,
    스택오버플로우가 발생할 수 있다.
* */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String userId;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenFirstGeneratedAt;

    private int sendCheckEmailCount;

    private LocalDateTime joinedAt;

    /*
        @Lob :
        기본적으로 String은 varchr(255)로 매핑됨.
        그것보다 더 커질 것 같을 때 @Lob으로 매핑을 해주면
        texttype으로 매핑을 해줌.

        @Basic(fetch = FetchType.EAGER) :
        부모 엔티티를 조회하면 자식 엔티티도 다같이 조회된다.

        @Basic(fetch = FetchType.LAZY) :
        부모 엔티티를 조회하면 자식 엔티티는 조회하지 않는다.
        대신, 조회한 부모 엔티티의 child 변수에 프록시 객체를 넣어둔다.
        이 프록시 객체는 실제 사용 될 때까지는 데이터베이스를 조회하지 않고,
        데이터가 필요한 순간이 되어서야 데이터베이스를 조회해서 프록시 객체를 초기화한다.

        기본값은 LAZY이나, 기본값 설정이 바뀔 수 있기 때문에 명시하는것이 좋음.
        유저를 로딩할 때 종종 같이 쓰일 것 같아서.
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean notificationByEmail;

    private boolean notificationByWeb;

    public void generateEmailCheckToken() {
        // 앞에서 재전송 허가받은 상태이므로,
        // 무조건 토큰값 생성.
        this.emailCheckToken = UUID.randomUUID().toString();
        if(sendCheckEmailCount == 0 || sendCheckEmailCount == 5){
            // 이미 앞에서 이메일 재전송 허가받은 상태.
            // 현재 가입 후 첫번째 또는 6번째 이메일 전송이라면,
            // 새로운 첫 번째 이메일 전송이 되는 것이므로,
            // 이메일 토큰 생성 시간 새로 설정
            emailCheckTokenFirstGeneratedAt = LocalDateTime.now();
            // 이메일 전송 카운트 1로 초기화
            sendCheckEmailCount = 1;
        }else{
            // 가입 후 첫 번째나, 6번째가 아니라면
            // 날짜 재설정 없이 카운트만 증가
            this.sendCheckEmailCount++;
        }
    }

    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        // 인증 이메일을 5번 보냈는가?
        if(this.sendCheckEmailCount == 5){
            // 보냈다면, 1번째 보냈을 때 보다 현재 12시간이 지났는가?
            return this.emailCheckTokenFirstGeneratedAt.isBefore(LocalDateTime.now().minusHours(12));
        }

        return true;
    }
}
