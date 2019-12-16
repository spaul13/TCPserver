//package netlab.furion.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

//public class TCPServer_preload_ondemand {
public class main_server {
	static String videoPath = "/Users/i-honghou/eclipse-workspace/SPLICE_server/videos/";
	//static String videoPath = "/Users/i-honghou/eclipse-workspace/SPLICE_server/videos/"; //"E:/CubeMaps/viking_texas/viking_texas_Ionly/"; //"/Users/spaul/Desktop/viking_final.mp4";";//"F:/SharedDisk/x264/x264/sunlh_test_encode.mkv";//"F:/FurionSystem/CuiyongLab/Furion/Media/sunlh_png.mp4";
	public static void main(String[] args) //throws InterruptedException

	{

		Socket socket = null;
		try {
			ServerSocket ss = new ServerSocket(5003);
			
			//byte[][] videoBuf = new byte[10][409600];
			//byte[][][] videoBuf = new byte[3][16100][256000]; //3: # of resolutions; 16100: # of locations
			byte[][][] videoBuf = new byte[3][1000][256000]; //3: # of resolutions; 16100: # of locations
			//For resolutions: 512->0, 1024 -> 1, 2048 -> 2
			String videoFile;
			File video;
			FileInputStream fis;
			int[][] filesize = new int[3][25921]; //storing the filesize of all files there
			System.out.println("Preload begin\n");
			//Loading 512 resolution
			for(int i=0; i<1000; i++)
			{
				videoFile = videoPath + "viking_texas_512_crf28_Ionly/" +  Integer.toString(i) + ".mp4";
				video = new File(videoFile);
				fis = new FileInputStream(video);
				filesize[0][i] = fis.read(videoBuf[0][i]);
				//System.out.println(" Filesize = " +filesize[0][i] + "\n");
			}
			
			//Loading 1024 resolution
			for(int i=0; i<1000; i++)
			{
				videoFile = videoPath + "viking_texas_1024_crf28_Ionly/" +  Integer.toString(i) + ".mp4";
				video = new File(videoFile);
				fis = new FileInputStream(video);
				filesize[1][i] = fis.read(videoBuf[1][i]);
				//System.out.println(" Filesize = " +filesize[1][i] + "\n");
			}

			
			//Loading 2048 resolution
			for(int i=0; i<1000; i++)
			{
				videoFile = videoPath + "viking_texas_2048_crf28_Ionly/" +  Integer.toString(i) + ".mp4";
				video = new File(videoFile);
				fis = new FileInputStream(video);
				filesize[2][i] = fis.read(videoBuf[2][i]);
				//System.out.println(" Filesize = " +filesize[2][i] + "\n");
			}
			
			System.out.println("Preload done\n");
			

			
			//for (int i =0 ; i<16100; i++) System.out.println(" \n Filesize = " +filesize[i]);

			while ((socket = ss.accept()) != null) {
				
				socket.setSendBufferSize(2000000);//setting the sendbuff size really high
				System.out.println("\n One Request In");
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();
				int temp;
				int fileSize = 0;
				
				try
				{
					while (true) 
					{
					
						
						int[] fid = new int[] {0, 0, 0,0};
						int [] resolution = new int[] {0,0,0,0};
						byte[] buf = new byte[1500];
						Long start_input = System.currentTimeMillis();
						System.out.println("\n Here 3");
						//The buf format is name1,name2,name3,...\0
						is.read(buf);
						//System.out.println("\n Received Buffer is = " + buf.toString());
						System.out.println("\n Length of the input buffer received is = " + (buf.toString().length()-1));
						
						
						//resolving the input stream to get the fid
						//for (int i = 0; i < buf.toString().length() - 1; i++) fid = fid * 10 + (buf[i] - '0');
						//Sending three requesting at time
						double resolve_start = System.currentTimeMillis();
						int count = 0;	//count is the position in the byte stream
						int curr = 0;	//curr is the position of the current name
						while(buf[count]!= '\0') {
							System.out.print((char)buf[count]);
							count++;
						}
						count = 0;
						while(buf[count] != '\0') {
							System.out.print((char)buf[count]);
							if(buf[count] == ',') {	//end of this name
								count++;
								curr++;
								continue;
							}
							if(buf[count] == '_') {	//next few bytes will be "x.mp4", where x is the resolution, x can only be 000512, 001024, 002048
								count+=3;
								//System.out.print((char)buf[count]);
								if(buf[count] == '0') {
									resolution[curr] = 0;
									count += 4; //the remaining is "512"
									System.out.println("\n Requested file = " + fid[curr] + ", 512\n");
								}else if(buf[count] == '1') {
									resolution[curr] = 1;
									count += 4;	//the remaining is "024"
									System.out.println("\n Requested file = " + fid[curr] + ", 1024\n");
								}else if(buf[count]== '2') {
									resolution[curr] = 2;
									count += 4; //the remaining is "048"
									System.out.println("\n Requested file = " + fid[curr] + ", 2048\n");
								}
								continue;
							}
							fid[curr] = fid[curr] * 10 + (buf[count] - '0');
							count++;
						}
						
						/*
						for (int i = 0; i < 7; i++)fid[curr] = fid[curr] * 10 + (buf[i] - '0');
						curr++;
						for (int i = 7; i < 13; i++)fid[curr] = fid[curr] * 10 + (buf[i] - '0');
						curr++;
						for (int i = 13; i < 19; i++)fid[curr] = fid[curr] * 10 + (buf[i] - '0');
						System.out.println("\n Resolve the requested Fids take = " + (System.currentTimeMillis() - resolve_start));
						*/
						/*
						for (int i=0; i<3; i++)
						{
							//if(fid[i] <=0) fid[i] = 0; //negative fids, fids not requested
							if(fid[i] <=0)System.out.println("No request for " + i);
							else System.out.println("curr fid: " + fid[i] + ", filesize : " + filesize[fid[i]]);
							
						}*/
						
						
						curr = 0;
						int account_1, account_2, account_3;
						account_1 = account_2 = account_3 = 0;
						double read_start = System.currentTimeMillis();
						//First video
						/*
						if(fid[curr]>0)
						{
							videoFile = videoPath + Integer.toString(fid[curr]) + ".mp4";
							video = new File(videoFile);
							fis = new FileInputStream(video);
							account_1 = fis.read(videoBuf_1);
						}
						curr++;
						
						
						//Second video
						if(fid[curr]>0)
						{
							videoFile = videoPath + Integer.toString(fid[curr]) + ".mp4";
							video = new File(videoFile);
							fis = new FileInputStream(video);
							account_2 = fis.read(videoBuf_2);							
						}
						curr++;
						
						//Third video
						if(fid[curr]>0)
						{
							videoFile = videoPath + Integer.toString(fid[curr]) + ".mp4";
							video = new File(videoFile);
							fis = new FileInputStream(video);
							account_3 = fis.read(videoBuf_3);
						}
						*/
						
						
						System.out.println("\n Read the requested Fids take = " + (System.currentTimeMillis() - read_start));
						
						//if((account_1 >0) || (account_2 >0) || (account_3 >0))
						//{
						//preparing the initial packet to all frameids and filesizes
						buf = new byte[40];
						curr=0;
						buf[0] = 'S';
						buf[1] = 'J';
						//first
						temp = fid[curr];
						
						double send_pkt_time = System.currentTimeMillis();
						for (int i = 7; i > 1; i--) 
						{
							buf[i] = (byte) (char) ((temp % 10) + '0');
							temp /= 10;
						}
						if(fid[curr]>0)temp = filesize[resolution[curr]][fid[curr]];
						else temp = 0;
						for (int i = 13; i > 7; i--) 
						{
							buf[i] =  (byte) (char) ((temp % 10) + '0');
							temp /= 10;
						}
						curr++;
						//second
						if(fid[curr]>0)temp = fid[curr];
						else temp = 0;
						
						for (int i = 19; i > 13; i--) 
						{
							buf[i] =  (byte) (char) ((temp % 10) + '0');
							temp /= 10;
						}
						if(fid[curr]>0)temp = filesize[resolution[curr]][fid[curr]];
						else temp = 0;;
						for (int i = 25; i > 19; i--) 
						{
							buf[i] =  (byte) (char) ((temp % 10) + '0');
							temp /= 10;
						}
						curr++;
						//third
						if(fid[curr]>0)temp = fid[curr];
						else temp = 0;
						for (int i = 31; i > 25; i--) 
						{
							buf[i] =  (byte) (char) ((temp % 10) + '0');
							temp /= 10;
						}
						if(fid[curr]>0)temp = filesize[resolution[curr]][fid[curr]];
						else temp = 0;
						
						for (int i = 37; i > 31; i--) 
						{
							buf[i] =  (byte) (char) ((temp % 10) + '0');
							temp /= 10;
						}
						System.out.println("\n Preparing small 40 byte pkt take = " + (System.currentTimeMillis() - send_pkt_time));
						//for (int i = 39; i > 37; i++) buf[i] = 0;
						curr=0;
						double send_all_pkts = System.currentTimeMillis();
						os.write(buf, 0, 40);
						if(fid[curr] > 0)os.write(videoBuf[resolution[curr]][fid[curr]], 0, filesize[resolution[curr]][fid[curr]]);
						curr++;
						//os.flush();
						if(fid[curr] > 0) os.write(videoBuf[resolution[curr]][fid[curr]], 0, filesize[resolution[curr]][fid[curr]]);
						curr++;
						//os.flush();
						if(fid[curr] > 0) os.write(videoBuf[resolution[curr]][fid[curr]], 0, filesize[resolution[curr]][fid[curr]]);
						System.out.println("\n sending all pkts take = " + (System.currentTimeMillis() - send_all_pkts));
						os.flush();
						
						
						
						
						/*
						for(int j = 0; j<3 && fid[j] != 0; j++)
						{
							System.out.println("curr fid: " + Integer.toString(fid[j]));
							String videoFile = videoPath + Integer.toString(fid[j]) + ".mp4";
							System.out.println("Video File name To send: " + videoFile);
							Long start_rs = System.currentTimeMillis();
							
							buf = new byte[1500];
							int account = 0;
							File video = new File(videoFile);
							System.out.println("\n the videofile size is = " + video.length());
							FileInputStream fis = new FileInputStream(video);
							int sendBufSize = socket.getSendBufferSize();
							System.out.println("\n Send Buffer size = " + Integer.toString(sendBufSize));
							Long start_send = System.currentTimeMillis();
						
						
							account = fis.read(videoBuf);
							System.out.println("\n the total size is = " + account);
							
						
							buf[0] = 'S';
							buf[1] = 'J';
							temp = fid[j];
							for (int i = 7; i > 1; i--) 
							{
								buf[i] = (byte) (char) ((temp % 10) + '0');
								temp /= 10;
							}
							temp = account;
							for (int i = 13; i > 7; i--) 
							{
								buf[i] =  (byte) (char) ((temp % 10) + '0');
								temp /= 10;
							}
							for (int i = 19; i > 14; i--) 
							{
								buf[i] = 0;
							}
							
							System.out.println("\n Small 20 byte Send Buffer is = " + buf.toString());
							os.write(buf, 0, 20);
							//if(j>0) Thread.sleep(1); //sleeping for 1 ms
							if(j>0) os.flush();
							os.write(videoBuf, 0, account);
						
							//while ((account = fis.read(videoBuf)) != -1) 
							//{
							//	System.out.println("\n the current buffer size is = " + account);
							//	socket.getOutputStream().write(videoBuf, 0, account);
							//}
							 
						
							System.out.println("fid: " + Integer.toString(fid[j]) + "; Send: " + Long.toString(System.currentTimeMillis() - start_send));
							System.out.println("fid: " + Integer.toString(fid[j]) + "; Read File + Send: " + Long.toString(System.currentTimeMillis() - start_rs));
							System.out.println("fid: " + Integer.toString(fid[j]) + "; Input Proc + Read File + Send: " + Long.toString(System.currentTimeMillis() - start_input));
							os.flush();
							//socket.getOutputStream().close();
							System.out.println("Video transmission finished.");
						}
						*/
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
