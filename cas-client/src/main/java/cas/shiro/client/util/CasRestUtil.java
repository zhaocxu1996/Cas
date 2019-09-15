package cas.shiro.client.util;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaocxu
 */
@Component
public class CasRestUtil {

    private final static Logger logger = LoggerFactory.getLogger(CasRestUtil.class);
    public static String restUrl;
    /**
     * 获取TGT
     */
    public static String getTGT(String username, String password) {
        try{
            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(restUrl);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);

            Header headerLocation = response.getFirstHeader("Location");
            String location = headerLocation == null ? null : headerLocation.getValue();

            System.out.println("Location：" + location);

            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
        }catch (Exception e){
            logger.error("get TicketGrantTicket failed", e);
        }
        return null;
    }

    /**
     * 获取ST
     */
    public static String getST(String TGT, String service){
        try {
            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(restUrl + "/" + TGT);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("service", service));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);

            String st = readResponse(response);
            return st == null ? null : (st == "" ? null : st);
        }catch (Exception e){
            logger.error("get ServiceTicket failed", e);
        }
        return null;
    }

    /**
     * 读取 response body 内容为字符串
     *
     * @param response
     * @return
     * @throws IOException
     */
    private static String readResponse(HttpResponse response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String result = new String();
        String line;
        while ((line = in.readLine()) != null) {
            result += line;
        }
        return result;
    }

    @Value("${cas.restUrl}")
    public void setRestUrl(String restUrl) {
        CasRestUtil.restUrl = restUrl;
    }
}
