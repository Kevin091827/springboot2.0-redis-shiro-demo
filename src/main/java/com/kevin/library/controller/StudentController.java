package com.kevin.library.controller;

import com.kevin.library.pojo.Student;
import com.kevin.library.service.StudentService;
import com.kevin.library.utils.CredentialsMatcher;
import com.kevin.library.utils.NewToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 跳转到登录页面
     */
    @RequestMapping("/student/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录成功跳转页面
     * @return
     */
    @RequestMapping("/student/toSuccessLogin")
    public String toSuccessLogin(){
        return "mainface";
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/student/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {

        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            NewToken token = new NewToken(username, password,"Stu");
            try {
                token.setRememberMe(true);
                subject.login(token);
                subject.getSession().setAttribute("msg",token.getUsername());
                return "redirect:/student/toSuccessLogin";
            } catch(Exception e){
                logger.info("用户名或密码不正确");
                logger.info("失败原因："+e.getMessage());
                return "login";
            }
        }
        return "redirect:/student/toSuccessLogin";
    }

    /**
     * 退出登录
     */
    @RequestMapping("/student/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "index";
    }

    /**
     * 跳转至注册页面
     * @return
     */
    @RequestMapping("/student/toRegister")
    public String toRegister(){
        return "stu_register";
    }

    /**
     * 注册，注册成功后跳转到登录界面
     * @param username
     * @param password
     * @param sex
     * @param profession
     * @return
     */
    @RequestMapping("/student/register")
    public String register(@RequestParam("username") String username,@RequestParam("password") String password,
                           @RequestParam("sex") String sex, @RequestParam("profession") String profession){
        CredentialsMatcher credentialsMatcher = new CredentialsMatcher();
        String md5_pwd = credentialsMatcher.md5(password,username);
        Student student = new Student();
        student.setSex(sex);
        student.setPassword(md5_pwd);
        student.setUsername(username);
        student.setRole("stu");
        student.setProfession(profession);
        studentService.register(student);
        return "login";
    }

    /**
     * student个人信息
     * @param model
     * @return
     */
    @RequestMapping("/student/personalInfomation")
    public String personalInfomation(Model model){
        Subject subject = SecurityUtils.getSubject();
        int id =(int) subject.getPrincipal();
        Student student = studentService.personalInfo(id);
        model.addAttribute("studentInfo",student);
        return "personalInfo";
    }

    /**
     * 跳转到student的信息更新
     * @param model
     * @return
     */
    @RequestMapping("/student/toUpdate")
    public String toUpdate(Model model){
        Subject subject = SecurityUtils.getSubject();
        model.addAttribute("student",studentService.personalInfo((int)subject.getPrincipal()));
        return "updateStuPwd";
    }

    /**
     * 修改密码
     * @param username
     * @param sex
     * @param profession
     * @param pwd1
     * @param pwd2
     * @param model
     * @return
     */
    @RequestMapping("/student/updateStuPwd")
    public String updateStuPwd(@RequestParam(value = "username") String username,@RequestParam("sex") String sex,
                               @RequestParam(value = "profession",required = false) String profession,
                               @RequestParam(value = "pwd1",required = false)String pwd1, @RequestParam(value = "pwd2",required = false)String pwd2,Model model){
        Student student = new Student();
        student.setProfession(profession);
        student.setUsername(username);
        student.setSex(sex);
        student.setId((int)SecurityUtils.getSubject().getPrincipal());
        if (pwd1!=""&&pwd2!="") {
            if (pwd2.equals(pwd1)) {
                String pwd = new CredentialsMatcher().md5(pwd1,username);
                student.setPassword(pwd);
                studentService.updateInfo(student);
                return "redirect:/student/personalInfomation";
            } else {
                model.addAttribute("msg","两次输入的密码不一致");
                return "updateStuPwd";
            }
        } else {
            model.addAttribute("msg", "输入为空");
            return "redirect:/student/personalInfomation";
        }
    }
}
