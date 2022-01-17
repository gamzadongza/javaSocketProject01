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
	static String chattingName = "chattingNameDefault"; // 내 채팅 이름
	static String chattingId = "";
	Panel messengerChatListPanel = new Panel(new BorderLayout()); // 채팅 리스트
	Panel messengerSearchPanel = new Panel(new GridLayout(10, 1)); // 찾기
	Panel messengerOptionPanel = new Panel(new GridLayout(10, 1)); // 설정
	
	static TextField registerName = new TextField(15);
	static TextField registerId = new TextField(15);
	static TextField registerPassword = new TextField(15);
	static TextField registerPasswordRe = new TextField(15);  // 패스워드 재확인
	
	static TextArea messengerChatbox = new TextArea(); // 메신저 채팅화면
	static TextField messengerMessage = new TextField(); // 메신저 채팅입력
	
	static Socket sock;
	static String outputMsg = ""; // 서버에서 받아오는 메세지
	
	static TextField textID = new TextField(20); // 로그인 화면 아이디
	static TextField textPassword = new TextField(20); // 로그인 화면 패스워드
	
	static TextField fontSize = new TextField(10); // 설정 글씨 크기
	static CheckboxGroup cbg = new CheckboxGroup(); // 설정 색변경 체크박스
	
	static TextField messengerSearchID = new TextField(20); // ID 검색
	
	
	WindowAdapter win = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			
			dispose();
		}
	};
	
	
	// 전체 패널
	Panel allPanel = new Panel(card);
	
	
	// actionListener 메서드
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// 로그인 화면에서 회원가입을 눌렀을 때
		if(e.getActionCommand().equals("회원가입")) {
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
		
		// 회원가입 화면에서 취소를 눌렀을 때
		if(e.getActionCommand().equals("취소")) {
			textID.setText(" ");
			textID.setText("");
			textPassword.setText(" ");
			textPassword.setText("");
			card.show(allPanel, "loginPanel");
		}
		
		// 회원가입 화면에서 가입을 눌렀을 때
		if(e.getActionCommand().equals("가입")) {
			outputMsg = "";
			int temp = createMember();
			
			if(temp < 0) { // 에러 메세지
				Dialog errorMessage = new Dialog(this, "에러", true);
				Panel errorMessageLine1 = new Panel();
				Panel errorMessageLine2 = new Panel();
				errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
				errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("확인");
				errorMessageLine2.add(errorMessageButton);
				errorMessageButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						errorMessage.dispose();
					}
				});
				
				if(temp == -1) {
					errorMessageLine1.add(new Label("이름을 입력해주세요"));
				} else if(temp == -2) {
					errorMessageLine1.add(new Label("ID를 입력해주세요"));
				} else if(temp == -3) {
					errorMessageLine1.add(new Label("비밀번호를 입력해주세요"));
				} else if(temp == -4) {
					errorMessageLine1.add(new Label("비밀번호가 다릅니다!"));
				} else if(temp == -5) {
					errorMessageLine1.add(new Label("이미 존재하는 ID입니다."));
				} else if(temp == -6) {
					errorMessageLine1.add(new Label("입력칸에 !, @, #, &, ~, =는 사용하실 수 없습니다."));
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
				Dialog registerComplete = new Dialog(this, "가입 완료", true);
				Panel registerCompleteLine1 = new Panel();
				Panel registerCompleteLine2 = new Panel();
				registerComplete.add(registerCompleteLine1, BorderLayout.NORTH);
				registerComplete.add(registerCompleteLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("확인");
				registerCompleteLine1.add(new Label("가입되었습니다."));
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
		
		// 로그인 버튼을 눌렀을 때
		if(e.getActionCommand().equals("로그인")) {
			outputMsg = "";
			messengerChatbox.setText(" ");
			messengerChatbox.setText("");
			
			// 에러 메세지
			Dialog errorMessage = new Dialog(this, "에러", true);
			Panel errorMessageLine1 = new Panel();
			Panel errorMessageLine2 = new Panel();
			errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
			errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
			Button errorMessageButton = new Button("확인");
			errorMessageLine2.add(errorMessageButton);
			errorMessageButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorMessage.dispose();
				}
			});
			
			int num = 0;
			
			// 입력하지 않은 경우
			if(textID.getText().equals("") && textPassword.getText().equals("")) {
				errorMessageLine1.add(new Label("ID, 비밀번호를 입력해주세요"));
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			} else if(textID.getText().equals("")) {
				errorMessageLine1.add(new Label("ID를 입력해주세요"));
				errorMessage.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						errorMessage.dispose();
					}
				});
				errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
				errorMessage.setVisible(true);
			} else if(textPassword.getText().equals("")) {
				errorMessageLine1.add(new Label("비밀번호를 입력해주세요"));
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
					errorMessageLine1.add(new Label("ID, 비밀번호를 확인해주세요"));
					errorMessage.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							errorMessage.dispose();
						}
					});
					errorMessage.setBounds(getX(), (int)(getHeight()/1.8), getWidth(), 100);
					errorMessage.setVisible(true);
				} else {
					errorMessageLine1.add(new Label("해당 계정은 이미 로그인중입니다."));
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
		
		// 메신저 화면에서 로그아웃을 눌렀을 때
		if(e.getActionCommand().equals("로그아웃")) {
			// 로그아웃 JDialog
			Dialog logout = new Dialog(this, "로그아웃", true);
			Panel logoutQuestion = new Panel();
			logoutQuestion.setLayout(new BorderLayout());
			logout.add(logoutQuestion);
			
			Panel logoutQuestionLine1 = new Panel();
			Panel logoutQuestionLine2 = new Panel();
			logout.add(logoutQuestionLine1, BorderLayout.NORTH);
			logout.add(logoutQuestionLine2, BorderLayout.SOUTH);
			
			// 로그아웃 Label
			logoutQuestionLine1.add(new Label("정말로 로그아웃 하시겠습니까?"));
			
			// 로그아웃 Button
			Button logoutOk = new Button("네");
			Button logoutNo = new Button("아니오");
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
		
		// 메신저 화면에서 채팅을 눌렀을 때
		if(e.getActionCommand().equals("채팅")) {
			messengerSearchPanel.setVisible(false);
			messengerOptionPanel.setVisible(false);
			messengerChatListPanel.setVisible(true);
		}
		
		// 메신저 화면에서 찾기를 눌렀을 때
		if(e.getActionCommand().equals("찾기")) {
			messengerChatListPanel.setVisible(false);
			messengerOptionPanel.setVisible(false);
			messengerSearchPanel.setVisible(true);
		}
		
		// 메신저 화면에서 설정을 눌렀을 때
		if(e.getActionCommand().equals("설정")) {
			messengerChatListPanel.setVisible(false);
			messengerSearchPanel.setVisible(false);
			messengerOptionPanel.setVisible(true);
		}
		
		// 찾기 화면에서 검색을 눌렀을때
		if(e.getActionCommand().equals("검색")) {
			outputMsg = "";
			String msg = messengerSearchID.getText();
			if(msg.equals("")) {
				return;
			}
			
			String[] arr = search();
			
			if(arr[1].equals("-1")) {
				Dialog errorMessage = new Dialog(this, "에러", true);
				Panel errorMessageLine1 = new Panel();
				Panel errorMessageLine2 = new Panel();
				errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
				errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("확인");
				errorMessageLine1.add(new Label("검색한 ID가 없습니다."));
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
				Dialog searchMessage = new Dialog(this, "검색 결과", true);
				searchMessage.setLayout(new GridLayout(3, 0));
				Panel serachMessageLine1 = new Panel();
				Panel serachMessageLine2 = new Panel();
				Panel serachMessageLine3 = new Panel();
				searchMessage.add(serachMessageLine1);
				searchMessage.add(serachMessageLine2);
				searchMessage.add(serachMessageLine3);
				Button errorMessageButton = new Button("확인");
				serachMessageLine1.add(new Label("ID : " + arr[2]));
				serachMessageLine2.add(new Label("이름 : " + arr[3]));
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
		
		// 설정 화면에서 설정 완료 버튼을 눌렀을때
		if(e.getActionCommand().equals("설정 완료")) {
			try {
				int num = Integer.parseInt(fontSize.getText());
				
				messengerChatbox.setFont(new Font("", 0, num){});
				
				if(cbg.getSelectedCheckbox().getLabel().equals("하얀색")) {
					messengerChatbox.setBackground(Color.WHITE);
				} else if(cbg.getSelectedCheckbox().getLabel().equals("하늘색")) {
					messengerChatbox.setBackground(new Color(196, 239, 253));
				} else if(cbg.getSelectedCheckbox().getLabel().equals("연두색")) {
					messengerChatbox.setBackground(new Color(182, 254, 177));
				}
			} catch (Exception e1) { // 숫자말고 다른거 입력할때
				Dialog errorMessage = new Dialog(this, "에러", true);
				Panel errorMessageLine1 = new Panel();
				Panel errorMessageLine2 = new Panel();
				errorMessage.add(errorMessageLine1, BorderLayout.NORTH);
				errorMessage.add(errorMessageLine2, BorderLayout.SOUTH);
				Button errorMessageButton = new Button("확인");
				errorMessageLine2.add(errorMessageButton);
				if(fontSize.getText().equals("")) {
					errorMessageLine1.add(new Label("글씨 크기를 입력해주세요"));
				} else {
					errorMessageLine1.add(new Label("글씨 크기는 숫자만 입력하세요"));	
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
		
		
		// 로그인 화면 구성
		Panel loginPanel = new Panel();
		loginPanel.setLayout(new GridLayout(10, 1));
		allPanel.add("loginPanel", loginPanel);
		
		// 화면 간격 구성
		Panel[] loginLine = new Panel[10];
		for(int i = 0; i < loginLine.length; i++) {
			loginLine[i] = new Panel();
			loginPanel.add(loginLine[i]);
		}

		// ID, Password Label
		Label labelID = new Label("　ID　　");
		loginLine[4].add(labelID);
		Label passwordLabel = new Label("Password");
		loginLine[5].add(passwordLabel);

		// ID, Password TextField 
		
		loginLine[4].add(textID);
		textPassword.setEchoChar('*');
		loginLine[5].add(textPassword);

		// 로그인, 회원가입 버튼
		JButton buttonLogin = new JButton("로그인");
		loginLine[6].add(buttonLogin);
		buttonLogin.addActionListener(this);
		JButton buttonRegister = new JButton("회원가입");
		buttonRegister.addActionListener(this);
		loginLine[7].add(buttonRegister);
		
		// 연결 성공시, 실패시 멘트
		if(boo) {
			loginLine[2].add(new Label("환영합니다."));
		} else {
			loginLine[2].add(new Label("서버 연결에 실패했습니다."));
		}
		
		// 연결 실패시 모두 비활성화
		textID.setEnabled(boo);
		textPassword.setEnabled(boo);
		buttonLogin.setEnabled(boo);
		buttonRegister.setEnabled(boo);

		/*********************************************************************/

		// 회원가입 화면 구성
		Panel registerPanel = new Panel();
		registerPanel.setLayout(new GridLayout(10, 1));
		allPanel.add("registerPanel", registerPanel);

		// 화면 간격 구성
		Panel[] registerLine = new Panel[10];
		for(int i = 0; i < registerLine.length; i++) {
			registerLine[i] = new Panel();
			registerPanel.add(registerLine[i]);
		}

		// 이름, ID, Password, Password 확인 Label
		registerLine[2].add(new Label("이름"));
		registerLine[3].add(new Label("ID"));
		registerLine[4].add(new Label("Password"));
		registerLine[5].add(new Label("Password 확인"));

		// 이름, ID, Password, Password 확인 TextField
		registerPassword.setEchoChar('*');
		registerPasswordRe.setEchoChar('*');
		registerLine[2].add(registerName);
		registerLine[3].add(registerId);
		registerLine[4].add(registerPassword);
		registerLine[5].add(registerPasswordRe);
		
		// 가입, 취소 버튼
		JButton registerOk = new JButton("가입");
		JButton registerCancel = new JButton("취소");
		registerLine[7].add(registerOk);
		registerLine[8].add(registerCancel);
		registerOk.addActionListener(this);
		registerCancel.addActionListener(this);
		
		/*********************************************************************/
		
		// 로그인 후, 채팅 화면
		Panel messengerPanel = new Panel();
		Panel messengerRightPanel = new Panel(card); // 메신저 화면 오른쪽 패널
		
		Panel messengerLeftPanel = new Panel(new GridLayout(10, 1)); // 왼쪽 메뉴 버튼
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
		
		// 채팅, 찾기, 설정, 로그아웃 버튼
		JButton messengerList = new JButton("채팅");
		JButton messengerSearch = new JButton("찾기");
		JButton messengerOption = new JButton("설정");
		JButton messengerLogout = new JButton("로그아웃");
		messengerLeftLine[0].add(messengerList);
		messengerLeftLine[1].add(messengerSearch);
		messengerLeftLine[2].add(messengerOption);
		messengerLeftLine[9].add(messengerLogout);
		messengerList.addActionListener(this);
		messengerSearch.addActionListener(this);
		messengerOption.addActionListener(this);
		messengerLogout.addActionListener(this);
		
		
		// 채팅 화면
		messengerChatListPanel.add(messengerChatbox, BorderLayout.CENTER);
		messengerChatListPanel.add(messengerMessage, BorderLayout.SOUTH);
		messengerChatbox.setEditable(false);
		messengerChatbox.setBackground(Color.WHITE);
		// 채팅 메세지 입력 후 엔터키를 쳤을 때
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
		
		
		// 찾기 화면
		messengerRightPanel.add(messengerSearchPanel);
		messengerSearchPanel.setVisible(false);
		Panel[] messengerSearchLine = new Panel[10];
		for(int i = 0; i < messengerSearchLine.length; i++) {
			messengerSearchLine[i] = new Panel();
			messengerSearchPanel.add(messengerSearchLine[i]);
		}
		
		// 찾기 화면 - ID검색
		messengerSearchLine[3].add(new Label("ID를 입력해주세요."));
		messengerSearchLine[4].add(messengerSearchID);
		Button messengerSearchButton = new Button("검색");
		messengerSearchLine[5].add(messengerSearchButton);
		messengerSearchButton.addActionListener(this);
		
		// 설정 화면
		messengerRightPanel.add(messengerOptionPanel);
		messengerOptionPanel.setVisible(false);
		Panel[] messengerOptionLine = new Panel[10];
		for(int i = 0; i < messengerOptionLine.length; i++) {
			messengerOptionLine[i] = new Panel();
			messengerOptionPanel.add(messengerOptionLine[i]);
		}
		
		// 설정 화면 - 글씨 크기, 채팅 배경색
		messengerOptionLine[2].add(new Label("글씨 크기"));
		
		messengerOptionLine[3].add(fontSize);
		fontSize.setText("12");
		messengerOptionLine[4].add(new Label("채팅 배경색"));
		
		
		Checkbox whiteColor = new Checkbox("하얀색", true, cbg);
		Checkbox blueColor = new Checkbox("하늘색", false, cbg);
		Checkbox greenColor = new Checkbox("연두색", false, cbg);
		Button optionComplete = new Button("설정 완료");
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
	
	// 서버와의 연결이 끊겼을때
	public static void serverDisconnection(Client me) {
		Dialog disconnection = new Dialog(me, "에러", true);
		
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
		
		JLabel disconnectionLabel = new JLabel("<html>서버와의 연결이 끊겼습니다.<br/>프로그램을 종료 합니다.</html>");
		disconnectionLabel.setHorizontalAlignment(JLabel.CENTER);
		disconnectionPanel.add(disconnectionLabel, BorderLayout.CENTER);
		Button disconnectionButton = new Button("종료");
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

	// 메인 메서드
	public static void main(String[] args) {
		Client me;
		
		try {
			sock = new Socket("192.168.0.76", 5002);
		} catch (UnknownHostException e) {
			System.out.println("1연결 실패");
			boo = false;
		} catch (IOException e) {
			System.out.println("2연결 실패");
			boo = false;
		} catch (NullPointerException e) {
			System.out.println("3연결 실패");
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
			// 서버가 끊겼을 경우
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
	
	// 메세지 전송 메서드
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
	
	// 회원가입 메서드
	public static int createMember() {
		int num = 0;
		String name = registerName.getText();
		String id = registerId.getText();
		String password = registerPassword.getText();
		String check = name + id + password;
		
		
		if(name.equals("")) {
			return -1; // 이름을 입력하지 않은 경우
		} else if(id.equals("")) {
			return -2; // ID를 입력하지 않은 경우
		} else if(password.equals("")) {
			return -3; // 패스워드를 입력하지 않은 경우
		} else if(!password.equals(registerPasswordRe.getText())) {
			return -4; // 패스워드와 패스워드 확인이 다른 경우
		} else if(check.contains("~") || 
				check.contains("!") || 
				check.contains("@") || 
				check.contains("#") || 
				check.contains("&") || 
				check.contains("=")) {
			return -6; // 해당 문자가 들어가는 경우
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
	
	// 로그인 기능 메서드
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
	
	// 찾기의 ID검색 메서드
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
		
		String[] arr = outputMsg.split("&"); // 0은 공백
		
		return arr;
	}// search end...
}

 