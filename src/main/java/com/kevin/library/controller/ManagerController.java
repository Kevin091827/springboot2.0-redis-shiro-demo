package com.kevin.library.controller;

import com.github.pagehelper.PageInfo;
import com.kevin.library.pojo.Manager;
import com.kevin.library.pojo.Student;
import com.kevin.library.service.ManagerService;
import com.kevin.library.util.CredentialsMatcherUtils;
import com.kevin.library.util.NewTokenUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *  跳转到登录页面
     */
    @RequestMapping("/manager/toLogin")
    public String toLogin() {
        logger.info("跳转至管理员登录页面");
        return "login2";
    }

    /**
     * 跳转到登录成功页面
     * @return
     */
    @RequestMapping("/manager/toSuccessLogin")
    public String toSuccessLogin() {
        logger.info("跳转至管理员登录成功页面");
        return "mainface";
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/manager/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {

        Subject subject = SecurityUtils.getSubject();
            if (!subject.isAuthenticated()) {
                NewTokenUtils token = new NewTokenUtils(username, password, "Man");
                try {
                    token.setRememberMe(true);
                    subject.login(token);
                    subject.getSession().setAttribute("msg",token.getUsername());
                    logger.info("管理员登录成功");
                    return "redirect:/manager/toSuccessLogin";
                } catch (AuthenticationException e) {
                    logger.info("登录失败原因：" + e.getMessage());
                    return "login2";
                }
            }
            return "redirect:/manager/toSuccessLogin";
    }

    /**
     * 退出登录
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        logger.info("管理员退出登录");
        return "index";
    }

    /**
     * 跳转到注册页面
     */
    @RequestMapping("/manager/toRegister")
    public String toRegister() {
        logger.info("跳转至管理员注册页面");
        return "man_register";
    }

    /**
     * 注册，注册成功后跳转到登录界面
     * @param managerName
     * @param password
     * @return
     */
    @RequestMapping("/manager/register")
    public String register(@RequestParam("managerName") String managerName, @RequestParam("password") String password) {
        CredentialsMatcherUtils credentialsMatcherUtils = new CredentialsMatcherUtils();
        String md5_pwd = credentialsMatcherUtils.md5(password, managerName);
        Manager manager = new Manager();
        manager.setManagerName(managerName);
        manager.setPassword(md5_pwd);
        manager.setRole("man");
        try {
            managerService.register(manager);
            logger.info("管理员"+manager.getManagerName()+"注册成功");
        }catch (Exception ee) {
            logger.info("注册失败原因："+ee);
            return "/manager/toRegister";
        }
        return "login2";
    }

    /**
     * 分页查询全体学生信息
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/allStudentInfo")
    public String allStudentInfo(@RequestParam(value = "selectPage", required = false) Integer selectPage, Model model) {
        //默认从第一页开始找
        if (selectPage == null) {
            selectPage = 1;
        }
        int pageSize = 8;
        List<Student> studentList = managerService.selectAllStudent(selectPage,pageSize);
        PageInfo pageInfo = new PageInfo(studentList);
        model.addAttribute("studentList", studentList);
        model.addAttribute("pageInfo",pageInfo);
        logger.info(SecurityUtils.getSubject().getPrincipal()+"分页查询所有学生信息");
        return "allStudentInfo";
    }

    /**
     * 根据学生id查找学生信息
     * @param id
     * @param model
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/selectStuById")
    public String selectStuById(@RequestParam("id") int id, Model model) {

        logger.info("根据id查询学生信息");
        Student student = managerService.selectStuById(id);
        if(student != null) {
            logger.info("查询到该学生"+"id为"+student.getId());
            model.addAttribute("selectStu", student);
        }else {
            logger.info("没有查询到该学生");
            model.addAttribute("selectStu",null);
        }
        return "selectStuById";
    }

    /**
     * 更新学生信息
     * @param student
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/updateStuInfo")
    public String updateStuInfo(Student student,Model model) {
        try {
            managerService.updateStuInfo(student);
            logger.info("学生信息更新成功");
        }catch (Exception ee){
            logger.info("学生信息更新失败");
        }
        return "redirect:/manager/allStudentInfo";
    }

    /**
     * 根据id删除学生信息
     * @param id
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/deleteStuInfo")
    public String deleteStuInfo(@RequestParam("id") int id) {
        try {
            managerService.deleteStuInfo(id);
            logger.info("删除成功");
        }catch (Exception ee){
            logger.info("删除失败");
        }
        return "redirect:/manager/allStudentInfo";
    }

    /**
     * 跳转到添加学生页面
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/toInsertStudent")
    public String toInsertStudent() {
        logger.info("跳转到添加页面");
        return "insertStudent";
    }

    /**
     * 添加学生信息
     * @param username
     * @param password
     * @param sex
     * @param profession
     * @param id
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/insertStudent")
    public String insertStudent(@RequestParam("username") String username, @RequestParam("password") String password,
                                @RequestParam("sex") String sex, @RequestParam("profession") String profession, @RequestParam(value = "id", required = false) Integer id) {
        CredentialsMatcherUtils credentialsMatcherUtils = new CredentialsMatcherUtils();
        String md5_pwd = credentialsMatcherUtils.md5(password, username);
        Student student = new Student();
        student.setUsername(username);
        student.setProfession(profession);
        student.setPassword(md5_pwd);
        student.setId(id);
        student.setRole("stu");
        student.setSex(sex);
        try {
            managerService.insertStudent(student);
            logger.info("添加成功");
        }catch (Exception ee){
            logger.info("添加失败");
            return "/manager/toInsertStudent";
        }
        return "redirect:/manager/allStudentInfo";
    }

    /**
     * 跳转到搜索页面
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/toSelectStuInfo")
    public String toSelectStuInfo() {
        logger.info("跳转到搜索页面");
        return "toSelectStuInfo";
    }

    /**
     *多条件搜索
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/selectStudentInfo")
    public String selectStudentInfo(@RequestParam(value = "username", required = false) String username, @RequestParam(value = "id", required = false) Integer id,
                                    @RequestParam(value = "profession", required = false) String profession, Model model) {
        Student student = new Student();
        if (id != null) {
            student.setId(id);
        }
        if (username != null) {
            student.setUsername(username);
        }
        if (profession != null) {
            student.setProfession(profession);
        }
        List<Student> studentList = managerService.selectStuInfo(student);
        if(studentList != null){
            logger.info("查询成功");
            model.addAttribute("studentList", studentList);
        }else {
            logger.info("查询不到结果集");
            model.addAttribute("studentList",studentList);
        }
        return "selectResult";
    }

    /**
     * 查看manager个人信息
     * @param model
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/toSelectManInfo")
    public String manPersonalInfo(Model model) {
        logger.info("查看管理员个人信息");
        Subject subject = SecurityUtils.getSubject();
        int id = (int) subject.getPrincipal();
        Manager manager = managerService.selectManById(id);
        model.addAttribute("manInfo", manager);
        return "personalInfo";
    }

    /**
     * 跳转到修改密码页面
     * @param model
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/toUpdate")
    public String toUpdateManInfo(Model model) {
        logger.info("跳转到管理员信息更新页面");
        model.addAttribute("manager", managerService.selectManById((int) SecurityUtils.getSubject().getPrincipal()));
        return "updateManPwd";
    }

    /**
     * 修改密码
     * @param pwd1
     * @param pwd2
     * @param managerName
     * @param model
     * @return
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/updateManPwd")
    public String updatePwd(@RequestParam(value = "password1") String pwd1, @RequestParam(value = "password2") String pwd2, @RequestParam("managerName") String managerName, Model model) {
        Subject subject = SecurityUtils.getSubject();
        CredentialsMatcherUtils credentialsMatcherUtils = new CredentialsMatcherUtils();
        int id = (int) subject.getPrincipal();
        Manager manager = new Manager();
        manager.setManagerName(managerName);
        manager.setId(id);
        if (pwd1 != "" && pwd2 != "") {
            if (pwd2.equals(pwd1)) {
                String pwd = credentialsMatcherUtils.md5("pwd1", managerName);
                manager.setPassword(pwd);
                managerService.updateManPerInfo(manager);
                return "redirect:/manager/toSelectManInfo";
            } else {
                model.addAttribute("msg", "两次密码输入不一致");
                return "updateManPwd";
            }
        } else {
            model.addAttribute("msg", "输入为空");
            return "redirect:/manager/toSelectManInfo";
        }
    }

}