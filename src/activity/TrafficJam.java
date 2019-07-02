package activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import gui.TrafficJamFrame;

/**
 * String 한 줄짜리 TestCase 데이터를
 * 2차원 배열의 목록(=3차원 배열[테스트케이스][row][column])으로 변형하여
 * 창(JFrame)에 데이터를 전송해주는 실행 클래스
 * @author Hwiyong Chang
 */
public class TrafficJam
{
	static final String file_path = "", file_name = "caseInput.txt";
	static final int test_case_count = 40, map_row = 6, map_col = 6;
	
	static char[][][] testCase;
	
	/**
	 * 파일에 있는 테스트케이스의 경우들을 3차원배열(테스트케이스-가로-세로)로 변환하여 반환하는 메소드
	 * @return	테스트 케이스의 3차원 배열 정보
	 */
	public static char[][][] read_file()
	{
		char[][][] testCase = null;
		try {
			Scanner input = new Scanner(new File(file_path + file_name));
			testCase = new char[test_case_count][map_row][map_col];
			for(int i=0; i<test_case_count; ++i)
			{
				String line = input.nextLine();
				for(int j=0; j<map_row; ++j)
				{
					testCase[i][j] = line.substring(0, map_col).toCharArray();
					line = line.substring(map_col);
				}
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return testCase;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException
	{
		new TrafficJamFrame(read_file(), test_case_count);
	}
}
