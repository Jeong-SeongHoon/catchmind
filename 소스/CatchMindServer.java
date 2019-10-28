package am;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CatchMindServer {

	ServerSocket ss;
	ArrayList<CopyClient> user;
	String[] subject = {"아름다운시절", "삼국시대", "포크레인", "연장전", "바람과함께사라지다", "눈물나는하루", "장갑차"};
	String sol;
	int count = 0;
	int exam_cnt = 0;
	
	public CatchMindServer() {
		
		user = new ArrayList<>();
		
		try {
			//서버 시작
			ss = new ServerSocket(3333);
			System.out.println("서버 풀가동!!");
			//무한루프를 돌면서 접속자를 받는다.
			while(true){
				try {
					//접속자 받기(대기상태에 빠짐)
					Socket s = ss.accept();
					//서버에 복사본 생성??
					CopyClient cc = new CopyClient(s, this);
					//복사본을 리스트에 추가
					user.add(cc);
					//복사본과 클라이언트 간 프로토콜을 주고 받을 스레드 시작
					cc.start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 제시어
	public String setSubject(){
		sol=subject[(int)(Math.random()*(subject.length))];
		return sol;
	}
	
	public void setTurn(){
		user.get((int)(Math.random()*(user.size()))).MyTun = true;
	}
	public void initTurn(){
		for(int i=0; i<user.size();i++){
			user.get(i).MyTun=false;
		}
	}

	public String[] getNames(){
		String[] str = new String[user.size()];
		
		for (int i = 0; i < str.length; i++) {
			str[i] = user.get(i).getNickName();
		}
			return str;
	}
	
	public int[] getScore(){
		int[] numScore = new int[user.size()];
		
		for (int i = 0; i < numScore.length; i++) {
			numScore[i] = user.get(i).getScore();
		}
			return numScore;
	} 
	
	public void sendProtocol(CatchMindProtocol p){
		for (int i = 0; i < user.size(); i++) {
			CopyClient cc = user.get(i);
			try {
				cc.out.writeObject(p);
				cc.out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void sort(){
		Collections.sort(user, new Comparator<CopyClient>() {

			@Override
			public int compare(CopyClient o1, CopyClient o2) {
				return (o1.score > o2.score)? -1: (o1.score > o2.score)?1:0;
			}
			
		});
		
	}
	
	
	public static void main(String[] args) {
		new CatchMindServer();

	}

}
