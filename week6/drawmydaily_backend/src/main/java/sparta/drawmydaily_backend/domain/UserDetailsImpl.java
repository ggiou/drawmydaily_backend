package sparta.drawmydaily_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sparta.drawmydaily_backend.shared.Authority;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Users users;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Authority.ROLE_USER.toString());
        //사용자에게 부여된 권한 반환
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        return authorities;
    }
    //회원가입한 유저를 ROLE_USER의 권한을 부여함 = auth 부분 가능

    @Override
    public String getPassword(){
        return users.getPassword();
    } //사용자를 인증하는데 사용된 암호 반환

    @Override
    public String getUsername(){
        return users.getName();
    } //사용자를 인증하는데 사용된 사용자 이름 반환

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //사용자의 계정이 만료되었는지 여부 나타냄, 만료된 계정은 인증 불가

    @Override
    public boolean isAccountNonLocked(){ return true; }
    //사용자가 잠겨 있는지 잠금 해제인지 나타냄, 잠긴 사용자는 인증 불가

    @Override
    public boolean isCredentialsNonExpired(){ return true; }
    //사용자의 자격 증명(암호)이 만료되었는지 여부를 나타냄, 만료된 자격증명은 인증 방해

    @Override
    public boolean isEnabled(){ return true; }
    //사용자가 활성화되어있는지 여부를 나타냄, 비활성된 사용자 인증 불가
}
//DB에 접근해 사용자(회원가입된 유저)의 정보 가져옴
