package com.killen.springboot_csm.controller;

import com.killen.springboot_csm.config.WebConfiguration;
import com.killen.springboot_csm.entity.User;
import com.killen.springboot_csm.param.LoginParam;
import com.killen.springboot_csm.param.RegisterParam;
import com.killen.springboot_csm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @ClassName LoginController
 * @Description: TODO
 * @Author killen
 * @Date 2020-03-19
 * @Version V1.0
 **/
@Controller
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @RequestMapping("/")
    public String index(HttpServletRequest request) {
        String id = (String) request.getSession().getAttribute(WebConfiguration.LOGIN_KEY);
        if (StringUtils.isEmpty(id)) {
            return "login";
        } else {
            return "redirect:/list";
        }
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/login")
    public String login(@Valid LoginParam loginParam, BindingResult result, ModelMap model, HttpServletRequest request){
        String errorMsg = "";
        if(result.hasErrors()){
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                errorMsg = errorMsg+ error.getCode()+"-"+error.getDefaultMessage()+";";
            }
            model.addAttribute("errorMsg",errorMsg);
            return "login";
        }
        //填的是用户名
        User user = userRepository.findByUserName(loginParam.getLoginName());
        //填的是邮箱
        if(user==null){
            user = userRepository.findByEmail(loginParam.getLoginName());
        }
        if(user == null){
            model.addAttribute("errorMsg","用户名不存在！");
            return "login";
        }else if(!user.getPassword().equals(loginParam.getPassword())){
            model.addAttribute("errorMsg","密码错误！");
            return "login";
        }
        request.getSession().setAttribute(WebConfiguration.LOGIN_KEY,user.getId());
        request.getSession().setAttribute(WebConfiguration.LOGIN_USER,user);
        return "redirect:/user/list";
    }

    /**
     * 退出登入清楚Session内容
     * @param request
     * @return
     */
    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request){
        request.getSession().removeAttribute(WebConfiguration.LOGIN_KEY);
        request.getSession().removeAttribute(WebConfiguration.LOGIN_USER);
        return "login";
    }

    @RequestMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    public void sendRegisterMail(User user){
        Context context = new Context();
        context.setVariable("id",user.getId());
        String emailContext = templateEngine.process("emailTemplate", context);
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("注册验证邮件");
            helper.setText(emailContext, true);
            mailSender.send(message);
        }catch (Exception e){
            logger.error("发送注册邮件时异常！", e);
        }

    }

    @RequestMapping("/verified/{id}")
    public String verified(@PathVariable("id") String id, ModelMap model) {
        User user=userRepository.findById(id).get();
        if (user!=null && "unverified".equals(user.getState())){
            user.setState("verified");
            userRepository.save(user);
            model.put("userName",user.getUserName());
        }
        return "verified";
    }

    @RequestMapping("/register")
    public String register(@Valid RegisterParam registerParam, BindingResult result, ModelMap model) {
        logger.info("register param"+ registerParam.toString());
        String errorMsg = "";
        if (result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                errorMsg = errorMsg + error.getCode() + "-" + error.getDefaultMessage() + ";";
            }
            model.addAttribute("errorMsg", errorMsg);
            return "register";
        }
        User u = userRepository.findByUserNameOrEmail(registerParam.getUserName(), registerParam.getEmail());
        if (u != null) {
            model.addAttribute("errorMsg", "用户已存在!");
            return "register";
        }
        User user = new User();
        BeanUtils.copyProperties(registerParam, user);
        user.setRegTime(new Date());
        user.setUserType("manage");
        user.setState("unverified");
        userRepository.save(user);
        sendRegisterMail(user);
        logger.info("register user "+ user.toString());
        return "login";
    }

}
