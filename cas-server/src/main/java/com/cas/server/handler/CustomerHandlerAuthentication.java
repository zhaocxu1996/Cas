package com.cas.server.handler;

import com.cas.server.entity.User;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * @author zhaocxu
 */
public class CustomerHandlerAuthentication extends AbstractUsernamePasswordAuthenticationHandler {

    @Value("${algorithmName}")
    private String algorithmName;
    @Value("${hashIterations}")
    private int hashIterations;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;
    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.sql}")
    private String sql;

    /**
     *
     * @param name the authentication handler name
     * @param servicesManager
     * @param principalFactory
     * @param order
     */
    public CustomerHandlerAuthentication(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential transformedCredential, String originalPassword) throws GeneralSecurityException, PreventedException {
        String username = transformedCredential.getUsername();
        String password = transformedCredential.getPassword();
        // JDBC模板依赖于连接池来获得数据的连接，所以必须先要构造连接池
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);

        // 创建JDBC模板
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);

        User user = jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper<>(User.class));
        if (user == null) {
            throw new AccountException("Sorry, account not found!");
        }

        if (!user.getPassword().equals(password)) {
            throw new FailedLoginException("Sorry, password not correct!");
        } else {
            Map<String, Object> map = new HashMap<>(16);
            final List<MessageDescriptor> list = new ArrayList<>();
            Principal principal = this.principalFactory.createPrincipal(username, map);
            return createHandlerResult(transformedCredential, principal, list);
        }
    }
}
