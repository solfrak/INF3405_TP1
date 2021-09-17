import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class clientHandler extends Thread {
	private Socket socket;
	private int clientNumber;
	private String clientDirecPath = "C:/";
	public clientHandler(Socket socket, int clientNumber)
	{
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client #" + clientNumber + " at " + socket);
	}

	public String lsCommand() 
	{
		File f = new File(clientDirecPath);
		String directory = "";
		for(int i =0; i < f.list().length; i++)
		{
			directory += f.list()[i] + "\n";
		}
		return directory;
	}

	public void cdCommand(String folder)
	{
		if(folder =="..")
		{

		}
		else{
			clientDirecPath += "/" + folder;
		}
	}
	public void run()
	{
		try
		{
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
			out.writeUTF("Hello from server = you are client #" + clientNumber);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            ObjectInputStream objIn = new ObjectInputStream(in);
            String message = "";
            while(true)
            {
                try {
                    commande c = (commande) objIn.readObject();
					switch (c.action) {
						case "ls": {
							String answer = lsCommand();
							out.writeUTF(answer);
						}
							break;
						case "cd": {
							cdCommand(c.parameter);
							out.writeUTF("Cd done correctly");
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

                    System.out.println("Client send commande: " + c.action + ", " + c.parameter);
					
				} catch (Exception e) {
					//TODO: handle exception
				}
                    // message = in.readUTF();
                    // System.out.println("Client say: " + message);
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
