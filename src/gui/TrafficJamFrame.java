package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 모든 단계(Round)를 기억하고 창(Window)에 보여주기 위한 JFrame클래스 
 * @author Hwiyong Chang
 */
public class TrafficJamFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final double width_ratio = 0.5, height_ratio = 0.5;		// 컴퓨터 모니터에 비례한 프로그램 화면 크기(<=1)
	private static final int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int frame_width = (int)(width_ratio * screen_width);
	private static final int frame_height = (int)(height_ratio * screen_height); 
	
	private char[][][] gameData;
	private int currentRound, caseCount;	// caseCount = 테스트케이스의 개수
	
	private JPanel menuPanel;	// 버튼을 놓을 수 있는 메뉴 패널
	private JButton prevButton, nextButton, rountInitButton;		// 이전 게임 버튼, 다음 게임 버튼, 현재 게임 초기화 버튼
	private JLabel currentRoundDisplay, currentState, exitLabel, moveCountLabel;	// 현재 단계 표시, 현재 단계에서 동작 상황, 출구 표시기능
	
	private TrafficJamPanel[] gamePanel;
	
	public TrafficJamFrame(char[][][] gameData, int caseCount)
	{
		super("Rush Hour Game");
		this.gameData = gameData;
		this.caseCount = caseCount;		// caseCount = gameData.length
		
		initAllPanel();
		
		setLocation((screen_width - frame_width) / 2, (screen_height - frame_height) / 2);
		setPreferredSize(new Dimension(frame_width, frame_height));
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * 모든 컴포넌트들과 게임패널들을 초기화시킨다.
	 */
	private void initAllPanel()
	{
		// 메뉴 패널 초기화
		menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, frame_width/8, frame_height/24));	// 컴포넌트 위치 설정, 좌우 간격, 상하 간격
		menuPanel.setPreferredSize(new Dimension(frame_width/8, frame_height));		// 메뉴 패널의 크기를 고정시켜준다.
		add(menuPanel, BorderLayout.EAST);
		
		// 메뉴 패널에 넣을 컴포넌트 초기화
		prevButton = new JButton("이전 게임");
		prevButton.addMouseListener(new TrafficJamFrameMouseListener());
		menuPanel.add(prevButton);
		
		currentRound = 0;
		currentRoundDisplay = new JLabel("Round : " + (currentRound + 1));
		menuPanel.add(currentRoundDisplay);
		
		nextButton = new JButton("다음 게임");
		nextButton.addMouseListener(new TrafficJamFrameMouseListener());
		menuPanel.add(nextButton);
		
		currentState = new JLabel("차량 미선택 중");
		menuPanel.add(currentState);
		
		exitLabel = new JLabel(">>>출구>>>");
		exitLabel.setFont(new Font(null, 0, frame_height/24));		// 글자 크기 설정
		menuPanel.add(exitLabel);
		
		moveCountLabel = new JLabel("0회 이동");
		menuPanel.add(moveCountLabel);
		
		rountInitButton = new JButton("현재 게임 초기화");
		rountInitButton.addMouseListener(new TrafficJamFrameMouseListener());
		menuPanel.add(rountInitButton);
		
		// 게임 패널들의 초기화
		gamePanel = new TrafficJamPanel[caseCount];
		for(int i=0; i<caseCount; ++i)
			gamePanel[i] = new TrafficJamPanel(gameData[i], gameData[i].length, gameData[i][0].length, this);
		add(gamePanel[currentRound]);
	}
	
	/**
	 * 게임 패널의 번호를 바꾼다. (단계 변경)
	 */
	private void changeBoard()
	{
		currentRoundDisplay.setText("Round : " + (currentRound + 1));		// 단계를 표시한다.
		TrafficJamPanel currentPanel = gamePanel[currentRound];
		add(currentPanel);
		setInformation(currentPanel.getFinish(), currentPanel.getSelectedCar(), currentPanel.getMoveCount());
		revalidate();	repaint();		// 컴포넌트 변경을 디스플레이에 적용시켜주는 두 메소드.
	}
	
	/**
	 * 이전 혹은 다음버튼에 의해 패널이 바뀌거나 게임진행으로 인해
	 * 게임 상태가 변경되었을 경우 해당 레이블을 제때 정확한 상태로 표시해주는 메소드.
	 * 게임이 끝났을 시 : 게임 종료, 차량이 선택되었을 때 : 차량 선택, 차량이 선택되지 않았을 때 : 차량 미선택.
	 * @param isFinish		해당 패널의 게임종료 여부
	 * @param selectedCar	해당 패널의 차량선택 여부
	 */
	void setInformation(boolean isFinish, boolean selectedCar, int moveCount)
	{
		if(isFinish)
			currentState.setText("게임 종료");
		else if(selectedCar)
			currentState.setText("차량 선택 중");
		else
			currentState.setText("차량 미선택 중");
		moveCountLabel.setText(moveCount + "회 이동");
	}	
	
	protected TrafficJamFrame getFrame()
	{
		return this;
	}
	
	/**
	 * 게임 외부의 메뉴버튼의 마우스 리스너 클래스
	 * @author Hwiyong Chang
	 */
	private class TrafficJamFrameMouseListener implements MouseListener
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			if(e.getSource() instanceof JButton)
			{
				if(e.getSource().equals(prevButton))				// 이전 게임으로 변경 이벤트 부분
				{
					remove(gamePanel[currentRound]);
					currentRound = currentRound <= 0 ? caseCount-1 : currentRound-1;
					changeBoard();
				}
				else if(e.getSource().equals(nextButton))			// 다음 게임으로 변경 이벤트 부분
				{
					remove(gamePanel[currentRound]);
					currentRound = currentRound >= caseCount - 1 ? 0 : currentRound+1;
					changeBoard();
				}
				else if(e.getSource().equals(rountInitButton))		// 현재 게임 초기화 이벤트 부분
				{
					remove(gamePanel[currentRound]);
					gamePanel[currentRound] = new TrafficJamPanel(gameData[currentRound], gameData[currentRound].length, gameData[currentRound][0].length, getFrame());
					changeBoard();
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mouseClicked(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
}
