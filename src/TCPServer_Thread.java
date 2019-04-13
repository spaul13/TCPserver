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



public class TCPServer_Thread {
    public static void main(String[] args) 
    {
    	TcpThread pt = new TcpThread("client_laptop", 5000, "192.168.1.145");
    	pt.start();	
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

    	TcpThread(String threadFlag, int prefetchPort,  String ipAddr) {
    		this.threadFlag = threadFlag;
    		this.prefetchPort = prefetchPort;
    		
    		try {
    			prefetchSocket = new DatagramSocket(prefetchPort);
    			group = InetAddress.getByName(ipAddr);
    		} catch (Exception e) {
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

    					
    					
    					while (true) 
    					{
    						fid = 0;
    						is.read(buf);
    						System.out.println("\n Received Buffer is = " + buf.toString());
    						System.out.println("\n Length of the input buffer received is = " + (buf.toString().length()-1));
    						for (int i = 0; i < buf.toString().length() - 1; i++) fid = fid * 10 + (buf[i] - '0');
    						
    						System.out.println("Actual fid: " + Integer.toString(fid));
    						Long start = System.currentTimeMillis();
    						
    						
    						System.out.println("Still fid: " + Integer.toString(fid));
    						int account = 0;
    						while ((account = fis.read(videoBuf)) != -1) 
    						{
    							System.out.println("\n the current buffer size is = " + videoBuf.length);
    							os.write(videoBuf, 0, account);
    						}
    						os.flush();
    						//os.write(videoBuf, 0, fileSize);
    						//os.flush();
    						System.out.println("fid: " + Integer.toString(fid) + "; Total Elapsed time: " + Long.toString(System.currentTimeMillis() - start));
    						
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
