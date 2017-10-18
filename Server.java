import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Server extends Thread {

	public final static int SOCKET_PORT = 6664;
	static int i = 1;
	static Thread ti;
	static String ServerFileLocation = "*Path*/ServerMusic";
	static String sync = "";
	static FileInputStream fis = null;
	static BufferedInputStream bis = null;
	static ServerSocket servsock = null;
	static Socket sock = null;
	static DataOutputStream out = null;
	static DataInputStream in = null;
 
	public static String fileName = "*Path*\Log.txt";
	public static String line = null;

	public Server(Socket clientSocket) {
		this.sock = clientSocket;
	}

 
	public static void main(String[] args)
			throws IOException, NoSuchAlgorithmException, InterruptedException, IllegalMonitorStateException {

		// For every client starting a separate thread
		servsock = new ServerSocket(SOCKET_PORT); 

		while (true) {

			try {
		 
				System.out.println("Waiting for request...");
				System.out.println("Enter 'Log' View log file\n or");
				System.out.println("Enter any key to connect to the client \n");
				Scanner se = new Scanner(System.in);				
				String input = se.nextLine();
				if (!input.equalsIgnoreCase("Log")) {
				 
					ti = new Thread(new ClientWorker(servsock.accept()));
					ti.start();
					synchronized (ti) {
						ti.wait();
					}
					i++;
				} else {
				FileReader fileReader = new FileReader(fileName);
				
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					while ((line = bufferedReader.readLine()) != null) {
						System.out.println(line);
					}
					fileReader.close();
					bufferedReader.close();
			 
				}
			}
			
				catch (IOException e) {

				System.out.println("Accept failed: 4444");

				System.exit(-1);
			 }
			}
		
	
		}
}
