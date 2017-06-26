package ks;

import org.apache.commons.io.FileUtils;
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

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;

public class VKDownloader extends JFrame {
	
	private static String pathToFolder = "";
	private static JProgressBar progressBar;
	public static String IDo, IDv;

    public static void download(final String ACCESS_TOKEN, final String USER_ID, int numberOfMultimedia, int offset, 
    		String path, boolean[] multimedia, JProgressBar progressBar, 
    		JLabel lblProgressOfChecking, JLabel lblNewLabel, String methodType, JTextPane downloadVideoLink) 
    				throws URISyntaxException, IOException, ParseException, KeyManagementException, NoSuchAlgorithmException, AWTException {
    	
    	pathToFolder = path;
    	VKDownloader.progressBar = progressBar;
        URIBuilder builder = new URIBuilder();
        
        if (methodType.equals("/method/audio.get")) {
        builder.setScheme("https").setHost("api.vk.com").setPath(methodType)
        		.setParameter("oid", USER_ID)
                .setParameter("need_user", "0")
                .setParameter("count", "" + numberOfMultimedia)
                .setParameter("offset", "" + offset)
                .setParameter("access_token", ACCESS_TOKEN);
        }
        
        else if (methodType.equals("/method/video.get")) {
        	IDo = Videolist.IDowner;
        	IDv = Videolist.IDid;
            builder.setScheme("https").setHost("api.vk.com").setPath(methodType)
                    .setParameter("oid", USER_ID) 
                    .setParameter("access_token", ACCESS_TOKEN);
        }
        
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
        
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        
        if (entity != null) {
            InputStream instream = null;
            try {
                instream = entity.getContent();
                String responseAsString = IOUtils.toString(instream);
                parseAndDownload(responseAsString, multimedia, lblProgressOfChecking, lblNewLabel, methodType, downloadVideoLink);
            } finally {
                if (instream != null) instream.close();
            }
        }
    }

    private static void parseAndDownload(String resp, boolean[] multimedia, 
    		JLabel lblProgressOfChecking, JLabel lblNewLabel, String methodType, JTextPane downloadVideoLink) throws IOException, ParseException, AWTException {
    	
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(resp);
        JSONArray multimediaList = (JSONArray) jsonResponse.get("response");
        
        
    	if (multimedia == null) {
    		multimedia = new boolean[multimediaList.size()];
    		for(int i = 0; i < multimedia.length; i++){
    			multimedia[i] = true;
    		}
    	}
    	
    	int countOfMultimedia = getCountOfMultimedia (multimedia);
    	int counter = 1;
        for (int i = 0; i < multimediaList.size() - 1; i++) {
        	
        	if(!multimedia[i]) continue;
        	
            if (methodType.equals("/method/audio.get")) { 
            	JSONObject media = (JSONObject) multimediaList.get(i);
	            String pathname = pathToFolder + checkFileName(media.get("artist") +
	                    " - " + media.get("title"));
	            pathname = new String (pathname.getBytes("windows-1251"), "UTF-8");
	            String temp = checkFileName(media.get("artist") +
	                    " - " + media.get("title"));
	            lblProgressOfChecking.setText(new String(temp.getBytes("windows-1251"), "UTF-8"));
	            lblNewLabel.setText("" + counter + " / " + countOfMultimedia);
	            
	            try {
	               File destination = new File (pathname + ".mp3");
	               if (!destination.exists()) {
	            	   System.out.println(new URL ((String) media.get("url")).toString());
	                   FileUtils.copyURLToFile(new URL((String) media.get("url")), destination);
	               }
	            } catch (FileNotFoundException e) {
	                System.out.print("ERROR " + pathname);
	            }
            }
            
            else if (methodType.equals("/method/video.get")) { 
            	if (i == multimediaList.size() - 1) break;
            	JSONObject media = (JSONObject) multimediaList.get(i + 1);
            	String pathname = pathToFolder + checkFileName(media.get("title").toString());
	            pathname = new String (pathname.getBytes("windows-1251"), "UTF-8");
	            String temp = checkFileName(media.get("title").toString());
	            lblProgressOfChecking.setText(new String(temp.getBytes("windows-1251"), "UTF-8"));
	            lblNewLabel.setText("" + counter + " / " + countOfMultimedia);
	            	            
	            try {
	            	File destination = new File(pathname + ".mp4");
	               if (!destination.exists()) {
	            	   
	            	   String str = "http://vk.com/" + media.get("link");
	            	  try {
	            		  String str1 = new String (str.getBytes("UTF-8"));
		            	  URL ur = new URL (str1);
		            	  try {
//		            		  FileUtils.copyURLToFile(new URL((String) media.get("image")), destination);
		            		  LineNumberReader reader = new LineNumberReader (new InputStreamReader (ur.openStream()));
		            		  
		            		  StringBuilder sb = new StringBuilder ();
		            		  String string = reader.readLine();
		            		  
		            		  while (string != null) {
		            			  System.out.println (string);
		            			  string = reader.readLine();
		            			  sb.append(string);
		            		  }
		            		  
		            		  int videoStart;
		            		  videoStart = sb.indexOf("url360");
		            		  if (videoStart != -1) {
			            		  videoStart += 11;
			            		  
			            		  StringBuilder link = new StringBuilder ();
			            		  
			            		  for(; !("" + sb.charAt(videoStart)).equals("?") ; videoStart++) {
			            				link.append(sb.charAt(videoStart));
			            		  }
			            		  
			            		  downloadVideoLink.setText (link.toString());
		            		  }
		            		  else 
		            			  if (videoStart == -1 ) {
			            			  videoStart = sb.indexOf("http%3A");
			            			  if (videoStart != -1) {
					            		  
					            		  StringBuilder link = new StringBuilder ();
					            		  
					            		  for(; !("" + sb.charAt(videoStart)).equals("\\") ; videoStart++) {
					            			  link.append(sb.charAt(videoStart));
					            		  }
		
					            		  String s = link.toString();
					            		  s = s.replaceAll("%3A", ":");
					            		  s = s.replaceAll("%2F", "/");
					            		  s = s.replaceAll("%3F", "?");
					            		  s = s.replaceAll("%3D", "=");
					            		  s = s.replaceAll("youtube", "ssyoutube");
		
					            		  downloadVideoLink.setText (s);
			            			  }
			            		  }
		            		  else {
		            			  JOptionPane.showMessageDialog(new JFrame (), "Ви не можете завантажити це відео");
		            		  }
		            	  } catch (IOException e) { e.printStackTrace(); }
		            	  
	            	  } catch (Exception e) { e.printStackTrace(); }
	               } 
	            } catch (Exception e) { e.printStackTrace(); }
            }
        	progressBar.setValue((int)(((double)(counter++) / (double)(countOfMultimedia)) * 100));
        }
    }
    
    public static int getCountOfMultimedia (boolean[] multimedia) { // отримуємо кількість мультимедіа для завантаження
    	
    	int counter = 0;
    	
    	for (boolean temp : multimedia) {
    		if(temp) counter++;
    	}
    	
    	return counter;
    }

    private static String checkFileName(String pathname) {
    	
    	String[] badSymbols = new String[] {":", "\"", "<", "*", ">",  "/", "|" , "\\", "?"};
        String result = pathname;
        
        for (String forbiddenSymbol: badSymbols) {
            result = StringUtils.replace(result, forbiddenSymbol, "");
        }
        
        return StringEscapeUtils.unescapeXml(result);
    }
}