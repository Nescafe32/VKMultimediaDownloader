package ks;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Videolist {
	
	public static String IDowner, IDid;
	
    public static String[] getVideolist(final String ACCESS_TOKEN, final String USER_ID) throws URISyntaxException, IOException, ParseException, NoSuchAlgorithmException, KeyManagementException {
        
    	URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("api.vk.com").setPath("/method/video.get") // отримання списку відеозаписів
                .setParameter("oid", USER_ID) 					// ідентифікатор користувача або спільноти, відеозаписи яких потрібно завантажити
                .setParameter("need_user", "0")					//
                .setParameter("count", "100") 					// число завантажуваних відеозаписів
                .setParameter("offset", "0") 					// зміщення, необхідне для вибірки визначеної кількості аудіозаписів
                .setParameter("access_token", ACCESS_TOKEN);	// токен доступу, отриманий раніше
        
        URI uri = builder.build();
        HttpGet httpget = new HttpGet(uri);
        HttpClient httpclient = new DefaultHttpClient();
        
        /********************************************Для https************************************************/
        
        SSLContext contxt = SSLContext.getInstance("TLS");
        X509TrustManager trust = new X509TrustManager() {
        	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
        	 
        	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {}
        	 
        	public X509Certificate[] getAcceptedIssuers() {
        		return null;
        	}
        };
        	
        contxt.init(null, new TrustManager[]{trust}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(contxt);
        ClientConnectionManager ccm = httpclient.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", ssf, 443));
        
        /*****************************************************************************************************/

        HttpResponse response = httpclient.execute(httpget); 	// відправляємо запит для отримання відгуку
        HttpEntity entity = response.getEntity();				// отримуємо сутність відгуку на запит
        
        
        if (entity != null) {									// обробляємо відповідь на запит
            InputStream instream = null;
            try {
                instream = entity.getContent();
                String responseAsString = IOUtils.toString(instream);
                IDowner = ownerID(responseAsString);
                IDid = videoID(responseAsString);
                return parseAndGetList(responseAsString);
            } finally {
                if (instream != null) instream.close();
            }
        }
        
        return null;
    }

    private static String[] parseAndGetList (String resp) throws IOException, ParseException { // парсимо та отримуємо список відеозаписів
    	
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(resp);
        JSONArray videolist = (JSONArray) jsonResponse.get("response"); 
        String[] names = new String[videolist.size() - 1];
        
        for (int i = 0; i < videolist.size() - 1; i++) {
	            JSONObject video = (JSONObject) videolist.get(i + 1);
	            String temp = checkFileName(video.get("title") + " - " + video.get("duration") + " seconds");
	            names[i] = new String(temp.getBytes("windows-1251"), "UTF-8");
        }
        
        return names;
    }
    
    public static String ownerID (String link) throws NullPointerException { // отримуємо ID користувача
		
		StringBuilder owner_id = new StringBuilder ();
		int start = link.indexOf("oid");
		if(start == -1) throw new NullPointerException();
		else start += 4;
		
		for(; !("" + link.charAt(start)).equals("&") ; start++) {
			owner_id.append(link.charAt(start));
		}
		
		return owner_id.toString();
	}
    
    public static String videoID (String link) throws NullPointerException { // отримуємо ID користувача
		
		StringBuilder video_id = new StringBuilder ();
		int start = link.indexOf("&id");
		if(start == -1) throw new NullPointerException();
		else start += 4;
		
		for(; !("" + link.charAt(start)).equals("&"); start++) {
			video_id.append(link.charAt(start));
		}
		
		return video_id.toString();
	}

    private static String checkFileName (String pathname) { // усунення заборонених для використання в іменах файлів символів
    	
    	String[] badSymbols = new String[] {":", "\"", "<", "*", ">",  "/", "|" , "\\", "?"};
        String result = pathname;
        
        for (String forbiddenSymbol: badSymbols) {
            result = StringUtils.replace(result, forbiddenSymbol, "");
        }
        
        return StringEscapeUtils.unescapeXml(result);
    }
}