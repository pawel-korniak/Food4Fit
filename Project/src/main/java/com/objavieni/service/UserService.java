package com.objavieni.service;


import com.objavieni.dto.UserDto;
import com.objavieni.repository.UserRepository;
import com.objavieni.user.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
@AllArgsConstructor
@Service
public class UserService {

    public final UserRepository userRepository;

    @Transactional
    public UserDto saveUser(UserDto userDto){
        User user = userRepository.save(new User(userDto));
        return new UserDto(user);
    }




}