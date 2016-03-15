package elink.websocket;

import com.coolkit.common.HLog;

import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketChatClient extends WebSocketClient {


	public static interface SocketListener {
		public void onOpen();

		public void onClose();

		public void onMessage(String msg);
	}

	SocketListener mListener;



	public WebSocketChatClient(URI serverUri, Map<String,String> headers,SocketListener listener,String tag) {
		super(serverUri, new Draft_17(), headers, 5*1000,tag);
		mListener = listener;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("on open Connected");
		if (null != mListener) {
			mListener.onOpen();
		}

	}

	@Override
	public void onMessage(String message) {
		System.out.println("got: " + message);
		if (null != mListener) {
			mListener.onMessage(message);
		}

	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Disconnected");
		if (null != mListener) {
			mListener.onClose();
		}

	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();

	}

	public void open() {
		try {

			// // load up the key store
			// String STORETYPE = "JKS";
			// String KEYSTORE = "keystore.jks";
			// String STOREPASSWORD = "storepassword";
			// String KEYPASSWORD = "keypassword";
			//
			// KeyStore ks = KeyStore.getInstance(STORETYPE);
			// File kf = new File(KEYSTORE);
			// ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());
			//
			// KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			// kmf.init(ks, KEYPASSWORD.toCharArray());
			// TrustManagerFactory tmf = TrustManagerFactory
			// .getInstance("SunX509");
			// tmf.init(ks);
			//
			// SSLContext sslContext = null;
			// sslContext = SSLContext.getInstance("TLS");
			// sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(),
			// null);
			// // sslContext.init( null, null, null ); // will use java's
			// default
			// // key and trust store which is sufficient unless you deal with
			// // self-signed certificates
			if ("wss".equals(this.getURI().getScheme())) {
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null,
						new TrustManager[] { new MyTrustManager() },
						new SecureRandom());
				SSLSocketFactory factory = sslContext.getSocketFactory();// (SSLSocketFactory)
				this.setSocket(factory.createSocket());
			}
			this.connectBlocking();
			HLog.i("", "connect blocking");

		} catch (Exception e) {
			HLog.e("TAG", e);
		}
	}

	public void send(String text) {
		super.send(text);
	}
}

class MyTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return null;
	}
}
