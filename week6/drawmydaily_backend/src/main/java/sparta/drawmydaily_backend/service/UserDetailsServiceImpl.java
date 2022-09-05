package sparta.drawmydaily_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sparta.drawmydaily_backend.domain.Users;
import sparta.drawmydaily_backend.domain.UserDetailsImpl;
import sparta.drawmydaily_backend.repository.UsersRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = usersRepository.findByName(username);
        return user
                .map(UserDetailsImpl::new)
                .orElseThrow(()->new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }//사용자 이름(회원가입시 가입한 id) 넘겨주기, 해당 이름 없으면 가입된 유저가 없으니 사용자 찾을 수 없다 에러
}
