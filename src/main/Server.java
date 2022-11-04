import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * Class for Server 
 * 
 * Compiling in terminal: javac Server.java
 * Usage in terminal:     java Server <port_number>
 */
public class Server {

    private ServerSocket serverSocket;

    /** 
     * Constructor for ServerSocket
     * @param serverSocket assign the socket to this instance of server
     */
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /** 
     * The method that creates threads for handling each client
     */
    public void startServer() {
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.checkUsername();
                System.out.println("New Client Connected!");

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    /** 
     * Method that closes server socket
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /** 
     * Checks for valid port number
     * @param args should only contain port number
     * @throws IOException regarding serverSocket creation
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port_number>");
            System.exit(0);
        }
        int port = Integer.parseInt(args[0]);
        if (port < 1 || port > 65535) {
            System.out.println("Port number must be between 1 and 65535");
            System.exit(0);
        }

        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.startServer();
        
    }
}
