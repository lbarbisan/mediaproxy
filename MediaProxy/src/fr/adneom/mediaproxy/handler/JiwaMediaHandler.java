package fr.adneom.mediaproxy.handler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.log4j.Logger;

import com.wpg.proxy.HttpMessageHandler;
import com.wpg.proxy.HttpMessageRequest;
import com.wpg.proxy.HttpMessageResponse;

public class JiwaMediaHandler implements HttpMessageHandler {

	private final static Logger logger = Logger.getLogger(JiwaMediaHandler.class);
	
	@Override
	public void failed(Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void failedRequest(HttpMessageRequest request, Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void failedResponse(HttpMessageResponse response,
			HttpMessageRequest request, Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedRequest(HttpMessageRequest request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receivedResponse(HttpMessageResponse response,
			HttpMessageRequest request) {
		
		if("GET".equals(request.getMethod())
			&& request.getUri().getPath().equals("/play.php")
			&& request.getToHost().contains("m.jiwa.fm"))
		{
			logger.trace(request.getHeadersAsString());
			logger.trace(response.getHeadersAsString());
			if(response.getStatusCode()==403) return;
			ByteBuffer buffer = ByteBuffer.wrap(response.getBodyContent());
			if(buffer!=null)
			{
					//Initialisation du fichier
					FileOutputStream file = null;
					//TODO: A mettre à jour
					try {
						byte[] data = buffer.array();
						Date date = new Date();
						file = new FileOutputStream("Music" + date.getTime() + ".mp3");
						logger.info("Starting recording to Music" + date.getTime() + ".mp3");
						file.write(data, 0, data.length-1);
						file.close();
						logger.info("Recording OK");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			else
			{
				logger.error("Unable to found data");
			}
		}

	}
	
	private int getIntFrom3Bytes(ByteBuffer buffer)
	{
			int firstByte = (0xFF & ((int)buffer.get()));
			int secondByte = (0xFF & ((int)buffer.get()));
			int thirdByte = (0xFF & ((int)buffer.get()));
			int anUnsignedInt  = ((int) (firstByte << 16 | secondByte << 8 | thirdByte)) & 0xFFFFFFFF;
			return anUnsignedInt;
	}
}
