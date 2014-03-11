package Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.toedter.calendar.JCalendar;
import Client.Event;
/**
 * @author Drem Darios
 */
public class WorkerThread extends Server implements Runnable{

	private Socket sock = null;
	private List<String> userList;
	private ObjectOutputStream serverReply = null;
	private ObjectInputStream clientInput = null;
	protected JCalendar calendar;
	private HashMap<String, JCalendar> calendarList = new HashMap<String, JCalendar>();
	protected HashMap<String, ArrayList<Event>> userRequestList = new HashMap<String, ArrayList<Event>>();
	protected HashMap<String, HashMap<String, Event>> eventMap = new HashMap<String, HashMap<String, Event>>();
	
	public WorkerThread(Socket sock, HashMap<String, HashMap<String, Event>> eventMap, HashMap<String, ArrayList<Event>> requestList) {
		this.sock = sock;
		this.eventMap = eventMap;
		this.userRequestList = requestList;
	}

	@Override
	public void run() {
		
		generateUserList();
		
		try {
			serverReply = new ObjectOutputStream(sock.getOutputStream());
			/*flush the stream immediately to ensure that 
			constructors for receiving ObjectInputStreams will 
			not block when reading the header.*/
			serverReply.flush();
			clientInput = new ObjectInputStream(sock.getInputStream());
			
			processCommands(); // method for server to process client commands
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void processCommands() throws IOException{
		
		serverReply.writeObject("*** Waiting for command ***"); //notify the client side that the server is waiting for a command
		serverReply.flush();
		
		try{			
			String command = (String) clientInput.readObject();
			
			if(command.equals("GET")) // if the client wants to get something from the client
			{
				serverReply.writeObject("Ready to recieve object type...");
				serverReply.flush(); //server takes in the object type to perform proper instructions
				
				command = (String) clientInput.readObject();
				if(command.equals("USERCAL")) //if the client wants the user's calendar
				{
					serverReply.writeObject("Waiting for key...");
					serverReply.flush();
					String user = (String) clientInput.readObject(); //take in the user's name
					getUserCalendar(user, serverReply, clientInput); //returns the user's calendar
				}
				else if(command.equals("USERS")) //returns a list of users that are in the system
				{
					serverReply.writeObject(getUserList());
					serverReply.flush();
					
					serverReply.writeObject("User list loaded!");
					serverReply.flush();
					
					processCommands();
				}
								
				else if(command.equals("CLONE")) //sends the calendar of a requested community member
				{
					serverReply.writeObject("Waiting for username...");
					serverReply.flush();
					
					String username = (String) clientInput.readObject();
										
					List<Event> userEvents = findUserEvents(username); //grab the user's event list
					
					serverReply.writeObject(userEvents); //send it to the client
					serverReply.flush();
					
					serverReply.writeObject("Schedule loaded for: " + username);
					serverReply.flush();
					
					processCommands();
				}
				
				else if(command.equals("EVENT")) // returns an appointment info
				{
					serverReply.writeObject("Waiting for username...");
					serverReply.flush();
					
					String username = (String)clientInput.readObject();
					
					serverReply.writeObject("Waiting for date...");
					serverReply.flush(); //for this user
					
					String userDate = (String)clientInput.readObject();
					
					HashMap<String, Event> userEL = getEventList(userDate); // gets the event map for given user date
					
					serverReply.writeObject(userEL);
					serverReply.flush();										
					
					processCommands();
				}
				
				else if(command.equals("REQT")) // returns a request list
				{
					serverReply.writeObject("Waiting for username...");
					serverReply.flush();	
					
					String username = (String)clientInput.readObject();//for this user
					
					ArrayList<Event> reqList = userRequestList.get(username);
					
					serverReply.writeObject(reqList);
					serverReply.flush(); 
					
					processCommands();
				}
				
				else
				    serverReply.writeObject("SOMETHING FATAL HAPPENED!"); //something wrong happened
					serverReply.flush();
				
			}
			
			else if(command.equals("PUT")) //client wants to store an object on the server
			{
				serverReply.writeObject("Ready to recive object type...");
				serverReply.flush();
				
				command = (String) clientInput.readObject();
				
				if(command.equals("CAL")) //client wants to store calendar
				{
					serverReply.writeObject("Waiting for calendar...");
					serverReply.flush();
					JCalendar calendar = (JCalendar) clientInput.readObject(); // writes calendar to the client
					putCalendar(calendar.getName(),calendar, serverReply);
				}
				
				else if(command.equals("EVENT")) //client wants to store an event
				{
					
					serverReply.writeObject("Waiting for username...");
					serverReply.flush();
					
					String username = (String)clientInput.readObject(); //reads username
					
					serverReply.writeObject("Waiting for date...");
					serverReply.flush();
					
					String date = (String)clientInput.readObject(); //reads date
					
					HashMap<String, Event> userEventList = new HashMap<String, Event>();
					
					if(eventMap.containsKey(username+date))					
						userEventList = eventMap.remove(username+date);
					
					
					serverReply.writeObject(userEventList);
					serverReply.flush();					
					
				
					userEventList = (HashMap<String, Event>)clientInput.readObject(); // read event
					
					if(userEventList.isEmpty())
						eventMap.remove(username+date);
					else
						eventMap.put(username+date, userEventList); // put event into event map
					
					setEventMap(eventMap);
					
					serverReply.writeObject("Event Created Successfully!");					
											
					
					processCommands();
					
				}
				else if(command.equals("REQT")) //client wants to send requests to other users					
				{
					
					serverReply.writeObject("Adding new or updating old?");
					serverReply.flush();
					
					command = (String)clientInput.readObject();
					
					if(command.equals("NEW")) //new event guest list is being created
					{
						serverReply.writeObject("Waiting for guest list...");
						serverReply.flush();
						
						List<String> guestList = (List)clientInput.readObject(); //reads guest list
						
						serverReply.writeObject("Waiting for event...");
						serverReply.flush();
						
						Event event = (Event)clientInput.readObject(); //reads event
						//this method is synchronized so two threads can not add requests at the same time to the request list
						addUserRequest(guestList,event); 								
					}
					
					else if(command.equals("OLD")) //an old event is being updated
					{
						serverReply.writeObject("Waiting for username...");
						serverReply.flush();
						
						String user = (String) clientInput.readObject();
						
						serverReply.writeObject("Waiting for updated event list...");
						serverReply.flush();
						
						List<Event> updatedEventList = (List<Event>)clientInput.readObject(); //reads guest list
						
						updateUserRequestList(user, updatedEventList);												
					}
					
					serverReply.writeObject("Done Processing Requests!");
					serverReply.flush();
					
					processCommands();			
				}
			}
			serverReply.close();
			clientInput.close();
			
		}catch(ClassNotFoundException ex)
		{
			System.out.println("String class not found!");
		}catch(SocketException se)
		{
			System.out.println("Client has ended session");
		}
	}

	private List<Event> findUserEvents(String username) {
		List<String> userEventKeys = new ArrayList<String>();
		List<Event> userEventList = new ArrayList<Event>();
		
		Iterator eventKeysIt = eventMap.keySet().iterator(); // iterate through the keys of the event map
		
		while(eventKeysIt.hasNext())
		{
			String key = (String)eventKeysIt.next();
			if(key.startsWith(username)) // find all the keys that belong to the specified user
			{
				System.out.println("User event key list found!"); 
				userEventKeys.add(key);//add this key to a new list so we can use it for lookup later
			}
		}
		
		Iterator userEventIt = userEventKeys.iterator(); // iterate through the found keys
		
		while(userEventIt.hasNext())
		{
			HashMap<String, Event> userDayEvent = eventMap.get(userEventIt.next()); // grab all events related to this user
			userEventList.addAll(userDayEvent.values());//add all events on this event map to the user's event list
		}
				
		return userEventList;
	}

	synchronized private void updateUserRequestList(String user, List<Event> updatedEventList) {
		
		userRequestList.remove(user); //remove this user's request list
		
		if(!updatedEventList.isEmpty()) // if the passed in request list isnt empty
		{
			List<Event> temp = new ArrayList<Event>();
			temp.addAll(updatedEventList);
			userRequestList.put(user, (ArrayList<Event>) temp);	//put the event back
			setRequestList(userRequestList);
		}			
		else
			System.out.println("User removed from request list");
	}

	synchronized private void addUserRequest(List<String> guestList, Event event) {
		ArrayList<Event> userEvents;
		
		if(userRequestList == null)
			 System.out.println("Request list is null!");
		
		for(String guest : guestList)
		{
			if(userRequestList.containsKey(guest)) // for all the guests on the guest list if they already have a reqst remove it for update
			{
				System.out.println("user event exsists");
				userEvents = userRequestList.remove(guest); //get the user's event req list
										
			}
			else
			{
				System.out.println("new array created");
				userEvents = new ArrayList<Event>();//reqst list does not exist so a new list should be created
			}

			userEvents.add(event); // add this event			
			userRequestList.put(guest, userEvents);	//put the event back
			setRequestList(userRequestList);
		}
	}

	private void putCalendar(String user, JCalendar newCal, ObjectOutputStream serverReply) throws IOException {
		
		JCalendar storedCal = calendarList.remove(user);		
		calendarList.put(user, newCal);//put user's calendar on the calendar list		
		serverReply.writeObject("Calendar for: " + user + " has been stored!");
		
	}

	private void getUserCalendar(String user, ObjectOutputStream serverReply, ObjectInputStream clientInput) throws IOException {			
		
		calendar = calendarList.get(user); 
		if(calendar == null) // if calendar is null then a new calendar should be created and added to calendar list
		{
			calendar = new JCalendar();
			calendarList.put(user, calendar);			
		}
		
		serverReply.writeObject(calendar);		
		serverReply.flush();		
		processCommands();
	}

	//trace method not implemented
	private void trace(){}
	
	//setters and getters
	public void setCalendarList(HashMap<String, JCalendar> calendarList) {
		this.calendarList = calendarList;
	}

	public HashMap<String, JCalendar> getCalendarList() {
		return calendarList;
	}

	private void setUserList(List<String> userList) {
		this.userList = userList;
	}

	private List<String> getUserList() {				 
		return userList;
	}
	
	//opens up the client list file. this is different than the file used for the login page on the client side since the server should
	//not be able to see that list
	private void generateUserList(){
		List<String> userList = new ArrayList<String>();
		
		FileInputStream fstream;
		try {
			fstream = new FileInputStream("clients.properties");
			Scanner in = new Scanner(fstream);
			while(in.hasNext())
			{
				String username = in.next();				
				userList.add(username);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File was not found!");
		}
		setUserList(userList);
	}
	
	private HashMap<String, Event> getEventList(String userDate) {
	
			return eventMap.get(userDate);		
	}


}
