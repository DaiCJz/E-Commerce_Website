package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void addNewUser(String firstname, String lastname, String password, String email, String tel) {
        User newUser = new User();
        newUser.setUsername(firstname + "," + lastname);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setTel(tel);
        newUser.setRole("customer");

        userRepository.save(newUser);
    }

}
