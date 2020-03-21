package com.killen.springboot_csm.repository;

import com.killen.springboot_csm.entity.User;
import com.sun.mail.imap.protocol.UID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save(){
        User user = new User();
        user.setEmail("1654401766@qq.com");
        user.setUserName("周利冬89");
        user.setPassword("admin");
        user.setRegTime(new Date());
        User u = userRepository.save(user);
        System.out.println(u);
    }

    @Test
    void findById(){
        User user = userRepository.findById("111111").get();
        System.out.println(user);
    }

    @Test
    void findAll(){
        List<User> all = userRepository.findAll();
        for (User user : all) {
            System.out.println(user);
        }
    }

    @Test
    void update(){
        User user = userRepository.findById("111111").get();
        user.setPassword("111111");
        User save = userRepository.save(user);
        System.out.println(save);
    }

    @Test
    void pageSelect(){
        Pageable pageable = PageRequest.of(1, 5,Sort.by(Sort.Direction.DESC, "id"));
        Page<User> all = userRepository.findAll(pageable);
        for (User user : all) {
            System.out.println(user);
        }
    }
}