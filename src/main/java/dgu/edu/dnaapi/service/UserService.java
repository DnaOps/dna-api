package dgu.edu.dnaapi.service;

import dgu.edu.dnaapi.config.CustomBCryptPasswordEncoder;
import dgu.edu.dnaapi.domain.User;
import dgu.edu.dnaapi.domain.dto.auth.LoginRequestDto;
import dgu.edu.dnaapi.domain.dto.auth.UnverifiedUser;
import dgu.edu.dnaapi.domain.response.DnaStatusCode;
import dgu.edu.dnaapi.exception.DNACustomException;
import dgu.edu.dnaapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
                .ifPresent((e)-> {throw new DNACustomException("이미 존재하는 회원입니다.", DnaStatusCode.DUPLICATE_EMAIL);});
    }

    public User login(LoginRequestDto loginRequestDto){
        User user = userRepository.findUserByEmail(loginRequestDto.getEmail())
                .orElseThrow(()-> new DNACustomException("해당 회원이 없습니다.", DnaStatusCode.INVALID_USER));
        if(!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
            throw new DNACustomException("비밀번호가 잘못되었습니다.", DnaStatusCode.INVALID_INPUT);
        }else{
            return user;
        }
    }

    public User getUserByEmail(String email){
        return userRepository.findUserByEmail(email)
                .orElseThrow(()-> new DNACustomException("해당 회원이 없습니다.", DnaStatusCode.INVALID_USER));
    }

    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new DNACustomException("해당 회원이 없습니다.", DnaStatusCode.INVALID_USER));
    }

    public List<UnverifiedUser> getUnverifiedUserList() {
        return userRepository.findAllUnverifiedUser();
    }

    @Transactional
    public long authorizeUser(User user) {
        user.authorizeUser();
        return user.getId();
    }
}
