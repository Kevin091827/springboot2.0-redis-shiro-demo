package com.kevin.library.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kevin.library.dao.ManagerDao;
import com.kevin.library.pojo.Manager;
import com.kevin.library.pojo.Student;
import com.kevin.library.service.ManagerService;
import com.kevin.library.utils.CredentialsMatcher;
import com.kevin.library.utils.NewToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        return "login2";
    }

    /**
     * 跳转到登录成功页面
     * @return
     */
    @RequestMapping("/manager/toSuccessLogin")
    public String toSuccessLogin() {
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
                NewToken token = new NewToken(username, password, "Man");
                try {
                    token.setRememberMe(true);
                    subject.login(token);
                    subject.getSession().setAttribute("msg",token.getUsername());
                    return "redirect:/manager/toSuccessLogin";
                } catch (Exception e) {
                    logger.info("失败原因：" + e.getMessage());
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
        return "index";
    }

    /**
     * 跳转到注册页面
     */
    @RequestMapping("/manager/toRegister")
    public String toRegister() {
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
        CredentialsMatcher credentialsMatcher = new CredentialsMatcher();
        String md5_pwd = credentialsMatcher.md5(password, managerName);
        Manager manager = new Manager();
        manager.setManagerName(managerName);
        manager.setPassword(md5_pwd);
        manager.setRole("man");
        managerService.register(manager);
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

        Student student = managerService.selectStuById(id);
        if(student != null) {
            model.addAttribute("selectStu", student);
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
    public String updateStuInfo(Student student) {
        managerService.updateStuInfo(student);
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
        managerService.deleteStuInfo(id);
        return "redirect:/manager/allStudentInfo";
    }

    /**
     * 跳转到添加学生页面
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/toInsertStudent")
    public String toInsertStudent() {
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
        CredentialsMatcher credentialsMatcher = new CredentialsMatcher();
        String md5_pwd = credentialsMatcher.md5(password, username);
        Student student = new Student();
        student.setUsername(username);
        student.setProfession(profession);
        student.setPassword(md5_pwd);
        student.setId(id);
        student.setRole("stu");
        student.setSex(sex);
        managerService.insertStudent(student);
        return "redirect:/manager/allStudentInfo";
    }

    /**
     * 跳转到搜索页面
     */
    @RequiresRoles("man")
    @RequestMapping("/manager/toSelectStuInfo")
    public String toSelectStuInfo() {
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
        model.addAttribute("studentList", studentList);
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
        CredentialsMatcher credentialsMatcher = new CredentialsMatcher();
        int id = (int) subject.getPrincipal();
        Manager manager = new Manager();
        manager.setManagerName(managerName);
        manager.setId(id);
        if (pwd1 != "" && pwd2 != "") {
            if (pwd2.equals(pwd1)) {
                String pwd = credentialsMatcher.md5("pwd1", managerName);
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