package com.kevin.library.realm;

import com.kevin.library.pojo.Student;
import com.kevin.library.service.StudentService;
import com.kevin.library.util.NewSimpleByteSourceUtils;
import com.kevin.library.util.NewTokenUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

public class StuLoginRealm extends AuthorizingRealm {

    @Resource
    private StudentService studentService;

    /**
     * 授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        System.out.print("执行授权逻辑");
        if (!principalCollection.getRealmNames().contains(getName())) return null;
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        int id = (int)subject.getPrincipal();
        Student student = studentService.personalInfo(id);
        if(student!=null){
            simpleAuthorizationInfo.addRole(student.getRole());
            return  simpleAuthorizationInfo;
        }
        return null;
    }

    /**
     * 认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        NewTokenUtils token = (NewTokenUtils) authenticationToken;
        String username = token.getUsername();
        Student student = studentService.login(username);
        if(student!=null){
            Object principal = student.getId();
            Object credentials = student.getPassword();
            ByteSource salt = new NewSimpleByteSourceUtils(username);
            String realmName = getName();
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal,credentials,salt,realmName);
            return info;
        }
        return null;
    }

}
