package crypto;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class GUI extends JFrame {

	private JPanel contentPane;
		
    KeyGenerator keyGenerator = null;
    static SecretKey secretKey = null;
    static Cipher cipher = null;
    static String ALGO="AES";
    String fileToEncrypt;
    String encryptedFile;
    String decryptedFile;
    String directoryPath;
    static File file;
    static String mode="enc";
    static String userKey="";
    private static File f;
    private JLabel lblDestinationIp;
    private static JTextField txtIp;
    private static JTextField textField;
    private JButton btnSend;
    private JLabel lblMyListOf;
	private static JTextField textField_1;
	private static JTextField textField_2;
	private static JTextArea textArea;
	private JScrollPane scrollPane_1;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Thread server=new Thread(){
			public void run(){
				try {
					runServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		server.start();
	}

	private static void runServer(){
		try {
			ServerSocket serverSocket = null;		  			
				try {
					Thread.sleep(2000); 
					serverSocket = new ServerSocket(Integer.parseInt(textField_1.getText()));
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null,"Can't setup server on specified port number");
					while(true) {
						try {
							Thread.sleep(5000); 
							serverSocket = new ServerSocket(Integer.parseInt(textField_1.getText()));
							break;
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,"Can't setup server on specified port number");
							e.printStackTrace();
						}
					}	
				}
			while(true) {
				try {
					
					Socket s=serverSocket.accept(); //establish connection 
					DataInputStream dis=new DataInputStream(s.getInputStream());
					String str=(String)dis.readUTF();
					textArea.append("\nThey:> "+str);
					textArea.setCaretPosition(textArea.getDocument().getLength());
					} catch (IOException ex) {
					    ex.printStackTrace();
						serverSocket.close();
						serverSocket = null;
						while(true) {
							try {
								Thread.sleep(5000); 
								serverSocket = new ServerSocket(Integer.parseInt(textField_1.getText()));
								break;
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null,"Can't setup server on specified port number");
								e.printStackTrace();
							}
						}
					}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	private static void runClient() {
		try {
			String msg = textField_2.getText();
			if(msg.length() < 1)
				return;
			
		        try {
		        	Socket s = new Socket(txtIp.getText(), Integer.parseInt(textField.getText()));
					DataOutputStream dout = new DataOutputStream(s.getOutputStream());
					dout.writeUTF(textField_2.getText());
					textArea.append("\n"+"Me:> "+msg);
					textArea.setCaretPosition(textArea.getDocument().getLength());
					textField_2.setText("");
					dout.flush();
					dout.close();
					s.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Create the frame.
	 */
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 369);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblDestinationIp = new JLabel("Destination IP:");
		lblDestinationIp.setBounds(12, 97, 128, 16);
		contentPane.add(lblDestinationIp);
		
		txtIp = new JTextField();
		txtIp.setText("127.16.1.1");
		txtIp.setBounds(152, 94, 119, 22);
		contentPane.add(txtIp);
		txtIp.setColumns(10);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(285, 97, 56, 16);
		contentPane.add(lblPort);
		
		textField = new JTextField();
		textField.setText("6001");
		textField.setBounds(351, 94, 69, 22);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnSend = new JButton("Send >");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runClient();
			}
		});
		btnSend.setBounds(323, 284, 97, 25);
		contentPane.add(btnSend);
		
		lblMyListOf = new JLabel("My list of IPs:");
		lblMyListOf.setBounds(12, 13, 97, 16);
		contentPane.add(lblMyListOf);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(125, 10, 295, 71);
		contentPane.add(scrollPane);
		
		JTextArea txtrIp = new JTextArea();
		txtrIp.setText("Loading ips please wait...");
		txtrIp.setEditable(false);
		scrollPane.setViewportView(txtrIp);
		Enumeration<?> e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		txtrIp.setText("");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Server Port", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(6, 41, 109, 47);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		textField_1 = new JTextField("6001");
		textField_1.setBounds(6, 18, 97, 22);
		panel_1.add(textField_1);
		textField_1.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Messages", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(16, 137, 404, 134);
		contentPane.add(panel);
		panel.setLayout(null);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 25, 380, 96);
		panel.add(scrollPane_1);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane_1.setViewportView(textArea);
		
		textField_2 = new JTextField();
		textField_2.setBounds(26, 285, 285, 22);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		while(e.hasMoreElements()){
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    Enumeration<?> ee = n.getInetAddresses();
		    while (ee.hasMoreElements()){
		        InetAddress i = (InetAddress) ee.nextElement();
		        txtrIp.append(i.getHostAddress()+"\n");
		    }
		}
	}
}
