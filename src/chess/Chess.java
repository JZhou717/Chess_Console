package chess;

import java.util.Scanner;

//NEED TO DO:

//Currently do not account for revealed checks, will patch
//Checkmate not implemented
//Stalemate not implemented
//Test check with all pieces
//Fix Draw to just draw after request
	//Execute move first, then draw, just remember that if the move results in checkmate, end the game, otherwise, print draw and the game is over

//Fix comments







/**
 * This will be Jake Zhou and Thomas Heck's Chess Project
 * 
 * @author Jake Zhou, Thomas Heck
 * @since 2019-03-01
 *
 */

public class Chess {
	/**
	 * This is the data structure that we will be using. It is a 9x8 2D Array of Pieces.
	 * We are ignoring row [0] in all our code so that the input rank correlates to the position in the 2D array without us having to subtract one from it.
	 * Blank spots are by default NULL.
	 */
	static Piece[][] board = new Piece[9][8];
	/**
	 * done is a flag that tracks to see if the game is over.
	 * white_moves is a flag that is true if it is white's turn, false if it is black's turn
	 * white_enpassant is usually 0 unless white had just taken a pawn double step in his last move, in which case it stores the file of that pawn as a char
	 * black_enpassant is the same for black
	 */
	static boolean done = false;
	static boolean white_moves = true;
	static char white_enpassant = 0;
	static char black_enpassant = 0;
	
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
			
			//Checking for stalemate
			stalemate();
			
			while(!valid_input) {
				
				
				//Resets the enpassant variables since enpassant is valid only immediately after the opponent has moved two steps with a pawn. Since white_enpassant stores a value only if white played a double step last turn and it is again white's turn, we reset it. Same thing for black
				if(white_moves) {
					white_enpassant = 0;
					System.out.print("\nWhite's move: ");
				}
				else {
					black_enpassant = 0;
					System.out.print("\nBlack's move: ");
				}
				
				/*Code that reads input. All input converted to lowercase*/
				input = in.nextLine().toLowerCase();
				
				//Checking for done conditions (i.e., draw, resignation)
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
						piece.check();
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
						piece.check();
					}
				} catch (IllegalArgumentException e) {
					System.out.println("\nIllegal move, try again");
					//e.printStackTrace();
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
				System.out.println("\ndraw");
				in.close();
				System.exit(0);
			}
			
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
	 * display() views the board 2D array and shows all the pieces that are in the board. Any position that is NULL instead of containing a piece will display blank or as ##.
	 * This method will also print out the file numbers and the rank letters
	 * @author Jake
	 */
	public static void display() {
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
	 * This helper method takes in the character for the file from the input and returns a number 0-7. Useful when referencing a position in the board 2D array
	 * @param file - a character that is denotes the file in the input
	 * @return int value between 0-7 associated with file character if successful. Throws an illegal argument exception if input is not valid
	 * @author Jake
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
			default: throw new IllegalArgumentException("Invalid File Char to convert to Num");
		}
		
	}
	
	/**
	 * This helper method takes an int and returns a char for the file associated. Useful when referencing a position in the board 2D array
	 * @param file - an int value associated with a column of the board 2D array
	 * @return char value of the file name a-h. Throws an illegal argument exception if input is not valid
	 * @author Jake
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
	 * This helper functions creates a copy of the current board. This copy of the board is used in the putsOwnKingInCheck() method
	 * @param none
	 * @return a copy of the current board
	 */
	public static Piece[][] copyBoard() {
		Piece[][] board_copy = new Piece[9][8];
		
		for(int r = 1; r < 9; r++) {
			for(int f = 0; f < 8; f++) {
				board_copy[r][f] = board[r][f];
			}
		}
		
		return board_copy;
	}
	
	/**
	 * This help method takes in a temporary board and checks to see if the King of the side playing is in check in this temporary board
	 * This is a method that is called by every piece before the move is committed since it has to ensure that the move does not place the same side's King in check
	 * @param takes in a board after a move has been made
	 * @return returns true if the input board has the current side's King in chess, meaning the last move was illegal, otherwise returns false
	 */
	public static boolean putsOwnKingInCheck(Piece[][] board) {
		
		Piece temp;
		
		//Going through all the ranks
		for(int r = 1; r < 9; r++) {
			//Going through all the files
			for(int f = 0; f < 8; f++) {
				//If there is a piece in this spot
				if(board[r][f] != null) {
					temp = board[r][f];
					//If the piece is an opponent
					if(temp.white_side != white_moves) {
						if(temp.check()) {
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
	 * This method is ran by each piece's check function, if there is a check on the King, check to see if there is a checkmate. If there is, end game, otherwise just print check
	 */
	public static void checkmate() {
		System.out.println("\nCheck");
	}
	
	/**
	 * Before every move is played, this method checks to see if the current side is in stalemate
	 */
	public static void stalemate() {
		
	}
	
	/**
	 * This is the abstract class that all pieces will extend. Each piece must store a String of its name, a char of its file (a-h), an int of its rank (1-8), and a boolean for its side: white_side == true for white false for black
	 * Each piece must also implement the move method and the check method.
	 * @param String input - the input string from the user. Each piece will check the input itself to see if valid for that piece
	 * @author Jake
	 *
	 */
	public static abstract class Piece {
		String name;
		char file;
		int rank;
		boolean white_side;
		abstract void move(String move_to) throws IllegalArgumentException;
		abstract boolean check();
	}
	
	/**
	 * This section contains all the pieces. White and Black Pawns are separate pieces since they move differently, but all other pieces move the same regardless of which side
	 * All pieces take an input string of the position they are to move to, checking to see if the path is clear and if the input is valid
	 * All pieces must be created with a starting position inputed as a string with FileRank
	 * Pawns have a separate method for promotion that occurs when the reached the opposite end of the board
	 *
	 */
	public static class White_Pawn extends Piece{
		
		public White_Pawn(char file, int rank) {
			this.name = "wp";
			this.file = file;
			this.rank = rank;
		}
		
		void move(String move_to) throws IllegalArgumentException{
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
							//Checking on board copy first
							int saved_rank = this.rank;
							char saved_file = this.file;
							//Removing the black pawn
							board_copy[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								this.rank = saved_rank;
								this.file = saved_file;
								throw new IllegalArgumentException();
							}
							
							//Actual move
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board[this.rank + 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							if(this.check()) {
								checkmate();
							}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank + 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
					return;
				} else if (move_file == (this.file - 1) && move_rank == (this.rank + 1)) {
					//System.out.println("Pawn moving up-left");
					//Checking to see there is a piece there
					if(board[this.rank + 1][fileToNum(this.file) - 1] == null) {
						//Checking for Enpassant
						if(move_file == black_enpassant && move_rank == 6) {
							//Checking on board copy first
							int saved_rank = this.rank;
							char saved_file = this.file;
							board_copy[this.rank][fileToNum(this.file) - 1] = null;
							
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								this.rank = saved_rank;
								this.file = saved_file;
								throw new IllegalArgumentException();
							}
							
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) - 1] = null;
							//Moving
							board[this.rank + 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							if(this.check()) {
								checkmate();
							}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank + 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
					return;
				} else {
					//System.out.println("Something wrong");
					throw new IllegalArgumentException();
				}
			} else {
				if(move_rank == this.rank + 1) {
					//Checking to see if path clear
					if(board[this.rank + 1][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving piece
					board[this.rank + 1][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
					return;
				} else if(move_rank == this.rank + 2 && this.rank == 2) {
					//Checking to see if path clear
					if(board[this.rank + 1][fileToNum(this.file)] != null 
					|| board[this.rank + 2][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving piece
					board[this.rank + 2][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					white_enpassant = this.file;
					if(this.check()) {
						checkmate();
					}
					return;
				} else {
					throw new IllegalArgumentException();
				}
			}

		}
		
		boolean check() {
			
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
		
		void promote(String promote_to) throws IllegalArgumentException{
			if(promote_to.equals("r")) {
				Piece newPiece = new Rook(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} 
			else if(promote_to.equals("n")) {
				Piece newPiece = new Knight(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} 
			else if(promote_to.equals("b")) {
				Piece newPiece = new Bishop(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} 
			else if(promote_to.equals("q")) {
				Piece newPiece = new Queen(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				newPiece.white_side = true;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public static class Black_Pawn extends Piece {
		public Black_Pawn(char file, int rank) {
			this.name = "bp";
			this.file = file;
			this.rank = rank;
		}
		void move(String move_to)  throws IllegalArgumentException{
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
							//Checking on board copy first
							int saved_rank = this.rank;
							char saved_file = this.file;
							board_copy[this.rank][fileToNum(this.file) + 1] = null;
							
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								this.rank = saved_rank;
								this.file = saved_file;
								throw new IllegalArgumentException();
							}
							
							//Removing the white pawn
							board[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board[this.rank - 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							if(this.check()) {
								checkmate();
							}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank - 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
					return;
				} else if (move_file == (this.file - 1) && move_rank == (this.rank - 1)) {
					//System.out.println("Pawn moving down-left");
					//Checking to see there is a piece there
					if(board[this.rank - 1][fileToNum(this.file) - 1] == null) {
						//Checking for Enpassant
						if(move_file == white_enpassant && move_rank == 3) {
							//Checking on board copy first
							int saved_rank = this.rank;
							char saved_file = this.file;
							board_copy[this.rank][fileToNum(this.file) - 1] = null;
							
							board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
							board_copy[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							if(putsOwnKingInCheck(board_copy)) {
								this.rank = saved_rank;
								this.file = saved_file;
								throw new IllegalArgumentException();
							}
							
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) - 1] = null;
							//Moving
							board[this.rank - 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							if(this.check()) {
								checkmate();
							}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving
					board[this.rank - 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving piece
					board[this.rank - 1][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
					return;
				} //Moving 2 spaces
				else if(move_rank == rank - 2 && this.rank == 7) {
					//Checking to see if path clear
					if(board[this.rank - 1][fileToNum(this.file)] != null 
					|| board[this.rank - 2][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving piece
					board[this.rank - 2][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					black_enpassant = this.file;
					if(this.check()) {
						checkmate();
					}
					return;
				} 
				else {
					throw new IllegalArgumentException();
				}
			}

		}
		
		boolean check() {
			
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
		
		void promote(String promote_to) throws IllegalArgumentException{
			if(promote_to.equals("r")) {
				Piece newPiece = new Rook(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} 
			else if(promote_to.equals("n")) {
				Piece newPiece = new Knight(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} 
			else if(promote_to.equals("b")) {
				Piece newPiece = new Bishop(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} 
			else if(promote_to.equals("q")) {
				Piece newPiece = new Queen(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				newPiece.white_side = false;
				board[this.rank][fileToNum(this.file)] = newPiece;
				newPiece.check();
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public static class Rook extends Piece {
		
		boolean has_moved = false;
		
		public Rook(char file, int rank) {
			this.name = "R";
			this.file = file;
			this.rank = rank;
		}
		void move(String move_to)  throws IllegalArgumentException{

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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.has_moved = true;
					if(this.check()) {
						checkmate();
					}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.has_moved = true;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[this.rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.has_moved = true;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[this.rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.has_moved = true;
					if(this.check()) {
						checkmate();
					}
					return;
				}
			}
			
		}
		
		boolean check() {
			
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
	}
	
	public static class Knight extends Piece {
		
		public Knight(char file, int rank) {
			this.name = "N";
			this.file = file;
			this.rank = rank;
		}
		
		void move(String move_to)  throws IllegalArgumentException{
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
		
		boolean check() {
			
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
	}
	
	public static class Bishop extends Piece {
		
		public Bishop(char file, int rank) {
			this.name = "B";
			this.file = file;
			this.rank = rank;
		}
		
		void move(String move_to)  throws IllegalArgumentException{
			
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
					return;
				} //Something's wrong
				else {
					//System.out.println("SOMETHING WRONG");
					throw new IllegalArgumentException();
				}
			}
		}
		
		boolean check() {
			
			Piece temp;
			
			//Check up-right
			for(int r = this.rank + 1; r < 9; r++) {
				int f = fileToNum((char) (this.file + (r - this.rank)));
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
				int f = fileToNum((char) (this.file - (r - this.rank)));
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
				int f = fileToNum((char) (this.file + (this.rank - r)));
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
				int f = fileToNum((char) (this.file - (this.rank - r)));
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
	}
	
	public static class Queen extends Piece {
		
		public Queen(char file, int rank) {
			this.name = "Q";
			this.file = file;
			this.rank = rank;
		}
		
		void move(String move_to)  throws IllegalArgumentException{
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					//Checking on board copy first
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
					int saved_rank = this.rank;
					char saved_file = this.file;
					
					board_copy[move_rank][fileToNum(move_file)] = board_copy[this.rank][fileToNum(this.file)];
					board_copy[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					if(putsOwnKingInCheck(board_copy)) {
						this.rank = saved_rank;
						this.file = saved_file;
						throw new IllegalArgumentException();
					}
					
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					if(this.check()) {
						checkmate();
					}
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
		
		boolean check() {
			
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
				int f = fileToNum((char) (this.file + (r - this.rank)));
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
				int f = fileToNum((char) (this.file - (r - this.rank)));
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
				int f = fileToNum((char) (this.file + (this.rank - r)));
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
				int f = fileToNum((char) (this.file - (this.rank - r)));
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
	}
	
	public static class King extends Piece {
		
		boolean has_moved = false;
		
		public King(char file, int rank) {
			this.name = "K";
			this.file = file;
			this.rank = rank;
		}
		
		void move(String move_to)  throws IllegalArgumentException{
			
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
							if(this.check()) {
								checkmate();
							}
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
							if(this.check()) {
								checkmate();
							}
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
							if(this.check()) {
								checkmate();
							}
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
							if(this.check()) {
								checkmate();
							}
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
				if(this.check()) {
					checkmate();
				}
				return;
			}
		}
		
		boolean check() {
			
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
			temp = board[this.rank][fileToNum((char) (this.file + 1))];
			//if it's the opposite king
			if(temp instanceof King) {
				//checkmate((char) temp.file, temp.rank);
				return true;
			}
			//Check left
			temp = board[this.rank][fileToNum((char) (this.file - 1))];
			//if it's the opposite king
			if(temp instanceof King) {
				//checkmate((char) temp.file, temp.rank);
				return true;
			}
			
			return false;
		}
	}
	
	
}