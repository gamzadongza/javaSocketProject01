package com.hi;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;


public class Client extends Frame implements ActionListener {

	static boolean boo = true;
	static File memberFile = new File("client.dat");
	CardLayout card = new CardLayout();
	static String chattingName = "chattingNameDefault"; // �� ä�� �̸�
	static String chattingId = "";
	Panel messengerChatListPanel = new Panel(new BorderLayout()); // ä�� ����Ʈ
	Panel messengerSearchPanel = new Panel(new GridLayout(10, 1)); // ã��
	Panel messengerOptionPanel = new Panel(new GridLayout(10, 1)); // ����
	
	static TextField registerName = new TextField(15);
	static TextField registerId = new TextField(15);
	static TextField registerPassword = new TextField(15);
	static TextField registerPasswordRe = new TextField(15);  // �н����� ��Ȯ��
	
	static TextArea messengerChatbox = new TextArea(); // �޽��� ä��ȭ��
	static TextField messengerMessage = new TextField(); // �޽��� ä���Է�
	
	static Socket sock;
	static String outputMsg = ""; // �������� �޾ƿ��� �޼���
	
	static TextField textID = new TextField(20); // �α��� ȭ�� ���̵�
	static TextField textPassword = new TextField(20); // �α��� ȭ�� �н�����
	
	static TextField fontSize = new TextField(10); // ���� �۾� ũ��
	static CheckboxGroup cbg = new CheckboxGroup(); // ���� ������ üũ�ڽ�
	
	static TextField messengerSearchID = new TextField(20); // ID �˻�
	
	
	WindowAdapter win = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			
			dispose();
		}
	};
	
	
	// ��ü �г�
	Panel allPanel = new Panel(card);
	
	
	// actionListener �޼���
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// �α��� ȭ�鿡�� ȸ�������� ������ ��
		if(e.getActionCommand().equals("ȸ������")) {
			outputMsg = "";
			registerName.setText(" ");
			registerId.setText(" ");
			registerPassword.setText(" ");
			registerPasswordRe.setText(" ");
			registerName.setText("");
			registerId.setText("");
			registerPassword.setText("");
			registerPasswordRe.setText("");
			card.show(allPanel, "registerPanel");
		}
		
		// ȸ������ ȭ�鿡�� ��Ҹ� ������ ��
		if(e.getActionCommand().equals("���")) {
			textID.setText(" ");
			textID.setText("");
			textPassword.setText(" ");
			textPassword.setText("");
			card.show(allPanel, "loginPanel");
		}
		
		// ȸ������ ȭ�鿡�� ������ ������ ��
		if(e.getActionCommand().equals("����")) {
			outputMsg = "";
			int temp = createMember();
			
			if(temp < 0) { // ���� �޼���
				Dialog errorMessage = new Dialog(this, "����", true);
				Panel errorMessageLine1 = new Panel();
				Panel errorMessageLine2 = new Panel();
				errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
				errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("Ȯ��");
				errorMessageLine2.add(errorMessageButton);
				errorMessageButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						errorMessage.dispose();
					}
				});
				
				if(temp == -1) {
					errorMessageLine1.add(new Label("�̸��� �Է����ּ���"));
				} else if(temp == -2) {
					errorMessageLine1.add(new Label("ID�� �Է����ּ���"));
				} else if(temp == -3) {
					errorMessageLine1.add(new Label("��й�ȣ�� �Է����ּ���"));
				} else if(temp == -4) {
					errorMessageLine1.add(new Label("��й�ȣ�� �ٸ��ϴ�!"));
				} else if(temp == -5) {
					errorMessageLine1.add(new Label("�̹� �����ϴ� ID�Դϴ�."));
				} else if(temp == -6) {
					errorMessageLine1.add(new Label("�Է�ĭ�� !, @, #, &, ~, =�� ����Ͻ� �� �����ϴ�."));
				}
				
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			} else if(temp > 0) {
				Dialog registerComplete = new Dialog(this, "���� �Ϸ�", true);
				Panel registerCompleteLine1 = new Panel();
				Panel registerCompleteLine2 = new Panel();
				registerComplete.add(registerCompleteLine1, BorderLayout.NORTH);
				registerComplete.add(registerCompleteLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("Ȯ��");
				registerCompleteLine1.add(new Label("���ԵǾ����ϴ�."));
				registerCompleteLine2.add(errorMessageButton);
				errorMessageButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						registerComplete.dispose();
						card.show(allPanel, "loginPanel");
					}
				});
				registerComplete.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						registerComplete.dispose();
						card.show(allPanel, "loginPanel");
					};
				});
				registerComplete.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				registerComplete.setVisible(true);
			}
		} // register action end...
		
		// �α��� ��ư�� ������ ��
		if(e.getActionCommand().equals("�α���")) {
			outputMsg = "";
			messengerChatbox.setText(" ");
			messengerChatbox.setText("");
			
			// ���� �޼���
			Dialog errorMessage = new Dialog(this, "����", true);
			Panel errorMessageLine1 = new Panel();
			Panel errorMessageLine2 = new Panel();
			errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
			errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
			Button errorMessageButton = new Button("Ȯ��");
			errorMessageLine2.add(errorMessageButton);
			errorMessageButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorMessage.dispose();
				}
			});
			
			int num = 0;
			
			// �Է����� ���� ���
			if(textID.getText().equals("") && textPassword.getText().equals("")) {
				errorMessageLine1.add(new Label("ID, ��й�ȣ�� �Է����ּ���"));
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			} else if(textID.getText().equals("")) {
				errorMessageLine1.add(new Label("ID�� �Է����ּ���"));
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			} else if(textPassword.getText().equals("")) {
				errorMessageLine1.add(new Label("��й�ȣ�� �Է����ּ���"));
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			} else if(num == 0){
				outputMsg = "";
				num = login();
				
				if(num > 0) {
					messengerSearchPanel.setVisible(false);
					messengerOptionPanel.setVisible(false);
					messengerChatListPanel.setVisible(true);
					chattingName = outputMsg.substring(4);
					chattingId = textID.getText();
					card.show(allPanel, "messengerPanel");
				} else if(num == -2) {
					errorMessageLine1.add(new Label("ID, ��й�ȣ�� Ȯ�����ּ���"));
					errorMessage.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							errorMessage.dispose();
						}
					});
					errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
					errorMessage.setVisible(true);
				} else {
					errorMessageLine1.add(new Label("�ش� ������ �̹� �α������Դϴ�."));
					errorMessage.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							errorMessage.dispose();
						}
					});
					errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
					errorMessage.setVisible(true);
				}
			}
		}// login action end...
		
		// �޽��� ȭ�鿡�� �α׾ƿ��� ������ ��
		if(e.getActionCommand().equals("�α׾ƿ�")) {
			// �α׾ƿ� JDialog
			Dialog logout = new Dialog(this, "�α׾ƿ�", true);
			Panel logoutQuestion = new Panel();
			logoutQuestion.setLayout(new BorderLayout());
			logout.add(logoutQuestion);
			
			Panel logoutQuestionLine1 = new Panel();
			Panel logoutQuestionLine2 = new Panel();
			logout.add(logoutQuestionLine1, BorderLayout.NORTH);
			logout.add(logoutQuestionLine2, BorderLayout.SOUTH);
			
			// �α׾ƿ� Label
			logoutQuestionLine1.add(new Label("������ �α׾ƿ� �Ͻðڽ��ϱ�?"));
			
			// �α׾ƿ� Button
			Button logoutOk = new Button("��");
			Button logoutNo = new Button("�ƴϿ�");
			logoutQuestionLine2.add(logoutOk);
			logoutQuestionLine2.add(logoutNo);
			logoutOk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textID.setText(" ");
					textID.setText("");
					textPassword.setText(" ");
					textPassword.setText("");
					card.show(allPanel, "loginPanel");
					message("=" + chattingId + "=" + chattingName);
					logout.dispose();
				}
			});
			logoutNo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					logout.dispose();
				}
			});
			
			logout.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					logout.dispose();
				}
			});
			logout.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
			logout.setVisible(true);
		} // logout action end...
		
		// �޽��� ȭ�鿡�� ä���� ������ ��
		if(e.getActionCommand().equals("ä��")) {
			messengerSearchPanel.setVisible(false);
			messengerOptionPanel.setVisible(false);
			messengerChatListPanel.setVisible(true);
		}
		
		// �޽��� ȭ�鿡�� ã�⸦ ������ ��
		if(e.getActionCommand().equals("ã��")) {
			messengerChatListPanel.setVisible(false);
			messengerOptionPanel.setVisible(false);
			messengerSearchPanel.setVisible(true);
		}
		
		// �޽��� ȭ�鿡�� ������ ������ ��
		if(e.getActionCommand().equals("����")) {
			messengerChatListPanel.setVisible(false);
			messengerSearchPanel.setVisible(false);
			messengerOptionPanel.setVisible(true);
		}
		
		// ã�� ȭ�鿡�� �˻��� ��������
		if(e.getActionCommand().equals("�˻�")) {
			outputMsg = "";
			String msg = messengerSearchID.getText();
			if(msg.equals("")) {
				return;
			}
			
			String[] arr = search();
			
			if(arr[1].equals("-1")) {
				Dialog errorMessage = new Dialog(this, "����", true);
				Panel errorMessageLine1 = new Panel();
				Panel errorMessageLine2 = new Panel();
				errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
				errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("Ȯ��");
				errorMessageLine1.add(new Label("�˻��� ID�� �����ϴ�."));
				errorMessageLine2.add(errorMessageButton);
				errorMessageButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						errorMessage.dispose();
					}
				});
				
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			} else {
				Dialog searchMessage = new Dialog(this, "�˻� ���", true);
				searchMessage.setLayout(new GridLayout(3, 0));
				Panel serachMessageLine1 = new Panel();
				Panel serachMessageLine2 = new Panel();
				Panel serachMessageLine3 = new Panel();
				searchMessage.add(serachMessageLine1);
				searchMessage.add(serachMessageLine2);
				searchMessage.add(serachMessageLine3);
				Button errorMessageButton = new Button("Ȯ��");
				serachMessageLine1.add(new Label("ID : " + arr[2]));
				serachMessageLine2.add(new Label("�̸� : " + arr[3]));
				serachMessageLine3.add(errorMessageButton);
				errorMessageButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						searchMessage.dispose();
					}
				});
				
				searchMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						searchMessage.dispose();
					}
				});
				searchMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 150);
				searchMessage.setVisible(true);
			}
		}
		
		// ���� ȭ�鿡�� ���� �Ϸ� ��ư�� ��������
		if(e.getActionCommand().equals("���� �Ϸ�")) {
			try {
				int num = Integer.parseInt(fontSize.getText());
				
				messengerChatbox.setFont(new Font("", 0, num){});
				
				if(cbg.getSelectedCheckbox().getLabel().equals("�Ͼ��")) {
					messengerChatbox.setBackground(Color.WHITE);
				} else if(cbg.getSelectedCheckbox().getLabel().equals("�ϴû�")) {
					messengerChatbox.setBackground(new Color(196, 239, 253));
				} else if(cbg.getSelectedCheckbox().getLabel().equals("���λ�")) {
					messengerChatbox.setBackground(new Color(182, 254, 177));
				}
			} catch (Exception e1) { // ���ڸ��� �ٸ��� �Է��Ҷ�
				Dialog errorMessage = new Dialog(this, "����", true);
				Panel errorMessageLine1 = new Panel();
				Panel errorMessageLine2 = new Panel();
				errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
				errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("Ȯ��");
				errorMessageLine2.add(errorMessageButton);
				if(fontSize.getText().equals("")) {
					errorMessageLine1.add(new Label("�۾� ũ�⸦ �Է����ּ���"));
				} else {
					errorMessageLine1.add(new Label("�۾� ũ��� ���ڸ� �Է��ϼ���"));	
				}
				errorMessageButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						errorMessage.dispose();
					}
				});
				
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			}
		}
		
	} // setting actionPerformed end...

	// GUI
	public Client() {
		
		
		// �α��� ȭ�� ����
		Panel loginPanel = new Panel();
		loginPanel.setLayout(new GridLayout(10, 1));
		allPanel.add("loginPanel", loginPanel);
		
		// ȭ�� ���� ����
		Panel[] loginLine = new Panel[10];
		for(int i = 0; i < loginLine.length; i++) {
			loginLine[i] = new Panel();
			loginPanel.add(loginLine[i]);
		}

		// ID, Password Label
		Label labelID = new Label("��ID����");
		loginLine[4].add(labelID);
		Label passwordLabel = new Label("Password");
		loginLine[5].add(passwordLabel);

		// ID, Password TextField 
		
		loginLine[4].add(textID);
		textPassword.setEchoChar('*');
		loginLine[5].add(textPassword);

		// �α���, ȸ������ ��ư
		JButton buttonLogin = new JButton("�α���");
		loginLine[6].add(buttonLogin);
		buttonLogin.addActionListener(this);
		JButton buttonRegister = new JButton("ȸ������");
		buttonRegister.addActionListener(this);
		loginLine[7].add(buttonRegister);
		
		// ���� ������, ���н� ��Ʈ
		if(boo) {
			loginLine[2].add(new Label("ȯ���մϴ�."));
		} else {
			loginLine[2].add(new Label("���� ���ῡ �����߽��ϴ�."));
		}
		
		// ���� ���н� ��� ��Ȱ��ȭ
		textID.setEnabled(boo);
		textPassword.setEnabled(boo);
		buttonLogin.setEnabled(boo);
		buttonRegister.setEnabled(boo);

		/*********************************************************************/

		// ȸ������ ȭ�� ����
		Panel registerPanel = new Panel();
		registerPanel.setLayout(new GridLayout(10, 1));
		allPanel.add("registerPanel", registerPanel);

		// ȭ�� ���� ����
		Panel[] registerLine = new Panel[10];
		for(int i = 0; i < registerLine.length; i++) {
			registerLine[i] = new Panel();
			registerPanel.add(registerLine[i]);
		}

		// �̸�, ID, Password, Password Ȯ�� Label
		registerLine[2].add(new Label("�̸�"));
		registerLine[3].add(new Label("ID"));
		registerLine[4].add(new Label("Password"));
		registerLine[5].add(new Label("Password Ȯ��"));

		// �̸�, ID, Password, Password Ȯ�� TextField
		registerPassword.setEchoChar('*');
		registerPasswordRe.setEchoChar('*');
		registerLine[2].add(registerName);
		registerLine[3].add(registerId);
		registerLine[4].add(registerPassword);
		registerLine[5].add(registerPasswordRe);
		
		// ����, ��� ��ư
		JButton registerOk = new JButton("����");
		JButton registerCancel = new JButton("���");
		registerLine[7].add(registerOk);
		registerLine[8].add(registerCancel);
		registerOk.addActionListener(this);
		registerCancel.addActionListener(this);
		
		/*********************************************************************/
		
		// �α��� ��, ä�� ȭ��
		Panel messengerPanel = new Panel();
		Panel messengerRightPanel = new Panel(card); // �޽��� ȭ�� ������ �г�
		
		Panel messengerLeftPanel = new Panel(new GridLayout(10, 1)); // ���� �޴� ��ư
		messengerPanel.setLayout(new BorderLayout());
		messengerPanel.add(messengerRightPanel, BorderLayout.CENTER);
		messengerRightPanel.add(messengerChatListPanel);
		messengerPanel.add(messengerLeftPanel, BorderLayout.WEST);
		allPanel.add("messengerPanel", messengerPanel);
		
		Panel[] messengerLeftLine = new Panel[10];
		for(int i = 0; i < messengerLeftLine.length; i++) {
			messengerLeftLine[i] = new Panel(new BorderLayout());
			messengerLeftPanel.add(messengerLeftLine[i]);
		}
		
		// ä��, ã��, ����, �α׾ƿ� ��ư
		JButton messengerList = new JButton("ä��");
		JButton messengerSearch = new JButton("ã��");
		JButton messengerOption = new JButton("����");
		JButton messengerLogout = new JButton("�α׾ƿ�");
		messengerLeftLine[0].add(messengerList);
		messengerLeftLine[1].add(messengerSearch);
		messengerLeftLine[2].add(messengerOption);
		messengerLeftLine[9].add(messengerLogout);
		messengerList.addActionListener(this);
		messengerSearch.addActionListener(this);
		messengerOption.addActionListener(this);
		messengerLogout.addActionListener(this);
		
		
		// ä�� ȭ��
		messengerChatListPanel.add(messengerChatbox, BorderLayout.CENTER);
		messengerChatListPanel.add(messengerMessage, BorderLayout.SOUTH);
		messengerChatbox.setEditable(false);
		messengerChatbox.setBackground(Color.WHITE);
		// ä�� �޼��� �Է� �� ����Ű�� ���� ��
		messengerMessage.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					message("!" + chattingName + " : " + messengerMessage.getText());
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
		
		
		// ã�� ȭ��
		messengerRightPanel.add(messengerSearchPanel);
		messengerSearchPanel.setVisible(false);
		Panel[] messengerSearchLine = new Panel[10];
		for(int i = 0; i < messengerSearchLine.length; i++) {
			messengerSearchLine[i] = new Panel();
			messengerSearchPanel.add(messengerSearchLine[i]);
		}
		
		// ã�� ȭ�� - ID�˻�
		messengerSearchLine[3].add(new Label("ID�� �Է����ּ���."));
		messengerSearchLine[4].add(messengerSearchID);
		Button messengerSearchButton = new Button("�˻�");
		messengerSearchLine[5].add(messengerSearchButton);
		messengerSearchButton.addActionListener(this);
		
		// ���� ȭ��
		messengerRightPanel.add(messengerOptionPanel);
		messengerOptionPanel.setVisible(false);
		Panel[] messengerOptionLine = new Panel[10];
		for(int i = 0; i < messengerOptionLine.length; i++) {
			messengerOptionLine[i] = new Panel();
			messengerOptionPanel.add(messengerOptionLine[i]);
		}
		
		// ���� ȭ�� - �۾� ũ��, ä�� ����
		messengerOptionLine[2].add(new Label("�۾� ũ��"));
		
		messengerOptionLine[3].add(fontSize);
		fontSize.setText("12");
		messengerOptionLine[4].add(new Label("ä�� ����"));
		
		
		Checkbox whiteColor = new Checkbox("�Ͼ��", true, cbg);
		Checkbox blueColor = new Checkbox("�ϴû�", false, cbg);
		Checkbox greenColor = new Checkbox("���λ�", false, cbg);
		Button optionComplete = new Button("���� �Ϸ�");
		messengerOptionLine[5].add(whiteColor);
		messengerOptionLine[5].add(blueColor);
		messengerOptionLine[5].add(greenColor);
		messengerOptionLine[7].add(optionComplete);
		
		optionComplete.addActionListener(this);
		
		/*********************************************************************/
		
		card.show(allPanel, "loginPanel");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(boo) {
					try {
						message("~" + InetAddress.getLocalHost().getHostAddress() + "~" + chattingId + "~" + chattingName);
						sock.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				boo = false;
				dispose();
			}
		});
		add(allPanel);
		setBounds(100, 100, 350, 500);
		setTitle("Potato Messenger");
		setVisible(true);
	} // Client end...
	
	// �������� ������ ��������
	public static void serverDisconnection(Client me) {
		Dialog disconnection = new Dialog(me, "����", true);
		
		WindowAdapter win = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(boo) {
					try {
						sock.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				disconnection.dispose();
				me.dispose();
			}
		};
		
		Panel disconnectionPanel = new Panel(new BorderLayout());
		
		JLabel disconnectionLabel = new JLabel("<html>�������� ������ ������ϴ�.<br/>���α׷��� ���� �մϴ�.</html>");
		disconnectionLabel.setHorizontalAlignment(JLabel.CENTER);
		disconnectionPanel.add(disconnectionLabel, BorderLayout.CENTER);
		Button disconnectionButton = new Button("����");
		disconnectionPanel.add(disconnectionButton, BorderLayout.SOUTH);
		disconnectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnection.dispose();
				me.dispose();
			}
		});
		
		disconnection.addWindowListener(win);
		disconnection.add(disconnectionPanel);
		disconnection.setBounds(me.getX(), (int)(me.getHeight()/1.8), me.getWidth(), 100);
		disconnection.setVisible(true);
	}

	// ���� �޼���
	public static void main(String[] args) {
		Client me;
		
		try {
			sock = new Socket("192.168.0.76", 5002);
		} catch (UnknownHostException e) {
			System.out.println("1���� ����");
			boo = false;
		} catch (IOException e) {
			System.out.println("2���� ����");
			boo = false;
		} catch (NullPointerException e) {
			System.out.println("3���� ����");
			boo = false;
		} finally {
			me = new Client();
		}
		
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			is = sock.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			
			while(true) {
				outputMsg = br.readLine();
				if(outputMsg == null) {
					break;
				} else if(outputMsg.startsWith("!")) {
					messengerChatbox.append(outputMsg.substring(1) + "\n");
				} 
			}
		} catch (SocketException e) {
			// ������ ������ ���
			if(e.getMessage().equals("Connection reset")) {
				serverDisconnection(me);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			try {
				br.close();
				isr.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	} // main end...
	
	// �޼��� ���� �޼���
	public static void message(String msg) {
		OutputStream os = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		
		try {
			
			os = sock.getOutputStream();
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
			
			if(msg.equals("")){return;}
			bw.write(msg);
			bw.newLine();
			bw.flush();
			messengerMessage.setText(" ");
			messengerMessage.setText("");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	} // message end...
	
	// ȸ������ �޼���
	public static int createMember() {
		int num = 0;
		String name = registerName.getText();
		String id = registerId.getText();
		String password = registerPassword.getText();
		String check = name + id + password;
		
		
		if(name.equals("")) {
			return -1; // �̸��� �Է����� ���� ���
		} else if(id.equals("")) {
			return -2; // ID�� �Է����� ���� ���
		} else if(password.equals("")) {
			return -3; // �н����带 �Է����� ���� ���
		} else if(!password.equals(registerPasswordRe.getText())) {
			return -4; // �н������ �н����� Ȯ���� �ٸ� ���
		} else if(check.contains("~") || 
				check.contains("!") || 
				check.contains("@") || 
				check.contains("#") || 
				check.contains("&") || 
				check.contains("=")) {
			return -6; // �ش� ���ڰ� ���� ���
		}
		
		String sendMsg = null;
		
		try {
			sendMsg = "@" + name + "@" + id + "@" + password + "@" + InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		message(sendMsg);
		
		while(true) {
			if(!outputMsg.startsWith("@")) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			break;
		}
		
		
		num = Integer.parseInt(outputMsg.substring(1));
		
		return num;
	} // createMember end...
	
	// �α��� ��� �޼���
	public static int login() {
		int num = 0;
		String id = textID.getText();
		String password = textPassword.getText();
		
		try {
			message("#" + id + "#" + password + "#" + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		while(true) {
			if(!outputMsg.startsWith("#")) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			break;
		}
		
		num = Integer.parseInt(outputMsg.substring(1, 3));
		
		return num;
	} // login end...
	
	// ã���� ID�˻� �޼���
	public static String[] search() {
		
		try {
			message("&" + messengerSearchID.getText() + "&" + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		while(true) {
			if(!outputMsg.startsWith("&")) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			break;
		}
		
		String[] arr = outputMsg.split("&"); // 0�� ����
		
		return arr;
	}// search end...
}

 