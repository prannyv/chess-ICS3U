import java.awt.Image;
import java.awt.Toolkit;

public class Piece {

	Piece piece;
	static int type;
	//1 == pawn, 2 == knight, 3 == bishop, 4 == rook, 5 == queen, 6 == king
	int positionY;
	int positionX;
	public static Image image;
	int color;
	
	
	public void getImg () {
		String fileName;
		if (color == 1)
			fileName = "White";
		else
			fileName = "Black";

		String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"}; 
		fileName += pieceTypes[Piece.type ] + ".png";
		Piece.image = Toolkit.getDefaultToolkit().getImage(fileName);


	}
	//	String filename;
	//    if (Piece.color == WHITE)
	//        fileName = "White";
	//    else
	//        fileName = "Black";
	//    String[] pieceTypes = {"Pawn", "Knight", "Bishop", "Rook", "Queen", "King"};

	
	public Piece getPiece() {
		return piece;
	}

	public Piece (int colour, int what, int y, int x)	{
		colour = color;
		what = type;
		y = positionY;
		x = positionX;
	}
}





