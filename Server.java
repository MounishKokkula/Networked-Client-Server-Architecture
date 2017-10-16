import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Server {

	public final static int SOCKET_PORT = 6664;
	static String ServerFileLocation = "C:/Users/Mounish/Documents/books/ComputerNetworks/ServerMusic";
	static String sync = "";
	static FileInputStream fis = null;
	static BufferedInputStream bis = null;
	// OutputStream os = null;
	static ServerSocket servsock = null;
	static Socket sock = null;
	static DataOutputStream out = null; // new
	// DataOutputStream(clientSocket.getOutputStream());
	static DataInputStream in = null; // new
	// DataInputStream(clientSocket.getInputStream());

	// FILE_TO_SEND +="a.mpeg";
    public Server(Socket clientSocket) {
        this.sock = clientSocket;
    }

	
		
	
//	public final static int SOCKET_PORT = 6664;
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
				
				//For every client starting a separate thread
		servsock = new ServerSocket(SOCKET_PORT);
		while (true) {
			System.out.println("Waiting for request...");

	 
			 
			ClientWorker w;
			 
			try{
			 
			//server.accept returns a client connection
			 
			w = new ClientWorker(servsock.accept());
			 
			Thread t = new Thread(w);
			 
			t.start();
			 
			}catch(IOException e){
			 
			System.out.println("Accept failed: 4444");
			 
			System.exit(-1);
			 
			}
			}
		
	}	}
	

