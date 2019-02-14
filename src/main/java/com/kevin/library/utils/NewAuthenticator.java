package com.kevin.library.utils;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义 ModularRealmAuthenticator
 */
public class NewAuthenticator extends ModularRealmAuthenticator {

    /**
     * 自定义 ModularRealmAuthenticator
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 判断getRealms()是否返回为空
        assertRealmsConfigured();
        // 强制转换回自定义的Token
        NewToken token = (NewToken) authenticationToken;
        // 所有Realm
        Collection<Realm> realms = getRealms();
        //登录类型
        String loginType = token.getLoginType();
        //判断realm为哪种登录类型
        List<Realm> typeRealms = new ArrayList<>();
        for (Realm realm : realms) {
            if (realm.getName().contains(loginType)) {
                typeRealms.add(realm);
            }
        }
        // 判断是单Realm还是多Realm
        if (realms.size() == 1)
            return doSingleRealmAuthentication(typeRealms.iterator().next(), token);
        else
            return doMultiRealmAuthentication(typeRealms, token);
    }

}
