package am;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;

public class ChatClient extends JFrame {

	private JPanel contentPane;
	private JTextField nickText;
	private JTextField input;
	private JPanel login;
	private JPanel game;
	private JPanel inputPanel;
	private JComboBox<Integer> cb;
	private JPanel p_north;
	private JPanel p_north_west;
	Color color;

	Integer[] items = new Integer[3];
	private JButton[] bt_color = new JButton[6];

	int x = -100, y = -100;
	boolean play;
	boolean yourTrun;
	Socket s = null;
	ObjectOutputStream out = null;
	ObjectInputStream in = null;
	String msg = null;
	CardLayout card = null;

	JList<String> user_list;
	JScrollPane pane = null;

	Image buf = null;
	Graphics buf_g;
	CatchMindProtocol temp = null;
	ArrayList<Point> point = new ArrayList<>();
	
	
	Canvas canvas = new Canvas() {

		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			Graphics2D g2 = (Graphics2D)g;
			//색상지정
			g2.setColor(temp.color);
			//굵기지정
			g2.setStroke(new BasicStroke(5));
			//리스트 그리기
			//라인으로 연속으로 그린다.
			for(int i=0; i<point.size()-1; i++){
				g2.drawLine(point.get(i).x, point.get(i).y, point.get(i+1).x, point.get(i+1).y);
			}
			
			//g.fillOval(x, y, 20, 20);
		}

		@Override
		public void update(Graphics g) {
			// TODO Auto-generated method stub
			paint(g);
		}

	};

	// 쓰레드실행
	Thread t = new Thread() {
		@Override
		public void run() {
			// 항상 서버로부터 전달되어 오는
			// 정보(Protocal)를 감지해야 한다.
			/* BK: */while (true) {

				try {
					temp = (CatchMindProtocol) in.readObject();
					if (temp.getCmd() == 1) {
						setTitle("접속자수 : " + temp.getSize());
						user_list.setListData(ListScore(temp.name, temp.numScore));
						canvas.getGraphics().clearRect(0, 0, canvas.getSize().width, canvas.getSize().width);
						input.setEditable(false);
						canvas.setEnabled(false);
						startBtn.setEnabled(true);
						word.setText(" ");
					} else if (temp.getCmd() == 2) {
						// 게임중
						cb.setEnabled(false);
						user_list.setListData(ListScore(temp.name, temp.numScore));
						canvas.getGraphics().clearRect(0, 0, canvas.getSize().width, canvas.getSize().width);
						yourTrun = temp.getMyTun();
						if (yourTrun) {
							word.setText(temp.getMsg());
							input.setEditable(false);
							input.setEnabled(false);
							canvas.setEnabled(true);
						} else {
							word.setText("");
							input.setEditable(true);
							input.setEnabled(true);
							canvas.setEnabled(false);
						}
						// temp.setCmd(3);
						// out.writeObject(temp);
						// out.flush();
						play = true;
						startBtn.setEnabled(false);
					} else if (temp.getCmd() == 3) {
//						x = temp.getX();
//						y = temp.getY();
						//마우스를 release함
						if(temp.p == null){
							point.removeAll(point);
						}
						//마우스를 드래그나, 프레스함
						else{
							point.add(temp.p);
						}
						canvas.repaint();
					} else if (temp.getCmd() == 4) {
						// 게임끝
//						user_list.setListData(ListScore(temp.name, temp.numScore));
						temp.setCmd(2);
						out.writeObject(temp);
						out.flush();
					} else if (temp.getCmd() == 5) {
						if (play) {
							play = false;
							out.writeObject(temp);
						} else {
							if(temp.getRank() == 1){
								JOptionPane.showMessageDialog(ChatClient.this, "축하합니다 당신이 이겼어요!!!");
							}else{
								JOptionPane.showMessageDialog(ChatClient.this, "안타깝지만 당신은 졌어요 ㅜㅜ");
							}
							break;
						}
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (s != null) {
					s.close();
				}
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	};
	private JLabel word;
	private JButton startBtn;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClient frame = new ChatClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatClient() {

		items[0] = 3;
		items[1] = 4;
		items[2] = 5;

		bt_color[0] = new JButton();
		bt_color[0].setBackground(Color.BLACK);
		bt_color[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				color = Color.BLACK;
			}
		});

		bt_color[1] = new JButton();
		bt_color[1].setBackground(Color.RED);
		bt_color[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				color = Color.RED;
			}
		});

		bt_color[2] = new JButton();
		bt_color[2].setBackground(Color.GREEN);
		bt_color[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				color = Color.GREEN;
			}
		});

		bt_color[3] = new JButton();
		bt_color[3].setBackground(Color.BLUE);
		bt_color[3].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				color = Color.BLUE;
			}
		});

		bt_color[4] = new JButton();
		bt_color[4].setBackground(Color.white);
		bt_color[4].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				color = Color.white;
			}
		});

		bt_color[5] = new JButton();
		bt_color[5].setBackground(Color.yellow);
		bt_color[5].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				color = Color.yellow;
			}
		});

		setBounds(100, 100, 552, 482);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout());

		login = new JPanel();
		login.setBackground(new Color(255, 215, 0));
		login.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(login, "login");
		login.setLayout(null);

		JLabel nickLabel = new JLabel("대화명");
		nickLabel.setFont(new Font("굴림체", Font.BOLD, 13));
		nickLabel.setBounds(324, 353, 57, 15);
		login.add(nickLabel);

		nickText = new JTextField();
		nickText.setBounds(371, 350, 116, 21);
		login.add(nickText);
		nickText.setColumns(10);

		JButton loginBtn = new JButton("접속하기");
		loginBtn.setBounds(324, 381, 163, 43);
		login.add(loginBtn);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("D:\\ca.png"));
		lblNewLabel.setBounds(123, 66, 291, 235);
		login.add(lblNewLabel);

		game = new JPanel();
		game.setBackground(Color.YELLOW);
		contentPane.add(game, "chat");
		game.setLayout(new BorderLayout(0, 0));

		p_north = new JPanel(new BorderLayout(0, 0));
		p_north.add(cb = new JComboBox<>(items), BorderLayout.EAST);
		cb.setPreferredSize(new Dimension(70, 10));
		game.add(p_north, BorderLayout.NORTH);

		inputPanel = new JPanel();
		game.add(inputPanel, BorderLayout.SOUTH);
		inputPanel.setLayout(new BorderLayout(0, 0));

		// 정답맞추기
		input = new JTextField();
		input.setEditable(false);
		input.setBackground(Color.WHITE);
		inputPanel.add(input, BorderLayout.CENTER);
		input.setColumns(10);

		JButton sendBtn = new JButton("보내기");
		sendBtn.setBackground(Color.YELLOW);
		sendBtn.setHorizontalAlignment(SwingConstants.RIGHT);
		inputPanel.add(sendBtn, BorderLayout.EAST);

		// 게임스타트버튼
		startBtn = new JButton("Start");
		startBtn.setBackground(new Color(220, 20, 60));
		inputPanel.add(startBtn, BorderLayout.WEST);
		card = (CardLayout) this.contentPane.getLayout();

		// 리스트정보
		user_list = new JList<>();
		game.add(pane = new JScrollPane(user_list), BorderLayout.EAST);

		// 제시어
		word = new JLabel("New label");
		word.setHorizontalAlignment(SwingConstants.CENTER);
		p_north.add(word, BorderLayout.CENTER);
		pane.setPreferredSize(new Dimension(150, 400));

		// 게임판
		game.add(canvas, BorderLayout.CENTER);
		canvas.setBackground(new Color(255, 255, 255));
		canvas.repaint();

		p_north_west = new JPanel(new GridLayout(2, 2));

		for (int i = 0; i < bt_color.length; i++) {
			bt_color[i].setPreferredSize(new Dimension(30, 30));
			p_north_west.add(bt_color[i]);
		}
		p_north.add(p_north_west, BorderLayout.WEST);
		p_north.setBackground(Color.PINK);
		
		canvas.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				if (play && yourTrun) {
//					x = e.getX();
//					y = e.getY();
					temp = new CatchMindProtocol();
					temp.setCmd(3);
//					temp.setX(x);
//					temp.setY(y);
					temp.p = null;
					temp.color = color;
					temp.setClick(true);
					try {
						out.writeObject(temp);
						out.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				if (play && yourTrun) {
//					x = e.getX();
//					y = e.getY();
					temp = new CatchMindProtocol();
					temp.setCmd(3);
//					temp.setX(x);
//					temp.setY(y);
					temp.p = e.getPoint();
					temp.color = color;
					temp.setClick(true);
					try {
						out.writeObject(temp);
						out.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		});

		canvas.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				if (play && yourTrun) {
//					x = e.getX();
//					y = e.getY();
					temp = new CatchMindProtocol();
					temp.setCmd(3);
//					temp.setX(x);
//					temp.setY(y);
					temp.p = e.getPoint();
					temp.color = color;
					temp.setClick(true);
					try {
						out.writeObject(temp);
						out.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}

		});
		//로그인 버튼 눌렀을때
		loginBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String n = nickText.getText().trim();
				if (n.length() > 0) {
					connect(n);
				} else {
					JOptionPane.showMessageDialog(ChatClient.this, "사용자명을입력하세요");
				}
			}
		});

		// 스타트버튼눌렀을때
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				temp = new CatchMindProtocol();
				temp.exam_cnt = (int) cb.getSelectedItem();
				temp.setCmd(2);
				try {
					out.writeObject(temp);
					out.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		// 정답입력
		input.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				send();
				input.setText("");
			}
		});

		// 보내기버튼눌렀을때
		sendBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
				input.setText("");
			}
		});

		// 닫기버튼눌렀을때
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if(play){
					setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}else{
				temp = new CatchMindProtocol();
				temp.setCmd(5);
				play = false;
				try {
					out.writeObject(temp);
					out.flush();
				} catch (Exception e2) {
					// TODO: handle exception
				}
				}
			}

		});
	}

	private void send() {
		temp = new CatchMindProtocol();
		temp.setCmd(3);
		temp.setMsg(input.getText().trim());
		temp.setCheck(true);
		temp.setClick(false);
		try {
			out.writeObject(temp);
			out.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void connect(String n) {
		try {
			s = new Socket("192.168.200.157", 3333);
			out = new ObjectOutputStream((s.getOutputStream()));
			in = new ObjectInputStream((s.getInputStream()));
			t.start();

			temp = new CatchMindProtocol();
			temp.setCmd(1);
			temp.setMsg(n);
			out.writeObject(temp);
			out.flush();
			card.show(ChatClient.this.contentPane, "chat");
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String[] ListScore(String[] users, int[] uScore){
		String[] uList = new String[users.length];
		for(int i=0; i< users.length; i++){
			uList[i]=users[i]+" : "+Integer.toString(uScore[i])+"점";
		}
		return uList;
	}
}
