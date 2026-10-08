package cc.wdev.platform.commons.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * @author elvea
 */
public class JsoupUtils {

    private static final int CONNECT_TIMEOUT = 3 * 60 * 1000;

    private static final String SSL_TYPE = "SSL";

    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
            .timeout(CONNECT_TIMEOUT)
            .sslContext(JsoupUtils.sslContext())
            .get();
    }

    private static SSLContext sslContext() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance(SSL_TYPE);
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

}
