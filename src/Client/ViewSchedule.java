/*
 * Created by JFormDesigner on Sat Feb 26 18:27:58 EST 2011
 */

package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Drem Darios
 * This class is to be used for viewing a users activities for a given date and time
 * 
 */
public class ViewSchedule extends JFrame {
	
	protected Event event;
	private String user;
	public ViewSchedule() {
		initComponents(); //initializes gui
	}
//takes in the date, user, input/output streams and a string of activities
	public ViewSchedule(String user, Event event) {
		this.event = event;
		this.user = user;
		System.out.println(event.comments);
		this.event.comments = event.comments;
		initComponents();//initializes the gui
		
	}

	private void doneButtonMouseReleased(MouseEvent e) {
		dispose();//destroys the context
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		startTimeLabel = new JLabel();
		label2 = new JLabel();
		endTimeLabel = new JLabel();
		scrollPane1 = new JScrollPane();
		viewTextArea = new JTextPane();
		buttonBar = new JPanel();
		doneButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		this.setTitle("Activities for: " + user + " "  + event.date);
		viewTextArea.setText(event.comments);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"38dlu, $lcgap, 53dlu, $lcgap, 34dlu, $lcgap, 72dlu, $lcgap, 9dlu, $lcgap, 33dlu",
					"default, $lgap, 121dlu, 2*($lgap, default)"));

				//---- label1 ----
				label1.setText("Begin Time:");
				contentPanel.add(label1, cc.xywh(1, 1, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

				//---- startTimeLabel ----
				startTimeLabel.setText(event.startTime);
				contentPanel.add(startTimeLabel, cc.xy(3, 1));

				//---- label2 ----
				label2.setText("End Time");
				contentPanel.add(label2, cc.xywh(5, 1, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

				//---- endTimeLabel ----
				endTimeLabel.setText(event.endTime);
				contentPanel.add(endTimeLabel, cc.xy(7, 1));

				//======== scrollPane1 ========
				{

					//---- viewTextArea ----
					viewTextArea.setEditable(false);
					viewTextArea.setForeground(Color.white);
					viewTextArea.setBackground(Color.black);
					scrollPane1.setViewportView(viewTextArea);
				}
				contentPanel.add(scrollPane1, cc.xywh(1, 3, 11, 5));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, $button",
					"pref"));

				//---- doneButton ----
				doneButton.setText("Done");
				doneButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						doneButtonMouseReleased(e);
					}
				});
				buttonBar.add(doneButton, cc.xy(2, 1));
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
	private JLabel startTimeLabel;
	private JLabel label2;
	private JLabel endTimeLabel;
	private JScrollPane scrollPane1;
	private JTextPane viewTextArea;
	private JPanel buttonBar;
	private JButton doneButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
