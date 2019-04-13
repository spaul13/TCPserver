import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class Furion_final {
    public static void __main(String[] args) {
    	/*
	TcpThread pt_1 = new TcpThread("client1", 4544, 4545, "192.168.1.147");
    	TcpThread pt_2 = new TcpThread("client2", 4554, 4555, "192.168.1.143");
    	TcpThread pt_3 = new TcpThread("client3", 4564, 4565, "192.168.1.127");
    	TcpThread pt_4 = new TcpThread("client4", 4574, 4575, "192.168.1.107");
    	TcpThread pt_5 = new TcpThread("client5", 5000, 5001 , "128.46.101.106"); //localhost, "192.168.1.131"
	pt_1.start();
    	pt_2.start();
    	pt_3.start();
    	pt_4.start();
    	pt_5.start();
	*/
	TcpThread pt_6 = new TcpThread("client6", 5000, 5001 , "192.168.1.145");
	pt_6.start();
    	
    }

    static class TcpThread implements Runnable {
    	String localPath = "E:/CubeMaps/viking_texas/";//"E:/CubeMaps/furion_complete/";//of_complete/"; // Ionly/"; //
    	Thread tcpThread;
    	String threadFlag;
    	int prefetchPort, sniffPort;
    	DatagramSocket prefetchSocket = null;
    	DatagramSocket sniffSocket = null;
    	byte[] buf = new byte[1500];
    	byte[] videoBuf = new byte[409600];
    	InetAddress group = null;
    	int dgramLen = 1400;
    	byte[] startBuf = new byte[409600];

    	TcpThread(String threadFlag, int prefetchPort, int sniffPort, String ipAddr) {
    		this.threadFlag = threadFlag;
    		this.prefetchPort = prefetchPort;
    		this.sniffPort = sniffPort;
    		try {
    			prefetchSocket = new DatagramSocket(prefetchPort);
    			sniffSocket = new DatagramSocket();
    			group = InetAddress.getByName(ipAddr);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}

    	@Override
    	public void run() {
    		int fid = 0;
    		File video = new File(localPath + "0.mp4"); //"25839.mp4");
    		//File video = new File(localPath + "viking_frame_000.png");
    		System.out.println("\n the videofile size is = " + video.length());
    		FileInputStream fis = null;
    		int fileSize = 0;
    		try 
    		{
    			fis = new FileInputStream(video);
    			fileSize = fis.read(videoBuf);
    			System.out.println("\n the videoBuf size is = " + fileSize);
    		} 
    		catch (Exception e) 
    		{
    			e.printStackTrace();
    		}
    		
                /*
    		File startVideo = new File("E:/CubeMaps/of_complete/check_7.mp4");
    		FileInputStream startFis = null;
    		int startFileSize = 0;
    		try {
    			startFis = new FileInputStream(startVideo);
    			startFileSize = startFis.read(startBuf);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		*/
    	
    		try
    		{
    			Socket socket = null;
    			int temp;
    			try {
    				ServerSocket ss = new ServerSocket(prefetchPort);
    				while ((socket = ss.accept()) != null) {
    					int sendBufSize = socket.getSendBufferSize();
    					socket.setSendBufferSize(65536);
    					System.out.println(Integer.toString(sendBufSize));
//    					socket.setSendBufferSize(65536);
    					InputStream is = socket.getInputStream();
    					OutputStream os = socket.getOutputStream();

    					// To open up the tcp window
    					// Step 1: Read the request from the client
//    					is.read(buf);
//    					for (int i = 0; i < buf.toString().length() - 1; i++) fid = fid * 10 + (buf[i] - '0');
//
//    					// Step 2: Send the video file to the client
//    					// The first packet to announce the file name and the file size
//    					buf[0] = 'S';
//    					buf[1] = 'J';
//    					temp = fid;
//    					for (int i = 7; i > 1; i--) {
//    						buf[i] = (byte) (char) ((temp % 10) + '0');
//    						temp /= 10;
//    					}
//    					temp = startFileSize;
//    					for (int i = 13; i > 7; i--) {
//    						buf[i] =  (byte) (char) ((temp % 10) + '0');
//    						temp /= 10;
//    					}
//    					for (int i = 19; i > 14; i--) {
//    						buf[i] = 0;
//    					}
//
//    					os.write(buf, 0, 20);
//    					os.write(startBuf, 0, startFileSize);
//    					os.flush();
    					
    					while (true) {
    						fid = 0;

    						// Step 1: Read the request from the client
    						is.read(buf);
    						System.out.println("\n Received Buffer is = " + buf.toString());
    						System.out.println("\n Length of the input buffer received is = " + (buf.toString().length()-1));
    						for (int i = 0; i < buf.toString().length() - 1; i++) fid = fid * 10 + (buf[i] - '0');
    						
    						System.out.println("Actual fid: " + Integer.toString(fid));
    						Long start = System.currentTimeMillis();
    						// Step 2: Send the video file to the client
    						// The first packet to announce the file name and the file size
    						if(fid<0)fid = 0; //to tackle the negative fids
    						
    						System.out.println("Still fid: " + Integer.toString(fid));
    						/*
    						buf[0] = 'S';
    						buf[1] = 'J';
    						temp = fid;
    						for (int i = 7; i > 1; i--) {
    							buf[i] = (byte) (char) ((temp % 10) + '0');
    							temp /= 10;
    						}
    						temp = fileSize;
    						for (int i = 13; i > 7; i--) {
    							buf[i] =  (byte) (char) ((temp % 10) + '0');
    							temp /= 10;
    						}
    						for (int i = 19; i > 14; i--) {
    							buf[i] = 0;
    						}
						
    						//os.write(buf, 0, 20);
    						*/
    						
    						os.write(videoBuf, 0, fileSize);
    						os.flush();
    						
    						System.out.println("fid: " + Integer.toString(fid) + "; Total Elapsed time: " + Long.toString(System.currentTimeMillis() - start));
    						//out.println(Long.toString(System.currentTimeMillis()) + ": " + Integer.toString(fileSize));	
    						//out.flush();
    					}
    				}
    			} catch (Exception e) {

    			}
    		} 
		
		catch (Exception e) {
    			//e.printStackTrace();
    		}
		
		

    	}

    	public void start() {
    		tcpThread = new Thread(this, threadFlag);
    		tcpThread.start();
    	}
    }
}
