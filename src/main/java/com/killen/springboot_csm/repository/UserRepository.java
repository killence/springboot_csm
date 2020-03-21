package com.killen.springboot_csm.repository;

import com.killen.springboot_csm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.awt.*;
/**
 * @ClassName UserRepository
 * @Description: TODO
 * @Author killen
 * @Date 2020-03-19
 * @Version V1.0
 **/

public interface UserRepository  extends MongoRepository<User,String> {

    Page<User> findAll(Pageable pageable);

    User findByUserName(String userName);

    User findByEmail(String email);

    User findByUserNameOrEmail(String userName,String email);
}
