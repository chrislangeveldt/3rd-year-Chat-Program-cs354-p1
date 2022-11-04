import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 * Main class for clients and gui
 * 
 * Compiling in terminal: javac ClientGUI.java
 * Usage in terminal:     java ClientGUI
 */
public class ClientGUI implements ActionListener {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String username;
    private String msg="";
    private JFrame frame;

    private JTextArea  enteredText;
    private JTextField typedText;
    private DefaultListModel<String> listModel;
    private JList<String> usersList;
    static ClientGUI client; 

    
    /** 
     * Performs actions regarding the GUI
     * 
     * @param e for the action performed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //get and send text from typedText.getText()
        msg=typedText.getText();
        
        client.sendMessage(msg);

        typedText.setText("");
        typedText.requestFocusInWindow();
        
    }
    
    /** 
     * Constructor forPerforms actions regarding the GUI
     * 
     * @param e for the action performed
     */
    public ClientGUI(Socket socket, String username) {
        frame = new JFrame();

        JButton btn = new JButton("send");
        btn.addActionListener(this);
        
        enteredText = new JTextArea(10, 32);
        typedText   = new JTextField(32);
      
        listModel = new DefaultListModel<String>();
        listModel.addElement("Online Users:");

        usersList = new JList<String>(listModel);
        
        enteredText.setEditable(false);
        usersList.setFocusable(false);
        enteredText.setBackground(Color.LIGHT_GRAY);
        typedText.addActionListener(this);

        Container content = frame.getContentPane();
        content.add(new JScrollPane(enteredText), BorderLayout.CENTER);
        content.add(typedText, BorderLayout.SOUTH);
        content.add(usersList, BorderLayout.EAST);
        typedText.requestFocusInWindow();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        try {
            this.socket = socket;
            
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            
            this.username = username;
        } catch (IOException e) {
            closeEverything();
        }
    }

    
    /** 
     * Handles messages typed in input area
     * 
     * @param msg the string type in input
     */
    public void sendMessage(String msg) {
        try{
            if(socket.isConnected()) {
                String textToSend = msg;
                Message messageToSend=  null;
                if (textToSend.equals("\\exit")) {
                    closeEverything();
                    System.exit(0);
                } else if (textToSend.startsWith("@")) { // Whisper
                    String str[] = textToSend.split(" ", 2);
                    String clientTo = str[0].substring(1);
                    textToSend = str[1];
                    messageToSend = new Message(textToSend, username, clientTo);
                } else {
                    messageToSend = new Message(textToSend, username);
                }
                objectOutputStream.writeObject(messageToSend);
                objectOutputStream.flush();
            }

        }  catch (IOException e) {
            closeEverything();
        }
    }

    /** 
     * Creates the thread that listens for messages
     */
    public void listenForMessage() {
        ClientListenerThread clientListenerThread = new ClientListenerThread(socket, objectInputStream, objectOutputStream, enteredText, listModel, frame);
        Thread thread = new Thread(clientListenerThread);
        thread.start(); //waiting for broadcasted msgs
    }

     
    
    /** 
     * Closes socket and streams neatly
     */
    public void closeEverything() {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
            }

            if (objectOutputStream != null) {
                objectOutputStream.close();
            }

            if (socket != null) {
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    
    /** 
     * Starts up client and prompts for info
     * 
     * @param args not used
     * @throws IOException for creating client and socket
     */
    public static void main(String[] args) throws IOException {
        
        Socket socket= null;
        String username;
        while (true) {
            String ip = JOptionPane.showInputDialog("Enter the IP address: ", "localhost");
            if (ip == null) {
                System.exit(0);
            }
            String port = JOptionPane.showInputDialog("Enter the port number: ", "1234");
            if (port == null) {
                System.exit(0);
            }
            username = JOptionPane.showInputDialog("Enter your unique username: ");
            if (username == null) {
                System.exit(0);
            }
            while (username.isBlank() || !username.matches("^[0-9A-Za-z]*$") || username.equals("SERVER")) {
                username = JOptionPane.showInputDialog("Enter your unique username: ", "Illegal Username!");
                if (username == null) {
                    System.exit(0);
                }
            }
            Timeout timeout = new Timeout();
            Thread t = new Thread(timeout);
            try {
                t.start();
                socket = new Socket(ip, Integer.parseInt(port));
                t.interrupt();
            } catch (Exception e) {
                t.interrupt();
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame, "Invalid IP address or invalid port number");
                continue;
            }
            break;
        }
        client = new ClientGUI(socket, username);
        
        client.objectOutputStream.writeObject(new Message(username, username));
        client.objectOutputStream.flush();
        
        client.listenForMessage();
    }
}

