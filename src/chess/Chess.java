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
	 * white_moves is a flag that is true if it is white's turn, false if it is black's turn
	 */
	static boolean done = false;
	static boolean white_moves = true;
	
	/**
	 * The main method is the one that will be interacting with the user. It displays the chess board and reads input from the user.
	 * @author Jake
	 */
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		
		String input;
			boolean valid_input = false;
		String loc;
		String move_to = "";
			char file;
			int rank;
		String third = "";
		boolean asked_for_draw = false;
		
		Piece piece = null;
		
		
		initialize();
		while(!done) {
			display();
			
			while(!valid_input) {
				
				
				/*Code that reads input*/
				if(white_moves) {
					System.out.print("\nWhite's move: ");
				}
				else {
					System.out.print("\nBlack's move: ");
				}
				
				input = in.nextLine().toLowerCase();
				
				if(input.equals("resign")) {
					if(white_moves) {
						System.out.println("\nBlack wins");
					}
					else {
						System.out.println("\nWhite wins");
					}
					in.close();
					return;
				}
				if(asked_for_draw && input.equals("draw")) {
					System.out.println("\ndraw");
					in.close();
					return;
				}
				else {
					asked_for_draw = false;
				}
				
				//loc - IS THE STARTING POSITION. 
				//move_to - IS THE LOCATION THAT THE PIECE IS TO MOVE TO. 
				//draw - IS EMPTY NORMALLY, UNLESS A PLAYER HAS ASKED FOR "draw?"
				String[] inputArray = input.split(" ", 3);
				if(inputArray.length < 2) {
					System.out.println("\nInvalid input line. Try again.");
					continue;
				}
				loc = inputArray[0];
				move_to = inputArray[1];
				if(inputArray.length > 2) {
					third = inputArray[2];
					if(third.equals("draw?")) {
						asked_for_draw = true;
					}
				}
				
				//This is just for testing, will remove later
				System.out.println("loc: " + loc + ". move_to: " + move_to + ". third: " + third);
				
				/*Part 1 code - Deals with proper starting position */
				file = loc.charAt(0);
				rank = Character.getNumericValue(loc.charAt(1));
				if(file < 'a' || file > 'h'
				|| rank < 1 || rank > 8
						) {
					System.out.println("\nInvalid file and/or rank for starting position.");
					asked_for_draw = false;
					continue;
				}
				if(board[rank-1][fileToNum(file)] == null) {
					System.out.println("\nNo piece at indicated starting position.");
					asked_for_draw = false;
					continue;
				}
				else {
					piece = board[rank-1][fileToNum(file)];
				}
				/*End of Part 1*/
				
				/*Part 2 code - Deals with proper move to position*/
				file = move_to.charAt(0);
				rank = Character.getNumericValue(move_to.charAt(1));
				if(file < 'a' || file > 'h'
				|| rank < 1 || rank > 8
						) {
					System.out.println("\nInvalid file and/or rank for move to position.");
					asked_for_draw = false;
					continue;
				}
				try {
					piece.move(move_to);
				} catch (IllegalArgumentException e) {
					System.out.println("\nIllegal move, try again");
					asked_for_draw = false;
					continue;
				}
				/*End of part 2*/
				
				//If nothing went wrong, we continue
				valid_input = true;
				System.out.println();
			}
			
			 //THE PIECE AT THE INPUTED POSITION IN PART 1
			
			//We leave part 2 to the piece to tell the user if it is valid or not, we should try to make the piece move and catch an exception the Piece class will throw if the move is not valid
			//For Part 3, if a user does ask for draw, check to see if the next input is draw, if it is, end the game as a tie, if not, continue as normal
			
			
			//Reset these variables at the end of the turn
			valid_input = false;
			white_moves = !white_moves;
			
		}
		
		in.close();
		return;
	}
	
	/**
	 * initialize() is called once. It initializes the empty 2d array of Pieces so that they are filled like a proper chess board with the white pieces on the bottom.
	 * @author Jake
	 */
	public static void initialize() {
		//Initializing white pieces
		board[0][0] = new Rook("a1");
		board[0][1] = new Knight("a2");
		board[0][2] = new Bishop("a3");
		board[0][3] = new Queen("a4");
		board[0][4] = new King("a5");
		board[0][5] = new Bishop("a6");
		board[0][6] = new Knight("a7");
		board[0][7] = new Rook("a8");
		for(int j = 0; j < 8; j++) {
			Piece piece = board[0][j];
			piece.name = "w" + piece.name;
		}
		for(int j = 0; j < 8; j++) {
			board[1][j] = new White_Pawn(numToFile(j) + "2");
		}
		
		//Initializing black pieces
		board[7][0] = new Rook("a1");
		board[7][1] = new Knight("a2");
		board[7][2] = new Bishop("a3");
		board[7][3] = new Queen("a4");
		board[7][4] = new King("a5");
		board[7][5] = new Bishop("a6");
		board[7][6] = new Knight("a7");
		board[7][7] = new Rook("a8");
		for(int j = 0; j < 8; j++) {
			Piece piece = board[7][j];
			piece.name = "b" + piece.name;
		}
		for(int j = 0; j < 8; j++) {
			board[6][j] = new Black_Pawn(numToFile(j) + "7");
		}
	}
	/**
	 * display() views the board 2D array and shows all the pieces that are in the board. Any position that is NULL instead of containing a piece will display blank or as ##.
	 * This method will also print out the file numbers and the rank letters
	 * @author Jake
	 */
	public static void display() {
		Piece piece;
		for(int i = 7; i >= 0; i--) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j] == null) {
					if((i+j) % 2 == 1) {
						System.out.print("   ");
					}
					else {
						System.out.print("## ");
					}
				}
				else {
					piece = board[i][j];
					System.out.print(piece.name+ " ");
				}
			}
			System.out.println(i+1);
		}
		for(int i = 0; i < 8; i++) {
			System.out.print(" " + numToFile(i) + " ");
		}
		System.out.println();
	}
	
	/**
	 * This helper method takes in the character for the file from the input and returns a number 0-7. Useful when referencing a position in the board 2D array
	 * @param file - a character that is denotes the file in the input
	 * @return int value between 0-7 associated with file character if successful, -1 if failed
	 * @author Jake
	 */
	public static int fileToNum(char file) {
		switch (file) {
			case 'a': return 0;
			case 'b': return 1;
			case 'c': return 2;
			case 'd': return 3;
			case 'e': return 4;
			case 'f': return 5;
			case 'g': return 6;
			case 'h': return 7;
			default: return -1;
		}
		
	}
	
	/**
	 * This helper method takes an int and returns a String of one character for the file associated. Useful when referencing a position in the board 2D array
	 * @param file - an int value associated with a column of the board 2D array
	 * @return String value of the file name a-h. Throws an illegal argument exception if input is not valid
	 * @author Jake
	 */
	public static String numToFile(int file) throws IllegalArgumentException{
		switch (file) {
			case 0: return "a";
			case 1: return "b";
			case 2: return "c";
			case 3: return "d";
			case 4: return "e";
			case 5: return "f";
			case 6: return "g";
			case 7: return "h";
			default: throw new IllegalArgumentException("Invalid File Num to convert to Letter");
		}
		
	}
	
	/**
	 * This is the abstract class that all pieces will extend. Each piece must store a String of its name, and a string of its pos (2 character String that contains the Pieces file and rank) and implement the move method.
	 * @param String input - the input string from the user. Each piece will check the input itself to see if valid for that piece
	 * @author Jake
	 *
	 */
	public static abstract class Piece {
		String name;
		String pos;
		abstract void move(String move_to) throws IllegalArgumentException;
	}
	
	/**
	 * This section contains all the pieces. White and Black Pawns are separate pieces since they move differently, but all other pieces move the same regardless of which side
	 * All pieces take an input string of the position they are to move to.
	 * All pieces must be created with a starting position inputed as a string with FileRank
	 * Pawns have a separate method for promotion that occurs when the reached the opposite end of the board
	 * @author Jake
	 *
	 */
	public static class White_Pawn extends Piece{
		public White_Pawn(String start) {
			this.name = "wp";
			this.pos = start;
		}
		void move(String move_to)  throws IllegalArgumentException{
			//WHEN MOVING MAKE SURE TO TEST IF THE MOVE IS VALID, IF IT IS, SET THE STARTING POSITION TO NULL IN THE board[][] AND CHANGE THIS OBJ'S POS AS WELL AS ADD ITS NEW POSITION TO board[][]
			throw new IllegalArgumentException();
			//return;
		}
		void promote() {
			return;
		}
	}
	
	public static class Black_Pawn extends Piece {
		public Black_Pawn(String start) {
			this.name = "bp";
			this.pos = start;
		}
		void move(String move_to)  throws IllegalArgumentException{
			//WHEN MOVING MAKE SURE TO TEST IF THE MOVE IS VALID, IF IT IS, SET THE STARTING POSITION TO NULL IN THE board[][] AND CHANGE THIS OBJ'S POS AS WELL AS ADD ITS NEW POSITION TO board[][]
			return;
		}
		void promote() {
			
		}
	}
	
	public static class Rook extends Piece {
		public Rook(String start) {
			this.name = "R";
			this.pos = start;
		}
		void move(String move_to)  throws IllegalArgumentException{
			return;
		}
	}
	
	public static class Knight extends Piece {
		public Knight(String start) {
			this.name = "N";
			this.pos = start;
		}
		void move(String move_to)  throws IllegalArgumentException{
			return;
		}
	}
	
	public static class Bishop extends Piece {
		public Bishop(String start) {
			this.name = "B";
			this.pos = start;
		}
		void move(String move_to)  throws IllegalArgumentException{
			return;
		}
	}
	
	public static class Queen extends Piece {
		public Queen(String start) {
			this.name = "Q";
			this.pos = start;
		}
		void move(String move_to)  throws IllegalArgumentException{
			return;
		}
	}
	
	public static class King extends Piece {
		public King(String start) {
			this.name = "K";
			this.pos = start;
		}
		void move(String move_to)  throws IllegalArgumentException{
			return;
		}
	}
	
	
}