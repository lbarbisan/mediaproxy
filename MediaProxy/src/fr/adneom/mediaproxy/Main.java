package fr.adneom.mediaproxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.wpg.proxy.Proxy;
import com.wpg.proxy.ProxyRegistry;

import fr.adneom.mediaproxy.handler.DeezerMediaHandler;
import fr.adneom.mediaproxy.handler.JiwaMediaHandler;

public class Main {

	/**
	 * @param args
	 * @throws UnknownHostException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws UnknownHostException, InterruptedException {
	        InetAddress adress = InetAddress.getLocalHost();
	        //InetAddress adress = InetAddress.getByAddress(new byte[] {(byte)192,(byte)168,75,10});
				
			DeezerMediaHandler handler =  new DeezerMediaHandler();
			JiwaMediaHandler handler2 =  new JiwaMediaHandler();
			ProxyRegistry.addHandler(handler);
			ProxyRegistry.addHandler(handler2);
			Proxy proxy = new Proxy(adress , 8000, 0);
			proxy.run();
	}

}
