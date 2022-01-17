package com.hi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Member implements Serializable {
	private String name;
	private String id;
	private String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}


public class Server extends Thread {
	
	static Socket sock = null;
	static ServerSocket server = null;
	
	static File memberFile = new File("member.dat");
	static ArrayList<Member> memberList = new ArrayList<>();
	static ArrayList<Socket> list = new ArrayList<>();
	static ArrayList<String> socketList = new ArrayList<>();
	static ArrayList<String> loggingID = new ArrayList<>();
	
	
	static OutputStream os = null;
	static OutputStreamWriter osw = null;
	static BufferedWriter bw = null;
	
	static String welcome = "";
	
	
	// ä��
	public static void sayAll(String msg) throws IOException {
		for(int i = 0; i < list.size(); i++) {
			Socket sock = list.get(i);
			os = sock.getOutputStream();
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
			
			System.out.println("ä�� ���� : " + msg);
			
			bw.write("!" + msg.substring(1));
			bw.newLine();
			bw.flush();
		}
	} // chatting end...
	
	// ����
	public static void register(String msg) {
		String num = "";
		String temp = "";
		
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		String[] registerMsg = msg.split("@"); // 0�� ����, 1���� ����
		// 1 > �̸�
		// 2 > ID
		// 3 > ��й�ȣ
		
		Socket sock = null;
		
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getInetAddress().getHostAddress().equals(registerMsg[4])) {
				sock = list.get(i);
			}
		}
		
		try {
			fis = new FileInputStream(memberFile);
			ois = new ObjectInputStream(fis);
			
			ArrayList<Member> readedObject = (ArrayList<Member>) ois.readObject();
			memberList = readedObject;
			
			ois.close();
			fis.close();
			
			os = sock.getOutputStream();
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
			
			for(int i = 1; i < memberList.size(); i++) {
				temp = memberList.get(i).getId();
				if(temp.equals(registerMsg[2])) {
					num = "@-5"; // �ߺ� �� ��
					bw.write(num);
					bw.newLine();
					bw.flush();
					return;
				}
			}
			
			Member member = new Member();
			
			member.setName(registerMsg[1]);
			member.setId(registerMsg[2]);
			member.setPassword(registerMsg[3]);
			
			memberList.add(member);
			
			fos = new FileOutputStream(memberFile);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(memberList);
			
			bw.write("@+1"); // ����
			bw.newLine();
			bw.flush();
			
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println("FFNE ���� ����");
		} catch (IOException e) {
			System.out.println("IOE ���� ����");
		} catch (ClassNotFoundException e) {
			System.out.println("CNFE ���� ����");
		} 
	} // register end...
	
	// �α���
	public static void login(String msg) {
		String num = "";
		String idTemp = "";
		String passTemp = "";
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		String[] loginMsg = msg.split("#");
		
		try {
			fis = new FileInputStream(memberFile);
			ois = new ObjectInputStream(fis);
			
			ArrayList<Member> readedObject = (ArrayList<Member>) ois.readObject();
			memberList = readedObject;
			
			ois.close();
			fis.close();
			
			Socket sock = null;
			
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getInetAddress().getHostAddress().equals(loginMsg[3])) {
					sock = list.get(i);
				}
			}
			
			os = sock.getOutputStream();
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
			
			boolean logginCheck = false;
			
			for(int i = 1; i < memberList.size(); i++) {
				idTemp = memberList.get(i).getId();
				passTemp = memberList.get(i).getPassword();
				if(loginMsg[1].equals(idTemp) && loginMsg[2].equals(passTemp)) {
					// ���� �ش� id�� �α��ε� ���
					for(int j = 0; j < loggingID.size(); j++) {
						if(loginMsg[1].equals(loggingID.get(j))) {
							logginCheck = true;
							break;
						}
					}
					if(logginCheck) {
						break;
					}
					
					welcome = memberList.get(i).getName();
					loggingID.add(loginMsg[1] + "@" + welcome);
					
					num = "#+1#" + memberList.get(i).getName();
					bw.write(num); // ����
					bw.newLine();
					bw.flush();
					try {
						Thread.sleep(300);
						sayAll("=== " + welcome + "���� �����ϼ̽��ϴ�. ==");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return;
				}
			}
			
			
			
			fos = new FileOutputStream(memberFile);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(memberList);
			
			if(logginCheck) {
				num = "#-3"; // �ߺ� �α���
			} else {
				num = "#-2"; // �ܼ� ����(id, password�� ���� �ʴ� ���)
			}
			
			bw.write(num); // ����
			bw.newLine();
			bw.flush();
			
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println("FFNE �α��� ����");
		} catch (IOException e) {
			System.out.println("IOE �α��� ����");
		} catch (ClassNotFoundException e) {
			System.out.println("CNFE �α��� ����");
		} 
	} // login end...
		
	// ���̵� �˻�
	public static void search(String msg) {
		String idTemp = "";
		String nameTemp = "";
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		String[] searchMsg = msg.split("&");
		
		
		
		try {
			fis = new FileInputStream(memberFile);
			ois = new ObjectInputStream(fis);
			
			ArrayList<Member> readedObject = (ArrayList<Member>) ois.readObject();
			memberList = readedObject;
			
			ois.close();
			fis.close();
			
			Socket sock = null;
			
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getInetAddress().getHostAddress().equals(searchMsg[2])) {
					sock = list.get(i);
				}
			}
			
			
			os = sock.getOutputStream();
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
			
			for(int i = 1; i < memberList.size(); i++) {
				idTemp = memberList.get(i).getId();
				nameTemp = memberList.get(i).getName();
				if(searchMsg[1].equals(idTemp)) {
					bw.write("&+1" + "&" + idTemp + "&" + nameTemp); // ����
					bw.newLine();
					bw.flush();
					return;
				}
			}
			
			fos = new FileOutputStream(memberFile);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(memberList);
			
			bw.write("&-1"); // ����
			bw.newLine();
			bw.flush();
			
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println("FFNE �α��� ����");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("IOE �α��� ����");
		} catch (ClassNotFoundException e) {
			System.out.println("CNFE �α��� ����");
		} 
	} // search end...
	
	


	
	
	@Override
	public void run() {
		InputStream is = null;
		OutputStream os = null;
		InputStreamReader isr = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		boolean boo = false;
		
		try {
			is = sock.getInputStream();
			os = sock.getOutputStream();
			isr = new InputStreamReader(is);
			osw = new OutputStreamWriter(os);
			br = new BufferedReader(isr);
			bw = new BufferedWriter(osw);
			
			while(true) {
				String msg = "";
				msg = br.readLine();
				
				if(msg.startsWith("~")) { // ���� ����
					String[] exitMessenger = msg.split("~");
					for(int i = 0; i < list.size(); i++) {
						if(exitMessenger[1].equals(list.get(i).getInetAddress().toString().substring(1))) {
							System.out.println(list.get(i).getInetAddress().toString().substring(1) + "���� ����");
							list.remove(i);
							for(int j = 0; j < loggingID.size(); j++) {
								if(exitMessenger[2].equals(loggingID.get(j))) {
									if(!exitMessenger[2].equals("chattingNameDefault")) {
										sayAll("=== " + exitMessenger[3] + "���� �����ϼ̽��ϴ�. ==");
									}
									loggingID.remove(j);
								}
							}
							boo = true;
							break;
						}
					}
					if(boo) {
						break;
					}
				} else if(msg.startsWith("!")) { // ä��
					sayAll(msg);
				} else if(msg.startsWith("@")) { // ȸ������
					register(msg);
				} else if(msg.startsWith("#")) { // �α���
					login(msg);
				} else if(msg.startsWith("&")) { // �˻�
					search(msg);
				} else if(msg.startsWith("=")) { // �α׾ƿ�
					String[] temp = msg.split("=");
					for(int i = 0; i < loggingID.size(); i++) {
						String[] temp2 = loggingID.get(i).split("@");
						if(temp[1].equals(temp2[0])) {
							sayAll("=== " + temp2[1] + "���� �����ϼ̽��ϴ�. ==");
							break;
						}
					}
					
				}
			}
		} catch (NullPointerException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				br.close();
				osw.close();
				isr.close();
				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		
		if(!memberFile.exists()) {
			try {
				memberFile.createNewFile();
				Member member = new Member();
				member.setId("");
				member.setName("");
				member.setPassword("");
				
				memberList.add(member);
				
				FileOutputStream fos = null;
				ObjectOutputStream oos = null;
				
				try {
					fos = new FileOutputStream(memberFile);
					oos = new ObjectOutputStream(fos);
					
					oos.writeObject(memberList);
					
					oos.close();
					fos.close();
				} catch (FileNotFoundException e2) {
					e2.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				System.out.println("���� ���� ����");
			}
		}
		
		System.out.println("������ ���������� ���Ƚ��ϴ�.");
		
		try {
			server = new ServerSocket(5002);
			while(true) {
				Server thr = new Server();
				thr.sock = server.accept();
				System.out.println(sock.getInetAddress().getHostAddress() + "����");
				thr.start();
				list.add(thr.sock);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(server != null) {
					server.close();	
				}
				if(!sock.isClosed()) {
					sock.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
