package sparta.drawmydaily_backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //user 고유의 id

    @Column(nullable = false)
    private String name;//이름 -> 통상적인 회원가입시 id 값

    @Column(nullable = false)
    @JsonIgnore
    private String password;//비밀번호
}
