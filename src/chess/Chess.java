package chess;

import java.util.Scanner;

/**
 * This will be Jake Zhou and Thomas Heck's Chess Project
 * 
 * @author Jake Zhou, Thomas Heck
 * @since 2019-03-01
 *
 */

public class Chess {
	/**
	 * This is the data structure that we will be using. It is a 2d Array of Pieces. Blank spots are by default NULL.
	 */
	static Piece[][] board = new Piece[8][8];
	/**
	 * done is a flag that tracks to see if the game is over.
	 */
	static boolean done = false;
	
	/**
	 * The main method is the one that will be interacting with the user. It displays the chess board and reads input from the user.
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String input;
		String loc;
		String move_to;
		String draw;
		boolean asked_for_draw = false;
		Piece piece;
		
		initialize();
		while(!done) {
			display();
			input = in.nextLine();
			//WE WILL DELIMINATE THE STRING INTO 3 PARTS. 
			//PART 1 - loc - IS THE STARTING POSITION. 
			//PART 2 - move_to - IS THE LOCATION THAT THE PIECE IS TO MOVE TO. 
			//PART 3 - draw - IS EMPTY NORMALLY, UNLESS A PLAYER HAS ASKED FOR DRAW?
			
			//We should check part 1 here to see if there is a piece in that position
			//We leave part 2 to the piece to tell the user if it is valid or not, we should try to make the piece move and catch an exception the Piece class will throw if the move is not valid
			//For Part 3, if a user does ask for draw, check to see if the next input is draw, if it is, end the game as a tie, if not, continue as normal
			
			
			piece = board[0][0]; //THE PIECE AT THE INPUTED POSITION IN PART 1
			piece.move(move_to);
			
			
			
			done = true;
		}
		
		return;
	}
	
	/**
	 * initialize() is called once. It initializes the empty 2d array of Pieces so that they are filled like a proper chess board with the white pieces on the bottom.
	 */
	public static void initialize() {
		//We should have code here that adds the white pieces to the bottom rows at board[7-8][1-8] and black pieces to board[1-2][1-8]
	}
	/**
	 * display() views the board 2D array and shows all the pieces that are in the board. Any position that is NULL instead of containing a piece will display blank or as ##.
	 * This method will also print out the file numbers and the rank letters
	 */
	public static void display() {
		//Code here the displays the board
	}
	
	/**
	 * This is the abstract class that all pieces will extend. Each piece must store a String of its name, and a string of its pos (2 character String that contains the Pieces file and rank) and implement the move method.
	 * @param String input - the input string from the user. Each piece will check the input itself to see if valid for that piece
	 * @author Jake
	 *
	 */
	public abstract class Piece {
		String name;
		String pos;
		abstract void move(String move_to);
	}
	
	/**
	 * This section contains all the pieces. White and Black Pawns are separate pieces since they move differently, but all other pieces move the same regardless of which side
	 * All pieces take an input string of the position they are to move to 
	 * @author Jake
	 *
	 */
	public class White_Pawn extends Piece {
		String name = "wp";
		String pos = "";
		
		public White_Pawn() {}
		void move(String move_to) {
			return;
		}
	}
	
}