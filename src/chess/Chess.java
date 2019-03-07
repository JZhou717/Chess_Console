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
				//System.out.println("loc: " + loc + ". move_to: " + move_to + ". third: " + third);
				
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
				if(board[rank][fileToNum(file)] == null) {
					System.out.println("\nNo piece at indicated starting position.");
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
					System.out.println("\nInvalid file and/or rank for move to position.");
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
						check(file, rank);
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
						check(file, rank);
					}
				} catch (IllegalArgumentException e) {
					System.out.println("\nIllegal move, try again");
					e.printStackTrace();
					asked_for_draw = false;
					continue;
				}
				/*End of part 2*/
				
				//If nothing went wrong, we continue
				valid_input = true;
				System.out.println();
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
		/*for(int j = 0; j < 8; j++) {
			board[2][j] = new White_Pawn(numToFile(j), 2);
			board[2][j].white_side = true;
		}*/
		
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
			default: throw new IllegalArgumentException("Invalid File Char to conver to Num");
		}
		
	}
	
	/**
	 * This helper method takes an int and returns a String of one character for the file associated. Useful when referencing a position in the board 2D array
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
	 * This method is ran after every move to see if the opponent is now in check
	 * @param char file, int rank - the file and rank of the piece that just moved. Checking to see if this piece is checking the opponent King
	 * @return returns true if opponent in check, false otherwise
	 */
	public static boolean check(char file, int rank) {
		return false;
	}
	
	/**
	 * This method is ran by the check function, if there is a check on the King, check to see if there is a checkmate. If there is, end game
	 */
	public static void checkmate() {
		
	}
	
	/**
	 * This is the abstract class that all pieces will extend. Each piece must store a String of its name, a char of its file (a-h), an int of its rank (1-8), and a boolean for its side: white_side == true for white false for black
	 * Each piece must also implement the move method.
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
			int move_rank = Integer.parseInt(move_to.substring(1,2));
			
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
							board[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board[this.rank + 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							check(this.file, this.rank);
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
					//Moving
					board[this.rank + 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
					return;
				} else if (move_file == (this.file - 1) && move_rank == (this.rank + 1)) {
					//System.out.println("Pawn moving up-left");
					//Checking to see there is a piece there
					if(board[this.rank + 1][fileToNum(this.file) - 1] == null) {
						//Checking for Enpassant
						if(move_file == black_enpassant && move_rank == 6) {
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) - 1] = null;
							//Moving
							board[this.rank + 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							check(this.file, this.rank);
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
					//Moving
					board[this.rank + 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
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
					//Moving piece
					board[this.rank + 1][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					check(this.file, this.rank);
					return;
				} else if(move_rank == rank + 2 && this.rank == 2) {
					//Checking to see if path clear
					if(board[this.rank + 1][fileToNum(this.file)] != null 
					|| board[this.rank + 2][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					//Moving piece
					board[this.rank + 2][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					white_enpassant = this.file;
					check(this.file, this.rank);
					return;
				} else {
					throw new IllegalArgumentException();
				}
			}

		}
		
		void promote(String promote_to) throws IllegalArgumentException{
			if(promote_to.equals("r")) {
				Piece newPiece = new Rook(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} 
			else if(promote_to.equals("n")) {
				Piece newPiece = new Knight(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} 
			else if(promote_to.equals("b")) {
				Piece newPiece = new Bishop(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} 
			else if(promote_to.equals("q")) {
				Piece newPiece = new Queen(this.file, this.rank);
				newPiece.name = "w" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} else {
				throw new IllegalArgumentException("Error. Invalid input for promote");
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
			
			int move_rank = Integer.parseInt(move_to.substring(1,2));
			if(move_file != this.file) {
				//System.out.println("Pawn moving to different file");		
				//If the designated move is not in this file, then we have to see if it is an attempt to capture
				if(move_file == (this.file + 1) && move_rank == (this.rank - 1)) {
					//System.out.println("Pawn moving down-right");
					//Checking to see there is a piece there
					if(board[this.rank - 1][fileToNum(this.file) + 1] == null) {
						//Checking for Enpassant
						if(move_file == white_enpassant && move_rank == 3) {
							//Removing the white pawn
							board[this.rank][fileToNum(this.file) + 1] = null;
							//Moving
							board[this.rank - 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							check(this.file, this.rank);
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
					//Moving
					board[this.rank - 1][fileToNum(this.file) + 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
					return;
				} else if (move_file == (this.file - 1) && move_rank == (this.rank - 1)) {
					//System.out.println("Pawn moving down-left");
					//Checking to see there is a piece there
					if(board[this.rank - 1][fileToNum(this.file) - 1] == null) {
						//Checking for Enpassant
						if(move_file == white_enpassant && move_rank == 3) {
							//Removing the black pawn
							board[this.rank][fileToNum(this.file) - 1] = null;
							//Moving
							board[this.rank - 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
							board[this.rank][fileToNum(this.file)] = null;
							this.rank = move_rank;
							this.file = move_file;
							check(this.file, this.rank);
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
					//Moving
					board[this.rank - 1][fileToNum(this.file) - 1] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
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
					//Moving piece
					board[this.rank - 1][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					check(this.file, this.rank);
					return;
				} //Moving 2 spaces
				else if(move_rank == rank - 2 && this.rank == 7) {
					//Checking to see if path clear
					if(board[this.rank - 1][fileToNum(this.file)] != null 
					|| board[this.rank - 2][fileToNum(this.file)] != null) {
						throw new IllegalArgumentException();
					}
					//Moving piece
					board[this.rank - 2][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					black_enpassant = this.file;
					check(this.file, this.rank);
					return;
				} 
				else {
					throw new IllegalArgumentException();
				}
			}

		}
		
		void promote(String promote_to) throws IllegalArgumentException{
			if(promote_to.equals("r")) {
				Piece newPiece = new Rook(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} 
			else if(promote_to.equals("n")) {
				Piece newPiece = new Knight(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} 
			else if(promote_to.equals("b")) {
				Piece newPiece = new Bishop(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} 
			else if(promote_to.equals("q")) {
				Piece newPiece = new Queen(this.file, this.rank);
				newPiece.name = "b" + newPiece.name;
				board[this.rank][fileToNum(this.file)] = newPiece;
				check(this.file, this.rank);
			} else {
				throw new IllegalArgumentException("Error. Invalid input for promote");
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
			int move_rank = Integer.parseInt(move_to.substring(1,2));
			
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
					//Moving to position
					board[move_rank][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					this.has_moved = true;
					check(this.file, this.rank);
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
					//Moving to position
					board[move_rank][fileToNum(this.file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					this.has_moved = true;
					check(this.file, this.rank);
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
					//Moving to position
					board[this.rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					this.has_moved = true;
					check(this.file, this.rank);
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
					//Moving to position
					board[this.rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					this.has_moved = true;
					check(this.file, this.rank);
					return;
				}
			}
			
		}
	}
	
	public static class Knight extends Piece {
		public Knight(char file, int rank) {
			this.name = "N";
			this.file = file;
			this.rank = rank;
		}
		void move(String move_to)  throws IllegalArgumentException{
			//FOR EACH PIECE TO MOVE, 
			//CHECK TO SEE IF THIS PIECE BELONGS TO THE CURRENT SIDE PLAYING USING ITS NAME AND white_moves VARIABLE
			//CHECK TO SEE IF THE MOVE_TO POSITION IS VALID FOR THIS PIECE
			//CHECK TO SEE IF THE PATH IS CLEAR FOR THIS MOVEMENT
			//IF VALID, SET THE POSITION IN THE BOARD TO THIS PIECE, CHANGE THIS PIECE'S file and rank, AND SET PREVIOUS POSITION TO NULL
			return;
		}
	}
	
	public static class Bishop extends Piece {
		public Bishop(char file, int rank) {
			this.name = "B";
			this.file = file;
			this.rank = rank;
		}
		void move(String move_to)  throws IllegalArgumentException{
			//System.out.println("TESTING IN BISHOP MOVE");
			//Trying to move opponent's piece
			if(this.white_side != white_moves) {
				throw new IllegalArgumentException();
			}
			char move_file = move_to.toLowerCase().charAt(0);
			int move_rank = Integer.parseInt(move_to.substring(1,2));
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
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
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
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
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
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
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
					//Moving to position
					board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
					board[this.rank][fileToNum(this.file)] = null;
					this.rank = move_rank;
					this.file = move_file;
					check(this.file, this.rank);
					return;
				} //Something's wrong
				else {
					//System.out.println("SOMETHING WRONG");
					throw new IllegalArgumentException();
				}
			}
		}
	}
	
	public static class Queen extends Piece {
		public Queen(char file, int rank) {
			this.name = "Q";
			this.file = file;
			this.rank = rank;
		}
		void move(String move_to)  throws IllegalArgumentException{
			
			
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
			int move_rank = Integer.parseInt(move_to.substring(1,2));
			
			//If trying to move to the same spot
			if(move_file == this.file && move_rank == this.rank) {
				throw new IllegalArgumentException();
			} //Trying to move more than two squares away
			else if(Math.abs(move_rank - this.rank) + Math.abs(fileToNum(move_file) - fileToNum(this.file)) > 2) {
				throw new IllegalArgumentException();
			} //Valid move
			else {
				//Creating a copy of the board to brute force test if this move will put King in check
				Piece[][] board_copy = new Piece[9][8];
				for(int r = 1; r < 9; r++) {
					for(int f = 0; f < 8; f++) {
						board_copy[r][f] = board[r][f];
					}
				}
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
					//If it's the white king
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
							if(board[1][fileToNum('h')].name != "wR") {
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
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Checking for g1 space
							board_copy[1][fileToNum('g')] = board_copy[1][fileToNum('f')];
							board_copy[1][fileToNum('f')] = null;
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Moving King
							board[move_rank][move_file] = board[this.rank][fileToNum(this.file)];
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
							check('f', 1);
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
							if(board[1][fileToNum('a')].name != "wR") {
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
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Checking for c1 space
							board_copy[1][fileToNum('c')] = board_copy[1][fileToNum('d')];
							board_copy[1][fileToNum('d')] = null;
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Moving King
							board[move_rank][move_file] = board[this.rank][fileToNum(this.file)];
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
							check('d', 1);
							return;
						} //Invalid move
						else {
							throw new IllegalArgumentException();
						}
						
						
					} //If it is the black king
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
							if(board[8][fileToNum('h')].name != "bR") {
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
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Checking for g1 space
							board_copy[8][fileToNum('g')] = board_copy[1][fileToNum('f')];
							board_copy[8][fileToNum('f')] = null;
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Moving King
							board[move_rank][move_file] = board[this.rank][fileToNum(this.file)];
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
							check('f', 8);
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
							if(board[8][fileToNum('a')].name != "bR") {
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
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Checking for c8 space
							board_copy[8][fileToNum('c')] = board_copy[8][fileToNum('d')];
							board_copy[8][fileToNum('d')] = null;
							for(int r = 1; r < 9; r++) {
								for(int f = 0; f < 8; f++) {
									//If there is a piece in this spot
									if(board_copy[r][f] != null) {
										//If the piece is an opponent
										if(board_copy[r][f].white_side != this.white_side) {
											check(numToFile(f), r);
										}
									}
								}
							}
							//Moving King
							board[move_rank][move_file] = board[this.rank][fileToNum(this.file)];
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
							check('d', 8);
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
				for(int r = 1; r < 9; r++) {
					for(int f = 0; f < 8; f++) {
						//If there is a piece in this spot
						if(board_copy[r][f] != null) {
							//If the piece is an opponent
							if(board_copy[r][f].white_side != this.white_side) {
								check(numToFile(f), r);
							}
						}
					}
				}
				//Moving to position
				board[move_rank][fileToNum(move_file)] = board[this.rank][fileToNum(this.file)];
				board[this.rank][fileToNum(this.file)] = null;
				this.rank = move_rank;
				this.file = move_file;
				this.has_moved = true;
				check(this.file, this.rank);
				return;
			}
		}
	}
	
	
}