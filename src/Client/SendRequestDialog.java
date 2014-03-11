/*
 * Created by JFormDesigner on Thu Apr 07 00:33:08 EDT 2011
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
public class SendRequestDialog extends JDialog {
	
	protected boolean sendRequest;
	//simple dialog box to ask if the user wants to send a request tot he guest list
	public SendRequestDialog(Frame owner) {
		super(owner);
		this.sendRequest = false;
		initComponents();
	}

	public SendRequestDialog(Dialog owner) {
		super(owner, "Send Request?", true);
		this.sendRequest = false;
		initComponents();
	}

	private void yesButtonMouseReleased(MouseEvent e) {
		this.sendRequest = true;
		this.dispose();
	}

	private void noButtonMouseReleased(MouseEvent e) {
		this.sendRequest = false;
		this.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		buttonBar = new JPanel();
		yesButton = new JButton();
		noButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
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
					"default, $lcgap, default",
					"2*(default, $lgap), default"));

				//---- label1 ----
				label1.setText("Would you like to send a request to the guest list?");
				contentPanel.add(label1, cc.xy(1, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
				buttonBar.setLayout(new FormLayout(
					"$glue, $button, $rgap, $button",
					"pref"));

				//---- yesButton ----
				yesButton.setText("Yes");
				yesButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						yesButtonMouseReleased(e);
					}
				});
				buttonBar.add(yesButton, cc.xy(2, 1));

				//---- noButton ----
				noButton.setText("No");
				noButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						noButtonMouseReleased(e);
					}
				});
				buttonBar.add(noButton, cc.xy(4, 1));
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
	private JPanel buttonBar;
	private JButton yesButton;
	private JButton noButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
