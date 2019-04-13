//package netlab.furion.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer_Previous {

	
	static String videoPath = "E:/CubeMaps/viking_texas/1.mp4"; //"/Users/spaul/Desktop/viking_final.mp4";";//"F:/SharedDisk/x264/x264/sunlh_test_encode.mkv";//"F:/FurionSystem/CuiyongLab/Furion/Media/sunlh_png.mp4";
	public static void __main(String[] args)

	{

		Socket socket = null;
		try {
			ServerSocket ss = new ServerSocket(5000);

			while ((socket = ss.accept()) != null) {
				try {
					System.out.println("New request in.");
					byte[] buffer = new byte[409600];
					int account = 0;
					File video = new File(videoPath);
					System.out.println("\n the videofile size is = " + video.length());
					FileInputStream fis = new FileInputStream(video);
					int sendBufSize = socket.getSendBufferSize();
					System.out.println("\n Send Buffer size = " + Integer.toString(sendBufSize));
					while ((account = fis.read(buffer)) != -1) {
						System.out.println("\n the current buffer size is = " + buffer.length);
						socket.getOutputStream().write(buffer, 0, account);
					}
					socket.getOutputStream().flush();
					socket.getOutputStream().close();
					System.out.println("Video transmission finished.");
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
