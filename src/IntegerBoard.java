import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class IntegerBoard extends JPanel {

	static JFrame frame;

	final int SQUARE_SIZE = 90;
	final int LEFT_BORDER = 768 - SQUARE_SIZE * 4;
	final int TOP_BORDER = 400 - SQUARE_SIZE * 4;
	
	Color lightSquareColor;
	Color darkSquareColor;

	int[][] board = {{-4, -2, -3, -5, -6, -3, -2, -4},
					 {-1, -1, -1, -1, -1, -1, -1, -1},
					 { 0,  0,  0,  0,  0,  0,  0,  0},
					 { 0,  0,  0,  0,  0,  0,  0,  0},
					 { 0,  0,  0,  0,  0,  0,  0,  0},
					 { 0,  0,  0,  0,  0,  0,  0,  0},
					 { 1,  1,  1,  1,  1,  1,  1,  1},
					 { 4,  2,  3,  5,  6,  3,  2,  4}};

	Image offScreenImage;
	Graphics offScreenBuffer;

	public IntegerBoard() {
		lightSquareColor = new Color(240, 215, 180);
		darkSquareColor = new Color(180, 135, 100);
	}

	public void paintComponent(Graphics g) {
		//Sets up the off-screen buffer the first time paint() is called
		if (offScreenBuffer == null) {
			offScreenImage = createImage(this.getWidth(), this.getHeight());
			offScreenBuffer = offScreenImage.getGraphics();
		}

		//Clears the off-screen buffer
		offScreenBuffer.clearRect (0, 0, this.getWidth(), this.getHeight());

		//Draws the board
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++) {
				if (row % 2 == col % 2)
					offScreenBuffer.setColor(lightSquareColor);
				else
					offScreenBuffer.setColor(darkSquareColor);
				
				int xPos = col * SQUARE_SIZE + LEFT_BORDER;
				int yPos = row * SQUARE_SIZE + TOP_BORDER;
				
				offScreenBuffer.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

				String pieceFileName;
				
				if (board[row][col] > 0)
					pieceFileName = "White";
				else if (board[row][col] < 0)
					pieceFileName = "Black";
				else
					continue;
				
				String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
				pieceFileName += pieceTypes[Math.abs(board[row][col]) - 1] + ".png";
				
				Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
				offScreenBuffer.drawImage(pieceImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
			}

		//Transfers the off-screen buffer to the screen
		g.drawImage(offScreenImage, 0, 0, this);
	}

	public static void main(String[] args) {

		frame = new JFrame("Chess");
		IntegerBoard panel = new IntegerBoard();

		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

	}

}
