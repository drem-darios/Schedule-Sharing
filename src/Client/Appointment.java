/*
 * Created by JFormDesigner on Wed Feb 23 15:15:28 EST 2011
 */

package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Drem Darios
 */
public class Appointment extends JDialog implements Serializable{
	protected String date;
	protected String user;
	private Socket sock;
	protected List guestList;
	protected ObjectOutputStream clientCommand;
	protected ObjectInputStream serverReply;
	
	//default constructor builds the gui
	public Appointment() throws IOException {		
		initComponents();
	}
	
	//overloaded constructor takes in and sets the date, user, and input/output streams then initializes the gui
	public Appointment(JFrame frame, String date, String user, ObjectOutputStream clientCommand , ObjectInputStream serverReply, List userList) throws IOException
	{
		super(frame, "Event Window", true);
		this.date = date;
		this.user = user;
		this.clientCommand = clientCommand;
		this.serverReply = serverReply;
		this.guestList = new ArrayList();
		initComponents();
	}

	public Appointment(JFrame frame, String date, String user,
			ObjectOutputStream clientCommand, ObjectInputStream serverReply,
			Event event, List userList) {
		
		super(frame, "Event Window", true);
		this.date = date;
		this.user = user;
		this.clientCommand = clientCommand;
		this.serverReply = serverReply;	
		this.guestList = event.guestList;
		initComponents();
		//This needs to be done in the appointment class. parse the string to get the time and grab the event to update it
		
		String[] eventStartString = event.getStartTime().split(":");
		String comments = "";

		startHr.setSelectedItem(eventStartString[0]);
		
		eventStartString = (eventStartString[1]).split(" ");
		
		startMin.setSelectedItem(eventStartString[0]);		
		startAMPM.setSelectedItem(eventStartString[1]);
		
		String[] eventEndString = event.getEndTime().split(":");

		endHr.setSelectedItem(eventEndString[0]);
		
		eventEndString = (eventEndString[1]).split(" ");
		
		endMin.setSelectedItem(eventEndString[0]);		
		endAMPM.setSelectedItem(eventEndString[1]);
		
		this.commentsArea.setText(event.getComments());		
	}

	private void saveBtnMouseReleased(MouseEvent e) {
		
		String startTime = startHr.getSelectedItem().toString() + ":" + startMin.getSelectedItem().toString() + " " + startAMPM.getSelectedItem().toString();
		String endTime = endHr.getSelectedItem().toString() + ":" + endMin.getSelectedItem().toString() + " " + endAMPM.getSelectedItem().toString();

		SimpleDateFormat stdTime = new SimpleDateFormat("h:mm a");
		Date startDateTime = null;
		Date endDateTime = null;
		try {
			startDateTime = stdTime.parse(startTime);
			endDateTime = stdTime.parse(endTime);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		
		//if the user didn't leave a comment, a warning appears on the gui to guide them
		if(commentsArea.getText().isEmpty())
		{
			System.out.println("Empty text area!");
			warningLabel.setText("Please enter comments before saving or click Cancel!");
			warningLabel.setVisible(true);
		}		
		
		else if(!startDateTime.before(endDateTime))
		{
			System.out.println("End time is before or the same as start time!");
			warningLabel.setText("Please select an End Time after the indicated Start Time!");
			warningLabel.setVisible(true);
		}
		
		else
		{					
			try {								
					Event event = new Event(user, date, startTime, endTime, commentsArea.getText(), getGuestList());
					
					clientCommand.writeObject("PUT"); //add this
					clientCommand.flush();
					
					System.out.println("Server: " + (String)serverReply.readObject());
					
					clientCommand.writeObject("EVENT"); //event to event list
					clientCommand.flush();
					
					System.out.println("Server: " + (String)serverReply.readObject());
					
					clientCommand.writeObject(user);
					clientCommand.flush();
					
					System.out.println("Server: " + (String)serverReply.readObject());
					
					clientCommand.writeObject(date);
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
										warningLabel.setText("This event conflicts with the following event: " + stdTime.format(otherStartTime));
										warningLabel.setVisible(true);
										
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
										warningLabel.setText("This event conflicts with the following event: " + stdTime.format(otherStartTime));
										warningLabel.setVisible(true);

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
								
								if(!guestList.isEmpty())
								{
									SendRequestDialog sr = new SendRequestDialog(this);
									sr.setSize(325, 110);
									sr.setVisible(true);
									
									if(sr.sendRequest)
									{
										clientCommand.writeObject("PUT"); //add this
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject());
										
										clientCommand.writeObject("REQT"); //request to request list
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); //Is this event new?
										
										clientCommand.writeObject("NEW"); //yeah its new
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); //ok send the guest list
										
										clientCommand.writeObject(guestList);
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); // ok now send the event
										
										clientCommand.writeObject(event);
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); // done processing requests
										System.out.println("Server: " + (String)serverReply.readObject()); // waiting for request
										
									}
									else
									{
										System.out.println("No don't send the request");
									}
									
								}
								
								setEnabled(false);
								dispose();
							}
							else
							{
								System.out.println("There were conflicts found!");
																
								clientCommand.writeObject(userEvent);
								clientCommand.flush();

								System.out.println("Server: " + (String)serverReply.readObject());					
								System.out.println("Server: " + (String)serverReply.readObject());
								
								if(!guestList.isEmpty())
								{
									SendRequestDialog sr = new SendRequestDialog(this);
									sr.setSize(325, 110);
									sr.setVisible(true);
									
									if(sr.sendRequest)
									{
										clientCommand.writeObject("PUT"); //add this
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject());
										
										clientCommand.writeObject("REQT"); //request to request list
										clientCommand.flush();

										System.out.println("Server: " + (String)serverReply.readObject()); //Is this event new?
										
										clientCommand.writeObject("NEW"); //yeah its new
										clientCommand.flush();
																				
										System.out.println("Server: " + (String)serverReply.readObject()); //ok send the guest list
										
										clientCommand.writeObject(guestList);
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); // ok now send the event
										
										clientCommand.writeObject(event);
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); // done processing requests
										System.out.println("Server: " + (String)serverReply.readObject()); // waiting for request
										
									}
									else
									{
										System.out.println("No don't send the request");
									}
									
								}
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
							
							if(od.okPressed)
							{
								if(!guestList.isEmpty())
								{
									SendRequestDialog sr = new SendRequestDialog(this);
									sr.setSize(325, 110);
									sr.setVisible(true);
									
									if(sr.sendRequest)
									{
										clientCommand.writeObject("PUT"); //add this
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject());
										
										clientCommand.writeObject("REQT"); //request to request list
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); //Is this event new?
										
										clientCommand.writeObject("NEW"); //yeah its new
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); //ok send the guest list
										
										clientCommand.writeObject(guestList);
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); // ok now send the event
										
										clientCommand.writeObject(event);
										clientCommand.flush();
										
										System.out.println("Server: " + (String)serverReply.readObject()); // done processing requests
										System.out.println("Server: " + (String)serverReply.readObject()); // waiting for request
										
									}
									else
									{
										System.out.println("No don't send the request");
									}
									
								}
								
								setEnabled(false);
								dispose();
							}
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
						
						if(!guestList.isEmpty())
						{
							SendRequestDialog sr = new SendRequestDialog(this);
							sr.setSize(325, 110);
							sr.setVisible(true);
							
							if(sr.sendRequest)
							{
								clientCommand.writeObject("PUT"); //add this
								clientCommand.flush();
								
								System.out.println("Server: " + (String)serverReply.readObject());
								
								clientCommand.writeObject("REQT"); //request to request list
								clientCommand.flush();
																
								System.out.println("Server: " + (String)serverReply.readObject()); //Is this event new?
								
								clientCommand.writeObject("NEW"); //yeah its new
								clientCommand.flush();
								
								System.out.println("Server: " + (String)serverReply.readObject()); //ok send the guest list
								
								clientCommand.writeObject(guestList);
								clientCommand.flush();
								
								System.out.println("Server: " + (String)serverReply.readObject()); // ok now send the event
								
								clientCommand.writeObject(event);
								clientCommand.flush();
								
								System.out.println("Server: " + (String)serverReply.readObject()); // done processing requests
								System.out.println("Server: " + (String)serverReply.readObject()); // waiting for request
								
							}
							else
							{
								System.out.println("No don't send the request");
							}
							
						}
						
						setEnabled(false);
						dispose();
						
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

	private void cancelBtnMouseReleased(MouseEvent e) {
		this.dispose(); //destroys the context
	}

	private void guestBtnMouseReleased(MouseEvent e) throws IOException {
		System.out.println("Add/Edit button clicked!");
		
		GuestList gl = new GuestList(this);
		gl.setSize(320,320);
		gl.setVisible(true);
		
		setGuestList(gl.guestList);				
	}
	
	public void setGuestList(List guestList)
	{
		this.guestList = guestList;
	}
	
	public List getGuestList()
	{
		return guestList;
	}
	

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label2 = new JLabel();
		String[] hours = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
		startHr = new JComboBox(hours); 
		String[] minutes = {"00","15","30","45"};
		startMin = new JComboBox(minutes);
		String[] ampm = {"AM", "PM"};
		startAMPM = new JComboBox(ampm);
		label1 = new JLabel();
		endHr = new JComboBox(hours); 
		endMin = new JComboBox(minutes);
		endAMPM = new JComboBox(ampm);
		scrollPane = new JScrollPane();
		commentsArea = new JTextArea();
		warningLabel = new JLabel();
		buttonBar = new JPanel();
		guestBtn = new JButton();
		saveBtn = new JButton();
		cancelBtn = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		this.setTitle("Appointment for: " + date);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"17dlu, 2*($lcgap, 20dlu), $lcgap, 25dlu, $lcgap, default, $lcgap, 13dlu, $lcgap, 20dlu, 2*($lcgap, 25dlu), $lcgap, 23dlu",
					"19dlu, 2*($lgap, default), $lgap, 91dlu, 2*($lgap, default)"));

				//---- label2 ----
				label2.setText("Start:");
				contentPanel.add(label2, cc.xy(1, 1));

				//---- startHr ----
				startHr.setSelectedIndex(0);
				contentPanel.add(startHr, cc.xy(3, 1));

				//---- startMin ----
				startMin.setSelectedIndex(0);
				contentPanel.add(startMin, cc.xy(5, 1));

				//---- startAMPM ----
				startAMPM.setSelectedIndex(0); 
				contentPanel.add(startAMPM, cc.xy(7, 1));

				//---- label1 ----
				label1.setText("End:");
				contentPanel.add(label1, cc.xywh(11, 1, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));

				//---- endHr ----
				endHr.setSelectedIndex(1);
				contentPanel.add(endHr, cc.xy(13, 1));

				//---- endMin ----
				endMin.setSelectedIndex(0);
				contentPanel.add(endMin, cc.xy(15, 1));

				//---- endAMPM ----
				endAMPM.setSelectedIndex(0); 
				contentPanel.add(endAMPM, cc.xy(17, 1));

				//======== scrollPane ========
				{
					scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

					//---- commentsArea ----
					commentsArea.setWrapStyleWord(true);
					commentsArea.setLineWrap(true);
					commentsArea.setBackground(new Color(153, 0, 0));
					commentsArea.setCaretColor(Color.white);
					commentsArea.setForeground(Color.white);
					scrollPane.setViewportView(commentsArea);
				}
				contentPanel.add(scrollPane, cc.xywh(1, 3, 19, 7));

				//---- warningLabel ----
				warningLabel.setText("Please enter comments before saving or click Cancel!");
				warningLabel.setForeground(Color.red);
				warningLabel.setVisible(false);
				contentPanel.add(warningLabel, cc.xywh(3, 11, 17, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, 2*(default, $lcgap), $button, $rgap, [57dlu,pref]",
					"pref"));

				//---- guestBtn ----
				guestBtn.setText("Add/Edit Guests");
				guestBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						try {
							guestBtnMouseReleased(e);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				buttonBar.add(guestBtn, cc.xy(4, 1));

				//---- saveBtn ----
				saveBtn.setText("Save");
				saveBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						saveBtnMouseReleased(e);
					}
				});
				buttonBar.add(saveBtn, cc.xy(6, 1));

				//---- cancelBtn ----
				cancelBtn.setText("Cancel");
				cancelBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						cancelBtnMouseReleased(e);
					}
				});
				buttonBar.add(cancelBtn, cc.xy(8, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
	
	public List loadUser(String user) throws IOException{
		List<String> userList  = new ArrayList<String>();
		
		try {
						
			clientCommand.writeObject("GET"); //gets a list of users in the community from the server. 
			
			clientCommand.flush();
			
			System.out.println("Server: "+(String) serverReply.readObject());
			//Since I am implementing this as a "stand-alone", the community members list needs to be populated by the server
			clientCommand.writeObject("USERS");
			clientCommand.flush();
			
			
			
			userList = (List<String>)serverReply.readObject();
				
			System.out.println("Server: " + (String)serverReply.readObject());
			System.out.println((String)serverReply.readObject());
			
			//initComponents(calendar, userList); //initializes the gui
			
		} catch (ClassNotFoundException e) {
			System.out.println("Custom Calendar Class not found!");
		}
		
		return userList;
	}
	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label2;
	private JComboBox startHr;
	private JComboBox startMin;
	private JComboBox startAMPM;
	private JLabel label1;
	private JComboBox endHr;
	private JComboBox endMin;
	private JComboBox endAMPM;
	private JScrollPane scrollPane;
	private JTextArea commentsArea;
	private JLabel warningLabel;
	private JPanel buttonBar;
	private JButton guestBtn;
	private JButton saveBtn;
	private JButton cancelBtn;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
