package cas.shiro.client.controller;

import cas.shiro.client.util.CasRestUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.pac4j.cas.client.rest.CasRestFormClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaocxu
 */
@RestController
public class IndexController {

    @Value("${cas.loginUrl}")
    private String casLoginUrl;
    @Value("${cas.serviceUrl}")
    private String serviceUrl;

    @GetMapping("/login/cas")
    public String casLogin(){
        return "Login successfully";
    }

    @RequestMapping("/login/rest")
    public String restLogin(String username, String password) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);

        subject.login(usernamePasswordToken);

        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final CasConfiguration casConfiguration = new CasConfiguration(casLoginUrl);
        casConfiguration.setRestUrl(CasRestUtil.restUrl);
        CasRestFormClient client = new CasRestFormClient();
        client.setConfiguration(casConfiguration);
        WebContext webContext = new J2EContext(request, response);

        String tgt = CasRestUtil.getTGT(username, password);
        String ticket = CasRestUtil.getST(tgt, serviceUrl);
        TokenCredentials casCredentials = new TokenCredentials(ticket);
        CasProfile casProfile = client.validateServiceTicket(serviceUrl, casCredentials, webContext);

        return "Login successfully";
    }

    @GetMapping("/logout/success")
    public String logoutSuccess(){
        return "Logout successfully";
    }
}
