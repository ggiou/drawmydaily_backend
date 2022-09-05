package sparta.drawmydaily_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken extends Timestamped {

    @Id
    @Column(nullable = false)
    private Long id;

    @JoinColumn(name = "users_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Users users;

    @Column(nullable = false)
    private String keyValue; //refresh token 값
}
