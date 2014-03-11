package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import Client.Event;
/**
 * @author Drem Darios
 */
// Class declaration for the Server class.
public class Server implements Runnable{
	private ServerSocket servSock;
	private int socketPort = 8080;
	protected boolean isRunning = true;
	protected Thread thread = null;
	protected HashMap<String, HashMap<String, Event>> eventMap;
	protected HashMap<String, ArrayList<Event>> requestList;
	
	// Main method, point of entry for the program.
	public static void main(String[] args)
	{
		Server server = new Server();		
		
		// when server is initialized, any existing event map and requst list is loaded from the server's machine
		HashMap<String, HashMap<String, Event>> em = new HashMap<String, HashMap<String, Event>>();
		em = (HashMap<String, HashMap<String, Event>>)server.readEventFile(); 
		server.setEventMap(em);
		
		HashMap<String, ArrayList<Event>> rl = new HashMap<String, ArrayList<Event>>();
		rl = (HashMap<String, ArrayList<Event>>)server.readRequestFile();
		server.setRequestList(rl);
		
		server.run(); //runs the server
	}
	
	public Server(){
		
	}
	
	public void run()
	{			
		synchronized(this){
			this.thread = Thread.currentThread(); //runs the thread synchronized so there is no race condition or r/w hazards
		}

		try {
			
			// Open a server socket to communicate with the client.
			servSock = new ServerSocket(this.socketPort);
							
			System.out.println("Now accepting connections");
			
		while(isRunning())
		{
			Socket sock = servSock.accept(); // Accept a new socket from the server to communicate with a client.						
			new Thread(new WorkerThread(sock, getEventMap(), getRequestList())).start(); //run the worker thread to do all the work
			
		}
		
			} catch (IOException e) {
				if(!isRunning())
				{
					System.out.println("Error Communicating with Server!");
				}
				throw new RuntimeException("Error! Here is the stack trace: ", e);
			}
			finally{
				if(isRunning())
					closeConnection();
			}
			
	}
	
	private boolean isRunning() {
		return this.isRunning;
	}
			
	public void closeConnection() {
		this.isRunning = false;
		
		try {					
			servSock.close(); //close the server if it is no longer running
			
		} catch (IOException e) {
			System.out.print("Error closing connection!");
		}
	}

	protected HashMap<String, HashMap<String, Event>> getEventMap() {
		if(eventMap == null) //grab event map from file		
			eventMap = readEventFile();
		return eventMap;
	}

	protected void setEventMap(HashMap<String, HashMap<String, Event>> eventMap) {		
		this.eventMap = eventMap;
		writeEventFile(eventMap); //each time an event map is set, it needs to be written to a file for storage
	}

	protected HashMap<String, ArrayList<Event>> getRequestList() {		
		if(requestList == null)//grab request list from file
			requestList = readRequestFile();
		return requestList;
	}

	protected void setRequestList(HashMap<String, ArrayList<Event>> requestList) {		
		this.requestList = requestList;
		writeRequestFile(requestList);//each time an request list is set, it needs to be written to a file for storage
	}
/*
 * The following methods are synchronized so there can not be an interruption between the process of reading or writing objects
 * */
	synchronized protected HashMap<String, HashMap<String, Event>> readEventFile() {
		if(eventMap == null) //if no event map exists
		{	
			FileInputStream fis = null;
			ObjectInputStream in = null;
			String filename = "EventMap.obj"; // event map object file
			HashMap<String, HashMap<String, Event>> tempEventMap = new HashMap<String, HashMap<String, Event>>();
			try
			{
			  fis = new FileInputStream(filename);
			  in = new ObjectInputStream(fis);
			  tempEventMap = (HashMap<String, HashMap<String, Event>>)in.readObject(); // reads in stored event map
			  in.close();
			}
			catch(IOException ex)
			{
				System.out.println("Event Map Object Loaded!");
			}
			catch(ClassNotFoundException ex)
			{
			  ex.printStackTrace();
			}			
			
			if(tempEventMap != null)
				return tempEventMap; //if the file read in has no event map...
			else
				return new HashMap<String, HashMap<String, Event>>();//...create a new hashmap
		}
		
		else			
			return eventMap; //return the event map
		
	}
	synchronized protected HashMap<String, ArrayList<Event>> readRequestFile() {
		if(requestList == null)
		{	
			FileInputStream fis = null;
			ObjectInputStream in = null;
			String filename = "RequestList.obj";
			HashMap<String, ArrayList<Event>> tempRequestList = new HashMap<String, ArrayList<Event>>();
			try
			{
			  fis = new FileInputStream(filename);
			  in = new ObjectInputStream(fis);
			  tempRequestList = (HashMap<String, ArrayList<Event>>)in.readObject(); // reads in a stored request list
			  in.close();
			}
			catch(IOException ex)
			{
				System.out.println("Request List Object Loaded!");
			}
			catch(ClassNotFoundException ex)
			{
			  ex.printStackTrace();
			}
			
			if(tempRequestList != null)// if the request list read in is null...
				return tempRequestList;
			else
				return new HashMap<String, ArrayList<Event>>();//...create a new hashmap
		}
		else			
			return requestList;//return the request list			
	}
	
	synchronized protected void writeEventFile(HashMap<String, HashMap<String, Event>> updatedEventMap) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		String filename = "EventMap.obj"; //event map object name
		 try
		 {
			 fos = new FileOutputStream(filename);
		     out = new ObjectOutputStream(fos);
		     out.writeObject(updatedEventMap);//writes an updated event map to the object file
		     out.close();
		   }
		   catch(IOException ex)
		   {
		     System.out.println("New Event Map Object Created!");
		   }		
	
	}
	
	synchronized protected void writeRequestFile(HashMap<String, ArrayList<Event>> updatedRequestList) {
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		String filename = "RequestList.obj"; //request list object name
		 try
		 {
			 fos = new FileOutputStream(filename);
		     out = new ObjectOutputStream(fos);
		     out.writeObject(updatedRequestList); // writes an updated request list to the object file
		     out.close();
		   }
		   catch(IOException ex)
		   {
		     System.out.println("New Request List Object Created!");
		   }		
	}
}
