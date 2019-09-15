package cas.shiro.client.config;

import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.filter.LogoutFilter;
import io.buji.pac4j.filter.SecurityFilter;
import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaocxu
 */
@Configuration
public class ShiroCasConfig extends AbstractShiroWebFilterConfiguration {

    @Value("${cas.prefixUrl}")
    private String casPrefixUrl;
    @Value("${cas.loginUrl}")
    private String casLoginUrl;
    @Value("${cas.serviceUrl}")
    private String serviceUrl;
    @Value("${cas.callbackUrl}")
    private String callbackUrl;

    public SecurityFilter securityFilter(){
        SecurityFilter filter = new SecurityFilter();
        filter.setClients("cas");
        filter.setConfig(config());
        return filter;
    }


    /**
     * 定义cas客户端集合
     * @return
     */
    @Bean
    public Clients clients() {
        Clients clients = new Clients();
        clients.setClients(casClient());
        return clients;
    }

    /**
     * 定义cas客户端
     * @return
     */
    @Bean
    public CasClient casClient() {
        CasClient casClient = new CasClient();
        casClient.setConfiguration(casConfiguration());
        casClient.setCallbackUrl(callbackUrl);
        // 设置cas客户端名称为cas
        casClient.setName("cas");
        return casClient;
    }

    /**
     * cas服务的基本设置
     * @return
     */
    @Bean
    public CasConfiguration casConfiguration() {
        CasConfiguration casConfiguration = new CasConfiguration(casLoginUrl);
        // 默认CasProtocol.CAS30
        casConfiguration.setProtocol(CasProtocol.CAS30);
        casConfiguration.setPrefixUrl(casPrefixUrl);
        return casConfiguration;
    }

    @Bean
    public Config config(){
        Config config = new Config();
        config.setClients(clients());
        return config;
    }

    public CallbackFilter callbackFilter(){
        CallbackFilter callbackFilter = new CallbackFilter();
        callbackFilter.setConfig(config());
        return callbackFilter;
    }

    public LogoutFilter logoutFilter(){
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setConfig(config());
        logoutFilter.setCentralLogout(true);
        logoutFilter.setDefaultUrl("/logout/success");
        return logoutFilter;
    }

    @Bean
    public Pac4jRealm pac4jRealm() {
        return new Pac4jRealm();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(Pac4jRealm pac4jRealm) {
//        ((DefaultWebSecurityManager) super.securityManager).setSubjectFactory(new Pac4jSubjectFactory());
//        ((DefaultWebSecurityManager) super.securityManager).setRealm(pac4jRealm);
        ShiroFilterFactoryBean shiroFilterFactoryBean = super.shiroFilterFactoryBean();
        Map<String, Filter> filters = new HashMap<>(16);
        filters.put("securityFilter", securityFilter());
        filters.put("callbackFilter", callbackFilter());
        filters.put("logoutFilter", logoutFilter());
        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition(){
        DefaultShiroFilterChainDefinition defaultShiroFilterChainDefinition = new DefaultShiroFilterChainDefinition();
        defaultShiroFilterChainDefinition.addPathDefinition("/callback", "callbackFilter");
        defaultShiroFilterChainDefinition.addPathDefinition("/login/rest", "anon");
        defaultShiroFilterChainDefinition.addPathDefinition("/login/cas", "securityFilter");
        defaultShiroFilterChainDefinition.addPathDefinition("/logout/success", "anon");
        defaultShiroFilterChainDefinition.addPathDefinition("/logout", "logoutFilter");
        defaultShiroFilterChainDefinition.addPathDefinition("/**", "securityFilter");
        return defaultShiroFilterChainDefinition;
    }

    /**
     * 注入 securityManager
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(pac4jRealm());
        securityManager.setSubjectFactory(new Pac4jSubjectFactory());
        return securityManager;
    }
}
