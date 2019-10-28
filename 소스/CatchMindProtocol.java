package am;


import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

public class CatchMindProtocol implements Serializable{
	int cmd; // 1.접속
			 // 2. 게임시작
			 // 3. 게임중
			 // 4. 게임하나 끝
			 // 5. 프로그램 종료
	
	String[] name; // 접속자 수 
	int[] numScore; //접속자 점수(전광판 역할)
	
	int score; // 게임 점수(자기점수)
	int size;
	int x,y;
	int rank;
	boolean click;
	boolean check;
	int exam_cnt;
	Point p;
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	boolean MyTun; // 그림 그리는 턴
	String msg; // 메세지
	Color color;
	
	public boolean isClick() {
		return click;
	}
	public int[] getNumScore() {
		return numScore;
	}
	public void setNumScore(int[] numScore) {
		this.numScore = numScore;
	}
	public void setClick(boolean click) {
		this.click = click;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public String[] getName() {
		return name;
	}
	public void setName(String[] name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public boolean isMyTun() {
		return MyTun;
	}
	public void setMyTun(boolean myTun) {
		MyTun = myTun;
	}
	public boolean getMyTun() {
		if(MyTun){
			return true;
		}else{
			return false;
		}
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
