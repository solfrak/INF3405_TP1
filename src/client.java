import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.*;

public class client {
	private static Socket socket;

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

	public static void main(String[] args) throws Exception
	{    
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
        
        boolean isConnected = false;
        while(!isConnected)
        {
            try
            {
                System.out.println("Entrer l'adresse IP et le port d'ecoute: ");
                String[] userInput = br.readLine().split(" ");
                String serverAddress = userInput[0];
		        int port = Integer.parseInt(userInput[1]);
                socket = new Socket(serverAddress, port);
                isConnected = true;	
            }
            catch (Exception e) {
                System.out.println("La connection avec le serveur à échouer. Veuillez-réessayer.");
            }
        }
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String helloMessageFromServer = in.readUTF();
        ObjectInputStream objIn = new ObjectInputStream(in);
        ObjectOutputStream objOut = new ObjectOutputStream(out);
		System.out.println(helloMessageFromServer);
        boolean test = true;
        String messageToSend = "";

		while(test)
        {
            messageToSend = br.readLine();
            commande c = processMessage(messageToSend);
            objOut.writeObject(c);
            objOut.flush();
            System.out.println(in.readUTF());
        }

		socket.close();
	}

    
}