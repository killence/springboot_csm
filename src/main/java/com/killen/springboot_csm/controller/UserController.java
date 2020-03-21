package com.killen.springboot_csm.controller;

import com.killen.springboot_csm.entity.User;
import com.killen.springboot_csm.param.RegisterParam;
import com.killen.springboot_csm.param.UserParam;
import com.killen.springboot_csm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserController
 * @Description: TODO
 * @Author killen
 * @Date 2020-03-19
 * @Version V1.0
 **/
@Controller
@RequestMapping("/user")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    @Cacheable(value="user_list")
    public String list(ModelMap model, @RequestParam(value = "page", defaultValue = "0") Integer page,
                       @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> users = userRepository.findAll(pageable);
        model.addAttribute("users", users);
        return "user/list";
    }

    @PostMapping("/userAdd")
    public String userAdd(@Valid UserParam userParam, BindingResult result, ModelMap model) {
        String msg = "";
        if (result.hasErrors()) {
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                msg = msg + error.getCode() + "-" + error.getDefaultMessage() + ";";
            }
            model.addAttribute("errorMsg", msg);
            return "user/userAdd";
        }
        User user = userRepository.findByUserNameOrEmail(userParam.getUserName(), userParam.getEmail());
        if (user != null) {
            model.addAttribute("errorMsg", "用户已存在！");
            return "user/userAdd";
        } else {
            User u = new User();
            BeanUtils.copyProperties(userParam, u);
            u.setRegTime(new Date());
            userRepository.save(u);
            return "redirect:/user/list";
        }
    }

    @RequestMapping("/toAdd")
    public String toAdd() {
        return "user/userAdd";
    }

    @RequestMapping("/toEdit")
    public String toEdit(Model model,String id) {
        User user=userRepository.findById(id).get();
        model.addAttribute("user", user);
        return "user/userEdit";
    }

    @RequestMapping("/edit")
    public String edit(@Valid UserParam userParam,BindingResult result,ModelMap model){
        String errorMsg = "";
        if(result.hasErrors()){
            List<ObjectError> list = result.getAllErrors();
            for (ObjectError error : list) {
                errorMsg = errorMsg+ error.getCode()+"-"+error.getDefaultMessage()+";";
            }
            model.addAttribute("errorMsg",errorMsg);
            model.addAttribute("user",userParam);
        }
        User user=userRepository.findById(userParam.getId()).get();
        userParam.setUserType(user.getUserType());
        BeanUtils.copyProperties(userParam,user);
        user.setRegTime(new Date());
        userRepository.save(user);
        return "redirect:/user/list";
    }

    @RequestMapping("/delete")
    public String delete(String id) {
        userRepository.deleteById(id);
        return "redirect:/user/list";
    }


}
