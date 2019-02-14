package com.kevin.library.utils;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 自定义UsernamePasswordToken
 */
public class NewToken extends UsernamePasswordToken {

    /**
     * 添加多一个登录类型的标识
     */
    private String loginType;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public NewToken(String username, String password, String loginType) {
        super(username, password);
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
