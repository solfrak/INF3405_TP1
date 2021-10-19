import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class clientHandler extends Thread {
	private Socket socket;
	private int clientNumber;
	private DataOutputStream out;
	private DataInputStream in;
	private String clientDirecPath;
	private ObjectOutputStream objOut;
	private ObjectInputStream objIn;
	private File file;


	public clientHandler(Socket socket, int clientNumber) throws URISyntaxException
	{
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client #" + clientNumber + " at " + socket);
		clientDirecPath = server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		clientDirecPath += "ServerRoot/";
		file = new File(clientDirecPath);
	}

	public String lsCommand() 
	{
		String directory = "";
		for(int i =0; i < file.list().length; i++)
		{
			
			directory += "\t" +file.list()[i] + "\n";
		}
		return directory;
	}

	public boolean cdCommand(String folder) throws IOException
	{
		if(folder.equals("..")) {
			String[] pathSplStrings = clientDirecPath.split("/");
			clientDirecPath = "";
			
			if(!pathSplStrings[pathSplStrings.length - 1].equals("ServerRoot"))
			{
				for(int i = 0; i < pathSplStrings.length - 1; i++)
				{
					clientDirecPath += pathSplStrings[i] + "/";
				}
				file = null;
				file = new File(clientDirecPath);
			}
			return true;
		}
		else{
			File tempFile = file;
			//pour libérer la mémoire allouer précédement
			file = null;
			clientDirecPath += folder + "/";
			file = new File(clientDirecPath);
			if(!file.exists())
			{
				file = null;
				file = tempFile;
				String message = "Erreur: aucun dossier du nom de: " + folder + " existe";
				out.writeUTF(message);
				return false;
			}
			return true;
		}
	}
	public boolean mkdirCommand(String name)
	{
		file = null;
		String tempPath = clientDirecPath + name + "/";
		file = new File(tempPath);
		boolean ans = file.mkdir();
		file = null;
		file = new File(clientDirecPath);
		return ans;
		
	}

	public boolean deleteCommand(String name)
	{
		file = null;
		String tempPath = clientDirecPath + name + "/";
		file = new File(tempPath);
		boolean ans = file.delete();
		file = null;
		file = new File(clientDirecPath);
		return ans;
	}

	public boolean uploadCommand(String namefile, String filesize) throws IOException
	{
		int fileS = Integer.parseInt(filesize);
		FileOutputStream fos = new FileOutputStream(clientDirecPath + namefile);
		byte[] buffer = new byte[4096];
		int read = 0;
		int remaining = fileS;
		while((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			remaining -= read;
			fos.write(buffer, 0, read);
		}
		fos.close();
		return true;
	}
	
	public void downloadCommand(String namefile, boolean isZip) throws IOException
	{
		FileInputStream fis = new FileInputStream(clientDirecPath + namefile);
		File tempFile = new File(clientDirecPath + namefile);
		int size = (int)tempFile.length();
		out.write(size);
		byte[] buffer = new byte[4096];
		while(fis.read(buffer) > 0)
        {
            out.write(buffer);
        }
		fis.close();
	}
	
	public void run()
	{
		try
		{
			out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
			out.writeUTF("La connection avec le serveur est établie");
            objOut = new ObjectOutputStream(out);
            objIn = new ObjectInputStream(in);
            String message = "";
            while(true)
            {
                try {
                	System.out.println("waiting for commande");
                	commande c = (commande) objIn.readObject();                		
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-DD @ HH:mm:ss");
					String date = LocalDateTime.now().format(dtf).toString();
					String logMessage = "[" + socket.getInetAddress().toString().replaceAll("/", "") +":" + socket.getPort() + "//" + date + "] ";
					System.out.println(logMessage + c.toString());
					switch (c.action) {
						case "ls": 
						{
							String answer = lsCommand();
							out.writeUTF(answer);
						}
							break;
						case "cd": 
						{
							if(cdCommand(c.parameter))
							{
								out.writeUTF("Cd done correctly");
							}
						}
							break;
						case "mkdir":
							if(mkdirCommand(c.parameter))
							{
								out.writeUTF("GOOD");
							}
							else
							{
								out.writeUTF("NO GOOD");
							}
							break;
						case "delete":
							if(deleteCommand(c.parameter))
							{
								out.writeUTF("GOOD");
							}
							else 
							{
								out.writeUTF("NO GOOD");
							}
							break;
						case "upload":
							if(uploadCommand(c.parameter, c.option))
							{
								out.writeUTF("good");								
							}
							break;
						case "download":
							break;
						default:
							out.writeUTF("commande non reconnue");
							break;
					}					
				} catch (Exception e) {
					//TODO: handle exception
					System.out.println(e);
				}
            }
		} catch (IOException e) {
			System.out.println("Error handling client #" + clientNumber + ": " + e);
			
		}
		finally {
			try
			{
				socket.close();
			}
			catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");
			}
			System.out.println("Connection with client #" + clientNumber + " closed");
		}
	}

  
}
