import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class server {

    public static void main(String[] args) throws Exception {
        int clientNumber = 0;

        String serverAddress = "10.200.32.154";
        int serverPort = 5030;

        ServerSocket listener = new ServerSocket();
        listener.setReuseAddress(true);

        InetAddress serverIP = InetAddress.getByName(serverAddress);

        listener.bind(new InetSocketAddress(serverIP, serverPort));

        System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
        try {
            while (true) {
                // Create a new clientHandler in a new thread
                new clientHandler(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

}
