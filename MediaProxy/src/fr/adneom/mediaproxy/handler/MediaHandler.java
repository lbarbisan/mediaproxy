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

public class MediaHandler implements HttpMessageHandler {

	private final static Logger logger = Logger.getLogger(MediaHandler.class);
	
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
			&& request.getToHost().endsWith("deezer.com")
			&& request.getToHost().contains("proxy"))
		{
			logger.trace(request.getHeadersAsString());
			logger.trace(response.getHeadersAsString());
			if(response.getStatusCode()==403) return;
			ByteBuffer buffer = ByteBuffer.wrap(response.getBodyContent());
			if(buffer!=null)
			{
				/*FLV header
				Signature 				UI8 Signature byte always 'F' (0x46)
				Signature 				UI8 Signature byte always 'L' (0x4C)
				Signature 				UI8 Signature byte always 'V' (0x56)
				Version 				UI8 File version (for example, 0x01 for FLV version 1)
				TypeFlagsReserved 		UB[5] Must be 0
				TypeFlagsAudio 			UB[1] Audio tags are present
				TypeFlagsReserved 		UB[1] Must be 0
				TypeFlagsVideo 			UB[1] Video tags are present
				DataOffset 				UI32 Offset in bytes from start of file to start of body (that is, size of header)
				*/
				//Lecture de l'entete
				if(buffer.get()== 0x46 && buffer.get()== 0x4C && buffer.get()== 0x56	//Signature FLV
					&& buffer.get() > 0x0												// Version number
					&&  buffer.get() == 0x04											// Only music
					&& buffer.getInt() == 0x09)											// DatOffset, always 9
					
				{
					//Initialisation du fichier
					FileOutputStream file = null;
					//TODO: A mettre à jour
					try {
						Date date = new Date();
						file = new FileOutputStream("Music" + date.getTime() + ".mp3");
						logger.info("Starting recording to Music" + date.getTime() + ".mp3");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					
					//Lecture du body
					while(buffer.hasRemaining())
					{
					/*	PreviousTagSizeN-1	UI32 Size of second-to-last tag
					 	TagType 			UI8 Type of this tag. Values are: 8: audio/9: video/18: script data/all others: reserved
						DataSize 			UI24 Length of the data in the Data field
						Timestamp 			UI24 Time in milliseconds at which the data in this tag applies. This value is relative to the first tag in the FLV file, which always has a timestamp of 0.
						TimestampExtended	UI8 Extension of the Timestamp field to form a UI32 value. This field represents the upper 8 bits, while the previous Timestamp field represents the lower 24 bits of the time in milliseconds.
						StreamID 			UI24 Always 0
						Data 				If TagType = 8 AUDIODATA If TagType = 9 VIDEODATA If TagType = 18 SCRIPTDATAOBJECT Body of the tag */
						
						buffer.getInt();													// Previous Size
						byte type = buffer.get();											// Tag Type
						//HACK
						if(type==87) 
						{
						 break;
						}
						int length = getIntFrom3Bytes(buffer);								// Data Size
						int timestamp = getIntFrom3Bytes(buffer);							// TimeStamp
						long timestampExt = (long)timestamp +  ((long)buffer.get() << 24);	// Extension
						int StreamId = getIntFrom3Bytes(buffer);							// Always 0
						byte[] data = new byte[length];										//  
						buffer.get(data);													// Data
						
						switch(type)
						{
						//META
						case 0x12 :
							break;
						//AUDIO
						case 0x8 :
								try {

									/* 
									SoundFormat UB[4]
										0 = Linear PCM, platform endian
										1 = ADPCM
										2 = MP3
										3 = Linear PCM, little endian
										4 = Nellymoser 16-kHz mono
										5 = Nellymoser 8-kHz mono
										6 = Nellymoser
										7 = G.711 A-law logarithmic PCM
										8 = G.711 mu-law logarithmic PCM
										9 = reserved
										10 = AAC
										14 = MP3 8-Khz
										15 = Device-specific sound
									SoundRate UB[2]
										0 = 5.5-kHz
										1 = 11-kHz
										2 = 22-kHz
										3 = 44-kHz
									SoundSize UB[1]
										0 = snd8Bit
										1 = snd16Bit
									SoundType UB[1]
										0 = sndMono
										1 = sndStereo
									SoundData UI8[size of sound data]
										if SoundFormat == 10
											AACAUDIODATA
										else
											Sound data—varies by format */
									file.write(data, 1, data.length-1);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							//run = false;
							break;
						//VIDEO
						case 0x9 :
							break;

						}
					}
					
					try {
						file.close();
						logger.info("Recording OK");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
