package com.kevin.library.util;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 自定义认证凭证器
 */
public class CredentialsMatcherUtils extends SimpleCredentialsMatcher {
    /**
     * 自定义认证凭证器
     * @param token
     * @param info
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        UsernamePasswordToken utoken=(UsernamePasswordToken) token;
        // 获得用户输入的用户名
        String token_username = new String(utoken.getUsername());
        // 获得用户输入的密码:
        String token_pwd = new String(utoken.getPassword());
        System.out.println("表单密码："+token_pwd);
        // 将表单密码md5加密
        String result = md5(token_pwd,token_username);
        String inPassword = new String(result);
        System.out.println("加密密码为："+inPassword);
        // 获得数据库中的密码
        String dbPassword=(String) info.getCredentials();
        System.out.println("数据库密码："+dbPassword);
        // 进行密码的比对
        return this.equals(inPassword, dbPassword);
    }

    /**
     * 密码md5加密
     * @param token_pwd
     * @param username
     * @return
     */
    public String md5(String token_pwd,String username){
        // 加密方式
        String hashAlgorithmName = "MD5";
        // 密码原值
        Object crdentials = token_pwd;
        // 盐值
        Object salt = username;
        // 加密1024次
        int hashIterations = 1024;
        Object result = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);
        return result.toString();
    }


    /**
     * 加密测试
     */
    public static void main(String[] args )
    {
        // 加密方式
        String hashAlgorithmName = "MD5";
        // 密码原值
        Object crdentials = "009";
        // 盐值
        Object salt = "009";
        // 加密1024次
        int hashIterations = 1024;
        Object result = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);
        System.out.println(result);
    }

}
