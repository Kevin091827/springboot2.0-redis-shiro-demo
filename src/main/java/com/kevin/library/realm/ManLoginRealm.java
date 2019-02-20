package com.kevin.library.realm;

import com.kevin.library.pojo.Manager;
import com.kevin.library.service.ManagerService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class ManLoginRealm extends AuthorizingRealm {

    @Resource
    private ManagerService managerService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        logger.info("执行授权逻辑");
        if (!principalCollection.getRealmNames().contains(getName())) return null;
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Subject subject = SecurityUtils.getSubject();
        int id = (int)subject.getPrincipal();
        logger.info("执行授权逻辑，走数据库");
        Manager manager = managerService.selectManById(id);
        if(manager!=null){
            simpleAuthorizationInfo.addRole(manager.getRole());
            logger.info("执行授权逻辑，走数据库");
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
        Manager manager = managerService.login(username);
        if (manager != null) {
            Object principal = manager.getId();
            Object credentials = manager.getPassword();
            ByteSource salt = new NewSimpleByteSourceUtils(username);
            String realmName = getName();
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, salt, realmName);
            return info;
        }
        return null;
    }
}
