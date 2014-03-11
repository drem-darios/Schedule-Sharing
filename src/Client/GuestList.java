/*
 * Created by JFormDesigner on Sun Apr 03 12:43:43 EDT 2011
 */

package Client;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Drem Darios
 */
public class GuestList extends JDialog {
	
	protected String date;
	protected String user;
	protected DefaultListModel comListModel = new DefaultListModel();
	protected DefaultListModel currListModel = new DefaultListModel();
	protected ObjectOutputStream clientCommand;
	protected ObjectInputStream serverReply;
	protected List guestList;
	protected List tempGuestList;
	
	
	public GuestList(Frame owner) {
		super(owner);
		initComponents(new ArrayList());
	}

	public GuestList(Appointment appointment) throws IOException {
		super(appointment, "Guest Window", true);
		this.user = appointment.user;
		this.date = appointment.date;
		this.guestList = appointment.guestList;
		this.tempGuestList = appointment.guestList;
		
		//make it so if the user is confirmed for this even they can not be removed from the list. 
		//if they delete the event then they are not going to be on the list anymore anyways but they are the only ones who have the power to do that. 
		//i.e.: you can't un-invite someone to a party once the party invitation is sent out
		
		List userList = appointment.loadUser(appointment.user);
		initComponents(userList);
	}

	private void cancelButtonMouseReleased(MouseEvent e) {
		this.dispose(); //destroys the context
	}

	private void comListMouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void addBtnMouseReleased(MouseEvent e) {
		Object userNames[] = comList.getSelectedValues();
		
		for(int i = 0; i < userNames.length; i++)
		{
			int index = comListModel.indexOf(userNames[i]);
			
			comListModel.remove(index);
		    currListModel.addElement(userNames[i]);
	
		    if(!removeBtn.isEnabled())
		    	removeBtn.setEnabled(true);
		    
		    int size = comListModel.getSize();
	
		    if (size == 0) { //Nobody's left, disable firing.
		        addBtn.setEnabled(false);
	
		    } else { //Select an index.
		        if (index == comListModel.getSize()) {
		            //removed item in last position
		            index--;
		        }
	
		        comList.setSelectedIndex(index);
		        comList.ensureIndexIsVisible(index);
		        currList.setModel(currListModel);
		        
		        tempGuestList = Arrays.asList(currListModel.toArray());

		    }
		}
	}

	private void removeBtnMouseReleased(MouseEvent e) {
		//int index = currList.getSelectedIndex();
		
		Object userNames[] = currList.getSelectedValues();
		
		for(int i = 0; i < userNames.length; i++)
		{
			int index = currListModel.indexOf(userNames[i]);
		    
			currListModel.remove(index);
		    comListModel.addElement(userNames[i]);
		    
		    if(!addBtn.isEnabled())
		    	addBtn.setEnabled(true);
		    
		    int size = currListModel.getSize();
	
		    if (size == 0) { //Nobody's left, disable firing.
		        removeBtn.setEnabled(false);
	
		    } 
		        if (index == currListModel.getSize()) {
		            //removed item in last position
		            index--;
		        }
	
		        currList.setSelectedIndex(index);
		        currList.ensureIndexIsVisible(index);
		        comList.setModel(comListModel);
		        
		        tempGuestList = Arrays.asList(currListModel.toArray());		    
		}
	}

	private void doneButtonMouseReleased(MouseEvent e) {
			guestList = tempGuestList;
			System.out.println(guestList.size());
			this.dispose();
	}

	private void initComponents(List<String> userList) {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		label2 = new JLabel();
		scrollPane1 = new JScrollPane();
		for(String users : userList)
		{
			if(!users.equals(user))
				if(!guestList.contains(users))
					comListModel.addElement(users);
		}
		comList = new JList(comListModel);
		scrollPane2 = new JScrollPane();
		if(!guestList.isEmpty())
			for(Object users : guestList)							
					currListModel.addElement(users);
		currList = new JList(currListModel);
		addBtn = new JButton();
		removeBtn = new JButton();
		buttonBar = new JPanel();
		doneButton = new JButton();
		cancelButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle("Guest List");
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
					"default, $lcgap, 35dlu, 5*($lcgap, default)",
					"default, $lgap, 17dlu, $lgap, 15dlu, $lgap, 17dlu, $lgap, 61dlu, 2*($lgap, default)"));

				//---- label1 ----
				label1.setText("Community");
				contentPanel.add(label1, cc.xy(3, 1));

				//---- label2 ----
				label2.setText("Current Guests");
				contentPanel.add(label2, cc.xy(9, 1));

				//======== scrollPane1 ========
				{

					//---- comList ----
					comList.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							comListMouseClicked(e);
						}
					});
					scrollPane1.setViewportView(comList);
				}
				contentPanel.add(scrollPane1, cc.xywh(2, 3, 4, 7));

				//======== scrollPane2 ========
				{
					scrollPane2.setViewportView(currList);
				}
				contentPanel.add(scrollPane2, cc.xywh(9, 3, 3, 7));

				//---- addBtn ----
				addBtn.setText("Add >>");
				addBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						addBtnMouseReleased(e);
					}
				});
				if(comListModel.isEmpty())
					addBtn.setEnabled(false);
				contentPanel.add(addBtn, cc.xy(7, 5));

				//---- removeBtn ----
				removeBtn.setText("<< Remove ");
				removeBtn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						removeBtnMouseReleased(e);
					}
				});
				if(currListModel.isEmpty())
					removeBtn.setEnabled(false);
				contentPanel.add(removeBtn, cc.xy(7, 7));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, $button, $rgap, $button",
					"pref"));

				//---- doneButton ----
				doneButton.setText("Done!");
				doneButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						doneButtonMouseReleased(e);
					}
				});
				buttonBar.add(doneButton, cc.xy(2, 1));

				//---- cancelButton ----
				cancelButton.setText("Cancel");
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						cancelButtonMouseReleased(e);
					}
				});
				buttonBar.add(cancelButton, cc.xy(4, 1));
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
	private JLabel label1;
	private JLabel label2;
	private JScrollPane scrollPane1;
	private JList comList;
	private JScrollPane scrollPane2;
	private JList currList;
	private JButton addBtn;
	private JButton removeBtn;
	private JPanel buttonBar;
	private JButton doneButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
