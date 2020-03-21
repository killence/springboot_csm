package com.killen.springboot_csm.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName User
 * @Description: TODO
 * @Author killen
 * @Date 2020-03-19
 * @Version V1.0
 **/
@Data
public class User implements Serializable {
    private String id;
    private String userName;
    private String userType;
    private String password;
    private String email;
    private int age;
    private Date regTime;
    private String state;

}
