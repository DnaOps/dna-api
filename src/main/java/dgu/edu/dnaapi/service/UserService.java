package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.config.CustomBCryptPasswordEncoder;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.LoginRequestDto;
import dgu.edu.dnaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CustomBCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Long join(User user) {
        validateDuplicateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("user.getPassword() = " + user.getPassword());
        userRepository.save(user);
        return user.getId();
    }

    // validateDuplicationUser By Email
    private void validateDuplicateUser(User user) {
        userRepository.findUserByEmail(user.getEmail())
                .ifPresent((e)-> {throw new IllegalStateException("이미 존재하는 회원입니다.");});
    }

    public User login(LoginRequestDto loginRequestDto){
        User user = userRepository.findUserByEmail(loginRequestDto.getEmail())
                .orElseThrow(()-> new NoSuchElementException("해당 회원이 없습니다."));
        if(!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 잘못되었습니다.");
        }else{
            return user;
        }
    }
}
