/*
 * Created by JFormDesigner on Sat Feb 19 11:04:46 EST 2011
 */

package Client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Drem Darios
 */
public class LoginScreen extends JDialog implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Socket sock;
	private int port;
	private String ipAddress;
	private HashMap<String,char[]> userList;
	protected ObjectOutputStream clientCommandStream;
	protected ObjectInputStream serverReplyStream;
	
	//default constuctor of the login screen initializes the screen
	public LoginScreen() {
		super(new JFrame(), "Event Window", true);
		initComponents();
	}
	
	// once the connect button is pressed the connect user procedure is called
	private void connectBtnMouseReleased(MouseEvent e) {
		connectUser();	
	}
	
	
	private void connectUser() {
		
		String username = usernameTF.getText();
		char[] password = passwordPF.getPassword();
		
		setIP(ipTF.getText());
		setPort(portTF.getText());
		
		ipAddress = getIP();
		port = getPort();
		 
		if(userList.containsKey(username))
		{
			char[] hashedPass = userList.get(username);
			if(Arrays.equals(hashedPass, password))
			{
				try {
					sock = new Socket(ipAddress, port);
					sock.setKeepAlive(true);
					setSocket(sock);
					System.out.println("User: " + username + " Password: " + new String(password));
					setName(username); //set the component name to the username so it can be grabed in the client and used in the server
					setEnabled(false);
					initStreams();
					dispose();
				}catch (IOException ex) {
					System.out.println("Problem establishing connection to server");
					invalidLabel.setText("Problem Connecting to server!");
				}												
			}
			else
				invalidLabel.setText("Invalid Username/Password!");
		}
		
		else
			invalidLabel.setText("Invalid Username/Password!");
			invalidLabel.setVisible(true);
		
	}

	private void initStreams() throws IOException {
		
		clientCommandStream = new ObjectOutputStream(sock.getOutputStream());
		clientCommandStream.flush(); //flush the header for inputstream will not block
			
			serverReplyStream = new ObjectInputStream(sock.getInputStream());
			try {	
			
				String serverMsg = (String) serverReplyStream.readObject();
				System.out.println("Server: "+serverMsg);
			
			} catch (ClassNotFoundException e) {
				System.out.println("Something went wrong reading server message!");
			}
		
	
	}
	
	
	public ObjectOutputStream getClientCommandStream() {
		return clientCommandStream;
	}

	public void setClientCommandStream(ObjectOutputStream clientCommandStream) {
		this.clientCommandStream = clientCommandStream;
	}

	public ObjectInputStream getServerReplyStream() {
		return serverReplyStream;
	}

	public void setServerReplyStream(ObjectInputStream serverReplyStream) {
		this.serverReplyStream = serverReplyStream;
	}
	
	public void setPort(String portString)
	{
		this.port = Integer.parseInt(portString);
	}
	public int getPort()
	{
		return port;
	}
	
	public void setIP(String ip)
	{			
		this.ipAddress = ip;
	}
	
	public String getIP()
	{
		return this.ipAddress;
	}
	
	public void setSocket(Socket socket)
	{
		this.sock = socket;
	}
	
	public Socket getSocket()
	{
		return this.sock;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ipLabel = new JLabel();
		ipTF = new JTextField();
		portLabel = new JLabel();
		portTF = new JTextField();
		usernameLabel = new JLabel();
		usernameTF = new JTextField();
		passwordLabel = new JLabel();
		passwordPF = new JPasswordField();
		invalidLabel = new JLabel();
		connectBtn = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle("Login");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"19dlu, $lcgap, 12dlu, $lcgap, 41dlu, $lcgap, 62dlu, $lcgap, 21dlu, $lcgap, 17dlu",
			"21dlu, 3*($lgap, default)"));

		//---- ipLabel ----
		ipLabel.setText("IP Address:");
		contentPane.add(ipLabel, cc.xy(5, 1));

		//---- ipTF ----
		ipTF.setText("127.0.0.1");
		ipTF.setEditable(false);
		ipTF.setToolTipText("Enter your IP");
		contentPane.add(ipTF, cc.xy(7, 1));

		//---- portLabel ----
		portLabel.setText("Port:");
		contentPane.add(portLabel, cc.xywh(9, 1, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- portTF ----
		portTF.setText("8080");
		portTF.setHorizontalAlignment(SwingConstants.RIGHT);
		portTF.setToolTipText("Enter port number");
		contentPane.add(portTF, cc.xywh(11, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT));

		//---- usernameLabel ----
		usernameLabel.setText("Username");
		contentPane.add(usernameLabel, cc.xywh(1, 3, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- usernameTF ----
		usernameTF.setText("ddarios");
		usernameTF.setToolTipText("Enter username");
		usernameTF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				LoginScreen.this.keyPressed(e);
			}
		});
		contentPane.add(usernameTF, cc.xywh(5, 3, 3, 1));

		//---- passwordLabel ----
		passwordLabel.setText("Password");
		contentPane.add(passwordLabel, cc.xywh(1, 5, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		//---- passwordPF ----
		passwordPF.setText("password");
		passwordPF.setEchoChar('*');
		passwordPF.setToolTipText("Enter password here");
		passwordPF.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				LoginScreen.this.keyPressed(e);
			}
		});
		contentPane.add(passwordPF, cc.xywh(5, 5, 3, 1));

		//---- invalidLabel ----
		invalidLabel.setForeground(Color.red);
		invalidLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		invalidLabel.setText("Problem connecting to server!");
		invalidLabel.setVisible(false);
		contentPane.add(invalidLabel, cc.xywh(3, 7, 7, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));

		//---- connectBtn ----
		connectBtn.setText("GO!");
		connectBtn.setToolTipText("Connect to server");
		connectBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				connectBtnMouseReleased(e);
			}
		});
		contentPane.add(connectBtn, cc.xywh(9, 7, 3, 1));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
		
		userList = getUserList(); // grabs the list of users and passwords for the system
	}
	
	/**
	 * This method opens up the client/password file. This file is stored locally for a number of reasons
	 * Since all clients created in this system will be created from my computer, the password list holds all
	 * clients and passwords without querying the server. If this was a practical system, each host computer would have 
	 * their own client(s) with respective password(s) (the idea is just like if you try to log onto my computer you will have to use my username and
	 * password but if you log into your own system you will use your own username and password). This doesn't interfere with the functionality
	 * of the program, it just makes it so the server doesn't check passwords, the client does.
	 * 
	 */
	private HashMap<String, char[]> getUserList() {
		HashMap<String, char[]> userList = new HashMap<String, char[]>();
		
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("passwords.properties"); //open new file stream for the client/password file			
			Scanner in = new Scanner(fstream);
			while(in.hasNext()) //read in all names and passwords
			{
				String username = in.next();
				String passwordString = in.next();
				char[] passwordChar = passwordString.toCharArray();
				
				userList.put(username, passwordChar); //add username/password to hashmap
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File was not found!");
		}
		 
		return userList;
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel ipLabel;
	private JTextField ipTF;
	private JLabel portLabel;
	private JTextField portTF;
	private JLabel usernameLabel;
	private JTextField usernameTF;
	private JLabel passwordLabel;
	private JPasswordField passwordPF;
	private JLabel invalidLabel;
	private JButton connectBtn;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//I added this method to call the connect user if the enter button is pressed
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
	     if (key == KeyEvent.VK_ENTER) {
	 		connectUser(); 
	        }
	}
}
