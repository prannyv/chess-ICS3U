import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

// Richard Yang and Pranav Varma
// January 27, 2021
// This program simulates a two player chess game that is controlled using the mouse.

public class monkey extends JPanel implements ActionListener, MouseListener {

	static JFrame frame;

	final int SQUARE_SIZE = 80;
	final int LEFT_BORDER = 768 - SQUARE_SIZE * 4;
	final int TOP_BORDER = 400 - SQUARE_SIZE * 4;

	final boolean WHITE = true, BLACK = false;
	final int PAWN = 1, KNIGHT = 2, BISHOP = 3, ROOK = 4, QUEEN = 5, KING = 6;

	JButton newGameButton;
	JButton rulesButton;
	JButton clocksButton;
	JButton themesButton;
	JButton exitButton;
	JButton color1;
	JButton color2;
	JButton color3;
	JButton color4;

	JTextArea rules;
	boolean isRulesShowing = false;
	boolean isThemesShowing = false;
	
	JButton resignButton;
    JButton drawButton;
    boolean showInGameButtons = false;
	
	JTextArea notation;
	JScrollPane notationWindow;
	
	Timer timer;
	boolean whiteTimerOn;
	boolean blackTimerOn;
	int blackTime;
	int whiteTime;
	int selectedTime = 6000;
	
	JButton time1;
	JButton time5;
	JButton time10;
	boolean isTimesShowing = false;
	
	Color lightSquareColor, darkSquareColor;
	Color lightSquareHighlight, darkSquareHighlight;
	ImageIcon currentTheme;
	
	int[][] board;
	
	static boolean currentPlayer;
	
	int lastPiece, lastRow, lastCol;
	ArrayList<Integer> legalRows, legalCols;
	
	int enPassantRow, enPassantCol;
	
	int whiteKingRow, whiteKingCol, blackKingRow, blackKingCol;
	boolean whiteCanCastleKingside, whiteCanCastleQueenside;
	boolean blackCanCastleKingside, blackCanCastleQueenside;
	
	ArrayList<Integer> whiteTaken, blackTaken;
	
	boolean gameOver;
	int turnCounter;
	int fiftyMoveCount;
	
	ArrayList<int[][]> boardStates;
	ArrayList<boolean[]> castlingRights;

	Image offScreenImage;
	Graphics offScreenBuffer;

	public Chess() {
		// Starts a new game
		newGameButton = new JButton("New Game");
		newGameButton.setBounds(80, 200, 200, 50);
		newGameButton.setFont(new Font("Arial", Font.PLAIN, 24));
		newGameButton.setActionCommand("New Game");
		newGameButton.addActionListener(this);

		// Displays the rules
		rulesButton = new JButton("Rules");
		rulesButton.setBounds(80, 300, 200, 50);
		rulesButton.setFont(new Font("Arial", Font.PLAIN, 24));
		rulesButton.setActionCommand("Rules");
		rulesButton.addActionListener(this);

		// Adjusts the clocks
		clocksButton = new JButton("Clocks");
		clocksButton.setBounds(80, 400, 200, 50);
		clocksButton.setFont(new Font("Arial", Font.PLAIN, 24));
		clocksButton.setActionCommand("Clocks");
		clocksButton.addActionListener(this);
		
		// 1 minute option
		time1 = new JButton("1 Minute");
		time1.setBounds(290, 475, 140, 25);
		time1.setFont(new Font("Arial", Font.PLAIN, 24));
		time1.setActionCommand("time1");
		time1.addActionListener(this);
		
		// 5 minute option
		time5 = new JButton("5 Minutes");
		time5.setBounds(290, 425, 140, 25);
		time5.setFont(new Font("Arial", Font.PLAIN, 24));
		time5.setActionCommand("time5");
		time5.addActionListener(this);
		
		// 10 minutes option
		time10 = new JButton("10 Minutes");
		time10.setBounds(290, 375, 140, 25);
		time10.setFont(new Font("Arial", Font.PLAIN, 24));
		time10.setActionCommand("time10");
		time10.addActionListener(this);

		// Changes the theme
		themesButton = new JButton("Themes");
		themesButton.setBounds(80, 500, 200, 50);
		themesButton.setFont(new Font("Arial", Font.PLAIN, 24));
		themesButton.setActionCommand("Themes");
		themesButton.addActionListener(this);

		// Closes the program
		exitButton = new JButton("Exit");
		exitButton.setBounds(80, 600, 200, 50);
		exitButton.setFont(new Font("Arial", Font.PLAIN, 24));
		exitButton.setActionCommand("Exit");
		exitButton.addActionListener(this);

		// Brown theme
		color1 = new JButton("Classic");
		color1.setBounds(300, 450, 130, 25);
		color1.setFont(new Font("Arial", Font.PLAIN, 24));
		color1.setActionCommand("Brown");
		color1.addActionListener(this);

		// Green theme
		color2 = new JButton("Modern");
		color2.setBounds(300, 500, 130, 25);
		color2.setFont(new Font("Arial", Font.PLAIN, 24));
		color2.setActionCommand("Green");
		color2.addActionListener(this);

		// Blue theme
		color3 = new JButton("Ocean");
		color3.setBounds(300, 550, 130, 25);
		color3.setFont(new Font("Arial", Font.PLAIN, 24));
		color3.setActionCommand("Blue");
		color3.addActionListener(this);

		// Grey theme
		color4 = new JButton("Ivory");
		color4.setBounds(300, 600, 130, 25);
		color4.setFont(new Font("Arial", Font.PLAIN, 24));
		color4.setActionCommand("Black");
		color4.addActionListener(this);

		// Summary of the rules of chess
		rules = new JTextArea("This is a two player game of chess. The ultimate goal of the game is "
				+ "to capture the other players' king.To move the pieces, click on them with your "
				+ "mouse. \nEach piece has a different moveset: \nPawns can move 1 space forward "
				+ "and capture diagonally one spot forward. They can also move two spaces on their "
				+ "first move.\nThe bishop can move across all free diagonal squares.\nThe rook can "
				+ "move across all free vertical and horizontal squares.\nThe knight can move in an "
				+ "L shape horizontally or vertically. The knight is the only piece which can jump "
				+ "over other pieces!\nThe queen can move in all available diagonal OR horizontal "
				+ "and vertical spaces (like the bishop & rook combined).\nThe king can only move "
				+ "one spot horizontally, vertically, or diagonally, but is the players most "
				+ "important piece!\nIf the king can be captured, that means you are in \"check\". "
				+ "This means that you must move a piece to stop the king from being captured.\nIf "
				+ "there are no possible moves to free your king, thats \"checkmate\"! You lose! "
				+ "This game was produced by Richrd Yang and Pranav Varma.");
		rules.setBounds(50, 360, 350, 430);
		rules.setFont(new Font("Arial", Font.PLAIN, 14));
		rules.setWrapStyleWord(true);
		rules.setLineWrap(true);
		
		// Sets the highlight color
		int lightRed = 235;
		int lightGreen = 235;
		int lightBlue = 210;
		int darkRed = 120;
		int darkGreen = 150;
		int darkBlue = 85;
		lightSquareColor = new Color(240, 215, 180);
		darkSquareColor = new Color(180, 135, 100);
		lightSquareHighlight = new Color((lightRed + 61) / 2, (lightGreen + 242) / 2, (lightBlue + 255) / 2);
		darkSquareHighlight = new Color((darkRed + 61) / 2, (darkGreen + 242) / 2, (darkBlue + 255) / 2);

		setLayout(null);

		// Column header for the move number
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		JTextArea moveHeader = new JTextArea(" #");
		moveHeader.setBounds(LEFT_BORDER + SQUARE_SIZE * 8 + 20, TOP_BORDER + SQUARE_SIZE * 2, 40, 30);
		moveHeader.setFont(new Font("Consolas", Font.BOLD, 20));
		moveHeader.setBackground(new Color(200, 200, 200));
		moveHeader.setBorder(border);
		moveHeader.setEditable(false);
		
		// Column header for white's moves
		JTextArea whiteHeader = new JTextArea("    White");
		whiteHeader.setBounds(LEFT_BORDER + SQUARE_SIZE * 8 + 20 + 39, TOP_BORDER + SQUARE_SIZE * 2, 142, 30);
		whiteHeader.setFont(new Font("Consolas", Font.BOLD, 20));
		whiteHeader.setBackground(new Color(200, 200, 200));
		whiteHeader.setBorder(border);
		whiteHeader.setEditable(false);
		
		// Column header for black's moves
		JTextArea blackHeader = new JTextArea("   Black");
		blackHeader.setBounds(LEFT_BORDER + SQUARE_SIZE * 8 + 20 + 182, TOP_BORDER + SQUARE_SIZE * 2, 140, 30);
		blackHeader.setFont(new Font("Consolas", Font.BOLD, 20));
		blackHeader.setBackground(new Color(200, 200, 200));
		blackHeader.setBorder(border);
		blackHeader.setEditable(false);
		
		// Notation window
		notation = new JTextArea();
		notation.setEditable(false);
		notation.setFont(new Font("Consolas", Font.PLAIN, 20));
		notationWindow = new JScrollPane(notation);
		notationWindow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		notationWindow.getVerticalScrollBar().setUnitIncrement(8);
		notationWindow.setBounds(LEFT_BORDER + SQUARE_SIZE * 8 + 20, TOP_BORDER + SQUARE_SIZE * 2 + 30,
				320, SQUARE_SIZE * 4 - 30);
		
		// Resign button
		resignButton = new JButton("Resign");
        resignButton.setBounds(120, 300, 200, 100);
        resignButton.setFont(new Font("Arial", Font.PLAIN, 24));
        resignButton.setActionCommand("Resign");
        resignButton.addActionListener(this);
        
        // Offer a draw
        drawButton = new JButton("Draw");
        drawButton.setBounds(120, 450, 200, 100);
        drawButton.setFont(new Font("Arial", Font.PLAIN, 24));
        drawButton.setActionCommand("Draw");
        drawButton.addActionListener(this);
        
        // Sets the default times
        whiteTimerOn = false;
		whiteTime = 6000;
		blackTimerOn = false;
		blackTime = 6000;
		timer = new Timer(100, new TimerEventHandler());
        
        // Sets up the board
        resetBoard();
        gameOver = true;
        
        // Adds all components to the JPanel
        add(newGameButton);
		add(rulesButton);
		add(clocksButton);
		add(time1);
		add(time5);
		add(time10);
		add(themesButton);
		add(exitButton);
		add(rules);
		add(color1);
		add(color2);
		add(color3);
		add(color4);
		add(moveHeader);
		add(whiteHeader);
		add(blackHeader);
		add(notationWindow);
		add(resignButton);
        add(drawButton);
		
        // Hides all sub-buttons
		rules.setVisible(false);
		time1.setVisible(false);
		time5.setVisible(false);
		time10.setVisible(false);
		color1.setVisible(false);
		color2.setVisible(false);
		color3.setVisible(false);
		color4.setVisible(false);
		resignButton.setVisible(false);
		drawButton.setVisible(false);

		setPreferredSize(new Dimension(1920, 1080));
		addMouseListener(this);
	}

	public void paintComponent(Graphics g) {
		
		// Sets up the off-screen buffer the first time paint() is called
		if (offScreenBuffer == null) {
			offScreenImage = createImage(this.getWidth(), this.getHeight());
			offScreenBuffer = offScreenImage.getGraphics();
		}

		// Clears the off-screen buffer
		offScreenBuffer.clearRect (0, 0, this.getWidth(), this.getHeight());

		// Draws the board
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++) {
				// Colors the squares in a checkered pattern
				setColor(row, col);

				// Finds the x and y positions for each row and column
				int xPos = col * SQUARE_SIZE + LEFT_BORDER;
				int yPos = row * SQUARE_SIZE + TOP_BORDER;

				// Draws the squares
				offScreenBuffer.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

				int piece = board[row][col];
				
				setHighlight(row, col);

				// Highlights the selected piece
				if (row == lastRow && col == lastCol)
					offScreenBuffer.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);

				// Highlights legal moves
				for (int i = 0; i < legalRows.size(); i++)
					if (legalRows.get(i) == row && legalCols.get(i) == col) {
						if (piece == 0)
							offScreenBuffer.fillOval(xPos + SQUARE_SIZE / 3, yPos + SQUARE_SIZE / 3,
									SQUARE_SIZE / 3, SQUARE_SIZE / 3);
						else {
							
							offScreenBuffer.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
							
						}
					}
				
				// Highlights the king if it is in check
				if (Math.abs(piece) == KING && belongsToPlayer(piece) && squareIsAttacked(row, col)) {
					Image checkHighlight = Toolkit.getDefaultToolkit().getImage("CheckHighlight.png");
					offScreenBuffer.drawImage(checkHighlight, xPos, yPos, SQUARE_SIZE, SQUARE_SIZE, this);
				}

				// Draws the pieces
				drawPiece(piece, xPos, yPos, SQUARE_SIZE);
			}
		
		// Updates the taken pieces
		displayCapturedPieces();
		
		offScreenBuffer.setColor(Color.BLACK);
		
		//draws timer for white
		int minutes = whiteTime / 600;
		int seconds = whiteTime % 600;
		seconds = (int) Math.floor(seconds / 10);
		String timeString = String.format("Time: %02d:%02d", minutes, seconds);
		offScreenBuffer.drawString (timeString, 650, 45);
		offScreenBuffer.drawRect(640, 25, 85, 30);

		//draws timer for black
		int minutes2 = blackTime / 600;
		int seconds2 = blackTime % 600;
		seconds2 = (int)Math.floor(seconds2 / 10);
		String timeString2 = String.format("Time: %02d:%02d", minutes2, seconds2);
		offScreenBuffer.drawString(timeString2, 950, 45);
		offScreenBuffer.drawRect(940, 25, 85, 30);

		// Transfers the off-screen buffer to the screen
		g.drawImage(offScreenImage, 0, 0,this);
	}
	
	/**Handles the timers and updates 10 times per second.
	 * @author reput
	 *
	 */
	public class TimerEventHandler implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{

			if (currentPlayer == WHITE) {
				// If white times out
				if (whiteTime <= 0 && whiteTimerOn)
				{
					whiteTimerOn = false;
					timer.stop();
					JOptionPane.showMessageDialog (monkey.this, "Black wins!", "Timeout", JOptionPane.INFORMATION_MESSAGE);
					endGame();
				}
				else
				{
					// Increment the time (you could also count down)
					whiteTime--;

					repaint (720, 15, 100, 100);
				}
			}
			
			// if black times out
			else if (currentPlayer == BLACK){
				if (blackTime <= 0 && blackTimerOn)
				{
					blackTimerOn = false;
					timer.stop();
					JOptionPane.showMessageDialog (monkey.this, "White wins!", "Timeout", JOptionPane.INFORMATION_MESSAGE);
					gameOver = true;
					newGameButton.setVisible(true);
					rulesButton.setVisible(true);
					clocksButton.setVisible(true);
					themesButton.setVisible(true);
					exitButton.setVisible(true);    
				}
				else
				{
					// Increment the time (you could also count down)
					blackTime--;

					repaint (600, 20, 500, 50);
				}
			}
			
			repaint (600, 20, 500, 50);

		}
	}
	
	/**Used for JButtons.
	 *@param e - the command string
	 */
	public void actionPerformed(ActionEvent e) {
		String eventName = e.getActionCommand();
		if (eventName.equals("New Game")) {

			//restarts the game by resetting the board, 
			//removing buttons, and allowing player to move pieces
			resetBoard();

			timer.start();
			whiteTimerOn = true;

			newGameButton.setVisible(false);
			rulesButton.setVisible(false);
			clocksButton.setVisible(false);
			themesButton.setVisible(false);
			exitButton.setVisible(false);

			rules.setVisible(false);
			color1.setVisible(false);
			color2.setVisible(false);
			color3.setVisible(false);
			color4.setVisible(false);
			
			time1.setVisible(false);
			time5.setVisible(false);
			time10.setVisible(false);
			
			blackTime = selectedTime;
			whiteTime = selectedTime;


			resignButton.setVisible(true);
			drawButton.setVisible(true);

			gameOver = false;


			repaint();

		}
		else if (eventName.equals("Rules")) {
			//toggles the rules JLabel on or off
			isRulesShowing = !isRulesShowing;

			newGameButton.setVisible(!isRulesShowing);
			rules.setVisible(isRulesShowing);
			clocksButton.setVisible(!isRulesShowing);
			themesButton.setVisible(!isRulesShowing);
			exitButton.setVisible(!isRulesShowing);

			isThemesShowing = false;
			color1.setVisible(false);
			color2.setVisible(false);
			color3.setVisible(false);
			color4.setVisible(false);
			
			isTimesShowing = false;
			time1.setVisible(false);
			time5.setVisible(false);
			time10.setVisible(false);

		}
		else if (eventName.equals("Clocks")) {
			
			// Shows options for times
			isTimesShowing = !isTimesShowing;
			
			time1.setVisible(isTimesShowing);
			time5.setVisible(isTimesShowing);
			time10.setVisible(isTimesShowing);
			
			isThemesShowing = false;
			color1.setVisible(false);
			color2.setVisible(false);
			color3.setVisible(false);
			color4.setVisible(false);
			
			
			
		}
		
		// 1 minute
		else if (eventName.equals("time1")) {
			selectedTime = 50;
			repaint();
		}
		// 5 minuutes
		else if (eventName.equals("time5")) {
			selectedTime = 3000;
			repaint();
		}
		// 10 minutes
		else if (eventName.equals("time10")) {
			selectedTime = 6000;
			repaint();
		}
		
		else if (eventName.equals("Themes")) {
			// Shows options for themes
			isThemesShowing = !isThemesShowing;
			color1.setVisible(isThemesShowing);
			color2.setVisible(isThemesShowing);
			color3.setVisible(isThemesShowing);
			color4.setVisible(isThemesShowing);

			isTimesShowing = false;
			rules.setVisible(false);
			time1.setVisible(false);
			time5.setVisible(false);
			time10.setVisible(false);

		}

		else if(eventName.equals("Brown")) {
			lightSquareColor = new Color(240, 215, 180);
			darkSquareColor = new Color(180, 135, 100);
			repaint();
		}

		else if(eventName.equals("Green")) {
			lightSquareColor = new Color(238, 238, 210);
			darkSquareColor = new Color(118, 150, 86);
			repaint();
		}

		else if(eventName.equals("Blue")) {
			lightSquareColor = new Color(151, 147, 204);
			darkSquareColor = new Color(75, 81, 152);
			repaint();
		}
		if(eventName.equals("Black")) {
			lightSquareColor = Color.white;
			darkSquareColor = new Color (168,170,172);
			repaint();
		}

		// Closes the program
		else if (eventName.equals("Exit"))
			System.exit(0);

		// Resign button
		else if (eventName.equals("Resign")) {
			int option = JOptionPane.showConfirmDialog(frame, "Are You Sure You Want to Resign",
					"Resign?", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				if (currentPlayer == WHITE)
					JOptionPane.showMessageDialog (this, "Black Wins!", "Resigned!", JOptionPane.WARNING_MESSAGE);
				else if (currentPlayer == BLACK)
					JOptionPane.showMessageDialog (this, "White Wins!", "Resigned!", JOptionPane.WARNING_MESSAGE);
				gameOver = true;
				newGameButton.setVisible(true);
				rulesButton.setVisible(true);
				clocksButton.setVisible(true);
				themesButton.setVisible(true);
				exitButton.setVisible(true);
				resignButton.setVisible(false);
				drawButton.setVisible(false);
				timer.stop();
			}
		}
		
		// Draw button
		else if (eventName.equals("Draw")) {
			int option = JOptionPane.showConfirmDialog(frame, "Do Both Players Agree To a Draw?",
					"Draw?", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog (this, "It's a Draw!", "Game Over!", JOptionPane.WARNING_MESSAGE);
				gameOver = true;
				newGameButton.setVisible(true);
				rulesButton.setVisible(true);
				clocksButton.setVisible(true);
				themesButton.setVisible(true);
				exitButton.setVisible(true);
				resignButton.setVisible(false);
				drawButton.setVisible(false);
				timer.stop();
				//add these into checkmate/stalemate as well
			}
		}
	}

	/**Receives input from the user to play the game.
	 *@param e - the mouse action
	 */
	public void mouseClicked(MouseEvent e) {
		
		// Ignores mouse input if the game is over
		if (gameOver)
			return;

		// Gets the row and column of the square that is clicked on
		int selectedRow = Math.floorDiv(e.getY() - TOP_BORDER, SQUARE_SIZE);
		int selectedCol = Math.floorDiv(e.getX() - LEFT_BORDER, SQUARE_SIZE);

		// Ignores squares outside of the board
		if (!squareIsValid(selectedRow, selectedCol))
			return;

		int selectedPiece = board[selectedRow][selectedCol];
		
		boolean isLegalMove = false;
		
		// Moves the previously selected piece to the selected square if it is a legal move
		for (int i = 0; i < legalRows.size(); i++)
			if (selectedRow == legalRows.get(i) && selectedCol == legalCols.get(i)) {
				movePiece(selectedRow, selectedCol);
				isLegalMove = true;
				deselectPiece();
				break;
			}
		
		// Handles clicks that are not for movement
		if (!isLegalMove) {
			
			// Selects the piece if it belongs to the current player and was not just selected
			if (belongsToPlayer(selectedPiece) && (selectedRow != lastRow || selectedCol != lastCol))
				selectPiece(selectedPiece, selectedRow, selectedCol);
			
			// Deselects the previous piece
			else
				deselectPiece();
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**Animates the specified piece moving from one square to another.
	 * @param piece - the piece that is being moved
	 * @param sourceRow - the row it started in
	 * @param sourceCol - the column it started in
	 * @param targetRow - the row it moved to
	 * @param targetCol - the column it moved to
	 */
	public void animatePiece(int piece, int sourceRow, int sourceCol, int targetRow, int targetCol) {
		Graphics g = getGraphics();

		// Gets the piece image
		String pieceFileName;
		if (piece > 0)
			pieceFileName = "White";
		else if (piece < 0)
			pieceFileName = "Black";
		else
			return;
		String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
		pieceFileName += pieceTypes[Math.abs(piece) - 1] + ".png";
		Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
		
		// Gets the starting and finsihing x and y positions
		int sourceX = LEFT_BORDER + sourceCol * SQUARE_SIZE;
		int sourceY = TOP_BORDER + sourceRow * SQUARE_SIZE;
		
		int targetX = LEFT_BORDER + targetCol * SQUARE_SIZE;
		int targetY = TOP_BORDER + targetRow * SQUARE_SIZE;
		
		// Gets the angle of elevation
		double angle = Math.atan2(targetY - sourceY, targetX - sourceX);
		
		// Sets a constant speed that the piece travels at
		double diagonalOffset = 10;
		
		// Sets the x and y increments based on the angle and speed
		double horizontalOffset = 0;
		double verticalOffset = 0;
		if (angle == Math.PI / 2) {
			horizontalOffset = 0;
			verticalOffset = diagonalOffset;
		}
		else if (angle == -Math.PI / 2) {
			horizontalOffset = 0;
			verticalOffset = -diagonalOffset;
		}
		else if (angle == 0) {
			horizontalOffset = diagonalOffset;
		}
		else if (angle == Math.PI) {
			horizontalOffset = diagonalOffset;
			verticalOffset = 0;
		}
		else {
			horizontalOffset = diagonalOffset * Math.cos(angle);
			verticalOffset = diagonalOffset * Math.sin(angle);
		}
		
		// Draws the piece moving frame by frame
		double x = sourceX;
		double y = sourceY;
		
		while ((targetX - x) * (targetX - sourceX) >= 0 && (targetY - y) * (targetY - sourceY) >= 0) {
			// Redraws the board
			for (int row = 0; row < 8; row++)
				for (int col = 0; col < 8; col++) {
					setColor(row, col);
					int xPos = col * SQUARE_SIZE + LEFT_BORDER;
					int yPos = row * SQUARE_SIZE + TOP_BORDER;
					offScreenBuffer.fillRect(xPos, yPos, SQUARE_SIZE, SQUARE_SIZE);
					drawPiece(board[row][col], xPos, yPos, SQUARE_SIZE);
				}
			offScreenBuffer.drawImage(pieceImage, (int)x, (int)y, SQUARE_SIZE, SQUARE_SIZE, this);
			g.drawImage(offScreenImage, 0, 0,this);
			
			x += horizontalOffset;
			y += verticalOffset;
		}
		
		setColor(targetRow, targetCol);
		offScreenBuffer.fillRect(targetX, targetY, SQUARE_SIZE, SQUARE_SIZE);
		offScreenBuffer.drawImage(pieceImage, targetX, targetY, SQUARE_SIZE, SQUARE_SIZE, this);
		g.drawImage(offScreenImage, 0, 0,this);
	}

	/**Sets all attributes related to movement to that of the specified piece.
	 * @param piece - the piece that was selected
	 * @param row - the row of the piece
	 * @param col - the column of the piece
	 */
	public void selectPiece(int piece, int row, int col) {
		lastPiece = piece;
		lastRow = row;
		lastCol = col;
		getLegalMoves(piece, row, col);
		repaint();
	}

	/**Sets all attributes related to movement to their "default" states.
	 * 
	 */
	public void deselectPiece() {
		lastPiece = 0;
		lastRow = -1;
		lastCol = -1;
		legalRows.clear();
		legalCols.clear();
		repaint();
	}
	
	/**Handles all things related to making a move in chess.
	 * @param selectedRow - the row to move to
	 * @param selectedCol - the column to move to
	 */
	public void movePiece(int selectedRow, int selectedCol) {
		
		// Stores the piece that was captured
		int capturedPiece = board[selectedRow][selectedCol];
		
		// Moves the piece
		board[lastRow][lastCol] = 0;
		animatePiece(lastPiece, lastRow, lastCol, selectedRow, selectedCol);
		board[selectedRow][selectedCol] = lastPiece;
		
		// Removes the en passant pawn if it was captured
		if (Math.abs(lastPiece) == PAWN && selectedCol == enPassantCol) {
			if (currentPlayer == WHITE && selectedRow == enPassantRow + 1)
				board[enPassantRow][enPassantCol] = 0;
			else if (currentPlayer == BLACK && selectedRow == enPassantRow - 1)
				board[enPassantRow][enPassantCol] = 0;
		}
		

		// Promotes a pawn to a queen if it reaches the last row
		if (lastPiece == 1 && selectedRow == 7) 
			board[7][selectedCol] = QUEEN;
		else if (lastPiece == -1 & selectedRow == 0)
			board[0][selectedCol] = -QUEEN;
		
		// Checks if the white king was moved
		else if (lastPiece == KING) {

			// Updates the king's row and column
			whiteKingRow = selectedRow;
			whiteKingCol = selectedCol;

			// Removes castling rights
			whiteCanCastleKingside = false;
			whiteCanCastleQueenside = false;

			// Jumps the rook if the king just castled

			// Kingside castle
			if (selectedCol == lastCol + 2) {
				board[0][5] = ROOK;
				board[0][7] = 0;
			}

			// Queenside castle
			else if (selectedCol == lastCol - 2) {
				board[0][3] = ROOK;
				board[0][0] = 0;
			}
		}

		// Checks if the black king was moved
		else if (lastPiece == -KING) {

			// Updates the king's row and column
			blackKingRow = selectedRow;
			blackKingCol = selectedCol;

			// Removes castling rights
			blackCanCastleKingside = false;
			blackCanCastleQueenside = false;

			// Jumps the rook if the king just castled

			// Kingside castle
			if (selectedCol == lastCol + 2) {
				board[7][5] = -ROOK;
				board[7][7] = 0;
			}

			// Queenside castle
			else if (selectedCol == lastCol - 2) {
				board[7][3] = -ROOK;
				board[7][0] = 0;
			}
		}
		
		// Removes castling rights if the kingside rook moves
		else if (lastPiece == ROOK && lastCol == 7)
			if (currentPlayer == WHITE && lastRow == 0)
				whiteCanCastleKingside = false;
			else if (currentPlayer == BLACK && lastRow == 7)
				blackCanCastleKingside = false;

		// Removes castling rights if the queenside rook moves
			else if (lastPiece == ROOK && lastCol == 0)
				if (currentPlayer == WHITE && lastRow == 0)
					whiteCanCastleQueenside = false;
				else if (currentPlayer == BLACK && lastRow == 7)
					blackCanCastleQueenside = false;
		
		// Resets the en passant pawn
		enPassantRow = -1;
		enPassantCol = -1;

		// Checks if a pawn moved two spaces
		if (Math.abs(lastPiece) == PAWN && Math.abs(selectedRow - lastRow) == 2) {
			enPassantRow = selectedRow;
			enPassantCol = selectedCol;
		}
		
		// Adds the captured piece to the player's ArrayList
		if (capturedPiece > 0)
			blackTaken.add(capturedPiece);
		else if (capturedPiece < 0)
			whiteTaken.add(capturedPiece);
		
		// Updates the notation window
		notateMove(selectedRow, selectedCol, capturedPiece);
		
		// Updates the fifty-move counter
		if (capturedPiece != 0 || Math.abs(lastPiece) == PAWN)
			fiftyMoveCount = 0;
		else
			fiftyMoveCount++;
		
		// Switches to the other player's turn
		currentPlayer = !currentPlayer;
		
		// Updates the turn counter
		turnCounter++;
		
		// Checks if the game is over
		gameIsOver();
		
		if (fiftyMoveCount == 100) {
			JOptionPane.showMessageDialog(this, "It's a Draw!", "Fifty-move Rule!", JOptionPane.WARNING_MESSAGE);
			endGame();
		}
	}
	
	/**Adds text to the notation window after every move.
	 * @param targetRow - the row that was moved to
	 * @param targetCol - the column that was moved to
	 * @param capturedPiece - the piece that used to occupy that square
	 */
	public void notateMove(int targetRow, int targetCol, int capturedPiece) {
		// Piece type
		String move = "";
		char[] pieceTypes = {'N', 'B', 'R', 'Q', 'K'};
		if (Math.abs(lastPiece) != PAWN)
			move += pieceTypes[Math.abs(lastPiece) - 2];
		
		// Capturing
		if (capturedPiece != 0)
			move += 'x';
		else if (Math.abs(lastPiece) == PAWN && targetCol == enPassantCol) {
			if (currentPlayer == WHITE && targetRow == enPassantRow + 1)
				move += 'x';
			else if (currentPlayer == BLACK && targetRow == enPassantRow - 1)
				move += 'x';
		}
		
		// Destination
		move += (char)('a' + targetCol) + "" + (targetRow + 1);
		if (currentPlayer == WHITE)
			notation.append(String.format("\n %-5s", turnCounter / 2 + 1 + "."));
		notation.append(String.format("%-10s", move));
	}
	
	/**Sets the color of the off-screen buffer to the color of the specified square.
	 * @param row - the row of the square
	 * @param col - the column of the square
	 */
	public void setColor(int row, int col) {
		if (row % 2 == col % 2)
			offScreenBuffer.setColor(lightSquareColor);
		else
			offScreenBuffer.setColor(darkSquareColor);
	}

	/**Sets the color of the off-screen buffer to the highlight color of that square.
	 * @param row - the row of the square
	 * @param col - the column of the square
	 */
	public void setHighlight(int row, int col) {
		if ((row + col) % 2 == 0)
			offScreenBuffer.setColor(lightSquareHighlight);
		else
			offScreenBuffer.setColor(darkSquareHighlight);
	}

	/**Draws a the image of the specified piece.
	 * @param piece - the piece to be drawn
	 * @param xPos - the x position to draw it at
	 * @param yPos - the y position to draw it at
	 * @param size - the side length of the image
	 */
	public void drawPiece(int piece, int xPos, int yPos, int size) {
		String pieceFileName;

		// Color of the piece
		if (piece > 0)
			pieceFileName = "White";
		else if (piece < 0)
			pieceFileName = "Black";
		else
			return;

		// Type of piece
		String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};
		pieceFileName += pieceTypes[Math.abs(piece) - 1] + ".png";

		Image pieceImage = Toolkit.getDefaultToolkit().getImage(pieceFileName);
		offScreenBuffer.drawImage(pieceImage, xPos, yPos, size, size, this);
	}

	/**Shows the pieces captured by each player on the right side of the board.
	 * 
	 */
	public void displayCapturedPieces() {
		Collections.sort(whiteTaken);
		Collections.reverse(whiteTaken);
		Collections.sort(blackTaken);
		Collections.reverse(blackTaken);
		
		for (int i = 0; i < whiteTaken.size(); i++) {
			int xPos = LEFT_BORDER + SQUARE_SIZE * 8 + 20 + i % 8 * 40;
			int yPos = TOP_BORDER + SQUARE_SIZE * 6 + 5 + i / 8 * 40;
			drawPiece(whiteTaken.get(i), xPos, yPos, 40);
		}
		
		for (int i = 0; i < blackTaken.size(); i++) {
			int xPos = LEFT_BORDER + SQUARE_SIZE * 8 + 20 + i % 8 * 40;
			int yPos = TOP_BORDER + SQUARE_SIZE * 1 + 40 - 5 - i / 8 * 40;
			drawPiece(blackTaken.get(i), xPos, yPos, 40);
		}
	}
	
	/**Resets the game of chess.
	 * 
	 */
	public void resetBoard() {
		board = new int[][] {{ 4,  2,  3,  5,  6,  3,  2,  4},
							 { 1,  1,  1,  1,  1,  1,  1,  1},
							 { 0,  0,  0,  0,  0,  0,  0,  0},
							 { 0,  0,  0,  0,  0,  0,  0,  0},
							 { 0,  0,  0,  0,  0,  0,  0,  0},
							 { 0,  0,  0,  0,  0,  0,  0,  0},
							 {-1, -1, -1, -1, -1, -1, -1, -1},
							 {-4, -2, -3, -5, -6, -3, -2, -4}};
		
		currentPlayer = WHITE;
		
		lastPiece = 0;
		lastRow = -1;
		lastCol = -1;
		legalRows = new ArrayList<Integer>();
		legalCols = new ArrayList<Integer>();
		
		enPassantRow = -1;
		enPassantCol = -1;
		
		whiteKingRow = 0;
		whiteKingCol = 4;
		blackKingRow = 7;
		blackKingCol = 4;
		
		whiteCanCastleKingside = true;
		whiteCanCastleQueenside = true;
		blackCanCastleKingside = true;
		blackCanCastleQueenside = true;
		
		whiteTaken = new ArrayList<Integer>();
		blackTaken = new ArrayList<Integer>();
		
		turnCounter = 0;
		fiftyMoveCount = 0;
        
		notation.selectAll();
		notation.replaceSelection("");
		
		whiteTime = selectedTime;
		blackTime = selectedTime;
	}
	
	/**Checks if the specified square exists.
	 * @param row - the row of the square
	 * @param col - the column of the square
	 * @return true if the square exists; false otherwise.
	 */
	public boolean squareIsValid(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}

	/**Checks if the specified piece belongs to the opponent.
	 * @param piece - the piece to be checked
	 * @return true if it belongs to the opponent; false otherwise.
	 */
	public boolean belongsToOpponent(int piece) {
		return currentPlayer == WHITE ^ piece > 0 && piece != 0;
	}
	
	/**Checks if the specified piece belongs to the current player.
	 * @param piece - the piece to be checked
	 * @return true if it belongs to the current player; false otherwise.
	 */
	public boolean belongsToPlayer(int piece) {
		return currentPlayer == WHITE ^ piece < 0 && piece != 0;
	}

	/**Removes any generated moves that would leave the king in check.
	 * @param piece - the piece to generate moves for
	 * @param sourceRow - the row of the piece
	 * @param sourceCol - the column of the piee
	 */
	public void getLegalMoves(int piece, int sourceRow, int sourceCol) {
		
		// Clears the ArrayLists
		legalRows.clear();
		legalCols.clear();
		
		// Gets the pseudo-legal moves for the selected piece
		if (Math.abs(piece) == PAWN)
			getMovesForPawn(sourceRow, sourceCol);
		else if (Math.abs(piece) == KNIGHT)
			getMovesForKnight(sourceRow, sourceCol);
		else if (Math.abs(piece) == BISHOP)
			getMovesForBishop(sourceRow, sourceCol);
		else if (Math.abs(piece) == ROOK)
			getMovesForRook(sourceRow, sourceCol);
		else if (Math.abs(piece) == QUEEN)
			getMovesForQueen(sourceRow, sourceCol);
		else
			getMovesForKing(sourceRow, sourceCol);
		
		// Sets up two temporary ArrayLists to transfer legal moves to
		ArrayList<Integer> tempRows = new ArrayList<Integer>();
		ArrayList<Integer> tempCols = new ArrayList<Integer>();
		
		// Removes any moves that leave the king in check
		for (int i = 0; i < legalRows.size(); i++) {
			int targetRow = legalRows.get(i);
			int targetCol = legalCols.get(i);
			
			// Stores the piece that is replaced
			int capturedPiece = board[targetRow][targetCol];
			
			// Simulates the move
			board[targetRow][targetCol] = piece;
			board[sourceRow][sourceCol] = 0;
			
			// Removes the en passant pawn if it was captured
			if (Math.abs(piece) == PAWN && targetCol == enPassantCol) {
				if (currentPlayer == WHITE && targetRow == enPassantRow + 1)
					board[enPassantRow][enPassantCol] = 0;
				else if (currentPlayer == BLACK && targetRow == enPassantRow - 1)
					board[enPassantRow][enPassantCol] = 0;
			}
			
			// Updates the king's row and column if the moved piece was the king
			if (piece == KING) {
				whiteKingRow = targetRow;
				whiteKingCol = targetCol;
			}
			else if (piece == -KING) {
				blackKingRow = targetRow;
				blackKingCol = targetCol;
			}
			
			// Checks if the king is in check in the new position
			if (currentPlayer == WHITE && !squareIsAttacked(whiteKingRow, whiteKingCol)) {
				tempRows.add(targetRow);
				tempCols.add(targetCol);
			}
			else if (currentPlayer == BLACK && !squareIsAttacked(blackKingRow, blackKingCol)) {
				tempRows.add(targetRow);
				tempCols.add(targetCol);
			}
			
			// Restores the board to its original state
			board[sourceRow][sourceCol] = piece;
			board[targetRow][targetCol] = capturedPiece;
			
			// Restores the king's row and column if the moved piece was the king
			if (piece == KING) {
				whiteKingRow = sourceRow;
				whiteKingCol = sourceCol;
			}
			else if (piece == -KING) {
				blackKingRow = sourceRow;
				blackKingCol = sourceCol;
			}
			
			// Restores the en passant pawn
			if (enPassantRow == 3)
				board[enPassantRow][enPassantCol] = PAWN;
			else if (enPassantRow == 4) {
				board[enPassantRow][enPassantCol] = -PAWN;
			}
		}
		
		// Transfers the temporary ArrayLists to the originals
		legalRows = tempRows;
		legalCols = tempCols;
	}

	/**Generates moves for pawns.
	 * @param row - the row of the pawn
	 * @param col - the column of the pawn
	 */
	public void getMovesForPawn(int row, int col) {
		// Sets the direction based on the color of the pawn
		int direction;
		if (currentPlayer == WHITE)
			direction = 1;
		else
			direction = -1;

		// Checks if the pawn can move forward
		if (board[row + direction][col] == 0) {
			legalRows.add(row + direction);
			legalCols.add(col);

			// Checks if the pawn can move forward two squares
			if (row == 3.5 - 2.5 * direction && board[row + 2 * direction][col] == 0) {
				legalRows.add(row + 2 * direction);
				legalCols.add(col);
			}
		}

		// Checks if the pawn can capture to the left
		if (col > 0 && belongsToOpponent(board[row + direction][col - 1])) {
			legalRows.add(row + direction);
			legalCols.add(col - 1);
		}

		// Checks if the pawn can capture to the right
		if (col < 7 && belongsToOpponent(board[row + direction][col + 1])) {
			legalRows.add(row + direction);
			legalCols.add(col + 1);
		}
		
		// Checks if the pawn can perform an en passant capture to the left
		if (col > 0 && enPassantRow == row && enPassantCol == col - 1) {
			legalRows.add(row + direction);
			legalCols.add(col - 1);
		}
		
		// Checks if the pawn can perform an en passant capture to the right
		if (col < 7 && enPassantRow == row && enPassantCol == col + 1) {
			legalRows.add(row + direction);
			legalCols.add(col + 1);
		}
	}

	/**Generates moves for knights.
	 * @param row - the row of the knight
	 * @param col - the column of the knight
	 */
	public void getMovesForKnight(int row, int col) {
		int[][] jumps = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};

		for (int[] jump: jumps) {
			int r = row + jump[0];
			int c = col + jump[1];
			if (squareIsValid(r, c) && (board[r][c] == 0 || belongsToOpponent(board[r][c]))) {
				legalRows.add(r);
				legalCols.add(c);
			}
		}
	}

	/**Generates moves for bishops.
	 * @param row - the row of the bishop
	 * @param col - the column of the bishop
	 */
	public void getMovesForBishop(int row, int col) {
		int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};

		for (int[] direction: directions) {
			int rowOffset = direction[0];
			int colOffset = direction[1];

			int r = row + rowOffset;
			int c = col + colOffset;
			
			while (squareIsValid(r, c)) {
				int piece = board[r][c];
				if (piece == 0 || belongsToOpponent(piece)) {
					legalRows.add(r);
					legalCols.add(c);
				}
				if (piece != 0)
					break;
				
				r += rowOffset;
				c += colOffset;
			}
		}
	}

	/**Generates moves for rooks.
	 * @param row - the row of the rook
	 * @param col - the column of the rook
	 */
	public void getMovesForRook(int row, int col) {
		int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

		for (int[] direction: directions) {
			int rowOffset = direction[0];
			int colOffset = direction[1];

			int r = row + rowOffset;
			int c = col + colOffset;
			
			while (squareIsValid(r, c)) {
				int piece = board[r][c];
				if (piece == 0 || belongsToOpponent(piece)) {
					legalRows.add(r);
					legalCols.add(c);
				}
				if (piece != 0)
					break;
				
				r += rowOffset;
				c += colOffset;
			}
		}
	}

	/**Generates moves for queens.
	 * @param row - the row of the queen
	 * @param col - the column of the queen
	 */
	public void getMovesForQueen(int row, int col) {
		getMovesForBishop(row, col);
		getMovesForRook(row, col);
	}

	/**Generates moves for kings.
	 * @param row - the row of the king
	 * @param col - the column of the king
	 */
	public void getMovesForKing(int row, int col) {
		int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};

		for (int[] direction: directions) {
			int r = row + direction[0];
			int c = col + direction[1];
			if (squareIsValid(r, c) && (board[r][c] == 0 || belongsToOpponent(board[r][c]))) {
				legalRows.add(r);
				legalCols.add(c);
			}
		}
		
		// Checks if the king can castle
		
		// Sets the castling rights to that of the current player
		boolean canCastleKingside, canCastleQueenside;
		if (currentPlayer == WHITE) {
			canCastleKingside = whiteCanCastleKingside;
			canCastleQueenside = whiteCanCastleQueenside;
		}
		else {
			canCastleKingside = blackCanCastleKingside;
			canCastleQueenside = blackCanCastleQueenside;
		}
		
		// Checks if the king is in check
		if (squareIsAttacked(row, col)) {
			canCastleKingside = false;
			canCastleQueenside = false;
		}
		
		// Checks kingside
		if (canCastleKingside)
			for (int c = 5; c < 7; c++)
				if (board[row][c] != 0 || squareIsAttacked(row, c)) {
					canCastleKingside = false;
					break;
				}
		
		// Checks queenside
		if (canCastleQueenside) {
			for (int c = 3; c > 1; c--)
				if (board[row][c] != 0 || squareIsAttacked(row, c)) {
					canCastleQueenside = false;
					break;
				}
			if (board[row][1] != 0)
				canCastleQueenside = false;
		}
		
		
		// Updates the legal moves
		if (canCastleKingside) {
			legalRows.add(row);
			legalCols.add(col + 2);
		}
		if (canCastleQueenside) {
			legalRows.add(row);
			legalCols.add(col - 2);
		}
	}

	/**Checks if a square is attacked by an opposing piece.
	 * @param row - the row of the square
	 * @param col - the column of the square
	 * @return true if the square is attacked; false otherwise.
	 */
	public boolean squareIsAttacked(int row, int col) {
		int[][] directions = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};

		// Searches diagonally for bishops and queens
		for (int i = 0; i < 4; i++) {
			int rowOffset = directions[i][0];
			int colOffset = directions[i][1];

			int r = row + rowOffset;
			int c = col + colOffset;

			while (squareIsValid(r, c)) {
				int piece = board[r][c];
				if ((Math.abs(piece) == BISHOP || Math.abs(piece) == QUEEN) && belongsToOpponent(piece))
					return true;
				if (piece != 0)
					break;
				r += rowOffset;
				c += colOffset;
			}
		}

		// Searches vertically and horizontally for rooks and queens
		for (int i = 4; i < 8; i++) {
			int rowOffset = directions[i][0];
			int colOffset = directions[i][1];

			int r = row + rowOffset;
			int c = col + colOffset;

			while (squareIsValid(r, c)) {
				int piece = board[r][c];
				if ((Math.abs(piece) == ROOK || Math.abs(piece) == QUEEN) && belongsToOpponent(piece))
					return true;
				if (piece != 0)
					break;
				r += rowOffset;
				c += colOffset;
			}
		}

		// Searches for the opponent's king
		for (int[] direction: directions) {
			int r = row + direction[0];
			int c = col + direction[1];
			if (squareIsValid(r, c)) {
				int piece = board[r][c];
				if (Math.abs(piece) == KING && belongsToOpponent(piece))
					return true;
			}
		}
		// Searches for knights
		int[][] jumps = {{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}};

		for (int[] jump: jumps) {
			int r = row + jump[0];
			int c = col + jump[1];
			if (squareIsValid(r, c)) {
				int piece = board[r][c];
				if (Math.abs(piece) == KNIGHT && belongsToOpponent(piece))
					return true;
			}
		}

		// Searches for pawns
		int r;
		if (currentPlayer == WHITE)
			r = row + 1;
		else
			r = row - 1;
		int c = col - 1;
		while (c <= col + 1) {
			if (squareIsValid(r, c)) {
				int piece = board[r][c];
				if (Math.abs(piece) == PAWN && belongsToOpponent(piece))
					return true;
			}
			c += 2;
		}

		return false;
	}
	
	/**Ends the game and displays the menu options.
	 * 
	 */
	public void endGame() {
		gameOver = true;
		newGameButton.setVisible(true);
		rulesButton.setVisible(true);
		clocksButton.setVisible(true);
		themesButton.setVisible(true);
		exitButton.setVisible(true);
		
		resignButton.setVisible(false);
		drawButton.setVisible(false);
		
		timer.stop();
	}
	
	/**Checks after every turn to see if there is a winner.
	 * 
	 */
	public void gameIsOver() {
		
		// Checks if the player has any legal moves
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				int piece = board[i][j];
				if (belongsToPlayer(piece)) {
					getLegalMoves(piece, i, j);
					if (legalRows.size() != 0)
						return;
				}
			}
		
		// Checks if the player's king is in check
		boolean kingIsInCheck;
		if (currentPlayer == WHITE)
			kingIsInCheck = squareIsAttacked(whiteKingRow, whiteKingCol);
		else
			kingIsInCheck = squareIsAttacked(blackKingRow, blackKingCol);

		if (kingIsInCheck && currentPlayer == WHITE)
			JOptionPane.showMessageDialog(this, "Black Wins!", "Checkmate!", JOptionPane.WARNING_MESSAGE);
		else if (kingIsInCheck && currentPlayer == BLACK)
			JOptionPane.showMessageDialog(this, "White Wins!", "Checkmate!", JOptionPane.WARNING_MESSAGE);
		else
			JOptionPane.showMessageDialog(this, "It's a Draw!", "Stalemate!", JOptionPane.WARNING_MESSAGE);
		
		endGame();
	}

	public static void main(String[] args) {

		frame = new JFrame("Chess");
		monkey panel = new monkey();
		
		frame.setPreferredSize(new Dimension(1920,1080));

		
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

	}

}