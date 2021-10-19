import java.net.Socket;
import java.io.*;

public class client {
	private static Socket socket;
    static DataOutputStream out;
    static ObjectOutputStream objOut;

	public static commande processMessage(String message)
    {
        String[] envoieSetting = message.split(" ");
        switch(envoieSetting.length)
        {
            case 1: return new commande(envoieSetting[0]);
            case 2: return new commande(envoieSetting[0], envoieSetting[1]);
            case 3: return new commande(envoieSetting[0], envoieSetting[1], envoieSetting[2]);
        }
        return new commande("");
    }

    public static void uploadCommand(String filename) throws IOException
    {
        
        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[4096];
        while(fis.read(buffer) > 0)
        {
            out.write(buffer);
        }
        fis.close();
    }
	public static void main(String[] args) throws Exception
	{    
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
        
        boolean isConnected = false;
        while(!isConnected)
        {
            try
            {
                System.out.println("Entrer l'adresse IP du serveur: ");
                String serverAddress = br.readLine();
                System.out.println("Entrer le port du serveur: ");
                int port = Integer.parseInt(br.readLine());
                socket = new Socket(serverAddress, port);
                isConnected = true;	
            }
            catch (Exception e) {
                System.out.println("La connection avec le serveur a echouer. Veuillez-reessayer.");
            }
        }
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		String helloMessageFromServer = in.readUTF();
        ObjectInputStream objIn = new ObjectInputStream(in);
        objOut = new ObjectOutputStream(out);
		System.out.println(helloMessageFromServer);
        boolean test = true;
        String messageToSend = "";

		while(test)
        {
            messageToSend = br.readLine();
            commande c = processMessage(messageToSend);
            if(c.action.equals("upload"))
            {
                File file = new File(c.parameter);
                c.option = String.valueOf(file.length());
                objOut.writeObject(c);
                uploadCommand(c.parameter);
            }
            else if(c.action.equals("download")){
            	int size = in.read();
            	FileOutputStream fos = new FileOutputStream(c.parameter);
            	byte[] buffer = new byte[4096];
        		int read = 0;
        		int remaining = size;
        		while((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
        			remaining -= read;
        			fos.write(buffer, 0, read);
        		}
        		fos.close();
            }
            objOut.writeObject(c);
            objOut.flush();
            System.out.println(in.readUTF());
        }

		socket.close();
	}

    
}