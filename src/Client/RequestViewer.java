/*
 * Created by JFormDesigner on Thu Apr 07 18:15:41 EDT 2011
 */

package Client;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Drem Darios
 */
public class RequestViewer extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ArrayList<Event> reqEvents = new ArrayList<Event>();
	protected ObjectOutputStream clientCommand;
	protected ObjectInputStream serverReply;
	protected String user;
	protected DefaultListModel reqEventListModel = new DefaultListModel();
	public RequestViewer() {
		initComponents();
	}
	
	public RequestViewer(ClientUI frame, ObjectOutputStream clientCommand, ObjectInputStream serverReply, String user) {
		super(frame, "Request Viewer", true);
		this.user = user;
		this.clientCommand = clientCommand;
		this.serverReply = serverReply;
		getReqestedEvents();
		
		initComponents();
	}

	private void getReqestedEvents() {
		int counter = 1;
		
		try {
			clientCommand.writeObject("GET");
			clientCommand.flush();
			
			System.out.println("Server: " + (String) serverReply.readObject()); // get what?
			
			clientCommand.writeObject("REQT");
			clientCommand.flush();
			
			System.out.println("Server: " + (String) serverReply.readObject()); // send user name
			
			clientCommand.writeObject(user);
			clientCommand.flush();
			
			reqEvents = (ArrayList<Event>) serverReply.readObject();
			
			System.out.println("Server: " + (String) serverReply.readObject());
			
			if(reqEvents !=null)
			{
				for(Event event : reqEvents)
				{
					
					String eventComments = counter + ": " +event.getDate() + ": " + event.getStartTime() +  " -  " + event.getEndTime(); 
					reqEventListModel.addElement(eventComments);
					counter++;
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private void addEventBtnMouseReleased(MouseEvent e) {
		// once the user decides to add this event to their list
		//the same procedure to handle new events on the appointment screen is implemented here
		int index = requestEventList.getSelectedIndex(); 
		if(index >= 0)
		{
			Event event = reqEvents.get(index);
			
			List<String> tempGuestList = new ArrayList<String>(event.getGuestList());
			tempGuestList.remove(tempGuestList.indexOf(user));
			event.setGuestList(tempGuestList);
			
			String startTime = event.getStartTime();
			String endTime = event.getEndTime();

			SimpleDateFormat stdTime = new SimpleDateFormat("h:mm a");
			Date startDateTime = null;
			Date endDateTime = null;
			
			try {
				startDateTime = stdTime.parse(startTime);
				endDateTime = stdTime.parse(endTime);
			} catch (ParseException e2) {
				e2.printStackTrace();
			}
			
								
				try {								
										
						clientCommand.writeObject("PUT"); //add this
						clientCommand.flush();
						
						System.out.println("Server: " + (String)serverReply.readObject());
						
						clientCommand.writeObject("EVENT"); //event to event list
						clientCommand.flush();
						
						System.out.println("Server: " + (String)serverReply.readObject());
						
						clientCommand.writeObject(user);
						clientCommand.flush();
						
						System.out.println("Server: " + (String)serverReply.readObject());
						
						clientCommand.writeObject(event.getDate());
						clientCommand.flush();
						

						HashMap<String, Event> userEvent = (HashMap<String, Event>) serverReply.readObject();					
						if(!userEvent.isEmpty()) // event list exists
						{						
							if(!userEvent.containsKey(startTime)) //no event that starts at this time
							{				
									
								Collection<Event> valueSet = userEvent.values();
								Iterator it = valueSet.iterator();
								boolean noConflicts = true;
																				
								while(it.hasNext())
								{
									Event otherEvent = (Event)it.next();
									Date otherStartTime = stdTime.parse(otherEvent.getStartTime());
									Date otherEndTime = stdTime.parse(otherEvent.getEndTime());
									
									if(startDateTime.after(otherStartTime))
									{
										if(startDateTime.before(otherEndTime))
										{
											System.out.println("The new event is happeniing before the old one finishes!"); //bad											
											noConflicts = false;
											break;
										}
										else if(startDateTime.after(otherEndTime))
										{
											System.out.println("This event is happening after the old one"); //good
											System.out.println("!!!"+stdTime.format(startDateTime));																				
										}																	
									}
									
									else if(startDateTime.before(otherStartTime))
									{
										if(endDateTime.before(otherStartTime))
										{
											System.out.println("The new event is happeniing before the old one finishes!"); //good
											System.out.println("!!!"+stdTime.format(startDateTime));																				
										}
										else if(endDateTime.after(otherStartTime))
										{
											System.out.println("An event start before this on ends!"); //bad
											noConflicts = false;
											break;
										}
									}
									else
										System.out.println("They are equal! This shouldnt happen");																									
								}	
								
								if(noConflicts)
								{
									userEvent.put(startTime, event);	
									
									clientCommand.writeObject(userEvent);
									clientCommand.flush();
									
									System.out.println("Server: " + (String)serverReply.readObject());					
									System.out.println("Server: " + (String)serverReply.readObject());									
																		
									/* Update old request list*/							
									clientCommand.writeObject("PUT");
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());	
									
									clientCommand.writeObject("REQT");
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());
									
									clientCommand.writeObject("OLD");
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());	
									
									clientCommand.writeObject(user);
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());								
									
									reqEvents.remove(index);
									reqEventListModel.remove(index);
									eventComponents.setText("");
									
									clientCommand.writeObject(Arrays.asList(reqEventListModel.toArray()));
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());					
									System.out.println("Server: " + (String)serverReply.readObject());			
									
								}
								else
								{
									System.out.println("There were conflicts found!");
																	
									clientCommand.writeObject(userEvent);
									clientCommand.flush();

									System.out.println("Server: " + (String)serverReply.readObject());					
									System.out.println("Server: " + (String)serverReply.readObject());
									
									/* Update old request list*/							
									clientCommand.writeObject("PUT");
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());	
									
									clientCommand.writeObject("REQT");
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());
									
									clientCommand.writeObject("OLD");
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());	
									
									clientCommand.writeObject(user);
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());								
									
									reqEvents.remove(index);
									reqEventListModel.remove(index);
									eventComponents.setText("");
									
									clientCommand.writeObject(Arrays.asList(reqEventListModel.toArray()));
									clientCommand.flush();						

									System.out.println("Server: " + (String)serverReply.readObject());					
									System.out.println("Server: " + (String)serverReply.readObject());			
								}
							}
							
							else
							{				
								System.out.println("This time exists for another event!");
													
								System.out.println("Do you want to overwrite this?");
								
								OverwriteDialog od = new OverwriteDialog(this, startTime);
								od.setSize(365, 100);
								od.setVisible(true);
								
								if(od.okPressed)
								{
									userEvent.put(startTime, event);
									System.out.println("OK PRESSED");
								}
								
								clientCommand.writeObject(userEvent);
								clientCommand.flush();								

								System.out.println("Server: " + (String)serverReply.readObject());					
								System.out.println("Server: " + (String)serverReply.readObject());

								/* Update old request list*/							
								clientCommand.writeObject("PUT");
								clientCommand.flush();						

								System.out.println("Server: " + (String)serverReply.readObject());	
								
								clientCommand.writeObject("REQT");
								clientCommand.flush();						

								System.out.println("Server: " + (String)serverReply.readObject());
								
								clientCommand.writeObject("OLD");
								clientCommand.flush();						

								System.out.println("Server: " + (String)serverReply.readObject());	
								
								clientCommand.writeObject(user);
								clientCommand.flush();						

								System.out.println("Server: " + (String)serverReply.readObject());								
								
								reqEvents.remove(index);
								reqEventListModel.remove(index);
								eventComponents.setText("");
								
								clientCommand.writeObject(Arrays.asList(reqEventListModel.toArray()));
								clientCommand.flush();						

								System.out.println("Server: " + (String)serverReply.readObject());					
								System.out.println("Server: " + (String)serverReply.readObject());		
							}
						}
						
						else // there is no event list yet for this user and date
						{
							userEvent.put(startTime, event);
							System.out.println("There is no event list yet for this user");
							
							clientCommand.writeObject(userEvent);
							clientCommand.flush();						

							System.out.println("Server: " + (String)serverReply.readObject());					
							System.out.println("Server: " + (String)serverReply.readObject());												
							
							/* Update old request list*/							
							clientCommand.writeObject("PUT");
							clientCommand.flush();						

							System.out.println("Server: " + (String)serverReply.readObject());	
							
							clientCommand.writeObject("REQT");
							clientCommand.flush();						

							System.out.println("Server: " + (String)serverReply.readObject());
							
							clientCommand.writeObject("OLD");
							clientCommand.flush();						

							System.out.println("Server: " + (String)serverReply.readObject());	
							
							clientCommand.writeObject(user);
							clientCommand.flush();						

							System.out.println("Server: " + (String)serverReply.readObject());								
							
							reqEvents.remove(index);
							reqEventListModel.remove(index);
							eventComponents.setText("");
							
							clientCommand.writeObject(Arrays.asList(reqEventListModel.toArray()));
							clientCommand.flush();						

							System.out.println("Server: " + (String)serverReply.readObject());					
							System.out.println("Server: " + (String)serverReply.readObject());							
							
						}																										
									
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				} catch (ParseException exp) {
					// TODO Auto-generated catch block
					exp.printStackTrace();
				}									
		}				
	}	

	private void denyEventBtnMouseReleased(MouseEvent e) {

		int index = requestEventList.getSelectedIndex();
		
		if(index >= 0)
		{
			/* Update old request list*/							
			try {
				clientCommand.writeObject("PUT");
			
				clientCommand.flush();						
	
				System.out.println("Server: " + (String)serverReply.readObject());	
				
				clientCommand.writeObject("REQT");
				clientCommand.flush();						
	
				System.out.println("Server: " + (String)serverReply.readObject());
				
				clientCommand.writeObject("OLD");
				clientCommand.flush();						
	
				System.out.println("Server: " + (String)serverReply.readObject());	
				
				clientCommand.writeObject(user);
				clientCommand.flush();						
	
				System.out.println("Server: " + (String)serverReply.readObject());								
				
				reqEvents.remove(index);
				reqEventListModel.remove(index);
				eventComponents.setText("");
				
				clientCommand.writeObject(Arrays.asList(reqEventListModel.toArray()));
				clientCommand.flush();						
	
				System.out.println("Server: " + (String)serverReply.readObject());					
				System.out.println("Server: " + (String)serverReply.readObject());							

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
			
	}

	private void requestEventListMouseClicked(MouseEvent e) {
		 int index = requestEventList.getSelectedIndex();
	     if(index >= 0) //displays comments for event given a selected event request
	     {
	    	 String item = (String)requestEventList.getSelectedValue();
		     requestEventList.ensureIndexIsVisible(index);
		     String[] eventDataSplit = item.split(": ");
		     int eventIndex = Integer.parseInt(eventDataSplit[0]);
		     System.out.println(index);
		     Event evt = ((Event)((ArrayList<Event>) reqEvents).get(eventIndex-1));
			 eventComponents.setText("User " + evt.user + ": " + evt.comments);	 
	     }		 
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label2 = new JLabel();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		requestEventList = new JList(reqEventListModel);
		scrollPane2 = new JScrollPane();
		eventComponents = new JTextPane();
		buttonBar = new JPanel();
		addEventBtn = new JButton();
		denyEventBtn = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Request Viewer");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"50dlu, $lcgap, 53dlu, $lcgap, 186dlu, $lcgap, default",
					"default, $lgap, 21dlu, $lgap, 161dlu, $lgap, default"));

				//---- label2 ----
				label2.setText("Requested Events");
				contentPanel.add(label2, cc.xywh(1, 1, 3, 1));

				//---- label1 ----
				label1.setText("Event Comments");
				contentPanel.add(label1, cc.xy(5, 1));

				//======== scrollPane1 ========
				{

					//---- requestEventList ----
					requestEventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					requestEventList.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							requestEventListMouseClicked(e);
						}
					});
					scrollPane1.setViewportView(requestEventList);
				}
				contentPanel.add(scrollPane1, cc.xywh(1, 3, 3, 3));

				//======== scrollPane2 ========
				{

					//---- eventComponents ----
					eventComponents.setEditable(false);
					scrollPane2.setViewportView(eventComponents);
				}
				contentPanel.add(scrollPane2, cc.xywh(5, 3, 3, 3));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, $button, $rgap, $button",
					"default, pref"));

				//---- addEventBtn ----
				addEventBtn.setText("Add Event");
				addEventBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						addEventBtnMouseReleased(e);
					}
				});
				buttonBar.add(addEventBtn, cc.xy(2, 2));

				//---- denyEventBtn ----
				denyEventBtn.setText("Deny Event");
				denyEventBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						denyEventBtnMouseReleased(e);
					}
				});
				buttonBar.add(denyEventBtn, cc.xy(4, 2));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label2;
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JList requestEventList;
	private JScrollPane scrollPane2;
	private JTextPane eventComponents;
	private JPanel buttonBar;
	private JButton addEventBtn;
	private JButton denyEventBtn;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
