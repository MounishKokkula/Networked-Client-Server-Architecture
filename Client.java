import java.io.*;
import java.net.Socket;
import static java.lang.Math.toIntExact;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Client extends Thread {
	private Thread t;
	private String threadName;

	Client(String name) {
		threadName = name;
		System.out.println("Creating " + threadName);
	}

	public static Scanner se = null;
	public static String clientName = "Client1";
	public final static int SOCKET_PORT = 6664;
	public final static String SERVER = "127.0.0.1";
	static String ClientFileLocation = "*Path*/ClientMusic";
	static String sync;
	public final static int FILE_SIZE = 10000000;
	public static FileOutputStream fos = null;
	public static BufferedOutputStream bos = null;
	public static Socket sock = null;
	public static String[] serverParts;
	public static String[] clientParts;
	public static String clientFileName;
	public static String inString;
	public static List<String> diffList = null;

	public static List list(String directoryName) throws NoSuchAlgorithmException, IOException {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		List<String> clientList = new ArrayList<String>();

		for (File file : fList) {
			if (file.isFile()) {
				clientList.add(file.getName() + "/@/" + calSHA(file));
			}
		}
		return clientList;
	}

	public static String calSHA(File filename) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(Files.readAllBytes(filename.toPath()));
		byte[] finalized = md.digest();
		String toSend = "";
		for (byte b : finalized)
			toSend += String.format("%02x", b);
		return toSend;
		// out.writeUTF(toSend);
		// out.flush();
	}

	public static List diff(HashMap<String, String> map, List clientList) {
		int i = 0;
		diffList = new ArrayList<String>();

		// checking each server file for each client file if there hash is equal
		while (i < clientList.size()) {
			String clientfile = (String) clientList.get(i);
			clientParts = clientfile.split("/@/");

			String Cpart1 = clientParts[0];
			String Cpart2 = clientParts[1];

			if (map.containsKey(Cpart2)) {
				map.remove(Cpart2);
			}
			i = i + 1;
		}
		diffList = new ArrayList(map.values());
		return diffList;
	}

	public static void leave() throws IOException {
		if (fos != null)
			fos.close();
		if (bos != null)
			bos.close();
		if (sock != null)
			sock.close();
	}

	public static void main(String[] args) {

		//
		// }
		//
		// public void run(){
		int bytesRead;
		int current;

		DataOutputStream out = null;// new
		// DataOutputStream(clientSocket.getOutputStream());
		DataInputStream in = null; // new
		// DataInputStream(clientSocket.getInputStream());
		se = new Scanner(System.in);
		System.out.println("Enter Client Name to connect to Server. (Enter 'No' to Exit!)");
		clientName = se.nextLine();
		if (!clientName.equalsIgnoreCase("No")) {

			try {
				sock = new Socket(SERVER, SOCKET_PORT);
				System.out.println("Connecting...");

				// send request for files at server
				out = new DataOutputStream(sock.getOutputStream());
				in = new DataInputStream(sock.getInputStream());
				out.writeUTF(clientName);

				System.out.println(
						" \nTo check Files on Server and not on " + clientName + "\nPress any key ('No' to quit !) \n");
				sync = se.nextLine();

				if (!sync.equalsIgnoreCase("No")) {
					out.writeUTF(sync);
				} else {
					System.out.println("Ok Man! As you wish \nEXITING !!!");
					System.exit(1);
				}
				// String inString = in.readUTF();
				// System.out.println(in.readUTF());

				List<String> serverList = new ArrayList<String>();
				HashMap<String, String> map = new HashMap<String, String>();
				while (true) {
					inString = in.readUTF();
					if (inString.equalsIgnoreCase("EOS")) {
						break;
					} else {
						serverParts = inString.split("/@/");
						String fileName = serverParts[0];
						String shaCode = serverParts[1];
						map.put(shaCode, fileName);
						// serverList.add(inString);
						// System.out.println(inString);
					}
				}
				// clientFileName = ClientFileLocation + "/" + inString;

				// Checking the difference between files on client and server
				
				List clientList = list(ClientFileLocation);
				// System.out.println(diff(serverList,clientList));
				// difference between server and client
				List diffList = diff(map, clientList);
				
				// send diff list to server
				for (int j = 0; j < diffList.size(); j++) {
					// System.out.println(diffList.get(j));
					out.writeUTF((String) diffList.get(j));
				}
				out.writeUTF("EOS");

				if (!diffList.isEmpty()){
					System.out.println("Creating Difflist (^_^) \n" );	
				System.out.println("The difference list- \nFiles on Server and not on Client: \n");
				System.out.println(diffList);
				System.out.println("Sending Difflist to Server \n");

				
				System.out.println("\n o(^_-)O  == Syncing files ==  O(-_^)o  \n");

				int d = 0;
				int n = 0;
				while (d < diffList.size()) {
					System.out.println(diffList.get(d).toString());
					clientFileName = ClientFileLocation + "/" + diffList.get(d).toString();
					// System.out.println(clientFileName);
					d += 1;
					current = 0;
					long fileSize = in.readLong();
					if (fileSize != -1) {

						fos = new FileOutputStream(clientFileName);
						byte[] mybytearray = new byte[toIntExact(fileSize)];
						while (fileSize > 0
								&& (n = in.read(mybytearray, 0, (int) Math.min(mybytearray.length, fileSize))) != -1) {
							fos.write(mybytearray, 0, n);
							// current = current + in.read(mybytearray, 0, (int)
							// Math.min(mybytearray.length, fileSize));
							fileSize -= n;
						}

					}
				}
				}else{System.out.println("(^.^) Files already in sync! ");}} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			finally {
				System.out.println("\n Closing all connections \n");
				try {
					leave();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("\n Ok Man! As you wish \n \\(`-^)/ EXITING !!!");
			System.exit(1);
		}
	}
}