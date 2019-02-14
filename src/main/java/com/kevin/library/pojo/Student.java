package com.kevin.library.pojo;

import java.io.Serializable;
import java.util.Date;

public class Student implements Serializable {

    /**
    * @Student 学生类
    * @username 学生姓名
    * @sex 学生性别
    * @id 学生编号
    * @profession 学生专业
    * @password 密码
    * @role 角色信息
    * @lastLoginTime 上次登录时间
    */
    private String username;
    private String sex;
    private int id;
    private String profession;
    private String password;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
