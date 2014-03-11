/*
 * Created by JFormDesigner on Mon Mar 21 22:27:09 EDT 2011
 */

package Client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Drem Darios
 */
public class OverwriteDialog extends JDialog {
	
	String startTime;
	boolean okPressed;
	//simple dialog box to ask the user if they want to overwrite the current event at this time
	public OverwriteDialog(Frame owner, String startTime) {
		super(owner, "Overwrite?", true);
		this.startTime = startTime;
		this.okPressed = false;
		initComponents();
	}

	public OverwriteDialog(Dialog owner, String startTime) {
		super(owner, "Overwrite?", true);
		this.startTime = startTime;
		this.okPressed = false;
		initComponents();
	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		this.dispose(); //destroys the context
	}

	private void okButtonActionPerformed(ActionEvent e) {
		this.okPressed = true;
		this.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		timeLabel = new JLabel();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle("Overwrite?");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"135dlu, $lcgap, 81dlu",
					"2*(default, $lgap), default"));

				//---- label1 ----
				label1.setText("Are you sure you want to overwrite the event at:");
				contentPanel.add(label1, cc.xy(1, 1));

				//---- timeLabel ----
				timeLabel.setText("text");
				timeLabel.setForeground(Color.red);
				timeLabel.setText(startTime);
				contentPanel.add(timeLabel, cc.xy(3, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, $button, $rgap, [23dlu,pref]",
					"pref"));

				//---- okButton ----
				okButton.setText("Yes");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, cc.xy(2, 1));

				//---- cancelButton ----
				cancelButton.setText("No");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
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
	private JLabel timeLabel;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
