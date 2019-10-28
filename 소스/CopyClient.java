package am;


import java.awt.Image;
import java.awt.Point;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CopyClient extends Thread{

   ObjectOutputStream out;
   ObjectInputStream in;
   Socket s;
   CatchMindServer server;
   String nickName;
   String[] subject;
   
   String[] name; // 접속자 수 
   int score=0; // 게임 점수
   boolean MyTun; // 그림 그리는 턴
   String msg; // 메세지
   int rank=0;
   
   public CopyClient(Socket s, CatchMindServer server) {

      this.s = s;
      this.server = server;
      
      try {
         out = new ObjectOutputStream(s.getOutputStream());
         in = new ObjectInputStream(s.getInputStream());
         
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   public String getNickName(){
	   return nickName;
   }
   public int getScore(){
	   return score;
   }
   
   @Override
   public void run() {
      
      bk:while(true){
         try {
            CatchMindProtocol cp = (CatchMindProtocol) in.readObject();
            switch(cp.getCmd()){
            
            case 1:
            	//닉네임 얻어옴
               nickName = cp.getMsg();
               cp.setName(server.getNames());
               cp.setSize(server.user.size());
               cp.setNumScore(server.getScore());
               server.sendProtocol(cp);
               break;
               
            case 2:
                if(server.exam_cnt ==0)
                	server.exam_cnt = cp.exam_cnt;
               cp = new CatchMindProtocol();
               cp.setCmd(2);
               cp.setMsg(server.setSubject());
               cp.setName(server.getNames());
               cp.setNumScore(server.getScore());
               server.initTurn();
               server.setTurn();
               for(int i = 0; i<server.user.size();i++){
                  System.out.println(i+"번째 : "+server.user.get(i).MyTun);
                     cp.setMyTun(server.user.get(i).MyTun);
                     server.user.get(i).out.writeObject(cp);
                     server.user.get(i).out.flush();
               }
               
               break;
               
            case 3:
               if(cp.isClick()){
                  server.sendProtocol(cp);
               }
               if(cp.isCheck() && server.sol.equals(cp.getMsg())){
                  System.out.println(server.count);
                  server.count++;
                  if(server.count >= server.exam_cnt){
                     cp.setCmd(5);
                     this.score+=100;
                     server.sort();
                     //점수및 종료 프로토콜 전송
                     for (int i = 0; i < server.user.size(); i++) {
                    	server.user.get(i).rank = i+1;
                        cp.setScore(server.user.get(i).score);
                        cp.setRank(server.user.get(i).rank+1);
                        server.user.get(i).out.writeObject(cp);
                        server.user.get(i).out.flush();

                     }
                  }else {
                     cp.setCmd(4);
                     this.score+=100;
                     System.out.println("갱신하자 : "+cp.cmd);
                     cp.setScore(score);
                     out.writeObject(cp);
                     out.flush();
                  }
                     
               }
               break;
               
            case 5:
            	System.out.println(nickName+"의 순위는 : "+rank);
                cp.setRank(rank);
            	server.user.remove(this);
                out.writeObject(cp);
                if(in != null){
                   in.close();
                }
                if(out != null){
                   out.close();
                }
                if(s !=null){
                   s.close();
                }
                server.count=0;
         
                break bk;
            }
            System.out.println(server.user.indexOf(this)+"지금은 몇번 ? "+ cp.getCmd());
         
            
         } catch (Exception e) {
            e.printStackTrace();
         }
         
      }
   }
   
   
   
}