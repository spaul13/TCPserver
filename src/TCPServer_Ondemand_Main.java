//package netlab.furion.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer_Ondemand_Main {

	
	static String videoPath = "E:/CubeMaps/viking_texas/viking_texas_Ionly/"; //"/Users/spaul/Desktop/viking_final.mp4";";//"F:/SharedDisk/x264/x264/sunlh_test_encode.mkv";//"F:/FurionSystem/CuiyongLab/Furion/Media/sunlh_png.mp4";
	public static void main(String[] args)

	{

		Socket socket = null;
		try {
			ServerSocket ss = new ServerSocket(5000);

			while ((socket = ss.accept()) != null) {
				InputStream is = socket.getInputStream();
				OutputStream os = socket.getOutputStream();
				int temp;
				int fileSize = 0;
				try
				{
					while (true) 
					{
					
						byte[] buf = new byte[1500];
						byte[] videoBuf = new byte[409600];
						//int fid = 0;
						//int fid1 = 0;
						//int fid2 = 0;
						int[] fid = new int[] {0, 0, 0};
						
						Long start_input = System.currentTimeMillis();
						is.read(buf);
						System.out.println("\n Received Buffer is = " + buf.toString());
						System.out.println("\n Length of the input buffer received is = " + (buf.toString().length()-1));
						
						//resolving the input stream to get the fid
						//for (int i = 0; i < buf.toString().length() - 1; i++) fid = fid * 10 + (buf[i] - '0');
						//Sending three requesting at time
						int curr = 0;
						for (int i = 0; i < 7; i++)fid[curr] = fid[curr] * 10 + (buf[i] - '0');
						curr++;
						for (int i = 7; i < 13; i++)fid[curr] = fid[curr] * 10 + (buf[i] - '0');
						curr++;
						for (int i = 13; i < 19; i++)fid[curr] = fid[curr] * 10 + (buf[i] - '0');
						for (int i=0; i<3; i++)
						{
							System.out.println("curr fid: " + fid[i]);
						}
						
						
						for(int j = 0; j<3 && fid[j] != 0; j++)
						{
							System.out.println("curr fid: " + Integer.toString(fid[j]));
							String videoFile = videoPath + Integer.toString(fid[j]) + ".mp4";
							System.out.println("Video File name To send: " + videoFile);
							Long start_rs = System.currentTimeMillis();
					
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
                    
							os.write(buf, 0, 20);
							
							os.write(videoBuf, 0, account);
						
							/*while ((account = fis.read(videoBuf)) != -1) 
							{
								System.out.println("\n the current buffer size is = " + account);
								socket.getOutputStream().write(videoBuf, 0, account);
							}
							 */
						
							System.out.println("fid: " + Integer.toString(fid[j]) + "; Send: " + Long.toString(System.currentTimeMillis() - start_send));
							System.out.println("fid: " + Integer.toString(fid[j]) + "; Read File + Send: " + Long.toString(System.currentTimeMillis() - start_rs));
							System.out.println("fid: " + Integer.toString(fid[j]) + "; Input Proc + Read File + Send: " + Long.toString(System.currentTimeMillis() - start_input));
							os.flush();
							//socket.getOutputStream().close();
							System.out.println("Video transmission finished.");
						}
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
