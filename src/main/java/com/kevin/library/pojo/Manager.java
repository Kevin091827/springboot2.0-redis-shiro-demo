package com.kevin.library.pojo;

import java.io.Serializable;

public class Manager implements Serializable {

    /**
     * @Manager 管理员类
     * @managerName 管理员姓名
     * @id 管理员编号
     * @role 角色信息
     * @password 密码
     */
    private String managerName;
    private int id;
    private String role;
    private String password;

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
