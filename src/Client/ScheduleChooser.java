package Client;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import com.jgoodies.forms.layout.*;
/*
 * Created by JFormDesigner on Mon Feb 21 17:15:29 EST 2011
 */



/**
 * @author Drem Darios
 */
public class ScheduleChooser extends JDialog{

	protected List<Event> eventList;
	protected String user = "Drem";
	protected List<String> userList  = new ArrayList<String>();
	protected DefaultListModel scheduleListModel = new DefaultListModel();
	
	public ScheduleChooser(List<Event> eventList, String user){
		this.eventList = eventList;
		this.user = user;	
		int counter = 1;
		if(eventList !=null)
		{
			for(Event event : eventList)//loads events to event list
			{				
				String eventComments = counter + ": " +event.getDate() + ": " + event.getStartTime() +  " -  " + event.getEndTime(); 
				scheduleListModel.addElement(eventComments);
				counter++;
			}
		}
		initComponents();//initializes gui
	}

	private void scheduleListMouseClicked(MouseEvent e) {
		System.out.println("Event was double clicked!");
		
		ViewSchedule vs = new ViewSchedule(user, eventList.get(scheduleList.getSelectedIndex()));//creates a new viewer when event double clicked
		vs.setSize(430,305);
		vs.setVisible(true);
	}
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane1 = new JScrollPane();
		scheduleList = new JList(scheduleListModel);
		CellConstraints cc = new CellConstraints();

		//======== this ========
		this.setTitle(user+"'s Schedule");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"5dlu, $lcgap, 22dlu, $lcgap, 86dlu",
			"4dlu, $lgap, fill:68dlu, $lgap, 167dlu"));

		//======== scrollPane1 ========
		{

			//---- scheduleList ----
						scheduleList.addMouseListener(new MouseAdapter() {
											@Override
											public void mouseClicked(MouseEvent e) {

												 if(e.getClickCount() == 2)
												 {										 
														scheduleListMouseClicked(e);										
												 }

											}
										});
			scrollPane1.setViewportView(scheduleList);
		}
		contentPane.add(scrollPane1, cc.xywh(2, 3, 4, 3));
		setSize(200, 410);
		setLocationRelativeTo(null);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane1;
	private JList scheduleList;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
