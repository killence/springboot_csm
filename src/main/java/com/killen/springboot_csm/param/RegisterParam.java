package com.killen.springboot_csm.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @ClassName RegisterParam
 * @Description: TODO
 * @Author killen
 * @Date 2020-03-21
 * @Version V1.0
 **/
@Data
public class RegisterParam {

    @NotEmpty(message="姓名不能为空")
    private String userName;
    @NotEmpty(message="密码不能为空")
    @Length(min=6,message="密码长度不能小于6位")
    private String password;
    @Email
    private String email;

}
