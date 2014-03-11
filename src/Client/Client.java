package Client;
import java.io.*;

// Declare the client class for the network testing.
public class Client {

	// Main method, start point for the program.
	public static void main(String[] args) throws IOException
	{
		LoginScreen login = new LoginScreen(); // creates a new login screen
		login.setSize(405,160);
		login.setEnabled(true);
		login.setVisible(true);
		
		if(!login.isEnabled()) //while the login screen is active wait...
		{
			login.dispose();
			ClientUI client = new ClientUI(login.getClientCommandStream(),login.getServerReplyStream(), login.getName());
			client.setSize(790, 500);
			client.setVisible(true); //creates a new client user interface with the calendar
		}		
	}
}
