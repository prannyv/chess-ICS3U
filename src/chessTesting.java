import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class chessTesting extends JPanel implements ActionListener, MouseListener {

	static JFrame frame;
	final static int SQUARE_SIZE = 80;
	final int LEFT_BORDER = 80;
	final int TOP_BORDER = 40;

	final int WHITE = 1;
	final int BLACK = -1;

	boolean game = false;
	boolean rules = false;


	Clip clip;
	AudioInputStream audioIn;


	JButton newGameButton = new JButton("New Game");
	JButton rulesButton = new JButton("Rules");
	JButton clocksButton = new JButton("Clocks");
	JButton themesButton = new JButton("Themes");
	JButton exitButton = new JButton("Exit");
	JButton color1 = new JButton("Color 1");
	JButton color2 = new JButton("Color 2");
	JButton color3 = new JButton("Color 3");

	JButton color4 = new JButton("Color 4");

	boolean theme = false;

	ArrayList<Integer> whiteTaken = new ArrayList<Integer>();
	ArrayList<Integer> blackTaken = new ArrayList<Integer>();



	int[] selectedPiece = new int[3];
	//made up of  [row, col, type] 
	boolean highlighted = false;

	boolean moving = false;



	Color lightSquareColor = new Color(240, 215, 180);
	Color darkSquareColor = new Color(180, 135, 100);


	static int[][] board = {{-4, -2, -3, -5, -6, -3, -2, -4},
			{-1, -1, -1, -1, -1, -1, -1, -1},
			{ 0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0},
			{ 0,  0,  0,  0,  0,  0,  0,  0},
			{ 1,  1,  1,  1,  1,  1,  1,  1},
			{ 4,  2,  3,  5,  6,  3,  2,  4}};

	Image offScreenImage;
	static Graphics offScreenBuffer;

	public chessTesting() {

		Piece wQueen = new Piece(WHITE, 4, 5, 0);
		wQueen.getImg();





		//		
		//		
		//
		//		



		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();


		setPreferredSize(new Dimension(1920, 1080));
		addMouseListener(this);
		setLayout(null);



		newGameButton.setBounds(860, 200, 200, 50);
		newGameButton.setFont(new Font("Arial", Font.PLAIN, 24));
		newGameButton.setActionCommand("New Game");
		newGameButton.addActionListener(this);


		rulesButton.setBounds(860, 300, 200, 50);
		rulesButton.setFont(new Font("Arial", Font.PLAIN, 24));
		rulesButton.setActionCommand("Rules");
		rulesButton.addActionListener(this);

		clocksButton.setBounds(860, 400, 200, 50);
		clocksButton.setFont(new Font("Arial", Font.PLAIN, 24));
		clocksButton.setActionCommand("Clocks");
		clocksButton.addActionListener(this);

		themesButton.setBounds(860, 500, 200, 50);
		themesButton.setFont(new Font("Arial", Font.PLAIN, 24));
		themesButton.setActionCommand("Themes");
		themesButton.addActionListener(this);

		exitButton.setBounds(860, 600, 200, 50);
		exitButton.setFont(new Font("Arial", Font.PLAIN, 24));
		exitButton.setActionCommand("Exit");
		exitButton.addActionListener(this);


		add(newGameButton);
		add(rulesButton);
		add(clocksButton);
		add(themesButton);
		add(exitButton);

		color1.setBounds(1150, 450, 100, 25);
		color1.setFont(new Font("Arial", Font.PLAIN, 24));
		color1.setActionCommand("Brown");
		color1.addActionListener(this);


		color2.setBounds(1150, 500, 100, 25);
		color2.setFont(new Font("Arial", Font.PLAIN, 24));
		color2.setActionCommand("Green");
		color2.addActionListener(this);

		color3.setBounds(1150, 550, 100, 25);
		color3.setFont(new Font("Arial", Font.PLAIN, 24));
		color3.setActionCommand("Blue");
		color3.addActionListener(this);

		color4.setBounds(1150, 600, 100, 25);
		color4.setFont(new Font("Arial", Font.PLAIN, 24));
		color4.setActionCommand("Black");
		color4.addActionListener(this);




		add(color1);
		add(color2);
		add(color3);
		add(color4);

		color1.setVisible(false);
		color2.setVisible(false);
		color3.setVisible(false);
		color4.setVisible(false);




		repaint();

		addMouseListener(this);
	}

	public void paintComponent(Graphics g) {
		// Sets up the off-screen buffer the first time paint() is called
		if (offScreenBuffer == null) {
			offScreenImage = createImage(this.getWidth(), this.getHeight());
			offScreenBuffer = offScreenImage.getGraphics();
		}

		// Clears the off-screen buffer
		offScreenBuffer.clearRect(0, 0, this.getWidth(), this.getHeight());

		// Draws the board

		if (game == true)
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

		String rulesString = "The rules for chess are.\n"
				+ "blah blah blah blah";
		if (rules == true) {
			offScreenBuffer.drawString(rulesString, 780, 340);
		}





		// Transfers the off-screen buffer to the screen
		g.drawImage(offScreenImage, 0, 0, this);
	}


	public void actionPerformed(ActionEvent e) {
		String eventName = e.getActionCommand();

		if (eventName.equals("New Game")) {

			int[][] reset = {{-4, -2, -3, -5, -6, -3, -2, -4},
					{-1, -1, -1, -1, -1, -1, -1, -1},
					{ 0,  0,  0,  0,  0,  0,  0,  0},
					{ 0,  0,  0,  0,  0,  0,  0,  0},
					{ 0,  0,  0,  0,  0,  0,  0,  0},
					{ 0,  0,  0,  0,  0,  0,  0,  0},
					{ 1,  1,  1,  1,  1,  1,  1,  1},
					{ 4,  2,  3,  5,  6,  3,  2,  4}};

			board = reset;
			// your code goes here

			//resets the pieces to the very beginning, draws the board
			newGameButton.setVisible(true);
			rulesButton.setVisible(false);
			clocksButton.setVisible(false);
			themesButton.setVisible(false);
			exitButton.setVisible(false);

			game = true;
			rules = false;

			repaint();

		}
		else if (eventName.equals("Rules")) {
			// your code goes here



			rules = !rules;
			repaint();

		}
		else if (eventName.equals("Clocks")) {
			// your code goes here
		}
		else if (eventName.equals("Themes")) {


			color1.setVisible(moving);
			color2.setVisible(moving);
			color3.setVisible(moving);
			color4.setVisible(moving);




			moving = !moving;
			if (moving == true)
				System.out.println("Moving enabled");
			if (moving == false)
				System.out.println("Moving disabled");



		}

		else if(eventName.equals("Brown")) {
			lightSquareColor = new Color(240, 215, 180);
			darkSquareColor = new Color(180, 135, 100);
		}

		else if(eventName.equals("Green")) {
			lightSquareColor = new Color(238, 238, 210);
			darkSquareColor = new Color(118, 150, 86);
		}

		else if(eventName.equals("Blue")) {
			lightSquareColor = new Color(151, 147, 204);
			darkSquareColor = new Color(75, 81, 152);
		}
		if(eventName.equals("Black")) {
			lightSquareColor = Color.white;
			darkSquareColor = new Color (168,170,172);
		}

		else if (eventName.equals("Exit"))
			System.exit(0);
	}

	public void mouseClicked(MouseEvent e) {

		int row = e.getX();
		int col = e.getY();
		System.out.println("row " + row);
		System.out.println("col " + col);

		int row2 = (row-40)/80;
		int col2 = (col-80)/80;



		//		if ((row > 79 && row < 721) && (col > 39 && col < 681) && board[row2][col2] != 0)

		if (moving == false)
			highlightPiece(row,col);

		//		if (board[row2][col2] == 0)
		if (moving == true)
			movePiece(row, col);

	}

	public void mousePressed(MouseEvent e) {





		//				offScreenBuffer.fillRect(row, col, SQUARE_SIZE, SQUARE_SIZE);

		//		String pieceFileName = "H";
		//
		//		int row2 = (row-80)/80;
		//		int col2 = (col-40)/80;
		//
		//		if ((row > 79 && row < 721) && (col > 39 && col < 681)) {
		//			if (board[row2][col2] > 0)
		//				pieceFileName = "White";
		//			else if (board[row2][col2] < 0)
		//				pieceFileName = "Black";
		//
		//			String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
		//			pieceFileName += pieceTypes[Math.abs(board[row2][col2])] + ".png";
		//
		//			Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
		//			offScreenBuffer.drawImage(pieceImage, row2, col2, SQUARE_SIZE, SQUARE_SIZE, this);
		//
		//			g.drawImage(offScreenImage, 0, 0, this);
	}








	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}



	public void takePiece(int type, int row, int col) {

		if (type > 0)
			//if it is white
			blackTaken.add(type);

		if (type < 0)
			whiteTaken.add(type);



	}


	public void displayTaken() {


		String pieceFileName = null;
		String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};


		for (int i = 0; i < whiteTaken.size(); i++) {
			if (whiteTaken.get(i) > 0)
				pieceFileName = "White";
			else if (whiteTaken.get(i) < 0)
				pieceFileName = "Black";


			pieceFileName += pieceTypes[Math.abs(whiteTaken.get(i)) - 1] + ".png";

			Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
			offScreenBuffer.drawImage(pieceImage, 1000+50*i, 800+50*i, 25, 25, this);

		}

		for (int i = 0; i < blackTaken.size(); i++) {
			if (blackTaken.get(i) > 0)
				pieceFileName = "White";
			else if (blackTaken.get(i) < 0)
				pieceFileName = "Black";


			pieceFileName += pieceTypes[Math.abs(blackTaken.get(i)) - 1] + ".png";

			Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
			offScreenBuffer.drawImage(pieceImage, 1000+50*i, 200+50*i, 25, 25, this);

		}

	}



	public void showMoves(int x, int y, int[] piece) {


		System.out.println("showing moves");


		//x in this case should be a corner of the square we want to move to 

		Graphics g = getGraphics();

		Image openMoves = Toolkit.getDefaultToolkit().getImage("legalMoves.png");

		ArrayList<Integer> xPos = new ArrayList<Integer>();
		ArrayList<Integer> yPos = new ArrayList<Integer>();


		int row = (y-40)/80;
		int col = (x-80)/80;

		//black pawn
		if (piece[2] == -1 && y+80 <= 680) {
			if (board[row+1][col] == 0) {

				xPos.add(x);
				yPos.add(y+80);


				offScreenBuffer.drawImage(openMoves, xPos.get(0), yPos.get(0), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);


			}
		}


		//white pawn
		if (piece[2] == 1 && y-80 >=40) {
			if (board[row-1][col] == 0) {

				xPos.add(x);
				yPos.add(y-80);


				offScreenBuffer.drawImage(openMoves, xPos.get(0), yPos.get(0), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);


			}
		}


		int i = 0;

		//rook
		if (Math.abs(piece[2]) == 4) {

			int ogX = x;
			int ogY = y;

			while (x < 720 && x > 80) {


				x = x+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;

			while (x < 720 && x > 80) {


				x = x-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;

			while (y < 680 && y > 40) {


				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			y = ogY;

			while (y < 680 && y > 40) {

				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}
		}



		//bishop
		if (Math.abs(piece[2]) == 2) {


			int ogX = x;
			int ogY = y;

			while (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+80;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-80;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+80;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-80;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}
		}

		//king
		if (Math.abs(piece[2]) == 6) {

			int ogX = x;
			int ogY = y;

			if (x < 720 && x > 80 && y < 680 && y > 40) {
				x = x+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}
			x = ogX;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}
			x = ogX;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}
			y = ogY;


			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+80;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}

			x = ogX;
			y = ogY;


			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-80;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+80;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-80;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);
			}

			x = ogX;
			y = ogY;


		}


		//queen
		if (Math.abs(piece[2]) == 5) {

			int ogX = x;
			int ogY = y;

			while (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+80;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-80;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+80;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (x < 720 && x > 80 && y < 680 && y > 40) {




				x = x-80;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;


			while (x < 720 && x > 80) {


				x = x+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (x < 720 && x > 80) {


				x = x-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (y < 680 && y > 40) {


				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			while (y < 680 && y > 40) {

				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}




		}


		//knight
		if (Math.abs(piece[2]) == 2) {

			int ogX = x;
			int ogY = y;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-80;
				y = y+160;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+80;
				y = y+160;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+160;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x+160;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;


			if (x < 720 && x > 80 && y < 680 && y > 40) {


				x = x+80;
				y = y-160;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {


				x = x-80;
				y = y-160;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-160;
				y = y+80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}

			x = ogX;
			y = ogY;

			if (x < 720 && x > 80 && y < 680 && y > 40) {

				x = x-160;
				y = y-80;
				xPos.add(x);
				yPos.add(y);


				offScreenBuffer.drawImage(openMoves, xPos.get(i++), yPos.get(i++), SQUARE_SIZE, SQUARE_SIZE, this);

				g.drawImage(offScreenImage, 0, 0, this);

			}




		}


	}


	//for the rook do a check extending from all four sides that goes until the board ends or you reach a non zero spot
	//**can be used for all four rooks

	//for the bishop do a check in 4 diagonals until the board ends or you reach a non zero piece (row/col both ++, none, or either)
	//can be used for all four bishops

	//king is same as rook/bishop but only checks one in each direction
	//same for both kings? i think

	//queen is the rook/bishop code combined
	//can be used for both i believe

	//not a damn clue hwo to do the knight

	//all above are done !!!



	//finish first then try to implement by fixing move highlighter/showmoves and  








	public void highlightPiece(int x, int y) {


		Graphics g  = getGraphics();

		offScreenBuffer.setColor(Color.black);




		//		offScreenBuffer.fillRect(row2, col2, SQUARE_SIZE, SQUARE_SIZE);

		String pieceFileName = "";

		System.out.println(x);
		System.out.println(y);


		int row = (y-40)/80;
		int col = (x-80)/80;
		System.out.println();
		System.out.println(row);
		System.out.println(col);



		int xPos = col * 80 + 80;
		int yPos = row * 80 + 40;
		System.out.println();
		System.out.println(xPos);
		System.out.println(yPos);

		repaint();

		if (board [row][col] != 0) {

			pieceFileName = "H";


			if (board[row][col] > 0)
				pieceFileName += "White";
			else if (board[row][col] < 0)
				pieceFileName += "Black";

			String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
			pieceFileName += pieceTypes[Math.abs(board[row][col])-1] + ".png";

			Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
			offScreenBuffer.drawImage(pieceImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);

			g.drawImage(offScreenImage, 0, 0, this);

			highlighted = true;

			selectedPiece[0] = row;
			selectedPiece[1] = col;
			selectedPiece[2] = board[row][col];






			//		if (highlighted == false){
			//
			//			pieceFileName = "H";
			//
			//
			//			if (board[row][col] > 0)
			//				pieceFileName += "White";
			//			else if (board[row][col] < 0)
			//				pieceFileName += "Black";
			//
			//			String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
			//			pieceFileName += pieceTypes[Math.abs(board[row][col])-1] + ".png";
			//
			//			Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
			//			offScreenBuffer.drawImage(pieceImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
			//
			//			g.drawImage(offScreenImage, 0, 0, this);
			//
			//			highlighted = true;
			//
			//			selectedPiece[0] = row;
			//			selectedPiece[1] = col;
			//			selectedPiece[2] = board[row][col];
			//
			//		}
			//
			//		if (highlighted == true) {
			//
			//			if (row % 2 == col % 2)
			//				offScreenBuffer.setColor(lightSquareColor);
			//			else
			//				offScreenBuffer.setColor(darkSquareColor);
			//
			//			offScreenBuffer.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
			//
			//
			//			if (board[row][col] > 0)
			//				pieceFileName = "White";
			//			else if (board[row][col] < 0)
			//				pieceFileName = "Black";
			//
			//			String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
			//			pieceFileName += pieceTypes[Math.abs(board[row][col]) - 1] + ".png";
			//
			//			Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
			//			offScreenBuffer.drawImage(pieceImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
			//
			//			highlighted = false;
			//
			//		}
			//

		}


		//		showMoves(xPos, yPos, selectedPiece);
		System.out.println(highlighted);

		for (int i = 0; i <3; i++) 
			System.out.println("select" + selectedPiece[i]);

		showMoves(xPos, yPos, selectedPiece);





	}


	public void movePiece (int x, int y) {

		System.out.println("move");

		Graphics g = getGraphics();

		int row = (y-40)/80;
		int col = (x-80)/80;


		int xPos = col * 80 + 80;
		int yPos = row * 80 + 40;


		int pieceType = selectedPiece[2];
		int oldRow = selectedPiece[0];
		int oldCol = selectedPiece[1];

		int oldXPos = oldCol * 80 + 80;
		int oldYPos = oldRow * 80 + 40;


		board [oldRow][oldCol] = 0;

		if (oldRow % 2 == oldCol % 2)
			offScreenBuffer.setColor(lightSquareColor);
		else
			offScreenBuffer.setColor(darkSquareColor);

		offScreenBuffer.fillRect(oldXPos, oldYPos, SQUARE_SIZE, SQUARE_SIZE);

		board[row][col] = pieceType;

		String pieceFileName = "";

		if (board[row][col] > 0)
			pieceFileName = "White";
		else if (board[row][col] < 0)
			pieceFileName = "Black";

		String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
		pieceFileName += pieceTypes[Math.abs(board[row][col]) - 1] + ".png";

		Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
		offScreenBuffer.drawImage(pieceImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);



		g.drawImage(offScreenImage, 0, 0, this);

		//		for (int i =0; i<8; i++) {
		//			System.out.println();
		//			for (int j = 0; j<8; j++)
		//				System.out.print(board[i] [j]);
		//		}

		boolean currentPlayer = true;

		//PLAYING SOUND ON PIECE MOVE

		if (currentPlayer == false) {
			try {
				audioIn = AudioSystem.getAudioInputStream(new File("blackMove.wav").getAbsoluteFile());
				clip = AudioSystem.getClip();
				clip.open(audioIn);
				clip.start();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			else {
				try {
					audioIn = AudioSystem.getAudioInputStream(new File("whiteMove.wav").getAbsoluteFile());
					clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
			}


			} 







	}



	public static void main(String[] args) {

		frame = new JFrame("Chess");
		chessTesting panel = new chessTesting();

		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		System.out.println("--------> "+ frame.getContentPane().getWidth());


	}

}




//
//
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.Toolkit;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.WindowConstants;
//
//public class chessTesting extends JPanel {
//
//	static JFrame frame;
//
//	final int SQUARE_SIZE = 90;
//	final int LEFT_BORDER = 768 - SQUARE_SIZE * 4;
//	final int TOP_BORDER = 400 - SQUARE_SIZE * 4;
//	
//	Color lightSquareColor;
//	Color darkSquareColor;
//
//	int[][] board = {{-4, -2, -3, -5, -6, -3, -2, -4},
//					 {-1, -1, -1, -1, -1, -1, -1, -1},
//					 { 0,  0,  0,  0,  0,  0,  0,  0},
//					 { 0,  0,  0,  0,  0,  0,  0,  0},
//					 { 0,  0,  0,  0,  0,  0,  0,  0},
//					 { 0,  0,  0,  0,  0,  0,  0,  0},
//					 { 1,  1,  1,  1,  1,  1,  1,  1},
//					 { 4,  2,  3,  5,  6,  3,  2,  4}};
//
//	Image offScreenImage;
//	Graphics offScreenBuffer;
//
//	public chessTesting() {
//		lightSquareColor = new Color(240, 215, 180);
//		darkSquareColor = new Color(180, 135, 100);
//		frame.setSize(1920,1080);
//	}
//
//	public void paintComponent(Graphics g) {
//		//Sets up the off-screen buffer the first time paint() is called
//		if (offScreenBuffer == null) {
//			offScreenImage = createImage(this.getWidth(), this.getHeight());
//			offScreenBuffer = offScreenImage.getGraphics();
//		}
//
//		//Clears the off-screen buffer
//		offScreenBuffer.clearRect (0, 0, this.getWidth(), this.getHeight());
//
//		//Draws the board
//		for (int row = 0; row < 8; row++)
//			for (int col = 0; col < 8; col++) {
//				if (row % 2 == col % 2)
//					offScreenBuffer.setColor(lightSquareColor);
//				else
//					offScreenBuffer.setColor(darkSquareColor);
//				
//				int xPos = col * SQUARE_SIZE + LEFT_BORDER;
//				int yPos = row * SQUARE_SIZE + TOP_BORDER;
//				
//				offScreenBuffer.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
//
//				String pieceFileName;
//				
//				if (board[row][col] > 0)
//					pieceFileName = "White";
//				else if (board[row][col] < 0)
//					pieceFileName = "Black";
//				else
//					continue;
//				
//				String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
//				pieceFileName += pieceTypes[Math.abs(board[row][col]) - 1] + ".png";
//				
//				Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
//				offScreenBuffer.drawImage(pieceImage, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
//			}
//
//		//Transfers the off-screen buffer to the screen
//		g.drawImage(offScreenImage, 0, 0, this);
//	}
//
//	public static void main(String[] args) {
//
//		frame = new JFrame("Chess");
//		chessTesting panel = new chessTesting();
//
//		frame.add(panel);
//		frame.pack();
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		frame.setVisible(true);
//
//	}
//
//}
