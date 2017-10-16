import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

public class ClientWorker implements Runnable {

	private Socket client;
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

	public ClientWorker(Socket client) {
		this.client = client;
	}
	// List function to get the list of server files

	public static List list(String directoryName) throws NoSuchAlgorithmException, IOException {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();

		List<String> serverList = new ArrayList<String>();

		for (File file : fList) {
			if (file.isFile()) {
				serverList.add(file.getName() + "/@/" + calSHA(file));
				// add hashed value for each file and concatenate it to the list
			}
		}
		return serverList;
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

	private static void pull(List<String> diffList) throws IOException {
		// TODO Auto-generated method stub
		int i = 0;
		String serverFileName;
		long fileSize;
		try {
			while (i < diffList.size()) {

				System.out.println(diffList.get(i));
				serverFileName = ServerFileLocation + "/" + (String) diffList.get(i);
				// ServerFileLocation += "/" + serverFileName;

				// send file
				File myFile = new File(serverFileName);
				byte[] mybytearray = new byte[(int) myFile.length()];

				try {
					if (out != null && myFile.exists() && myFile.isFile()) {
						FileInputStream input = new FileInputStream(myFile);
						out.writeLong(myFile.length());
						// System.out.println(myFile.getAbsolutePath());
						int read = 0;
						while ((read = input.read()) != -1)
							out.writeByte(read);
						out.flush();
						input.close();
						System.out.println("File successfully sent!");
					}
					i = i + 1;
					/*
					 * fis = new FileInputStream(myFile);
					 * 
					 * bis = new BufferedInputStream(fis); bis.read(mybytearray,
					 * 0, mybytearray.length); // os = sock.getOutputStream();
					 * 
					 * System.out.println("Sending " + (String) diffList.get(i)
					 * + "(" + mybytearray.length + " bytes)");
					 * out.write(mybytearray, 0, mybytearray.length); i = i + 1;
					 */

					// out.flush();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} finally {
			leave();
			System.out.println("Connection Closed!");
		}
	}

	public static void leave() throws IOException {
		System.out.println("Closing the connections ");
		if (fis != null)
			fis.close();
		if (bis != null)
			bis.close();
		if (out != null)
			out.close();
		if (sock != null)
			sock.close();
		if (servsock != null)
			servsock.close();

		// System.exit(1);
	}

	@Override
	public void run() {

		try {

			System.out.println("Accepted connection : " + client);
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());

			if (!in.readUTF().equalsIgnoreCase("No")) {
				List serverList = list(ServerFileLocation);
				System.out.println("List of Files on Server: ");
				for (int j = 0; j < serverList.size(); j++) {

					System.out.println(((String) serverList.get(j)).split("/@/")[0]);

					out.writeUTF((String) serverList.get(j));
					// out.writeUTF(" ");
				}
				out.writeUTF("EOS");
				String inString;
				List<String> diffList = new ArrayList<String>();
				while (true) {
					inString = in.readUTF();
					if (inString.equalsIgnoreCase("EOS")) {
						break;
					} else {
						// System.out.println(inString);
						diffList.add(inString);
					}
				}

				System.out.println("The difference list- \nFiles on Server and not on Client: ");
				System.out.println(diffList);
				pull(diffList);
				// String serverDir =
				// "C:/Users/Mounish/Documents/books/ComputerNetworks/ServerMusic";
				// List serverList = list(ServerFileLocation);

			}

			System.out.println("Done.");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			try {
				leave();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
