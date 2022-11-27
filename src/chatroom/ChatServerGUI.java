package chatroom;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChatServerGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton startButton;
	private JButton stopButton;
	
	private ChatServer chatServer = null;
	
	private String URL;

	public ChatServerGUI(String url) {
		URL=url;
		JFrame frame = new JFrame("Discord Server ™");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		JLabel label = new JLabel("Discord Server");
		panel1.add(label);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout());
		startButton = new JButton();
		startButton.setText("Start");
		startButton.addActionListener(this);
		stopButton = new JButton();
		stopButton.setText("Stop");
		stopButton.setEnabled(false);
		stopButton.addActionListener(this);
		panel2.add(startButton);
		panel2.add(stopButton);
		

		panel.add(panel1);
		panel.add(panel2);
		
		frame.add(panel);
		frame.setSize(300, 100);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		// TODO Auto-generated method stub
		try {
			if (ev.getSource() == startButton) {
				if (chatServer==null) {
					System.out.println("RMI Server ready");
					chatServer= new ChatServer();
					Naming.rebind(URL, chatServer);
					System.out.println("Group Chat RMI Server is running...");
					
					startButton.setEnabled(false);
					stopButton.setEnabled(true);
				}
			}

			if (ev.getSource() == stopButton) {
				if (chatServer!=null) {
					chatServer.sendCloseToAllChannel("Server is closing!");
					chatServer=null;
					System.out.println("Group Chat RMI Server is stopping...");
					stopButton.setEnabled(false);
					startButton.setEnabled(true);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
