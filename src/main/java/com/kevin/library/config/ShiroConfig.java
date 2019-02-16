package com.kevin.library.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.kevin.library.realm.StuLoginRealm;
import com.kevin.library.realm.ManLoginRealm;
import com.kevin.library.utils.CredentialsMatcher;
import com.kevin.library.utils.NewAuthenticator;
import com.kevin.library.utils.ShiroRedisCacheManager;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.*;

@Configuration
public class ShiroConfig {

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 配置shiroDialect,用于thymeleaf和shiro标签使用
     */
    @Bean
    public ShiroDialect getShiroDialect(){
        return new ShiroDialect();
    }

    /**
     * 自定义密码加密器(认证凭证器)
     */
    @Bean(name = "CredentialsMatcher")
    public CredentialsMatcher credentialsMatcher() {
        return new CredentialsMatcher();
    }

    /**
     * 自定义ModularRealmAuthenticator
     * @return
     */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator(){
        NewAuthenticator newAuthenticator = new NewAuthenticator();
        //认证策略
        newAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return newAuthenticator;
    }

    /**
     * 创建realm实体bean,交给spring容器
     */
    @Bean(name="loginRealm")
    public StuLoginRealm getLoginRealm(@Qualifier("CredentialsMatcher") CredentialsMatcher credentialsMatcher){
        StuLoginRealm stuLoginRealm = new StuLoginRealm();
        //配置认证凭证器
        stuLoginRealm.setCredentialsMatcher(credentialsMatcher);
        //开启认证和授权的缓存
        stuLoginRealm.setCachingEnabled(true);
        //stuLoginRealm.setAuthenticationCachingEnabled(true);
        stuLoginRealm.setAuthorizationCachingEnabled(true);
        System.out.print("realm已经加载");
        return stuLoginRealm;
    }

    @Bean(name="manLoginRealm")
    public ManLoginRealm getManLoginRealm(@Qualifier("CredentialsMatcher") CredentialsMatcher credentialsMatcher){
        ManLoginRealm manLoginRealm = new ManLoginRealm();
        //配置认证凭证器
        manLoginRealm.setCredentialsMatcher(credentialsMatcher);
        //开启认证和授权的缓存
        manLoginRealm.setCachingEnabled(true);
        //manLoginRealm.setAuthenticationCachingEnabled(true);
        manLoginRealm.setAuthorizationCachingEnabled(true);
        System.out.print("Manrealm已经加载");
        return manLoginRealm;
    }

    /**
     * 配置rememberMeCookie
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setMaxAge(259200);
        return simpleCookie;
    }

    /**
     * 配置rememberMe管理器
     * @return
     */
    @Bean
    public CookieRememberMeManager rememberMeManager(){

        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
        return cookieRememberMeManager;
    }

    /**
     * redis缓存管理器
     * @return
     */
    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setPassword(password);
        redisManager.setTimeout(timeout);
        redisManager.setExpire(1000*100);
        return redisManager;
    }

    /**
     * 使用uuid作为sessionID
     * @return
     */
    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator(){
        return new JavaUuidSessionIdGenerator();
    }

    /**
     * sessionDao
     * @return
     */
    @Bean
    public RedisSessionDAO sessionDAO(){
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setRedisManager(redisManager());
        //  Session ID 生成器
        sessionDAO.setSessionIdGenerator(sessionIdGenerator());
        return sessionDAO;
    }

    @Bean
    public SimpleCookie cookie(){
        //cookie的name,对应的默认是 JSESSIONID
        SimpleCookie cookie = new SimpleCookie("SHAREJSESSIONID");
        cookie.setHttpOnly(true);
        //  path为 / 用于多个系统共享JSESSIONID
        cookie.setPath("/");
        // 浏览器关闭时失效此cookie
        cookie.setMaxAge(-1);
        return cookie;
    }

    /**
     * session管理器
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 设置session超时
        sessionManager.setGlobalSessionTimeout(1000*100);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 删除无效session
        sessionManager.setDeleteInvalidSessions(true);
        // 设置JSESSIONID
        sessionManager.setSessionIdCookie(cookie());
        // 设置sessionDAO
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setSessionIdCookieEnabled(true);
        return sessionManager;
    }

    /**
     * 创建securityManager 关联realm
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager getSecurityManager(@Qualifier("CredentialsMatcher") CredentialsMatcher credentialsMatcher){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //缓存管理器
        securityManager.setCacheManager(new ShiroRedisCacheManager());
        //配置自定义的modularRealmAuthenticator
        securityManager.setAuthenticator(modularRealmAuthenticator());
        //配置realms
        List<Realm> realms = new ArrayList<>();
        realms.add(getLoginRealm(credentialsMatcher()));
        realms.add(getManLoginRealm(credentialsMatcher()));
        securityManager.setRealms(realms);
        //rememberMe管理器
        securityManager.setRememberMeManager(rememberMeManager());
        //session管理器
        securityManager.setSessionManager(sessionManager());
        System.out.print("安全管理器已经加载");
        return securityManager;
    }

    /**
     * shiro AOP 支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(getSecurityManager(credentialsMatcher()));
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 异常处理
     * @return
     */
    @Bean(name="simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();
        //数据库异常处理
        mappings.setProperty("DatabaseException", "500");
        //设置未授权页面
        mappings.setProperty("UnauthorizedException","unanthorized");
        simpleMappingExceptionResolver.setExceptionMappings(mappings);
        return simpleMappingExceptionResolver;
    }


    /**
     * 创建shiroFilter关联securityManager
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String,String> filterMap = new LinkedHashMap<>();
        //配置拦截路径
        filterMap.put("/manager/toLogin","user");
        filterMap.put("/manager/toRegister","user");
        filterMap.put("/manager/allStudentInfo","user");
        filterMap.put("/student/personalInfomation","user");
        filterMap.put("/student/toLogin","user");
        filterMap.put("/student/toRegister","user");
        filterMap.put("/student/login","anon");
        filterMap.put("/student/toLogin","anon");
        filterMap.put("/manager/login","anon");
        filterMap.put("/manager/toLogin","anon");
        filterMap.put("/student/toRegister","anon");
        filterMap.put("/student/register","anon");
        filterMap.put("/manager/toRegister","anon");
        filterMap.put("/manager/register","anon");
        filterMap.put("/manager/logout","logout");
        filterMap.put("/student/logout","logout");
        filterMap.put("/student/**","authc");
        filterMap.put("/manager/**","authc");
        //设置登录url
        shiroFilterFactoryBean.setLoginUrl("/toIndex");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        System.out.print("shiro拦截器已经加载");
        return shiroFilterFactoryBean;
    }
}
