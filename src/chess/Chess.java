package chess;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * This class is a console based chess program. All input and output is handled through the console with System.in and System.out. Upon Run, this program initializes the chess board and runs turn by turn until a win or draw condition is met such as resignation, checkmate, stalemate, or proposed draw.
 * 
 * <p>Input is taken in the form of FileRank FileRank where the first FileRank is the starting position and the second FileRank is the position intended move-to position. The program ensures that all moves are valid before execution. There is an option third input after the FileRank FileRank that either takes the input "draw?" where the game would draw after the valid move, or a letter to indicate a promotion type when pawns are moved to the opposite final rank.
 * 
 * <p>This class uses polymorphism to refer to each piece collectively and use their shared methods. All pieces are extensions of the abstract class: Piece
 * 
 * @author Jake Zhou
 * @author Thomas Heck
 *
 */

public class Chess {
	
	/**
	 * Board is the data structure that we will be using. It is a 9x8 2D Array of Pieces.
	 * row[0] is ignored so that we do not need to convert the input ranks that range from 1-8 to 0-7 to minimize confusion while coding.
	 * Blank spots are by default NULL while occupied positions are pointers to concrete implementations of the abstract Piece class.
	 * 
	 * @author Jake
	 * 
	 */
	public static Piece[][] board = new Piece[9][8];
	
	/**
	 * white_moves is a flag that is true if it is white's turn, false if it is black's turn. white_moves always starts as true and is reversed after a valid move has been committed.
	 * white_enpassant is usually 0 unless white had just taken a pawn double step as their last move, in which case the variable stores the file that pawn the pawn moved in
	 * black_enpassant is the same for black
	 * 
	 * @author Jake
	 * 
	 */
	public static boolean white_moves = true;
	public static char white_enpassant = 0;
	public static char black_enpassant = 0;
	
	/**
	 * in is the {@link Scanner} we use to get input from the user. It is global so that methods like stalemate and checkmate may close it before exiting
	 * 
	 * @author Jake
	 * 
	 */
	public static Scanner in = new Scanner(System.in);
	
	/**
	 * Upon Run, the main method initializes calls the initialize method to place all pieces in their starting positions. It runs in an infinite loop and only exists if one of the players resign or asked for draw with a valid move. The method reads input and separates it into 3 parts delimited by spaces. 
	 * 
	 * <p>The first part of the input is the location of the piece to move. If there is no piece in this position of it is is out of the bounds of the board, the user will be told the input is invalid and will be asked for another input until the input is valid
	 * 
	 * <p>The second part of the input is the location for the piece in the first part to move to. If this location is out of the bounds of the board, the user will be prompted for a valid move. Each piece knows its own valid moves and if the position in part 2 is not valid for the piece, the piece will throw an IllegalArgumentException which main catches. This exception causes the user to prompted for a valid move and the illegal move will not be committed.
	 * 
	 * <p>The third and final part of the input is optional. If it is gibberish, it will be ignored. If it is resign, the game terminates as the side whose turn it is has resigned. If the input is instead "draw?" the move will be indicated by the first two parts will be executed if a valid move and then the game ends in a draw. If the move proposed along with the draw is NOT valid, the draw will not be executed and the "draw?" call will be forgotten. If the move proposed with the draw results in another end condition like stalemate or checkmate, those conditions take precedence before the draw.
	 * 
	 * <p>Upon the completion and commitment of every valid move, main reverses white_moves to change the side that is playing. It displays the board after the last move has been committed, and it checks for stalemate before taking an input.
	 * 
	 * @author Jake
	 */
	public static void main(String[] args) {
		
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
		
		while(true) {
			display(board);
			
			//Checking for stalemate
			stalemate();
			
			while(!valid_input) {
				
				//Resets the enpassant variables since enpassant is valid only immediately after the opponent has moved two steps with a pawn. If it is white's turn, it means that black no longer has the option to enpassant on the file that white has moved their pawn. white_enpassant therefore no longer stores a value. The same is true if it is black's turn, during which white no longer has the option to enpassant on the file that black has moved their pawn.
				if(white_moves) {
					white_enpassant = 0;
					System.out.print("\nWhite's move: ");
				}
				else {
					black_enpassant = 0;
					System.out.print("\nBlack's move: ");
				}
				
				//Code that reads input. All input converted to lowercase
				input = in.nextLine().toLowerCase();
				
				//Checking for resignation
				if(input.equals("resign")) {
					if(white_moves) {
						System.out.println("\nBlack wins");
					}
					else {
						System.out.println("\nWhite wins");
					}
					in.close();
					System.exit(0);
				}
				
				//loc - IS THE STARTING POSITION. 
				//move_to - IS THE LOCATION THAT THE PIECE IS TO MOVE TO. 
				//third - IS EMPTY NORMALLY, UNLESS A PLAYER HAS ASKED FOR "draw?" or is promoting Pawn
				//The input is delimited by spaces, if there are fewer than 2 parts of the input, it is invalid
				String[] inputArray = input.split(" ", 3);
				if(inputArray.length < 2) {
					System.out.println("\nIllegal move, try again");
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
				
				/*Part 1 code - Deals with proper starting position */
				file = loc.charAt(0);
				rank = Character.getNumericValue(loc.charAt(1));
				if(file < 'a' || file > 'h'
				|| rank < 1 || rank > 8
						) {
					System.out.println("\nIllegal move, try again");
					asked_for_draw = false;
					continue;
				}
				if(board[rank][fileToNum(file)] == null) {
					System.out.println("\nIllegal move, try again");
					asked_for_draw = false;
					continue;
				}
				else {
					piece = board[rank][fileToNum(file)];
					if(piece.white_side != white_moves) {
						System.out.println("\nIllegal move, try again");
						asked_for_draw = false;
						continue;
					}
				}
				/*End of Part 1*/
				
				/*Part 2 code - Deals with proper move to position*/
				file = move_to.charAt(0);
				rank = Character.getNumericValue(move_to.charAt(1));
				if(file < 'a' || file > 'h'
				|| rank < 1 || rank > 8
						) {
					System.out.println("\nIllegal move, try again");
					asked_for_draw = false;
					continue;
				}
				try {
					piece.move(move_to);
					if(piece instanceof White_Pawn && rank == 8) {
						if(third.equals("r")) {
							((White_Pawn) piece).promote("r");
						}
						else if(third.equals("n")) {
							((White_Pawn) piece).promote("n");
						}
						else if(third.equals("b")) {
							((White_Pawn) piece).promote("b");
						}
						else {
							((White_Pawn) piece).promote("q");
						}
						piece.check(board);
					}
					if(piece instanceof Black_Pawn && rank == 1) {
						if(third.equals("r")) {
							((Black_Pawn) piece).promote("r");
						}
						else if(third.equals("n")) {
							((Black_Pawn) piece).promote("n");
						}
						else if(third.equals("b")) {
							((Black_Pawn) piece).promote("b");
						}
						else {
							((Black_Pawn) piece).promote("q");
						}
						piece.check(board);
					}
				} catch (IllegalArgumentException e) {
					//e.printStackTrace();
					System.out.println("\nIllegal move, try again");
					asked_for_draw = false;
					continue;
				}
				/*End of part 2*/
				
				//If nothing went wrong, we continue
				valid_input = true;
				System.out.println();
			}
			
			//User asked for draw and the move was valid and executed
			if(asked_for_draw) {
				System.out.println("draw");
				in.close();
				System.exit(0);
			}
			
			//Reset these variables at the end of the turn
			valid_input = false;
			white_moves = !white_moves;
			
		}
		
	}
	
	/**
	 * initialize is called at boot up. It creates instances of all pieces at their proper starting positions and initializes their fields to the proper values.
	 * 
	 * @author Jake
	 */
	public static void initialize() {
		//Initializing white pieces
		board[1][0] = new Rook('a', 1);
		board[1][1] = new Knight('b', 1);
		board[1][2] = new Bishop('c', 1);
		board[1][3] = new Queen('d', 1);
		board[1][4] = new King('e', 1);
		board[1][5] = new Bishop('f', 1);
		board[1][6] = new Knight('g', 1);
		board[1][7] = new Rook('h', 1);
		for(int j = 0; j < 8; j++) {
			Piece piece = board[1][j];
			piece.name = "w" + piece.name;
			piece.white_side = true;
		}
		for(int j = 0; j < 8; j++) {
			board[2][j] = new White_Pawn(numToFile(j), 2);
			board[2][j].white_side = true;
		}
		
		//Initializing black pieces
		board[8][0] = new Rook('a', 8);
		board[8][1] = new Knight('b', 8);
		board[8][2] = new Bishop('c', 8);
		board[8][3] = new Queen('d', 8);
		board[8][4] = new King('e', 8);
		board[8][5] = new Bishop('f', 8);
		board[8][6] = new Knight('g', 8);
		board[8][7] = new Rook('h', 8);
		for(int j = 0; j < 8; j++) {
			Piece piece = board[8][j];
			piece.name = "b" + piece.name;
			piece.white_side = false;
		}
		for(int j = 0; j < 8; j++) {
			board[7][j] = new Black_Pawn(numToFile(j), 7);
			board[7][j].white_side = false;
		}
	}
	
	/**
	 * display prints out the board given by the parameter. All pieces are represented by two character spaces to indicate their side and their type followed by an empty space. Each rank is marked on the right hand side and each file is marked on the bottom where the file letters are aligned with the piece type of all pieces in that file. Any position that does not contain a piece shows either as two empty spaces to represent a white square followed by another empty space or as two "#" characters to represent a black square followed by an empty space.
	 * 
	 * <p>In practice display is only ever passed the global board since that is the board the user is interacting with. During development, display was also passed temporary boards that were created to ensure moves were valid which where used in methods like putsOwnKingInCheck. Since it was much easier to debug logical issues when the temporary boards were shown, this method not takes an input board.
	 * 
	 * @author Jake
	 * @param board the board that display displays. In practice only every the global board
	 */
	public static void display(Piece[][] board) {
		Piece piece;
		for(int i = 8; i > 0; i--) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j] == null) {
					if((i+j) % 2 == 0) {
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
			System.out.println(i);
		}
		for(int i = 0; i < 8; i++) {
			System.out.print(" " + numToFile(i) + " ");
		}
		System.out.println();
	}
	
	/**
	 * This helper method takes in the character for the file from the input and returns a number 0-7 correlating with the file. Useful when referencing a position in the board 2D array
	 * 
	 * @author Jake
	 * @param file - a character that is denotes the file of the board ranging from a-h
	 * @return int value between 0-7 associated with file character if successful. 
	 * @throws IllegalArgumentException if the input is not a char value from a-h
	 */
	public static int fileToNum(char file) throws IllegalArgumentException{
		switch (file) {
			case 'a': return 0;
			case 'b': return 1;
			case 'c': return 2;
			case 'd': return 3;
			case 'e': return 4;
			case 'f': return 5;
			case 'g': return 6;
			case 'h': return 7;
			default: throw new IllegalArgumentException();
		}
		
	}
	
	/**
	 * This helper method takes an int and returns a char for the corresponding file. Useful when assigning file value to a piece
	 * 
	 * @author Jake
	 * @param file - an int value associated with a column of the board 2D array ranging from 0-7
	 * @return char value of the file name ranging from a-h
	 * @throws IllegalArgumentException if input is not int between 0-7
	 */
	public static char numToFile(int file) throws IllegalArgumentException{
		switch (file) {
			case 0: return 'a';
			case 1: return 'b';
			case 2: return 'c';
			case 3: return 'd';
			case 4: return 'e';
			case 5: return 'f';
			case 6: return 'g';
			case 7: return 'h';
			default: throw new IllegalArgumentException("Invalid File Num to convert to Letter");
		}
		
	}
	
	/**
	 * copyBoard creates a copy of the current global board. All moves are first made on the copied board where they are ran through the {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} method that runs through the copied board to see if any of the opposing pieces are checking the current side's King.
	 * 
	 * @author Jake
	 * @return a copy of the global board
	 */
	public static Piece[][] copyBoard() {
		Piece[][] board_copy = new Piece[9][8];
		Piece original;
		Piece copy;
		
		for(int r = 1; r < 9; r++) {
			for(int f = 0; f < 8; f++) {
				if(board[r][f] != null) {
					//Create a copy of the piece there
					original = board[r][f];
					//If the piece is a White_Pawn
					if(original.name.equals("wp")) {
						copy = new White_Pawn(original.file, original.rank);
						copy.white_side = original.white_side;
					}//If the piece is a Black_Pawn 
					else if(original.name.equals("bp")) {
						copy = new Black_Pawn(original.file, original.rank);
						copy.white_side = original.white_side;
					}//If the piece is a rook
					else if(original.name.charAt(1) == 'R') {
						copy = new Rook(original.file, original.rank);
						copy.name = original.name;
						copy.white_side = original.white_side;
						((Rook) copy).has_moved = ((Rook) original).has_moved;
					}//If the piece is a knight
					else if(original.name.charAt(1) == 'N') {
						copy = new Knight(original.file, original.rank);
						copy.name = original.name;
						copy.white_side = original.white_side;
					}//If the piece is a bishop
					else if(original.name.charAt(1) == 'B') {
						copy = new Bishop(original.file, original.rank);
						copy.name = original.name;
						copy.white_side = original.white_side;
					}//If the piece is a Queen
					else if(original.name.charAt(1) == 'Q') {
						copy = new Queen(original.file, original.rank);
						copy.name = original.name;
						copy.white_side = original.white_side;
					}//The piece is a King
					else {
						copy = new King(original.file, original.rank);
						copy.name = original.name;
						copy.white_side = original.white_side;
						((King) copy).has_moved = ((King) original).has_moved;
					}
					
					board_copy[r][f] = copy;
				}
				
				
			}
		}
		
		return board_copy;
	}
	
	/**
	 * putsOwnKingInCheck checks to see if the current side playing, indicated by the global variable {@link #white_moves white_moves}, is in check. This method is called before any move is committed since players are not allowed to place their own King in check with a move. This method is also called by every pieces' allValidMoves method that finds all of that piece instance's valid moves. These moves are first checked to ensure they do not place the piece's own King in check before they are considered valid.
	 * 
	 * @author Jake
	 * @param board_copy - a copy of the global board created by {@link #copyBoard()} after a move has been made on the board_copy
	 * @return returns true if the input board has the current side's King in chess, meaning the last move was illegal, otherwise returns false
	 */
	public static boolean putsOwnKingInCheck(Piece[][] board_copy) {
		
		//System.out.println("\nTESTING: In putsOwnKingInCheck");
		//display(board_copy);
		
		Piece temp;
		
		//Going through all the ranks
		for(int r = 1; r < 9; r++) {
			//Going through all the files
			for(int f = 0; f < 8; f++) {
				//If there is a piece in this spot
				if(board_copy[r][f] != null) {
					temp = board_copy[r][f];
					
					//System.out.println("\nTESITNG:\nName: " + temp.name + "\nFile: " + String.valueOf(temp.file) + "\nRank: " + temp.rank); 
					
					//If the piece is not the current side playing
					if(temp.white_side != white_moves) {
						
						//System.out.println("\nTESITNG: temp's white_side = : " + temp.white_side); 
						
						if(temp.check(board_copy)) {
							//if the move in the input board has current side's King in check
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * checkForCheck runs through the entire board and finds all pieces of the side playing and sees if they are currently checking the opponent's King. If so, calls checkmate, otherwise, we continue normally
	 * 
	 * @author Jake
	 * @param board the board that is being checked for checks on the opponent King. Can be the global board or a temporary board used for {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} for instance
	 */
	public static void checkForCheck(Piece[][] board) {
		
		Piece temp;
		
		//Going through all the ranks
		for(int r = 1; r < 9; r++) {
			//Going through all the files
			for(int f = 0; f < 8; f++) {
				//If there is a piece in this spot
				if(board[r][f] != null) {
					temp = board[r][f];
					
					//System.out.println("\nTESITNG:\nName: " + temp.name + "\nFile: " + String.valueOf(temp.file) + "\nRank: " + temp.rank); 
					
					//If the piece is on the current side playing
					if(temp.white_side == white_moves) {
						
						//System.out.println("\nTESITNG: temp's white_side = : " + temp.white_side); 
						
						if(temp.check(board)) {
							checkmate();
							return;
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * checkmate checks to see if the opposing king, whose white_side variable should be the opposite value of the global {@link #white_moves} variable, is in checkmate or just a check. This method is ran by each piece's check function upon finding a check on the opponent side's King
	 * 
	 * <p>This method goes through the global board and finds all pieces on the opposite side of the current side playing and checks for any valid moves that puts their King out of check
	 * 
	 * <p>If a valid move is found, the method prints Check and returns. If no valid move is found, the method prints Checkmate, indicates the winning side, and exists the program.
	 * 
	 * @author Jake
	 */
	public static void checkmate() {
		
		Piece temp;
		ArrayList<String> tempMoves;
		Piece[][] board_copy;
		
		//Going through all the ranks
		for(int r = 1; r < 9; r++) {
			//Going through all the files
			for(int f = 0; f < 8; f++) {
				//Checking to see if there is a piece in this spot
				if(board[r][f] != null) {
					temp = board[r][f];
					//If the piece is on the opponent side
					if(temp.white_side != white_moves) {
						//System.out.println("\nTESITNG - in checkmate:\nName: " + temp.name + "\nFile: " + String.valueOf(temp.file) + "\nRank: " + temp.rank); 
						
						//Grab their valid moves
						tempMoves = temp.allValidMoves();
						//System.out.println("TESTING - in checkmate: tempMoves.size(): " + tempMoves.size());
						
						//Go through all their valid moves
						for(int i = 0; i < tempMoves.size(); i++) {
							
							//System.out.println("TESTING - in checkmate: i: " + i + " tempMoves.get(i): " + tempMoves.get(i));
							
							int move_file = fileToNum(tempMoves.get(i).charAt(0));
							int move_rank = Character.getNumericValue(tempMoves.get(i).charAt(1));
							
							board_copy = copyBoard();
							//moves them there on the board copy
							board_copy[move_rank][move_file] = board_copy[temp.rank][fileToNum(temp.file)];
							board_copy[temp.rank][fileToNum(temp.file)] = null;
							board_copy[move_rank][move_file].rank = move_rank;
							board_copy[move_rank][move_file].file = numToFile(move_file);
							//Test to see if the opponent King is still in check after this move
							//Have to change the current side since putsOwnKingInCheck only checks if the current side's King is in check
							white_moves = !white_moves;
							if(!putsOwnKingInCheck(board_copy)) {
								white_moves = !white_moves;
								//If there is no longer a check after this move
								//This is only a check, not a checkmate
								System.out.println("\nCheck");
								return;
							}
							else {
								white_moves = !white_moves;
							}
							
						}
					}
				}
			}
		}
		
		//Went through all the possible pieces and there is no instance where a valid move brings the opponent King out of check
		display(board);
		System.out.println("\nCheckmate");
		if(white_moves) {
			System.out.print("\nBlack wins");
		}
		else {
			System.out.print("\nWhite wins");
		}
		in.close();
		System.exit(0);
		
	}
	
	/**
	 * Stalemate goes through the entire global board and finds pieces of the side that is currently playing, which are the pieces whose white_side variables are equal to the global {@link #white_moves} variable, and tests to see if those pieces have a valid move. Upon the first piece that has a valid move, the method is returned out of. If the entire board has been iterated through and no pieces of the playing side has a valid move, there is a stalemate and the proper messages a printed and the program exited. This method is called at the beginning of every turn.
	 * 
	 * @author Jake
	 */
	public static void stalemate() {
		
		//System.out.println("TESTING: IN STALEMATE");
		
		Piece temp;
		ArrayList<String> tempMoves;
		
		//Going through all the ranks
		for(int r = 1; r < 9; r++) {
			//Going through all the files
			for(int f = 0; f < 8; f++) {
				//If there is a piece in this spot
				if(board[r][f] != null) {
					temp = board[r][f];
					//If the piece is on the current side
					if(temp.white_side == white_moves) {
						
						//System.out.println("\nTESITNG: Stalemate\nName: " + temp.name + "\nFile: " + String.valueOf(temp.file) + "\nRank: " + temp.rank); 
						
						//Get all its valid moves
						tempMoves = temp.allValidMoves();
						
						//System.out.println("TESTING: Stalemate - tempMoves.size(): " + tempMoves.size());
						
						//If there is a valid move
						if(tempMoves.size() != 0) {
							//There is no stalemate
							return;
						}
					}
				}
			}
		}
		//Went through the entire board and no pieces on this side has a valid move
		display(board);
		System.out.println("\nStalemate");
		System.out.println("\ndraw");
		in.close();
		System.exit(0);
		
	}
	
	/**
	 * Piece is the abstract class that all pieces will extend and exists so that each piece can be controlled polymorphically, e.g., the main method can say piece.move(), the {@link #putsOwnKingInCheck(Piece[][] board) putsOwnKingInCheck} method can say piece.check, etc. 
	 * 
	 * <p>Each piece must store a String of its name, a char of its file (a-h), an int of its rank (1-8), and a boolean for its side: white_side == true for white or false for black. 
	 * 
	 * <p>Each piece must also implement a move method, which throws an IllegalArgumentException if the position it is to move to indicated by the parameter is not valid, a check method that checks all the positions the piece can move to to see if it is placing the opponent's King in check, and an allValidMoves method that returns that piece's valid moves
	 * 
	 * @author Jake
	 *
	 */
	public static abstract class Piece {
		String name;
		char file;
		int rank;
		public boolean white_side;
		
		/**
		 * Move takes an input string of 2 characters composing of a file and a rank of the destination. Move always runs {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} before committing the move. Move also checks to see if the move has placed the opponent's king in check with {@link #checkForCheck(Piece[][]) checkForCheck} method. If a move were valid and to be committed, the Piece's position on the global board is changed and its file and rank fields are updated. More specific descriptions of move exists for each class that extends Piece.
		 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public abstract void move(String move_to) throws IllegalArgumentException;
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public abstract boolean check(Piece[][] board);
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public abstract ArrayList<String> allValidMoves();
	}
	
	/**
	 * White_Pawn and Black_Pawns are the only cases where there are separate classes for the same type of piece since pawns can only advance while other pieces may move in any direction. Pawns also are unique in that they have an additional method called {@link #promote(String) Promote} called when they reach the opposing end's final rank
	 * 
	 * See {@link chess.Chess.Black_Pawn}
	 * 
	 * @author Jake
	 */
	public static class White_Pawn extends Piece{
		
		/**
		 * Constructor initializes the piece's name as "wp", its file as the input file, its rank as the input rank, and its white_side as true
		 * 
		 * @author Jake
		 * @param file - the file where the piece was created
		 * @param rank - the rank where the piece was created
		 */
		public White_Pawn(char file, int rank) {
			this.name = "wp";
			this.file = file;
			this.rank = rank;
			this.white_side = true;
		}
		
		/**
		 * White_Pawns may move up towards ranks of greater value. They can move up one in their file if the path is clear, up two in their file if the path is clear and they are still in their starting position, up-left or up-right if there is an opponent piece there or if they are performing an En Passant. When they reach the opposite side's final rank, they are {@link #promote(String) promoted}. All moves are ensured not to place the piece's own King in check by {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} method before being committed. If a move is valid, this piece's position is changed in the global board and its own file and rank fields are updated
		 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public void move(String move_to) throws IllegalArgumentException{
			
			/*
			if(!board[this.rank][fileToNum(this.file)].equals(this)) {
				System.out.println("we have not tracked this file and rank properly.");
				System.out.println("File: " + this.file);
				System.out.println("Rank: " + this.rank);
				in.close();
				System.exit(0);
			}*/
			
			//If you are trying to move a white pawn on a black turn
			if(!white_moves) {
				throw new IllegalArgumentException();
			}
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Character.getNumericValue(move_to.charAt(1));
			
			Piece[][] board_copy = copyBoard();
			
			if(move_file != this.file) {
				//System.out.println("Pawn moving to different file");		
				//If the designated move is not in this file, then we have to see if it is an attempt to capture
				if(move_file == (this.file + 1) && move_rank == (this.rank + 1)) {
					//System.out.println("Pawn moving up-right");
					//Checking to see there is a piece there
					if(board[this.rank + 1][fileToNum(this.file) + 1] == null) {
						//Checking for Enpassant
						if(move_file == black_enpassant && move_rank == 6) {
							
							//Removing the black pawn
							board_copy[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
							board_copy[move_rank][fileToNum(move_file)].file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								
								throw new IllegalArgumentException();
							}
							
							//Actual move
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board[this.rank + 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							checkForCheck(board);
							return;
						}
						else {
							throw new IllegalArgumentException();
						}
					}
					//Making sure the piece isn't on the same side
					if(board[this.rank + 1][fileToNum(this.file) + 1].white_side == true) {
						throw new IllegalArgumentException();
					}
					
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank + 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} else if (move_file == (this.file - 1) && move_rank == (this.rank + 1)) {
					//System.out.println("Pawn moving up-left");
					//Checking to see there is a piece there
					if(board[this.rank + 1][fileToNum(this.file) - 1] == null) {
						//Checking for Enpassant
						if(move_file == black_enpassant && move_rank == 6) {
							
							board_copy[this.rank][fileToNum(this.file) - 1] = null;
							
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
							board_copy[move_rank][fileToNum(move_file)].file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) - 1] = null;
							//Moving
							board[this.rank + 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							checkForCheck(board);
							return;
						}
						else {
							throw new IllegalArgumentException();
						}
					}
					//Making sure the piece isn't on the same side
					if(board[this.rank + 1][fileToNum(this.file) - 1].white_side == true) {
						throw new IllegalArgumentException();
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank + 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} else {
					//System.out.println("Something wrong");
					throw new IllegalArgumentException();
				}
			} else {
				
				//System.out.println("TESTING: We should be in here");
				
				if(move_rank == this.rank + 1) {
					//Checking to see if path clear
					if(board[this.rank + 1][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving piece
					board[this.rank + 1][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} else if(move_rank == this.rank + 2 && this.rank == 2) {
					
					//System.out.println("TESTING: WE ARE MOVING TWO SPACES");
					
					//Checking to see if path clear
					if(board[this.rank + 1][fileToNum(this.file)] != null 
					|| board[this.rank + 2][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//System.out.println("TESTING ARE WE GETTTING HERE?");
					
					//Moving piece
					board[this.rank + 2][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					white_enpassant = this.file;
					checkForCheck(board);
					
					//System.out.println("TESTING: ABOUT TO FINISH MOVING");
					
					return;
				} else {
					throw new IllegalArgumentException();
				}
			}

		}
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board - the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public boolean check(Piece[][] board) {
			
			Piece temp;
			
			//If the pawn is in the last rank, it should have promoted
			if(this.rank == 8) {
				throw new IllegalArgumentException();
			}
			//a pawns only check one side
			if(this.file == 'a') {
				//checking up 1 right 1
				temp = board[this.rank + 1][fileToNum((char) (this.file + 1))];
				if(temp != null) {
					//checking if black King
					if(temp.name.equals("bK")) {
						//checkmate(temp.file, temp.rank);
						return true;
					}
				}
			} //h pawns only check one side
			else if(this.file == 'h') {
				//checking up 1 left 1
				temp = board[this.rank + 1][fileToNum((char) (this.file - 1))];
				if(temp != null) {
					//checking if black King
					if(temp.name.equals("bK")) {
						//checkmate(temp.file, temp.rank);
						return true;
					}
				}
			} //middle pawns check for two sides
			else {
				//checking up 1 right 1
				temp = board[this.rank + 1][fileToNum((char) (this.file + 1))];
				if(temp != null) {
					//checking if black King
					if(temp.name.equals("bK")) {
						//checkmate((char) temp.file, temp.rank);
						return true;
					}
				}
				//checking up 1 left 1
				temp = board[this.rank + 1][fileToNum((char) (this.file - 1))];
				if(temp != null) {
					//checking if black King
					if(temp.name.equals("bK")) {
						//checkmate((char) temp.file, temp.rank);
						return true;
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Takes in an input indicating what the pawn should be prmoted to. Replaces the piece in the pawn's position on the final rank with a piece of the indicated promotion type. Initializes the new piece's values to be the proper values. It also checks to see if this promotion places the opponent King in check
		 * 
		 * @author Jake
		 * @param promote_to - should be one of the 4 possible values to promote to. It is a lowercase lettering indicating Rook, Knight, Bishop, or Queen
		 * @throws IllegalArgumentException if the input is not one of four the valid promotion types
		 */
		void promote(String promote_to) throws IllegalArgumentException{
			if(promote_to.equals("r")) {
				Piece newPiece = new Rook(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} 
			else if(promote_to.equals("n")) {
				Piece newPiece = new Knight(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} 
			else if(promote_to.equals("b")) {
				Piece newPiece = new Bishop(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} 
			else if(promote_to.equals("q")) {
				Piece newPiece = new Queen(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public ArrayList<String> allValidMoves() {
			
			ArrayList<String> result = new ArrayList<String>();
			String move;
			Piece[][] board_copy;
			final boolean side_playing = white_moves;
			
			//Checking up 1
			if(board[this.rank + 1][fileToNum(this.file)] == null) {
				//Testing if this move puts own King in check
				board_copy = copyBoard();
				board_copy[this.rank + 1][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
				board_copy[this.rank][fileToNum(this.file)] = null;
				board_copy[this.rank + 1][fileToNum(this.file)].rank = board_copy[this.rank + 1][fileToNum(this.file)].rank + 1;
				white_moves = board_copy[this.rank + 1][fileToNum(this.file)].white_side;
				if(!putsOwnKingInCheck(board_copy)) {
					move = String.valueOf(this.file).concat((this.rank + 1) + "");
					result.add(move);
				}
				white_moves = side_playing;
				
				//Checking up 2
				if(this.rank == 2 && board[this.rank + 2][fileToNum(this.file)] == null) {
					//Testing if this move puts own King in check
					board_copy = copyBoard();
					board_copy[this.rank + 2][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank + 2][fileToNum(this.file)].rank = board_copy[this.rank + 2][fileToNum(this.file)].rank + 2;
					white_moves = board_copy[this.rank + 2][fileToNum(this.file)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat((this.rank + 2) + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
			}
			//Checking up-left
			if(this.file != 'a') {
				//If you can enpassant
				if(this.rank == 5 && this.file - 1 == black_enpassant) {
					//Testing if this move puts own King in check
					board_copy = copyBoard();
					board_copy[6][fileToNum(black_enpassant)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[6][fileToNum(black_enpassant)].rank = 6;
					board_copy[6][fileToNum(black_enpassant)].file = black_enpassant;
					white_moves = board_copy[6][fileToNum(black_enpassant)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf((char) (this.file - 1)).concat((this.rank + 1) + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}//Else if you can capture
				else if(board[this.rank + 1][fileToNum((char) (this.file - 1))] != null) {
					if(board[this.rank + 1][fileToNum((char) (this.file - 1))].white_side != this.white_side) {
						//Testing if this move puts own King in check
						board_copy = copyBoard();
						board_copy[this.rank + 1][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].rank + 1;
						board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].file - 1);
						white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file - 1)).concat((this.rank + 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
			}
			//Checking up-right
			if(this.file != 'h') {
				//If you can enpassant
				if(this.rank == 5 && this.file + 1 == black_enpassant) {
					//Testing if this move puts own King in check
					board_copy = copyBoard();
					board_copy[6][fileToNum(black_enpassant)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[6][fileToNum(black_enpassant)].rank = 6;
					board_copy[6][fileToNum(black_enpassant)].file = black_enpassant;
					white_moves = board_copy[6][fileToNum(black_enpassant)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf((char) (this.file + 1)).concat((this.rank + 1) + "");
						result.add(move);
					}
					white_moves = side_playing;
				}//Else if you can capture
				else if(board[this.rank + 1][fileToNum((char) (this.file + 1))] != null) {
					if(board[this.rank + 1][fileToNum((char) (this.file + 1))].white_side != this.white_side) {
						//Testing if this move puts own King in check
						board_copy = copyBoard();
						board_copy[this.rank + 1][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].rank + 1;
						board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].file + 1);
						white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file + 1)).concat((this.rank + 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
			}
			
			
			return result;
		}
	}
	
	/**
	 * White_Pawn and Black_Pawns are the only cases where there are separate classes for the same type of piece since pawns can only advance while other pieces may move in any direction.  Pawns also are unique in that they have an additional method called {@link #promote(String) Promote} called when they reach the opposing end's final rank
	 * 
	 * See {@link chess.Chess.White_Pawn}
	 * 
	 * @author Jake
	 */
	public static class Black_Pawn extends Piece {
		
		/**
		 * Constructor initializes the piece's name as "bp", its file as the input file, its rank as the input rank, and its white_side as false
		 * 
		 * @author Jake
		 * @param file - the file where the piece was created
		 * @param rank - the rank where the piece was created
		 */
		public Black_Pawn(char file, int rank) {
			this.name = "bp";
			this.file = file;
			this.rank = rank;
			this.white_side = false;
		}
		
		/**
		 * Black_Pawns may move up towards ranks of lesser value. They can move down one in their file if the path is clear, up down in their file if the path is clear and they are still in their starting position, down-left or down-right if there is an opponent piece there or if they are performing an En Passant. When they reach the opposite side's final rank, they are {@link #promote(String) promoted}. All moves are ensured not to place the piece's own King in check by {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} method before being committed. If a move is valid, this piece's position is changed in the global board and its own file and rank fields are updated
		 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public void move(String move_to)  throws IllegalArgumentException{
			
			/*
			if(!board[this.rank][fileToNum(this.file)].equals(this)) {
				System.out.println("we have not tracked this file and rank properly.");
				System.out.println("File: " + this.file);
				System.out.println("Rank: " + this.rank);
				in.close();
				System.exit(0);
			}*/
			
			//If you're trying to move a black piece on a white move
			if(white_moves) {
				throw new IllegalArgumentException();
			}
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Character.getNumericValue(move_to.charAt(1));
			
			Piece[][] board_copy = copyBoard();
			
			if(move_file != this.file) {
				//System.out.println("Pawn moving to different file");		
				//If the designated move is not in this file, then we have to see if it is an attempt to capture
				if(move_file == (this.file + 1) && move_rank == (this.rank - 1)) {
					//System.out.println("Pawn moving down-right");
					//Checking to see there is a piece there
					if(board[this.rank - 1][fileToNum(this.file) + 1] == null) {
						//Checking for Enpassant
						if(move_file == white_enpassant && move_rank == 3) {
							
							board_copy[this.rank][fileToNum(this.file) + 1] = null;
							
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
							board_copy[move_rank][fileToNum(move_file)].file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							
							//Removing the white pawn
							board[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board[this.rank - 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							checkForCheck(board);
							return;
						}
						else {
							throw new IllegalArgumentException();
						}
					}
					//Making sure the piece isn't on the same side
					if(board[this.rank - 1][fileToNum(this.file) + 1].white_side == false) {
						throw new IllegalArgumentException();
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank - 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} else if (move_file == (this.file - 1) && move_rank == (this.rank - 1)) {
					//System.out.println("Pawn moving down-left");
					//Checking to see there is a piece there
					if(board[this.rank - 1][fileToNum(this.file) - 1] == null) {
						//Checking for Enpassant
						if(move_file == white_enpassant && move_rank == 3) {
							//Checking on board copy first
							board_copy[this.rank][fileToNum(this.file) - 1] = null;
							
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
							board_copy[move_rank][fileToNum(move_file)].file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) - 1] = null;
							//Moving
							board[this.rank - 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							checkForCheck(board);
							return;
						}
						else {
							throw new IllegalArgumentException();
						}
					}
					//Making sure the piece isn't on the same side
					if(board[this.rank - 1][fileToNum(this.file) - 1].white_side == false) {
						throw new IllegalArgumentException();
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank - 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} else {
					//System.out.println("Something wrong");
					throw new IllegalArgumentException();
				}
			} else {
				if(move_rank == this.rank - 1) {
					//Checking to see if path clear
					if(board[this.rank - 1][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving piece
					board[this.rank - 1][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving 2 spaces
				else if(move_rank == rank - 2 && this.rank == 7) {
					//Checking to see if path clear
					if(board[this.rank - 1][fileToNum(this.file)] != null 
					|| board[this.rank - 2][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving piece
					board[this.rank - 2][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					black_enpassant = this.file;
					checkForCheck(board);
					return;
				} 
				else {
					throw new IllegalArgumentException();
				}
			}

		}
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board - the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public boolean check(Piece[][] board) {
			
			Piece temp;
			
			//If the pawn is in the last rank, it should have promoted
			if(this.rank == 1) {
				throw new IllegalArgumentException();
			}
			
			//a pawns only check one side
			if(this.file == 'a') {
				//checking down 1 right 1
				temp = board[this.rank - 1][fileToNum((char) (this.file + 1))];
				if(temp != null) {
					//checking if white King
					if(temp.name.equals("wK")) {
						//checkmate(temp.file, temp.rank);
						return true;
					}
				}
			} //h pawns only check one side
			else if(this.file == 'h') {
				//checking down 1 left 1
				temp = board[this.rank - 1][fileToNum((char) (this.file - 1))];
				if(temp != null) {
					//checking if white King
					if(temp.name.equals("wK")) {
						//checkmate(temp.file, temp.rank);
						return true;
					}
				}
			} //middle pawns check for two sides
			else {
				//checking down 1 right 1
				temp = board[this.rank - 1][fileToNum((char) (this.file + 1))];
				if(temp != null) {
					//checking if white King
					if(temp.name.equals("wK")) {
						//checkmate(temp.file, temp.rank);
						return true;
					}
				}
				//checking down 1 left 1
				temp = board[this.rank - 1][fileToNum((char) (this.file - 1))];
				if(temp != null) {
					//checking if white King
					if(temp.name.equals("wK")) {
						//checkmate(temp.file, temp.rank);
						return true;
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Takes in an input indicating what the pawn should be prmoted to. Replaces the piece in the pawn's position on the final rank with a piece of the indicated promotion type. Initializes the new piece's values to be the proper values. It also checks to see if this promotion places the opponent King in check
		 * 
		 * @author Jake
		 * @param promote_to - should be one of the 4 possible values to promote to. It is a lowercase lettering indicating Rook, Knight, Bishop, or Queen
		 * @throws IllegalArgumentException if the input is not one of four the valid promotion types
		 */
		void promote(String promote_to) throws IllegalArgumentException{
			if(promote_to.equals("r")) {
				Piece newPiece = new Rook(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} 
			else if(promote_to.equals("n")) {
				Piece newPiece = new Knight(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} 
			else if(promote_to.equals("b")) {
				Piece newPiece = new Bishop(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} 
			else if(promote_to.equals("q")) {
				Piece newPiece = new Queen(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check(board);
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public ArrayList<String> allValidMoves() {
			
			ArrayList<String> result = new ArrayList<String>();
			String move;
			Piece[][] board_copy;
			final boolean side_playing = white_moves;
			
			//Checking down 1
			if(board[this.rank - 1][fileToNum(this.file)] == null) {
				//Testing if this move puts own King in check
				board_copy = copyBoard();
				board_copy[this.rank - 1][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
				board_copy[this.rank][fileToNum(this.file)] = null;
				board_copy[this.rank - 1][fileToNum(this.file)].rank = board_copy[this.rank - 1][fileToNum(this.file)].rank - 1;
				white_moves = board_copy[this.rank - 1][fileToNum(this.file)].white_side;
				if(!putsOwnKingInCheck(board_copy)) {
					move = String.valueOf(this.file).concat((this.rank - 1) + "");
					result.add(move);
				}
				white_moves = side_playing;
				
				//Checking down 2
				if(this.rank == 7 && board[this.rank - 2][fileToNum(this.file)] == null) {
					//Testing if this move puts own King in check
					board_copy = copyBoard();
					board_copy[this.rank - 2][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank - 2][fileToNum(this.file)].rank = board_copy[this.rank - 2][fileToNum(this.file)].rank - 2;
					white_moves = board_copy[this.rank - 2][fileToNum(this.file)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat((this.rank - 2) + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
			}
			//Checking down-left
			if(this.file != 'a') {
				//If you can enpassant
				if(this.rank == 4 && this.file - 1 == white_enpassant) {
					//Testing if this move puts own King in check
					board_copy = copyBoard();
					board_copy[4][fileToNum(white_enpassant)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[4][fileToNum(white_enpassant)].rank = 4;
					board_copy[4][fileToNum(white_enpassant)].file = white_enpassant;
					white_moves = board_copy[4][fileToNum(white_enpassant)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf((char) (this.file - 1)).concat((this.rank - 1) + "");
						result.add(move);
					}
					white_moves = side_playing;
				}//Else if you can capture
				else if(board[this.rank - 1][fileToNum((char) (this.file - 1))] != null) {
					if(board[this.rank - 1][fileToNum((char) (this.file - 1))].white_side != this.white_side) {
						//Testing if this move puts own King in check
						board_copy = copyBoard();
						board_copy[this.rank - 1][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].rank - 1;
						board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].file - 1);
						white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file - 1)).concat((this.rank - 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
			}
			//Checking down-right
			if(this.file != 'h') {
				//If you can enpassant
				if(this.rank == 4 && this.file + 1 == white_enpassant) {
					//Testing if this move puts own King in check
					board_copy = copyBoard();
					board_copy[4][fileToNum(white_enpassant)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[4][fileToNum(white_enpassant)].rank = 4;
					board_copy[4][fileToNum(white_enpassant)].file = white_enpassant;
					white_moves = board_copy[6][fileToNum(white_enpassant)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf((char) (this.file + 1)).concat((this.rank - 1) + "");
						result.add(move);
					}
					white_moves = side_playing;
				}//Else if you can capture
				else if(board[this.rank - 1][fileToNum((char) (this.file + 1))] != null) {
					if(board[this.rank - 1][fileToNum((char) (this.file + 1))].white_side != this.white_side) {
						//Testing if this move puts own King in check
						board_copy = copyBoard();
						board_copy[this.rank - 1][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].rank - 1;
						board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].file + 1);
						white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file + 1)).concat((this.rank - 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
			}
			
			
			return result;
		}
	}
	
	/**
	 * Rook class and King class are the only Pieces to have an additional field to other Pieces. They store a boolean has_moved that is false until they have moved. This boolean is used when a King tries to castle since castling is only allowed when both the King and the Rook have not moved.
	 * 
	 * See {@link chess.Chess.King}
	 * 
	 * @author Jake
	 */
	public static class Rook extends Piece {
		
		public boolean has_moved = false;
		
		/**
		 * Constructor initializes the piece's name as "R", its file as the input file, its rank as the input rank. A "w" or "b" is added before the name and its white_side value is set when the piece is created either in {@link #initialize()} or by a Pawn's promotion method
		 * 
		 * @author Jake
		 * @param file - the file where the piece was created
		 * @param rank - the rank where the piece was created
		 */
		public Rook(char file, int rank) {
			this.name = "R";
			this.file = file;
			this.rank = rank;
		}
		
		/**
		 * Rooks may move to any position in their file or rank as long as the path is clear and the destination is not occupied by a piece of the same side. All moves are ensured not to place the piece's own King in check by {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} method before being committed. If a move is valid, this piece's position is changed in the global board and its own file and rank fields are updated
		 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public void move(String move_to)  throws IllegalArgumentException{
			
			/*if(!board[this.rank][fileToNum(this.file)].equals(this)) {
				System.out.println("we have not tracked this file and rank properly.");
				System.out.println("File: " + this.file);
				System.out.println("Rank: " + this.rank);
				in.close();
				System.exit(0);
			}*/

			//Trying to move opponent's piece
			if(this.white_side != white_moves) {
				throw new IllegalArgumentException();
			}
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Character.getNumericValue(move_to.charAt(1));
			
			Piece[][] board_copy = copyBoard();
			
			//If trying to move to the same spot
			if(move_file == this.file && move_rank == this.rank) {
				throw new IllegalArgumentException();
			} //If moving to somewhere besides this position's file and rank, throw exception
			else if(move_file != this.file && move_rank != this.rank) {
				throw new IllegalArgumentException();
			} //If moving along the file
			else if(move_file == this.file) {
				//Moving up the board
				if(move_rank > this.rank) {
					//Checking to see if path clear
					for(int i = this.rank + 1; i < move_rank; i++) {
						if(board[i][fileToNum(this.file)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[move_rank][fileToNum(this.file)] != null) {
						//making sure the piece isn't on the same side
						if(board[move_rank][fileToNum(this.file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					this.has_moved = true;
					checkForCheck(board);
					return;
				} //Moving down the board
				else {
					//Checking to see if path clear
					for(int i = this.rank - 1; i > move_rank; i--) {
						if(board[i][fileToNum(this.file)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[move_rank][fileToNum(this.file)] != null) {
						//making sure the piece isn't on the same side
						if(board[move_rank][fileToNum(this.file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.has_moved = true;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				}
			} //Moving along the rank
			else {
				//Moving to the right
				if(move_file > this.file) {
					//Checking to see if path clear
					for(int i = this.file + 1; i < move_file; i++) {
						if(board[this.rank][fileToNum((char) i)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[this.rank][fileToNum(move_file)] != null) {
						//making sure the piece isn't on the same side
						if(board[this.rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[this.rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.has_moved = true;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving to the left
				else {
					//Checking to see if path clear
					for(int i = this.file - 1; i > move_file; i--) {
						if(board[this.rank][fileToNum((char) i)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[this.rank][fileToNum(move_file)] != null) {
						//making sure the piece isn't on the same side
						if(board[this.rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[this.rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.has_moved = true;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				}
			}
			
		}
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board - the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public boolean check(Piece[][] board) {
			
			Piece temp;
			
			//Check for checks on the row to the left
			for(int f = fileToNum(this.file) - 1; f >= 0; f--) {
				temp = board[this.rank][f];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check for checks on the row to the right
			for(int f = fileToNum(this.file) + 1; f < 8; f++) {
				temp = board[this.rank][f];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Checks for checks on top
			for(int r = this.rank + 1; r < 9; r++) {
				temp = board[r][fileToNum(this.file)];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check for checks below
			for(int r = this.rank - 1; r > 0; r--) {
				temp = board[r][fileToNum(this.file)];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public ArrayList<String> allValidMoves() {
			
			ArrayList<String> result = new ArrayList<String>();
			String move;
			Piece[][] board_copy;
			final boolean side_playing = white_moves;
			
			//Checking all possible moves up
			for(int r = this.rank + 1; r < 9; r++) {
				//If there is no piece in this position
				if(board[r][fileToNum(this.file)] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][fileToNum(this.file)].rank = r;
					white_moves = board_copy[r][fileToNum(this.file)].white_side;
					
					//display(board_copy);
					
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[r][fileToNum(this.file)].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][fileToNum(this.file)].rank = r;
						white_moves = board_copy[r][fileToNum(this.file)].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(this.file).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			//Checking all possible moves down
			for(int r = this.rank - 1; r > 0; r--) {
				//If there is no piece in this position
				if(board[r][fileToNum(this.file)] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][fileToNum(this.file)].rank = r;
					white_moves = board_copy[r][fileToNum(this.file)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[r][fileToNum(this.file)].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][fileToNum(this.file)].rank = r;
						white_moves = board_copy[r][fileToNum(this.file)].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(this.file).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			//Checking all possible moves right
			for(int f = fileToNum(this.file) + 1; f < 8; f++) {
				//If there is no piece in this position
				if(board[this.rank][f] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank][f].file = numToFile(f);
					white_moves = board_copy[this.rank][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(this.rank + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[this.rank][f].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank][f].file = numToFile(f);
						white_moves = board_copy[this.rank][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(this.rank + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			//Checking all possible moves left
			for(int f = fileToNum(this.file) - 1; f >= 0; f--) {
				//If there is no piece in this position
				if(board[this.rank][f] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank][f].file = numToFile(f);
					white_moves = board_copy[this.rank][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(this.rank + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[this.rank][f].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank][f].file = numToFile(f);
						white_moves = board_copy[this.rank][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(this.rank + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			
			return result;
		}
	}
	
	/**
	 * The Knight class acts like a knight in any chess game.
	 * 
	 * @author Jake
	 */
	public static class Knight extends Piece {
		
		/**
		 * Constructor initializes the piece's name as "N", its file as the input file, its rank as the input rank. A "w" or "b" is added before the name and its white_side value is set when the piece is created either in {@link #initialize()} or by a Pawn's promotion method
		 * 
		 * @author Jake
		 * @param file - the file where the piece was created
		 * @param rank - the rank where the piece was created
		 */
		public Knight(char file, int rank) {
			this.name = "N";
			this.file = file;
			this.rank = rank;
		}
		
		/**
		 * Knights have at most 8 possible moves at a given point. They may not move to one of these possible positions if a piece of the same side is in that position of the position is out of the bounds of the playing board. All moves are ensured not to place the piece's own King in check by {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} method before being committed. If a move is valid, this piece's position is changed in the global board and its own file and rank fields are updated
		 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public void move(String move_to)  throws IllegalArgumentException{
			
			/*if(!board[this.rank][fileToNum(this.file)].equals(this)) {
				System.out.println("we have not tracked this file and rank properly.");
				System.out.println("File: " + this.file);
				System.out.println("Rank: " + this.rank);
				in.close();
				System.exit(0);
			}*/
			
			//Trying to move opponent's piece
			if(this.white_side != white_moves) {
				throw new IllegalArgumentException();
			}
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Character.getNumericValue(move_to.charAt(1));
			
			Piece[][] board_copy = copyBoard();
			
			//If trying to move to the same spot
			if(move_file == this.file && move_rank == this.rank) {
				throw new IllegalArgumentException();
			} //Moving horizontally first
			else if(Math.abs(move_file - this.file) == 2) {
				//Moving 1 vertical space
				if(Math.abs(move_rank - this.rank) == 1) {
					//Checking if piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Making sure piece isn't on the same side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Invalid move
				else {
					throw new IllegalArgumentException();
				}
			} //Moving vertically first
			else if(Math.abs(move_rank - this.rank) == 2) {
				//Moving 1 horizontal space
				if(Math.abs(move_file - this.file) == 1) {
					//Checking if piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Making sure piece isn't on the same side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Invalid move
				else {
					throw new IllegalArgumentException();
				}
			} //Invalid move
			else {
				throw new IllegalArgumentException();
			}
			
		}
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board - the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public boolean check(Piece[][] board) {
			
			Piece temp;
			
			//Check 1st layer above 
			if(this.rank <= 7) {
				//Check up-left one
				if(this.file >= 'c') {
					temp = board[this.rank + 1][fileToNum((char) (this.file - 2))];
					if(temp != null) {
						if(temp.white_side != this.white_side && temp instanceof King) {
							//checkmate(temp.file, temp.rank);
							return true;
						}
					}
				}
				//Check up-right one
				if(this.file <= 'f') {
					temp = board[this.rank + 1][fileToNum((char) (this.file + 2))];
					if(temp != null) {
						if(temp.white_side != this.white_side && temp instanceof King) {
							//checkmate(temp.file, temp.rank);
							return true;
						}
					}
				}
				//Check 2nd layer above
				if(this.rank <= 6) {
					//Check up-left two
					if(this.file >= 'b') {
						temp = board[this.rank + 2][fileToNum((char) (this.file - 1))];
						if(temp != null) {
							if(temp.white_side != this.white_side && temp instanceof King) {
								//checkmate(temp.file, temp.rank);
								return true;
							}
						}
					}
					//Check up-right two
					if(this.file <= 'g') {
						temp = board[this.rank + 2][fileToNum((char) (this.file + 1))];
						if(temp != null) {
							if(temp.white_side != this.white_side && temp instanceof King) {
								//checkmate(temp.file, temp.rank);
								return true;
							}
						}
					}
				}
			}
			//Check 1st layer below 
			if(this.rank >= 2) {
				//Check down-left one
				if(this.file >= 'c') {
					temp = board[this.rank - 1][fileToNum((char) (this.file - 2))];
					if(temp != null) {
						if(temp.white_side != this.white_side && temp instanceof King) {
							//checkmate(temp.file, temp.rank);
							return true;
						}
					}
				}
				//Check down-right one
				if(this.file <= 'f') {
					temp = board[this.rank - 1][fileToNum((char) (this.file + 2))];
					if(temp != null) {
						if(temp.white_side != this.white_side && temp instanceof King) {
							//checkmate(temp.file, temp.rank);
							return true;
						}
					}
				}
				//Check 2nd layer below
				if(this.rank >= 3) {
					//Check down-left two
					if(this.file >= 'b') {
						temp = board[this.rank - 2][fileToNum((char) (this.file - 1))];
						if(temp != null) {
							if(temp.white_side != this.white_side && temp instanceof King) {
								//checkmate(temp.file, temp.rank);
								return true;
							}
						}
					}
					//Check down-right two
					if(this.file <= 'g') {
						temp = board[this.rank - 2][fileToNum((char) (this.file + 1))];
						if(temp != null) {
							if(temp.white_side != this.white_side && temp instanceof King) {
								//checkmate(temp.file, temp.rank);
								return true;
							}
						}
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public ArrayList<String> allValidMoves() {
			
			ArrayList<String> result = new ArrayList<String>();
			String move;
			Piece[][] board_copy;
			final boolean side_playing = white_moves;
			
			//Check first layer above
			if(this.rank <= 7) {
				//Check up-left one
				if(this.file >= 'c') {
					if(board[this.rank + 1][fileToNum((char) (this.file - 2))] == null) {
						board_copy = copyBoard();
						board_copy[this.rank + 1][fileToNum((char) (this.file - 2))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].rank + 1;
						board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].file - 2);
						white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file - 2)).concat(this.rank + 1 + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank + 1][fileToNum((char) (this.file - 2))].white_side != this.white_side) {
							board_copy = copyBoard();
							board_copy[this.rank + 1][fileToNum((char) (this.file - 2))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].rank + 1;
							board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].file - 2);
							white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file - 2))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file - 2)).concat(this.rank + 1 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
				//Check up-right one
				if(this.file <= 'f') {
					if(board[this.rank + 1][fileToNum((char) (this.file + 2))] == null) {
						board_copy = copyBoard();
						board_copy[this.rank + 1][fileToNum((char) (this.file + 2))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].rank + 1;
						board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].file + 2);
						white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file + 2)).concat(this.rank + 1 + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank + 1][fileToNum((char) (this.file + 2))].white_side != this.white_side) {
							board_copy = copyBoard();
							board_copy[this.rank + 1][fileToNum((char) (this.file + 2))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].rank + 1;
							board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].file + 2);
							white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file + 2))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file + 2)).concat(this.rank + 1 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
				//Check 2nd layer above
				if(this.rank <= 6) {
					//Check up-left two
					if(this.file >= 'b') {
						if(board[this.rank + 2][fileToNum((char) (this.file - 1))] == null) {
							board_copy = copyBoard();
							board_copy[this.rank + 2][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].rank + 2;
							board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].file - 1);
							white_moves = board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file - 1)).concat(this.rank + 2 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
						else {
							if(board[this.rank + 2][fileToNum((char) (this.file - 1))].white_side != this.white_side) {
								board_copy = copyBoard();
								board_copy[this.rank + 2][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
								board_copy[this.rank][fileToNum(this.file)] = null;
								board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].rank + 2;
								board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].file - 1);
								white_moves = board_copy[this.rank + 2][fileToNum((char) (this.file - 1))].white_side;
								if(!putsOwnKingInCheck(board_copy)) {
									move = String.valueOf((char) (this.file - 1)).concat(this.rank + 2 + "");
									result.add(move);
								}
								white_moves = side_playing;
							}
						}
					}
					//Check up-right two
					if(this.file <= 'g') {
						if(board[this.rank + 2][fileToNum((char) (this.file + 1))] == null) {
							board_copy = copyBoard();
							board_copy[this.rank + 2][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].rank + 2;
							board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].file + 1);
							white_moves = board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file + 1)).concat(this.rank + 2 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
						else {
							if(board[this.rank + 2][fileToNum((char) (this.file + 1))].white_side != this.white_side) {
								board_copy = copyBoard();
								board_copy[this.rank + 2][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
								board_copy[this.rank][fileToNum(this.file)] = null;
								board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].rank + 2;
								board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].file + 1);
								white_moves = board_copy[this.rank + 2][fileToNum((char) (this.file + 1))].white_side;
								if(!putsOwnKingInCheck(board_copy)) {
									move = String.valueOf((char) (this.file + 1)).concat(this.rank + 2 + "");
									result.add(move);
								}
								white_moves = side_playing;
							}
						}
					}
				}
			}
			//Check 1st layer below 
			if(this.rank >= 2) {
				
				//Check down-left one
				if(this.file >= 'c') {
					if(board[this.rank - 1][fileToNum((char) (this.file - 2))] == null) {
						board_copy = copyBoard();
						board_copy[this.rank - 1][fileToNum((char) (this.file - 2))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].rank - 1;
						board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].file - 2);
						white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file - 2)).concat(this.rank - 1 + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank - 1][fileToNum((char) (this.file - 2))].white_side != this.white_side) {
							board_copy = copyBoard();
							board_copy[this.rank - 1][fileToNum((char) (this.file - 2))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].rank - 1;
							board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].file - 2);
							white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file - 2))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file - 2)).concat(this.rank - 1 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
				//Check down-right one
				if(this.file <= 'f') {
					if(board[this.rank - 1][fileToNum((char) (this.file + 2))] == null) {
						board_copy = copyBoard();
						board_copy[this.rank - 1][fileToNum((char) (this.file + 2))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].rank - 1;
						board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].file + 2);
						white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file + 2)).concat(this.rank - 1 + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank - 1][fileToNum((char) (this.file + 2))].white_side != this.white_side) {
							board_copy = copyBoard();
							board_copy[this.rank - 1][fileToNum((char) (this.file + 2))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].rank - 1;
							board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].file + 2);
							white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file + 2))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file + 2)).concat(this.rank - 1 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
				//Check 2nd layer below
				if(this.rank <= 6) {
					//Check down-left two
					if(this.file >= 'b') {
						if(board[this.rank - 2][fileToNum((char) (this.file - 1))] == null) {
							board_copy = copyBoard();
							board_copy[this.rank - 2][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].rank - 2;
							board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].file - 1);
							white_moves = board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file - 1)).concat(this.rank - 2 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
						else {
							if(board[this.rank - 2][fileToNum((char) (this.file - 1))].white_side != this.white_side) {
								board_copy = copyBoard();
								board_copy[this.rank - 2][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
								board_copy[this.rank][fileToNum(this.file)] = null;
								board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].rank - 2;
								board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].file - 1);
								white_moves = board_copy[this.rank - 2][fileToNum((char) (this.file - 1))].white_side;
								if(!putsOwnKingInCheck(board_copy)) {
									move = String.valueOf((char) (this.file - 1)).concat(this.rank - 2 + "");
									result.add(move);
								}
								white_moves = side_playing;
							}
						}
					}
					//Check down-right two
					if(this.file <= 'g') {
						if(board[this.rank - 2][fileToNum((char) (this.file + 1))] == null) {
							board_copy = copyBoard();
							board_copy[this.rank - 2][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].rank - 2;
							board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].file + 1);
							white_moves = board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].white_side;
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file + 1)).concat(this.rank - 2 + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
						else {
							if(board[this.rank - 2][fileToNum((char) (this.file + 1))].white_side != this.white_side) {
								board_copy = copyBoard();
								board_copy[this.rank - 2][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
								board_copy[this.rank][fileToNum(this.file)] = null;
								board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].rank - 2;
								board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].file + 1);
								white_moves = board_copy[this.rank - 2][fileToNum((char) (this.file + 1))].white_side;
								if(!putsOwnKingInCheck(board_copy)) {
									move = String.valueOf((char) (this.file + 1)).concat(this.rank - 2 + "");
									result.add(move);
								}
								white_moves = side_playing;
							}
						}
					}
				}
			}
			
			return result;
		}
		
	}
	
	/**
	 * The Bishop class acts like a bishop in any chess game.
	 * 
	 * @author Jake
	 */
	public static class Bishop extends Piece {
		
		/**
		 * Constructor initializes the piece's name as "B", its file as the input file, its rank as the input rank. A "w" or "b" is added before the name and its white_side value is set when the piece is created either in {@link #initialize()} or by a Pawn's promotion method
		 * 
		 * @author Jake
		 * @param file - the file where the piece was created
		 * @param rank - the rank where the piece was created
		 */
		public Bishop(char file, int rank) {
			this.name = "B";
			this.file = file;
			this.rank = rank;
		}
		
		/**
		 * Bishops may move to any position that is diagonal to them as long as the path is clear and the target destination does not contain a piece of the same side. All moves are ensured not to place the piece's own King in check by {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} method before being committed. If a move is valid, this piece's position is changed in the global board and its own file and rank fields are updated	 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public void move(String move_to)  throws IllegalArgumentException{
			
			/*if(!board[this.rank][fileToNum(this.file)].equals(this)) {
				System.out.println("we have not tracked this file and rank properly.");
				System.out.println("File: " + this.file);
				System.out.println("Rank: " + this.rank);
				in.close();
				System.exit(0);
			}*/
			
			//Trying to move opponent's piece
			if(this.white_side != white_moves) {
				throw new IllegalArgumentException();
			}
			
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Character.getNumericValue(move_to.charAt(1));
			
			Piece[][] board_copy = copyBoard();
			
			//If trying to move to the same spot
			if(move_file == this.file && move_rank == this.rank) {
				throw new IllegalArgumentException();
			} //If not moving on a diagonal
			else if(Math.abs(move_rank - this.rank) != Math.abs(fileToNum(move_file) - fileToNum(this.file))) {
				//System.out.println("TESTING DIAGONAL MATH WRONG");
				throw new IllegalArgumentException();
			} //Moving diagonally
			else {
				//Moving up right
				if(move_file > this.file && move_rank > this.rank) {
					//Checking to see if path clear
					for(int i = (move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank + i][fileToNum((char) (this.file + i))] != null) {
							//System.out.println("TESTING PATH NOT CLEAR");
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							//System.out.println("TESTING PIECE IN POSITION");
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving up left
				else if(move_file < this.file && move_rank > this.rank) {
					//Checking to see if path clear
					for(int i = (move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank + i][fileToNum((char) (this.file - i))] != null) {
							//System.out.println("TESTING PATH NOT CLEAR");
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							//System.out.println("TESTING PIECE IN POSITION");
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving down right
				else if(move_file > this.file && move_rank < this.rank) {
					//Checking to see if path clear
					for(int i = Math.abs(move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank - i][fileToNum((char) (this.file + i))] != null) {
							//System.out.println("TESTING PATH NOT CLEAR");
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							//System.out.println("TESTING PIECE IN POSITION");
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving down left
				else if(move_file < this.file && move_rank < this.rank){
					//Checking to see if path clear
					for(int i = Math.abs(move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank - i][fileToNum((char) (this.file - i))] != null) {
							//System.out.println("TESTING PATH NOT CLEAR");
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							//System.out.println("TESTING PIECE IN POSITION");
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Something's wrong
				else {
					//System.out.println("SOMETHING WRONG");
					throw new IllegalArgumentException();
				}
			}
		}
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board - the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public boolean check(Piece[][] board) {
			
			Piece temp;
			
			//Check up-right
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file + (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check up-left
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file - (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check down-right
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file + (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check down-left
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file - (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			
			return false;
		}
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public ArrayList<String> allValidMoves() {
			
			ArrayList<String> result = new ArrayList<String>();
			String move;
			Piece[][] board_copy;
			final boolean side_playing = white_moves;
			
			//Check up-right
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file + (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
				
			}
			//Check up-left
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file - (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
			}
			//Check down-right
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file + (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
			}
			//Check down-left
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file - (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
			}
			
			return result;
			
		}
	}
	
	/**
	 * The Queen class acts like a queen in any chess game.
	 * 
	 * @author Jake
	 */
	public static class Queen extends Piece {
		
		/**
		 * Constructor initializes the piece's name as "Q", its file as the input file, its rank as the input rank. A "w" or "b" is added before the name and its white_side value is set when the piece is created either in {@link #initialize()} or by a Pawn's promotion method
		 * 
		 * @author Jake
		 * @param file - the file where the piece was created
		 * @param rank - the rank where the piece was created
		 */
		public Queen(char file, int rank) {
			this.name = "Q";
			this.file = file;
			this.rank = rank;
		}
		
		/**
		 * Queens move like a {@link Rook} or a {@link Bishop}. All moves are ensured not to place the piece's own King in check by {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} method before being committed. If a move is valid, this piece's position is changed in the global board and its own file and rank fields are updated
		 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public void move(String move_to)  throws IllegalArgumentException{
			
			if(!board[this.rank][fileToNum(this.file)].equals(this)) {
				System.out.println("we have not tracked this file and rank properly.");
				System.out.println("File: " + this.file);
				System.out.println("Rank: " + this.rank);
				in.close();
				System.exit(0);
			}
			
			//Trying to move opponent's piece
			if(this.white_side != white_moves) {
				throw new IllegalArgumentException();
			}
			
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Character.getNumericValue(move_to.charAt(1));
			
			Piece[][] board_copy = copyBoard();
			
			//If trying to move to the same spot
			if(move_file == this.file && move_rank == this.rank) {
				throw new IllegalArgumentException();
			} //Moving vertically
			else if(move_file == this.file) {
				//Moving up
				if(move_rank > this.rank) {
					//Checking to see if path clear
					for(int i = this.rank + 1; i < move_rank; i++) {
						if(board[i][fileToNum(this.file)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[move_rank][fileToNum(this.file)] != null) {
						//making sure the piece isn't on the same side
						if(board[move_rank][fileToNum(this.file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving down
				else {
					//Checking to see if path clear
					for(int i = this.rank - 1; i > move_rank; i--) {
						if(board[i][fileToNum(this.file)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[move_rank][fileToNum(this.file)] != null) {
						//making sure the piece isn't on the same side
						if(board[move_rank][fileToNum(this.file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				}
			} //Moving horizontally
			else if(move_rank == this.rank) {
				//Moving right
				if(move_file > this.file) {
					//Checking to see if path clear
					for(int i = this.file + 1; i < move_file; i++) {
						if(board[this.rank][fileToNum((char) i)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[this.rank][fileToNum(move_file)] != null) {
						//making sure the piece isn't on the same side
						if(board[this.rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving left
				else {
					//Checking to see if path clear
					for(int i = this.file - 1; i > move_file; i--) {
						if(board[this.rank][fileToNum((char) i)] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there and 
					if(board[this.rank][fileToNum(move_file)] != null) {
						//making sure the piece isn't on the same side
						if(board[this.rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				}
			} //Moving diagonally
			else if(Math.abs(move_rank - this.rank) == Math.abs(fileToNum(move_file) - fileToNum(this.file))) {
				//Moving up-right
				if(move_file > this.file && move_rank > this.rank) {
					//Checking to see if path clear
					for(int i = (move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank + i][fileToNum((char) (this.file + i))] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving up-left
				else if(move_file < this.file && move_rank > this.rank) {
					//Checking to see if path clear
					for(int i = (move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank + i][fileToNum((char) (this.file - i))] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving down-right
				else if(move_file > this.file && move_rank < this.rank) {
					//Checking to see if path clear
					for(int i = Math.abs(move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank - i][fileToNum((char) (this.file + i))] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Moving down left
				else if(move_file < this.file && move_rank < this.rank) {
					//Checking to see if path clear
					for(int i = Math.abs(move_rank - this.rank) - 1; i > 0; i--) {
						if(board[this.rank - i][fileToNum((char) (this.file - i))] != null) {
							throw new IllegalArgumentException();
						}
					}
					//Seeing if there is a piece there
					if(board[move_rank][fileToNum(move_file)] != null) {
						//Checking its side
						if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
							throw new IllegalArgumentException();
						}
					}
					//Checking on board copy first
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[move_rank][fileToNum(move_file)].rank = move_rank;
					board_copy[move_rank][fileToNum(move_file)].file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					checkForCheck(board);
					return;
				} //Invalid move
				else {
					throw new IllegalArgumentException();
				}
			} //Invalid move
			else {
				throw new IllegalArgumentException();
			}
			
		}
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board - the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public boolean check(Piece[][] board) {
			
			Piece temp;
			
			//Check for checks on the row to the left
			for(int f = fileToNum(this.file) - 1; f >= 0; f--) {
				temp = board[this.rank][f];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check for checks on the row to the right
			for(int f = fileToNum(this.file) + 1; f < 8; f++) {
				temp = board[this.rank][f];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Checks for checks on top
			for(int r = this.rank + 1; r < 9; r++) {
				temp = board[r][fileToNum(this.file)];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check for checks below
			for(int r = this.rank - 1; r > 0; r--) {
				temp = board[r][fileToNum(this.file)];
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			
			//Check up-right
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file + (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check up-left
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file - (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check down-right
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file + (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
			//Check down-left
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file - (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				temp = board[r][f];
				//Piece on diagonal
				if(temp != null) {
					if(temp.white_side != this.white_side && temp instanceof King) {
						//checkmate(temp.file, temp.rank);
						return true;
					} //Piece blocking king
					else {
						break;
					}
				}
			}
		
			return false;
			
		}
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public ArrayList<String> allValidMoves() {
			
			ArrayList<String> result = new ArrayList<String>();
			String move;
			Piece[][] board_copy;
			final boolean side_playing = white_moves;
			
			//Checking all possible moves up
			for(int r = this.rank + 1; r < 9; r++) {
				//If there is no piece in this position
				if(board[r][fileToNum(this.file)] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][fileToNum(this.file)].rank = r;
					white_moves = board_copy[r][fileToNum(this.file)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[r][fileToNum(this.file)].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][fileToNum(this.file)].rank = r;
						white_moves = board_copy[r][fileToNum(this.file)].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(this.file).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			//Checking all possible moves down
			for(int r = this.rank - 1; r > 0; r--) {
				//If there is no piece in this position
				if(board[r][fileToNum(this.file)] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][fileToNum(this.file)].rank = r;
					white_moves = board_copy[r][fileToNum(this.file)].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[r][fileToNum(this.file)].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[r][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][fileToNum(this.file)].rank = r;
						white_moves = board_copy[r][fileToNum(this.file)].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(this.file).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			//Checking all possible moves right
			for(int f = fileToNum(this.file) + 1; f < 8; f++) {
				//If there is no piece in this position
				if(board[this.rank][f] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank][f].file = numToFile(f);
					white_moves = board_copy[this.rank][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(this.rank + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[this.rank][f].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank][f].file = numToFile(f);
						white_moves = board_copy[this.rank][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(this.rank + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			//Checking all possible moves left
			for(int f = fileToNum(this.file) - 1; f >= 0; f--) {
				//If there is no piece in this position
				if(board[this.rank][f] == null) {
					//Check to see if this move puts the king in check
					board_copy = copyBoard();
					board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank][f].file = numToFile(f);
					white_moves = board_copy[this.rank][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(this.rank + "");
						result.add(move);
					}
					white_moves = side_playing;
					
				}
				else {
					//Can capture this piece
					if(board[this.rank][f].white_side != this.white_side) {
						//Check to see if this move puts the king in check
						board_copy = copyBoard();
						board_copy[this.rank][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank][f].file = numToFile(f);
						white_moves = board_copy[this.rank][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(this.rank + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					//Can only do it once, not to the pieces behind it too
					break;
				}
			}
			
			//Check up-right
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file + (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
				
			}
			//Check up-left
			for(int r = this.rank + 1; r < 9; r++) {
				char tempFile = (char) (this.file - (r - this.rank));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
			}
			//Check down-right
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file + (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f > 7) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
			}
			//Check down-left
			for(int r = this.rank - 1; r > 0; r--) {
				char tempFile = (char) (this.file - (this.rank - r));
				if(tempFile < 'a' || tempFile > 'h') {
					break;
				}
				int f = fileToNum(tempFile);
				if(f < 0) {
					break;
				}
				if(board[r][f] == null) {
					//Check to see if this move puts our own king in check
					board_copy = copyBoard();
					board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[r][f].rank = r;
					board_copy[r][f].file = numToFile(f);
					white_moves = board_copy[r][f].white_side;
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(numToFile(f)).concat(r + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[r][f].white_side != this.white_side) {
						//Check to see if this move puts our own king in check
						board_copy = copyBoard();
						board_copy[r][f] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[r][f].rank = r;
						board_copy[r][f].file = numToFile(f);
						white_moves = board_copy[r][f].white_side;
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(numToFile(f)).concat(r + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					break;
				}
			}
			
			return result;
		
		}
	}
	
	/**
	 * Rook class and King class are the only Pieces to have an additional field to other Pieces. They store a boolean has_moved that is false until they have moved. This boolean is used when a King tries to castle since castling is only allowed when both the King and the Rook have not moved.
	 * 
	 * See {@link chess.Chess.Rook}
	 * 
	 * @author Jake
	 */
	public static class King extends Piece {
		
		public boolean has_moved = false;
		
		/**
		 * Constructor initializes the piece's name as "K", its file as the input file, its rank as the input rank. A "w" or "b" is added before the name and its white_side value is set when the piece is created in {@link #initialize()}
		 * 
		 * @author Jake
		 * @param file - the file where the piece was created
		 * @param rank - the rank where the piece was created
		 */
		public King(char file, int rank) {
			this.name = "K";
			this.file = file;
			this.rank = rank;
		}
		
		/**
		 * Kings may move to any space adjacent to any space adjacent to them as long as there is not a piece of the same side in that position and the move will not place it in check. Kings may also castle if they have not yet moved, have a clear path to one of the rooks, and that rook has not moved. King's move method is the only move method that moves another Piece, which is the Rook that is moved during castling. A King may not castle if it moves through a check or if it will be in check after the castling. If a move is valid, this piece's position is changed in the global board and its own file and rank fields are updated
		 * 
		 * <p>King's move method does not call {@link #putsOwnKingInCheck(Piece[][]) putsOwnKingInCheck} since it was written before that method existed. Instead, each move is checked to ensure it does not place this King in check in the move method itself.
		 * 
		 * @author Jake
		 * @param move_to a two part String with the file and the rank that they are to move to
		 * @throws IllegalArgumentException if the move_to position is not valid
		 */
		public void move(String move_to)  throws IllegalArgumentException{
			
			//Trying to move opponent's piece
			if(this.white_side != white_moves) {
				throw new IllegalArgumentException();
			}
			
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Character.getNumericValue(move_to.charAt(1));
			
			//If trying to move to the same spot
			if(move_file == this.file && move_rank == this.rank) {
				throw new IllegalArgumentException();
			} //Trying to move more than two squares away
			else if(Math.abs(move_rank - this.rank) + Math.abs(fileToNum(move_file) - fileToNum(this.file)) > 2) {
				throw new IllegalArgumentException();
			} //Valid move
			else {
				
				//Creating a copy of the board to brute force test if this move will put King in check
				Piece[][] board_copy = copyBoard();
				
				//Moving up-center
				if(move_file == this.file && move_rank == this.rank + 1) {
					
				} //Moving up-right
				else if(move_file == this.file + 1 && move_rank == this.rank + 1) {
					
				} //Moving center-right
				else if(move_file == this.file + 1 && move_rank == this.rank) {
					
				} //Moving down-right
				else if(move_file == this.file + 1 && move_rank == this.rank - 1) {
					
				} // Moving down-center
				else if(move_file == this.file && move_rank == this.rank - 1) {
					
				} //Moving down-left
				else if(move_file == this.file - 1 && move_rank == this.rank - 1) {
					
				} //Moving center-left
				else if(move_file == this.file - 1 && move_rank == this.rank) {
					
				} //Moving up-left
				else if(move_file == this.file - 1 && move_rank == this.rank + 1) {
					
				} //Trying to move twice in one direction
				else {
					
					//Check for castling
					//If it's the white king castling
					if(this.white_side) {
						//Trying to castle kingside
						if(move_to.equals("g1")) {
							//If this king has already moved
							if(this.has_moved) {
								throw new IllegalArgumentException();
							}
							//Checking if King Rook Moved
							if(board[1][fileToNum('h')] == null) {
								throw new IllegalArgumentException();
							}
							if(!board[1][fileToNum('h')].name.equals("wR")) {
								//System.out.println("TESTING: NAME: " + board[1][fileToNum('h')].name);
								throw new IllegalArgumentException();
							}
							if(((Rook) board[1][fileToNum('h')]).has_moved == true) {
								throw new IllegalArgumentException();
							}
							//Checking if path clear
							for(int i = 1; i < 3; i++) {
								if(board[this.rank][fileToNum((char) (this.file + i))] != null) {
									throw new IllegalArgumentException();
								}
							}
							//Making sure King doesn't pass through check
							//Checking for f1 space
							board_copy[1][fileToNum('f')] = board_copy[1][fileToNum('e')];
							board_copy[1][fileToNum('e')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Checking for g1 space
							board_copy[1][fileToNum('g')] = board_copy[1][fileToNum('f')];
							board_copy[1][fileToNum('f')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Moving King
							board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							this.has_moved = true;
							//Moving Rook
							board[1][fileToNum('f')] = board[1][fileToNum('h')];
							board[1][fileToNum('h')] = null;
							board[1][fileToNum('f')].rank = 1;
							board[1][fileToNum('f')].file = 'f';
							((Rook) board[1][fileToNum('f')]).has_moved = true;
							checkForCheck(board);
							return;
						} //Trying to castle queenside
						else if(move_to.equals("c1")) {
							//If this king has already moved
							if(this.has_moved) {
								throw new IllegalArgumentException();
							}
							//Checking if Queen Rook Moved
							if(board[1][fileToNum('a')] == null) {
								throw new IllegalArgumentException();
							}
							if(!board[1][fileToNum('a')].name.equals("wR")) {
								throw new IllegalArgumentException();
							}
							if(((Rook) board[1][fileToNum('a')]).has_moved == true) {
								throw new IllegalArgumentException();
							}
							//Checking if path clear
							for(int i = 1; i < 3; i++) {
								if(board[this.rank][fileToNum((char) (this.file - i))] != null) {
									throw new IllegalArgumentException();
								}
							}
							//Making sure King doesn't pass through check
							//Checking for d1 space
							board_copy[1][fileToNum('d')] = board_copy[1][fileToNum('e')];
							board_copy[1][fileToNum('e')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Checking for c1 space
							board_copy[1][fileToNum('c')] = board_copy[1][fileToNum('d')];
							board_copy[1][fileToNum('d')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Moving King
							board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							this.has_moved = true;
							//Moving Rook
							board[1][fileToNum('d')] = board[1][fileToNum('a')];
							board[1][fileToNum('a')] = null;
							board[1][fileToNum('d')].rank = 1;
							board[1][fileToNum('d')].file = 'd';
							((Rook) board[1][fileToNum('d')]).has_moved = true;
							checkForCheck(board);
							return;
						} //Invalid move
						else {
							throw new IllegalArgumentException();
						}
					} //If it is the black king castling
					else {
						//Trying to castle kingside
						if(move_to.equals("g8")) {
							//If this king has already moved
							if(this.has_moved) {
								throw new IllegalArgumentException();
							}
							//Checking if King Rook Moved
							if(board[8][fileToNum('h')] == null) {
								throw new IllegalArgumentException();
							}
							if(!board[8][fileToNum('h')].name.equals("bR")) {
								throw new IllegalArgumentException();
							}
							if(((Rook) board[8][fileToNum('h')]).has_moved == true) {
								throw new IllegalArgumentException();
							}
							//Checking if path clear
							for(int i = 1; i < 3; i++) {
								if(board[this.rank][fileToNum((char) (this.file + i))] != null) {
									throw new IllegalArgumentException();
								}
							}
							//Making sure King doesn't pass through check
							//Checking for f8 space
							board_copy[8][fileToNum('f')] = board_copy[8][fileToNum('e')];
							board_copy[8][fileToNum('e')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Checking for g1 space
							board_copy[8][fileToNum('g')] = board_copy[1][fileToNum('f')];
							board_copy[8][fileToNum('f')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Moving King
							board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							this.has_moved = true;
							//Moving Rook
							board[8][fileToNum('f')] = board[8][fileToNum('h')];
							board[8][fileToNum('h')] = null;
							board[8][fileToNum('f')].rank = 8;
							board[8][fileToNum('f')].file = 'f';
							((Rook) board[8][fileToNum('f')]).has_moved = true;
							checkForCheck(board);
							return;
						} //Trying to castle queenside
						else if(move_to.equals("c8")) {
							//If this king has already moved
							if(this.has_moved) {
								throw new IllegalArgumentException();
							}
							//Checking if Queen Rook Moved
							if(board[8][fileToNum('a')] == null) {
								throw new IllegalArgumentException();
							}
							if(!board[8][fileToNum('a')].name.equals("bR")) {
								throw new IllegalArgumentException();
							}
							if(((Rook) board[8][fileToNum('a')]).has_moved == true) {
								throw new IllegalArgumentException();
							}
							//Checking if path clear
							for(int i = 1; i < 3; i++) {
								if(board[this.rank][fileToNum((char) (this.file - i))] != null) {
									throw new IllegalArgumentException();
								}
							}
							//Making sure King doesn't pass through check
							//Checking for d8 space
							board_copy[8][fileToNum('d')] = board_copy[8][fileToNum('e')];
							board_copy[8][fileToNum('e')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Checking for c8 space
							board_copy[8][fileToNum('c')] = board_copy[8][fileToNum('d')];
							board_copy[8][fileToNum('d')] = null;
							if(putsOwnKingInCheck(board_copy)) {
								throw new IllegalArgumentException();
							}
							//Moving King
							board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							this.has_moved = true;
							//Moving Rook
							board[8][fileToNum('d')] = board[8][fileToNum('a')];
							board[8][fileToNum('a')] = null;
							board[8][fileToNum('d')].rank = 8;
							board[8][fileToNum('d')].file = 'd';
							((Rook) board[8][fileToNum('d')]).has_moved = true;
							checkForCheck(board);
							return;
						} //Invalid move
						else {
							throw new IllegalArgumentException();
						}
					}
					
				}
				
				//Checking to see if path clear
				if(board[move_rank][fileToNum(move_file)] != null) {
					//Checking if same side
					if(board[move_rank][fileToNum(move_file)].white_side == this.white_side) {
						throw new IllegalArgumentException();
					}
				}
				//Moving on the board copy
				board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
				board_copy[this.rank][fileToNum(this.file)] = null;
				//Checking for checks
				if(putsOwnKingInCheck(board_copy)) {
					throw new IllegalArgumentException();
				}
				//Moving to position
				board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
				board[this.rank][fileToNum(this.file)] = null;
				this.rank = move_rank;
				this.file = move_file;
				this.has_moved = true;
				checkForCheck(board);
				return;
			}
		}
		
		/**
		 * check checks the positions in the inputed board that this piece can capture in to see if the opponent side's King is there. In the {@link #checkForCheck(Piece[][]) checkForCheck} method if check returns true, checkmate is called. Check does not call checkmate itself since the check may be in a temporary board used in testing like the ones used in {@link #allValidMoves() allValidMoves} method
		 * 
		 * @author Jake
		 * @param board - the board the that check is being tested in. This can be the global board or a temporary board created in putsOwnKingInCheck for instance
		 * @return true if it is checking the opponent King, false otherwise
		 */
		public boolean check(Piece[][] board) {
			
			Piece temp;
			
			//Check above
			if(this.rank != 8) {
				
				//Check up center
				temp = board[this.rank + 1][fileToNum(this.file)];
				if(temp != null) {
					//if it's the opposite king
					if(temp instanceof King) {
						//checkmate((char) temp.file, temp.rank);
						return true;
					}
					//Check up right
					if(this.file != 'h') {
						temp = board[this.rank + 1][fileToNum((char) (this.file + 1))];
						//if it's the opposite king
						if(temp instanceof King) {
							//checkmate((char) temp.file, temp.rank);
							return true;
						}
					}
					//Check up left
					if(this.file != 'a') {
						temp = board[this.rank + 1][fileToNum((char) (this.file - 1))];
						//if it's the opposite king
						if(temp instanceof King) {
							//checkmate((char) temp.file, temp.rank);
							return true;
						}
					}
				}
				
			}
			//Check below
			if(this.rank != 1) {
				
				//Check down center
				temp = board[this.rank - 1][fileToNum(this.file)];
				if(temp != null) {
					//if it's the opposite king
					if(temp instanceof King) {
						//checkmate((char) temp.file, temp.rank);
						return true;
					}
					//Check down right
					if(this.file != 'h') {
						temp = board[this.rank - 1][fileToNum((char) (this.file + 1))];
						//if it's the opposite king
						if(temp instanceof King) {
							//checkmate((char) temp.file, temp.rank);
							return true;
						}
					}
					//Check down left
					if(this.file != 'a') {
						temp = board[this.rank - 1][fileToNum((char) (this.file - 1))];
						//if it's the opposite king
						if(temp instanceof King) {
							//checkmate((char) temp.file, temp.rank);
							return true;
						}
					}
				}
			}
			//Check right
			if(this.file != 'h') {
				temp = board[this.rank][fileToNum((char) (this.file + 1))];
				//if it's the opposite king
				if(temp instanceof King) {
					//checkmate((char) temp.file, temp.rank);
					return true;
				}
			}
			//Check left
			if(this.file != 'a') {
				temp = board[this.rank][fileToNum((char) (this.file - 1))];
				//if it's the opposite king
				if(temp instanceof King) {
					//checkmate((char) temp.file, temp.rank);
					return true;
				}
			}
			
			return false;
		}
		
		/**
		 * Returns all the positions that this piece can move to as an ArrayList of Strings. Each String is 2 characters consisting of a file and a rank
		 * 
		 * @author Jake
		 * @return ArrayList of strings of FileRank format of all the places this piece can move to
		 */
		public ArrayList<String> allValidMoves() {
			
			//System.out.println("TESTING: King Valid Moves");
			
			ArrayList<String> result = new ArrayList<String>();
			String move;
			Piece[][] board_copy;
			final boolean side_playing = white_moves;
			
			//System.out.println("TESTING: side_playing: white_moves: " + side_playing);
			
			//Checking Above
			if(this.rank != 8) {
				//Checking up-center
				if(board[this.rank + 1][fileToNum(this.file)] == null) {
					//Making sure it doesn't put itself in check
					board_copy = copyBoard();
					board_copy[this.rank + 1][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank + 1][fileToNum(this.file)].rank = board_copy[this.rank + 1][fileToNum(this.file)].rank + 1;
					
					//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
					
					white_moves = board_copy[this.rank + 1][fileToNum(this.file)].white_side;
					
					//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
					
					
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat((this.rank + 1) + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[this.rank + 1][fileToNum(this.file)].white_side != this.white_side) {
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank + 1][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank + 1][fileToNum(this.file)].rank = board_copy[this.rank + 1][fileToNum(this.file)].rank + 1;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank + 1][fileToNum(this.file)].white_side;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(this.file).concat((this.rank + 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
				//Checking up-right
				if(this.file != 'h') {
					if(board[this.rank + 1][fileToNum((char) (this.file + 1))] == null) {
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank + 1][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].rank + 1;
						board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].file + 1);
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].white_side;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char ) (this.file + 1)).concat((this.rank + 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank + 1][fileToNum((char) (this.file + 1))].white_side != this.white_side) {
							//Making sure it doesn't put itself in check
							board_copy = copyBoard();
							board_copy[this.rank + 1][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].rank + 1;
							board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].file + 1);
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file + 1))].white_side;
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file + 1)).concat((this.rank + 1) + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
				//Checking up-left
				if(this.file != 'a') {
					if(board[this.rank + 1][fileToNum((char) (this.file - 1))] == null) {
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank + 1][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].rank + 1;
						board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].file - 1);
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].white_side;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char ) (this.file - 1)).concat((this.rank + 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank + 1][fileToNum((char) (this.file - 1))].white_side != this.white_side) {
							//Making sure it doesn't put itself in check
							board_copy = copyBoard();
							board_copy[this.rank + 1][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].rank + 1;
							board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].file - 1);
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							white_moves = board_copy[this.rank + 1][fileToNum((char) (this.file - 1))].white_side;
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file - 1)).concat((this.rank + 1) + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
			}
			//Checking Below
			if(this.rank != 1) {
				
				//System.out.println("TESTING: Checking valid King moves below");
				
				//Checking down-center
				if(board[this.rank - 1][fileToNum(this.file)] == null) {
					
					//Making sure it doesn't put itself in check
					board_copy = copyBoard();
					board_copy[this.rank - 1][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank - 1][fileToNum(this.file)].rank = board_copy[this.rank - 1][fileToNum(this.file)].rank - 1;
					
					//System.out.println("TESTING: below king: WHITE_SIDE = TRUE?: " + white_moves);
					
					white_moves = board_copy[this.rank - 1][fileToNum(this.file)].white_side;
					
					//System.out.println("TESTING: below king set: WHITE_SIDE = TRUE?: " + white_moves);
					
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf(this.file).concat((this.rank - 1) + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[this.rank - 1][fileToNum(this.file)].white_side != this.white_side) {
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank - 1][fileToNum(this.file)] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank - 1][fileToNum(this.file)].rank = board_copy[this.rank - 1][fileToNum(this.file)].rank - 1;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank - 1][fileToNum(this.file)].white_side;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf(this.file).concat((this.rank - 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
				//Checking down-right
				if(this.file != 'h') {
					if(board[this.rank - 1][fileToNum((char) (this.file + 1))] == null) {
						
						//System.out.println("TESTING: Checking valid King moves below - no piece down-right");
						
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank - 1][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].rank - 1;
						board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].file + 1);
						
						//System.out.println("TESTING: BOARD_COPY");
						//display(board_copy);
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].white_side;
						
						//System.out.println("TESTING: After setting: WHITE_SIDE = TRUE?: " + white_moves);
						if(!putsOwnKingInCheck(board_copy)) {
							//System.out.println("TESTING: AM I GETTING HERE?????");
							move = String.valueOf((char ) (this.file + 1)).concat((this.rank - 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank - 1][fileToNum((char) (this.file + 1))].white_side != this.white_side) {
							//Making sure it doesn't put itself in check
							board_copy = copyBoard();
							board_copy[this.rank - 1][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].rank - 1;
							board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].file + 1);
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file + 1))].white_side;
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file + 1)).concat((this.rank - 1) + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
				//Checking down-left
				if(this.file != 'a') {
					if(board[this.rank - 1][fileToNum((char) (this.file - 1))] == null) {
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank - 1][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].rank - 1;
						board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].file - 1);
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].white_side;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char ) (this.file - 1)).concat((this.rank - 1) + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
					else {
						if(board[this.rank - 1][fileToNum((char) (this.file - 1))].white_side != this.white_side) {
							//Making sure it doesn't put itself in check
							board_copy = copyBoard();
							board_copy[this.rank - 1][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].rank = board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].rank - 1;
							board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].file - 1);
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							white_moves = board_copy[this.rank - 1][fileToNum((char) (this.file - 1))].white_side;
							
							//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
							
							if(!putsOwnKingInCheck(board_copy)) {
								move = String.valueOf((char) (this.file - 1)).concat((this.rank - 1) + "");
								result.add(move);
							}
							white_moves = side_playing;
						}
					}
				}
			}
			//Checking Right
			if(this.file != 'h') {
				if(board[this.rank][fileToNum((char) (this.file + 1))] == null) {
					//Making sure it doesn't put itself in check
					board_copy = copyBoard();
					board_copy[this.rank][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank][fileToNum((char) (this.file + 1))].file + 1);
					
					//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
					
					white_moves = board_copy[this.rank][fileToNum((char) (this.file + 1))].white_side;
					
					//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
					
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf((char) (this.file + 1)).concat(this.rank + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[this.rank][fileToNum((char) (this.file + 1))].white_side != this.white_side) {
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank][fileToNum((char) (this.file + 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank][fileToNum((char) (this.file + 1))].file = (char) (board_copy[this.rank][fileToNum((char) (this.file + 1))].file + 1);
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank][fileToNum((char) (this.file + 1))].white_side;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file + 1)).concat(this.rank + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
			}
			//Checking Left
			if(this.file != 'a') {
				if(board[this.rank][fileToNum((char) (this.file - 1))] == null) {
					//Making sure it doesn't put itself in check
					board_copy = copyBoard();
					board_copy[this.rank][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					board_copy[this.rank][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank][fileToNum((char) (this.file - 1))].file - 1);
					
					//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
					
					white_moves = board_copy[this.rank][fileToNum((char) (this.file - 1))].white_side;
					
					//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
					
					if(!putsOwnKingInCheck(board_copy)) {
						move = String.valueOf((char) (this.file - 1)).concat(this.rank + "");
						result.add(move);
					}
					white_moves = side_playing;
				}
				else {
					if(board[this.rank][fileToNum((char) (this.file - 1))].white_side != this.white_side) {
						//Making sure it doesn't put itself in check
						board_copy = copyBoard();
						board_copy[this.rank][fileToNum((char) (this.file - 1))] = board_copy[this.rank][fileToNum(this.file)];
						board_copy[this.rank][fileToNum(this.file)] = null;
						board_copy[this.rank][fileToNum((char) (this.file - 1))].file = (char) (board_copy[this.rank][fileToNum((char) (this.file - 1))].file - 1);
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						white_moves = board_copy[this.rank][fileToNum((char) (this.file - 1))].white_side;
						
						//System.out.println("TESTING: WHITE_SIDE = TRUE?: " + white_moves);
						
						if(!putsOwnKingInCheck(board_copy)) {
							move = String.valueOf((char) (this.file - 1)).concat(this.rank + "");
							result.add(move);
						}
						white_moves = side_playing;
					}
				}
			}
			
			return result;
		}
	}
	
	
}