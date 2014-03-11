package Client;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.*;
import java.beans.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import javax.swing.event.*;
import com.jgoodies.forms.layout.*;
import com.toedter.calendar.*;
/*
 * Created by JFormDesigner on Wed Jan 19 00:04:23 EST 2011
 */


/**
 * @author Drem Darios
 */
public class ClientUI extends JFrame implements ActionListener {
	
	protected Socket sock;
	protected String user;
	protected ObjectOutputStream clientCommand;
	protected ObjectInputStream serverReply;
	protected DefaultListModel eventListModel = new DefaultListModel();
	protected List<String> userList  = new ArrayList<String>();


	
	//empty constuctor for ClientUI
	public ClientUI() {
		
	}
	
	public ClientUI(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream, String user) throws IOException {			
		setUser(user); //sets the name of the user using the system
		setClientCommand(objectOutputStream); // takes in the input and output streams created at login
		setServerReply(objectInputStream);
		
		loadUser(user); //loads user info and calendar from server					
	}


		private void loadUser(String user) throws IOException{

			try {
				clientCommand.writeObject("GET");//Sends request to get an object
				clientCommand.flush();
				
				System.out.println("Server: " +(String) serverReply.readObject());
				
				clientCommand.writeObject("USERCAL");//Then tells the server it is the user's calendar we want
				clientCommand.flush();
				
				System.out.println("Server: " +serverReply.readObject().toString());
				
				clientCommand.writeObject(user); //sends the username of the user that we want the calendar to be loaded for
				clientCommand.flush();
				
					JCalendar calendar = (JCalendar) serverReply.readObject();
					//calendar.setUser(user);
					//calendar.setClientCommand(clientCommand);
					//calendar.setServerReply(serverReply);
				
				setUserCalendar(calendar);	//set the user calendar
					
				System.out.println("Calendar Sent for user: " + user);	
				System.out.println((String)serverReply.readObject());
				
				clientCommand.writeObject("GET"); //gets a list of users in the community from the server. 
				
				clientCommand.flush();
				
				System.out.println("Server: "+(String) serverReply.readObject());
				//Since I am implementing this as a "stand-alone", the community members list needs to be populated by the server
				clientCommand.writeObject("USERS");
				clientCommand.flush();
											
				this.userList = (List<String>)serverReply.readObject();
					
				System.out.println("Server: " + (String)serverReply.readObject());
				System.out.println((String)serverReply.readObject());
				
				initComponents(calendar, userList); //initializes the gui
				
			} catch (ClassNotFoundException e) {
				System.out.println("Custom Calendar Class not found!");
			}
		}

	//getters and setters	
	private void setUserCalendar(JCalendar calendar) {
			this.userCal = calendar;			
	}

	public ObjectOutputStream getClientCommand() {
		return clientCommand;
	}

	public void setClientCommand(ObjectOutputStream clientCommand) {
		this.clientCommand = clientCommand;
	}

	public ObjectInputStream getServerReply() {
		return serverReply;
	}

	public void setServerReply(ObjectInputStream serverReply) {
		this.serverReply = serverReply;
	}

	private void userCalPropertyChange(PropertyChangeEvent e) {				
		setEventList(getDailyEvents());				
	}

	private void eventListMouseClicked(MouseEvent e) throws IOException {
		 
		int index = eventList.locationToIndex(e.getPoint());
	    ListModel dlm = eventList.getModel();
	    Object item = dlm.getElementAt(index);
	    eventList.ensureIndexIsVisible(index);
	     
		Date date = userCal.getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		
		String event = (String)item;				
		String[] eventStart = event.split(":");
		HashMap<String, Event> userEventList = getDailyEvents();
			
			if(!(userEventList==null))
			{
	
					Appointment appt = new Appointment(ClientUI.this, sdf.format(date), user, clientCommand, serverReply, userEventList.get(eventStart[0]+":"+eventStart[1]), userList);
					appt.setSize(430,305);
					appt.setEnabled(true);
					appt.setVisible(true);
			}
			
			else
			{
				Appointment appt = new Appointment(ClientUI.this, sdf.format(date), user, clientCommand, serverReply, userList);
				appt.setSize(430,305);
				appt.setEnabled(true);
				appt.setVisible(true);
			}
			
			System.out.println("About to get events");			
			setEventList(getDailyEvents());
			
	}
	

	private void eventListKeyPressed(KeyEvent e) {		
		//write event list to sever
		System.out.println("Delete Selected!");
		
	}

	private void initComponents(JCalendar calendar, List<String> userList) {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		mainMenuBar = new JMenuBar();
		menu1 = new JMenu();
		createEventMenu = new JMenuItem();
		requestMenu = new JMenuItem();
		communMenu = new JMenu();
		memberList = new JMenu();
		panel1 = new JPanel();
		dateLabel = new JLabel();
		scrollPane2 = new JScrollPane();
		eventList = new JList(eventListModel);
		userCal = new JCalendar();
		userCal.setCalendar(calendar.getCalendar());
		CellConstraints cc = new CellConstraints();

		//======== this ========
		this.setTitle("Welcome " + user + "!");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new CardLayout());

		//======== mainMenuBar ========
		{

			//======== menu1 ========
			{
				menu1.setText("Schedule");
				menu1.setForeground(Color.blue);
				menu1.setName("scheduleMenu");

				//---- createEventMenu ----
				createEventMenu.setText("Create an event");
				createEventMenu.setName("@@createEventMenu");
				createEventMenu.addActionListener(this);
				menu1.add(createEventMenu);

				//---- requestMenu ----
				requestMenu.setText("View requests");
				requestMenu.setName("@@requestMenu");
				requestMenu.addActionListener(this);
				menu1.add(requestMenu);
			}
			mainMenuBar.add(menu1);

			//======== communMenu ========
			{
				communMenu.setText("Community");
				communMenu.setForeground(Color.blue);
				communMenu.setName("communityMenu");

				//======== memberList ========
				{
					for(String users : userList)
										{
											JMenuItem temp = new JMenuItem();
											temp.setText(users);
											temp.setName(users); //set the name of the menu item to be used later
											if(user.equals(users)) // if the user who is logged in then they can  not select their own calendar
												temp.setEnabled(false);
											temp.addActionListener(this);
											memberList.add(temp);
										}
					memberList.setText("View Member Calendar");
					memberList.setName("memberListMenu");
				}
				communMenu.add(memberList);
			}
			mainMenuBar.add(communMenu);
		}
		setJMenuBar(mainMenuBar);

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"5dlu, $lcgap, 116dlu, $lcgap, 15dlu, $lcgap, 240dlu",
				"default, $lgap, 231dlu, $lgap, 24dlu, $lgap, 21dlu"));

			//---- dateLabel ----
			dateLabel.setText("text");
			panel1.add(dateLabel, cc.xywh(3, 1, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

			//======== scrollPane2 ========
			{
				scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

				//---- eventList ----
				eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				eventList.addKeyListener(new KeyAdapter() {
									@Override
									public void keyPressed(KeyEvent e) {
										if(e.getKeyCode() == e.VK_DELETE)
											eventListKeyPressed(e);
									}
								});

				eventList.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {

										 if(e.getClickCount() == 2)
										 {
											 try {
													eventListMouseClicked(e);
												 } catch (IOException e1) {										
														e1.printStackTrace();
												 }
										 }

									}
								});
				scrollPane2.setViewportView(eventList);
			}
			panel1.add(scrollPane2, cc.xywh(3, 3, 1, 3));

			//---- userCal ----
			userCal.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					userCalPropertyChange(e);
				}
			});
			panel1.add(userCal, cc.xywh(7, 3, 1, 5));
		}
		contentPane.add(panel1, "card1");
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenuBar mainMenuBar;
	private JMenu menu1;
	private JMenuItem createEventMenu;
	private JMenuItem requestMenu;
	private JMenu communMenu;
	private JMenu memberList;
	private JPanel panel1;
	private JLabel dateLabel;
	private JScrollPane scrollPane2;
	private JList eventList;
	private JCalendar userCal;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	@Override
	public void actionPerformed(ActionEvent event) {
	//once the user selects a member from the member list to view, the sever is queried to return a copy of the selected user's calendar for viewing	
		JMenuItem menuItem = (JMenuItem)event.getSource();
		
		
		if(menuItem.getName().equals("@@createEventMenu"))
		{
			System.out.println("Create an event selected!");	
			try {
				Date date = userCal.getDate();
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
				Appointment appt = new Appointment(ClientUI.this, sdf.format(date), user, clientCommand, serverReply, userList);
				appt.setSize(430,305);
				appt.setEnabled(true);
				appt.setVisible(true);
				
				System.out.println("About to get events");
				setEventList(getDailyEvents());
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //info is sent to the appointment gui
		}
		
		else if(menuItem.getName().equals("@@requestMenu"))
		{
			RequestViewer rv = new RequestViewer(this, clientCommand, serverReply, user);
			rv.setSize(640, 450);
			rv.setVisible(true);
			
			System.out.println("Request Menu Selected!");
			
			
		}
		else
		{
					
			
			try {
				clientCommand.writeObject("GET");
				clientCommand.flush();
				
				System.out.println("Server: " + (String) serverReply.readObject());
				
				clientCommand.writeObject("CLONE");
				clientCommand.flush();
				
				System.out.println("Server: " + (String) serverReply.readObject());
				
				clientCommand.writeObject(menuItem.getName());
				clientCommand.flush();
				
				List<Event> eventList = (List<Event>) serverReply.readObject();
				
				System.out.println("Server: " + (String) serverReply.readObject());
				System.out.println("Server: " + (String) serverReply.readObject());
				
				ScheduleChooser sc = new ScheduleChooser(eventList, menuItem.getName());
				sc.setSize(255,435);
				sc.setVisible(true);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setEventList(getDailyEvents());
	}

	private HashMap<String, Event> getDailyEvents() {
		HashMap<String, Event> userEL = new HashMap<String, Event>();
		
		try {
			clientCommand.writeObject("GET");//Sends request to get an object
			clientCommand.flush();
			
			System.out.println("Server: " +(String) serverReply.readObject());
			
			clientCommand.writeObject("EVENT");//Then tells the server it is the user's calendar we want
			clientCommand.flush();
			
			System.out.println("Server: " +(String) serverReply.readObject());
			
			clientCommand.writeObject(user);//Then tells the server it is the user's calendar we want
			clientCommand.flush();
			
			System.out.println("Server: " +(String) serverReply.readObject());
			
			Date date = userCal.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			clientCommand.writeObject(user+sdf.format(date));//Then tells the server it is the user's calendar we want
			clientCommand.flush();
			
			userEL = (HashMap<String, Event>) serverReply.readObject();
			System.out.println("Server: " +(String) serverReply.readObject());			
									
		}catch (ClassNotFoundException exp) {
			System.out.println("Class not found!");
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		return userEL;
	}
	
	private void setEventList(HashMap<String, Event> userEL) {
		
		Date date = userCal.getDate();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		dateLabel.setText("******** " + sdf.format(date) + " ********\n");
		
		if(userEL == null)
		{				
			if(eventListModel.isEmpty())
			{
				eventListModel.removeAllElements();
				eventListModel.add(0, "No Events Today");
			}

			else
			{
				eventListModel.removeAllElements();
				eventListModel.add(0, "No Events Today");
			}
			
		}		
		
		else
		{
			String eventComments = new String();				
			eventListModel.removeAllElements();				
			int counter = 12;
			//iterate here and find all dates and times. This can be done with threads so it can be done concurrently
			for(int i = 0; i < 2; i++)
			{
				String zone;
				if(i == 0)
					zone = "AM";
				else
					zone = "PM";
				for(int k = 1; k <= 12; k++)
				{
					
					for(int j = 0; j < 4; j++)
					{
						String min = "";
						if(j == 0)
							min = ":00 ";
						else if(j == 1)
							min = ":15 ";
						else if(j == 2)
							min = ":30 ";
						else if(j==3)
							min = ":45 ";
						
						String hr = Integer.toString(counter);
						String time = hr+min+zone;
																		
						if(userEL.get(time) != null)
						{												
							eventComments = time + ": " + userEL.get(time).comments;
							eventListModel.addElement(eventComments);
						}
						
						else{}
					}
					if(counter == 12)
						counter = 1;
					else
						counter++;
				}		
			}								
		}											
	}	
}
