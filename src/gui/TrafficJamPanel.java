package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 테스트 케이스의 하나의 단계를 표현하기 위한 Panel.
 * 맨 처음에는 gameData의 글자로 게임판을 초기화시키지만
 * 그 이후부터는 색깔로 구별한다.
 * (글자이동으로 게임을 진행하면 다시 초기 상태로 돌아갈 자료가 없기 때문이다.)
 * @author Hwiyong Chang
 */
public class TrafficJamPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static final char blank = '.', myCar = 'X', canMoveLetter = 'O';	// 빈칸과 빠져나가야할 차를 설정, 그리고 움직임이 가능한 빈칸에 표시할 글자
	private static final Color blankColor = Color.WHITE, myCarColor = Color.RED;			// 빈칸과 목표 자동차의 색상.
	private static final int[][] movement = new int[][] {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
	
	private boolean selectedCar, isFinish;		// 현재 차량 버튼을 클릭하였는지에 대한 여부, 게임 종료 여부
	private int mrow, mcol, moveCount;			// 맵의 행과 열의 크기, 이동 횟수
	private char[][] gameData;					// 현재 단계의 데이터
	
	private Color selectedColor;				// 현재 선택된 차량 색상.
	private JButton[][] buttons;
	private TrafficJamFrame window;				// 현재 패널을 가지고 있는 상위 프레임을 알아보기 위함. (창(window) 객체를 하위클래스가 알고 있어야 인터페이스 변경이 쉬움)
		
	protected TrafficJamPanel(char[][] gameData, int mrow, int mcol, TrafficJamFrame window)
	{
		super(new GridLayout(mrow, mcol));
		this.selectedCar = false;
		this.selectedColor = null;
		this.mrow = mrow;
		this.mcol = mcol;
		this.gameData = gameData;
		this.window = window;
		this.setBackground(blankColor);
		initPanel();
	}
	
	/**
	 * 초기의 게임 데이터에서 글자와 빈칸을 구별한 후,
	 * 빈칸은 흰색, 글자는 동일 글자끼리 같은 색을 유지하도록 색을 설정한다.
	 */
	private void initPanel()
	{
		moveCount = 0;
		buttons = new JButton[mrow][mcol];
		for(int i=0; i<mrow; ++i)
			for(int j=0; j<mcol; ++j)
			{	
				buttons[i][j] = new JButton();
				buttons[i][j].setBackground(initButtonColor(gameData[i][j]));
				buttons[i][j].addMouseListener(new TrafficJamPanelMouseListener());
				if(gameData[i][j] == blank)	buttons[i][j].setEnabled(false);		// 초기에 빈칸이라면 활성화를 false로 한다.
				add(buttons[i][j]);
			}
		isFinish();		// 처음부터 게임이 끝난 상태인지 확인한다. 게임이 초기화 될 때 isfinish값을 초기화하는 역할도 한다.
	}
	
	/**
	 * 임의의 공식에 의하여 색깔을 지정해준다. (목표 자동차는 지정된 색을 갖고 빈칸은 흰색을 가진다.)
	 * @param letter	글자 (글자에 따라 색이 결정됨 = 같은글자는 같은 색상)
	 * @return	임의의 공식에 의해 다른 글자끼리 다른 색을 갖도록하는 색깔 값
	 */
	private Color initButtonColor(char letter)
	{
		if(letter == blank)	return blankColor;		// 빈칸이면 지정된 빈 칸색을 반환
		// new Color(int r, int g, int b)에서 각 int의 값은 0 ~ 255의 범위이다.
		// r, g, b는 (|letter - myCar| * 임의의 상수 + 목표 자동차의 기존 색상) % 256 공식을 사용하여
		// 각 문자마다 다른색을 가지면서 myCarColor와 중복된 색이 나오지 않게 한다.
		int r, g, b;
		r = (Math.abs(letter - myCar) * 10 + myCarColor.getRed()) % 256;
		g = (Math.abs(letter - myCar) * 30 + myCarColor.getGreen()) % 256;
		b = (Math.abs(letter - myCar) * 50 + myCarColor.getBlue()) % 256;
		return new Color(r, g, b);
	}
	
	/**
	 * 게임의 종료 여부를 확인한다.
	 * 현재는 가장 오른쪽 열에 myCarColor가 있으면 종료하는 것으로 설정했다.
	 * @return	게임의 종료 여부
	 */
	private boolean isFinish()
	{
		int scanRow = 0;
		while(scanRow < mrow)
			if(buttons[scanRow++][mcol-1].getBackground().equals(myCarColor))
				isFinish = true;
		return isFinish;
	}
	
	boolean getSelectedCar()
	{
		return selectedCar;
	}
	
	boolean getFinish()
	{
		return isFinish;
	}
	
	int getMoveCount()
	{
		return moveCount;
	}
	
	/**
	 * 게임 내부의 버튼(자동차)들을 현재 상태(차량선택, 차량미선택)에 따라 행동을 하기 위한 마우스 리스너 클래스
	 * @author Hwiyong Chang
	 */
	class TrafficJamPanelMouseListener implements MouseListener
	{
		/**
		 * 해당 이벤트 버튼의 row와 column 인덱스 값을 배열형태로 반환한다.
		 * @param e	해당 이벤트 버튼
		 * @return	row와 column 인덱스 값
		 */
		private int[] find(MouseEvent e)
		{
			for(int i=0; i<mrow; ++i)
				for(int j=0; j<mcol; ++j)
					if(e.getSource().equals(buttons[i][j]))
						return new int[] {i, j};
			return null;		// 이 부분이 작동될 가능성은 없다.
		}
		
		/**
		 * 모든 자동차의 버튼들을 활성화 시킨다.
		 * 동시에 모든 빈칸들을 비활성화 시킨다.
		 */
		private void enableCarButton()
		{
			for(int i=0; i<mrow; ++i)
				for(int j=0; j<mcol; ++j)
				{	
					if(buttons[i][j].getBackground().equals(blankColor))	// 빈 칸이면
					{
						buttons[i][j].setText(null);						// 글자를 지운다.
						buttons[i][j].setEnabled(false);					// 버튼을 비활성화
					}
					else													// 자동차라면
					{
						buttons[i][j].setText(null);						// 글자를 지운다.
						buttons[i][j].setEnabled(true);						// 버튼을 활성화
					}
				}
		}
		
		/**
		 * 선택된 자동차를 제외한 자동차의 버튼들을 비활성화 시킨다.
		 */
		private void disenableCarButton()
		{
			for(int i=0; i<mrow; ++i)
				for(int j=0; j<mcol; ++j)
					// 빈칸이 아니면서 선택된 자동차와 다른 색이라면 버튼을 비활성화한다.
					if(!buttons[i][j].getBackground().equals(blankColor) && !buttons[i][j].getBackground().equals(selectedColor))
						buttons[i][j].setEnabled(false);
		}
		
		/**
		 * 해당 차량이 수평방향인지 수직방향인지 알려준다.
		 * 해당 위치의 왼쪽 혹은 오른쪽과 같은 색깔이라면 수평방향으로 움직이는 차량이다.
		 * @param row	해당 차량의 1칸 row 위치
		 * @param col	해당 차량의 1칸 column 위치
		 * @return	해당 차량이 수평방향으로 움직이면 true, 수직방향으로 움직이면 false.
		 */
		private boolean isHorizon(int row, int col)
		{
			return (col > 0 && buttons[row][col-1].getBackground().equals(buttons[row][col].getBackground()))
				|| (col < mcol-1 && buttons[row][col+1].getBackground().equals(buttons[row][col].getBackground()));
		}
		
		/**
		 * 해당 자동차의 인접한 빈칸들을 활성화 시킨다.
		 * 몇 칸의 빈칸이 활성화 되었는지 반환한다.
		 * @param horizon	수평이면 true, 수직이면 false.
		 * @param row	해당 자동차의 row 위치
		 * @param col	해당 자동차의 column 위치
		 * @return	활성화된 빈 칸의 개수
		 */
		private int enableAdjacentBlankButton(boolean horizon, int row, int col)
		{
			int enableCount = 0;
			if(horizon)
			{
				enableCount += enableAdjacentBlankButton(row, col, 0, -1);	// 왼쪽방향
				enableCount += enableAdjacentBlankButton(row, col, 0, 1);	// 오른쪽방향
			}
			else
			{
				enableCount += enableAdjacentBlankButton(row, col, -1, 0);	// 위쪽방향
				enableCount += enableAdjacentBlankButton(row, col, 1, 0);	// 오른쪽방향
			}
			return enableCount;
		}
		/**
		 * 기준점과 지정된 방향으로 이동 가능한 빈 칸의 개수를 반환한다.
		 * @param stdRow	기준점의 row위치
		 * @param stdCol	기준점의 column위치
		 * @param moveRow	움직이려는 row방향값(-1 <= moveRow <= 1)
		 * @param moveCol	움직이려는 column방향값(-1 <= moveCol <= 1)
		 * @return	지정된 방향으로의 이동가능한 빈 칸의 개수
		 */
		private int enableAdjacentBlankButton(int stdRow, int stdCol, int moveRow, int moveCol)
		{
			int enableCount = 0;
			Color fix = buttons[stdRow][stdCol].getBackground();
			stdRow += moveRow;
			stdCol += moveCol;
			while(stdRow >= 0 && stdRow < mrow && stdCol >= 0 && stdCol < mcol)
			{
				Color temp = buttons[stdRow][stdCol].getBackground();
				if(temp.equals(blankColor))							// 빈칸이라면
				{
					buttons[stdRow][stdCol].setEnabled(true);		// 해당 버튼을 활성화시킨다.
					buttons[stdRow][stdCol].setText(canMoveLetter+"");	// 움직일 수 있는 곳이라는 표시를 해준다.
					++enableCount;
				}
				else if(!temp.equals(fix))							// 다른 색 차라면
					break;											// 반복문을 종료한다.
				stdRow += moveRow;
				stdCol += moveCol;
			}
			return enableCount;
		}
		
		/**
		 * 빈 칸으로 선택된 자동차를 이동시킨다.
		 * @param row	빈 칸의 row 위치
		 * @param col	빈 칸의 column 위치
		 */
		private void move(int row, int col)
		{
			int[] carInformation = getCarInformation(row, col);
			int length = 0, scanRow = carInformation[0], scanCol = carInformation[1], direction = carInformation[2];
			// 차의 길이를 구하는 부분
			while(scanRow >= 0 && scanRow < mrow && scanCol >= 0 && scanCol < mcol && buttons[scanRow][scanCol].getBackground().equals(selectedColor))
			{
				++length;
				scanRow += movement[direction][0];
				scanCol += movement[direction][1];
			}
			
			scanRow = carInformation[0];
			scanCol = carInformation[1];
			// 선택된 자동차의 부분을 length만큼 빈칸으로 설정한다.
			for(int i=0; i<length; ++i)
			{
				buttons[scanRow][scanCol].setBackground(blankColor);
				scanRow += movement[direction][0];
				scanCol += movement[direction][1];
			}
			
			scanRow = row;
			scanCol = col;
			// 선택된 빈 칸에서 length만큼 자동차로 설정한다.
			for(int i=0; i<length; ++i)
			{
				buttons[scanRow][scanCol].setBackground(selectedColor);
				scanRow += movement[direction][0];
				scanCol += movement[direction][1];
			}
			++moveCount;	// 이동 횟수 1증가 (이동 거리에 상관없이 차량의 이동 횟수를 카운트)
		}
		/**
		 * 해당 위치에서 가장 가까운 선택된 자동차(버튼) 위치와 빈칸에서 자동차로 가는 방향을 반환한다. 
		 * @param row	빈 칸의 row 위치
		 * @param col	빈 칸의 column 위치
		 * @return	가장 가까운 선택된 자동차(버튼)의 위치와 (빈칸->자동차)방향 {row, column, direction}
		 */
		private int[] getCarInformation(int row, int col)
		{
			for(int i=0; i<movement.length; ++i)
			{
				int scanRow = row + movement[i][0], scanCol = col + movement[i][1];
				while(scanRow >= 0 && scanRow < mrow && scanCol >= 0 && scanCol < mcol)
				{
					if(buttons[scanRow][scanCol].getBackground().equals(selectedColor))
						return new int[] {scanRow, scanCol, i};
					scanRow += movement[i][0];
					scanCol += movement[i][1];
				}
			}
			return null;	// 이 부분이 작동될 가능성은 없다.
		}
		
		/**
		 * 게임이 끝났을 때의 동작.
		 * 모든 버튼을 비활성화 시킨다.
		 */
		private void disenableAllButton()
		{
			for(int i=0; i<mrow; ++i)
				for(int j=0; j<mcol; ++j)
					buttons[i][j].setEnabled(false);
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{	
			if(isFinish || !((JButton)e.getSource()).isEnabled())
				return;		// 게임이 끝난경우거나 Enable이 불가능한 버튼이라면 클릭작동을 하지 않는다.
			int[] index = find(e);
			int row = index[0], col = index[1];
			if(selectedCar)		// 자동차를 선택한 상황이라면
			{
				if(buttons[row][col].getBackground().equals(blankColor))			// 빈칸을 선택했다면 (이동)
					move(row, col);	
				// 그 외의 버튼을 클릭했다면 이동을 하지 않고 자동차 선택 취소가 된다.
				selectedCar = false;
				selectedColor = null;
				enableCarButton();			// 모든 자동차 버튼을 활성화하고 빈칸들을 비활성화 한다.
				isFinish();					// 게임이 끝났는지 확인한다.
				if(isFinish)				// 게임이 끝났으면,
					disenableAllButton();	// 모든 버튼을 비활성화 시킨다.
			}
			else				// 자동차를 선택한 상황이 아니라면,
			{
				if(enableAdjacentBlankButton(isHorizon(row, col), row, col) == 0)	
					return;		// 해당 자동차가 움직일 수 있는 빈칸이 없다면 return.
				selectedCar = true;									// 자동차를 선택하였다고 바꾸어준다.
				selectedColor = buttons[row][col].getBackground();	// 선택된 자동차의 색을 설정해준다.
				disenableCarButton();
			}
			window.setInformation(isFinish, selectedCar, moveCount);		// 창(window)의 변경사항 적용
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
