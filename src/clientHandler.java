import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class clientHandler extends Thread {
	private Socket socket;
	private int clientNumber;
	private String clientDirecPath = "C:/";
	private DataOutputStream out;
	private DataInputStream in;
	private ObjectOutputStream objOut;
	private ObjectInputStream objIn;
	private File file = new File(clientDirecPath);


	public clientHandler(Socket socket, int clientNumber)
	{
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client #" + clientNumber + " at " + socket);
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
			if(pathSplStrings.length != 1)
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
							break;
						case "delete":
							break;
						case "upload":
							break;
						case "download":
							break;
						default:
							break;
					}					
				} catch (Exception e) {
					//TODO: handle exception
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
