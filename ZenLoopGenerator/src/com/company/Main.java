package com.company;

import java.util.Random;
import java.util.Scanner;

public class Main {

    //these numbers determine the density of loops.
    public static final int NUMERATOR = 2;
    public static final int DENOMINATOR = 3;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        ZenBoard board = new ZenBoard();
        System.out.println("How big do you want to make your board?");
        System.out.print("Length: ");
        int length = s.nextInt();
        System.out.print("Width: ");
        int width = s.nextInt();
        board.generate(length, width);
        //System.out.println("Solution:");
        //System.out.println(board.toString());
        board.scramble();
        System.out.println(board.toString());
        boolean done = false;
        int row;
        int col;
        while (!done) {
            System.out.println("Make your move");
            System.out.print("row: ");
            row = s.nextInt();
            if (row > board.board.length) {
                System.out.print("Please insert a number within range: 0-");
                System.out.println(board.board.length);
                continue;
            }
            System.out.print("col: ");
            col = s.nextInt();
            if (col > board.board[0].length) {
                System.out.print("Please insert a number within range: 0-");
                System.out.println(board.board[0].length);
                continue;
            }
            done = board.play(row, col);
            System.out.println(board.toString());
        }
        System.out.println("Congratulations! You solved the board!");
    }

    //returns true (specified fraction)-th of the time
    public static boolean chance(int numerator, int denominator) {
        Random r = new Random();
        int temp = r.nextInt(denominator);
        return temp < numerator;
    }

    public static class ZenBoard {
        ZenTile[][] board;

        public ZenBoard() {

        }

        /*
        public ZenBoard(ZenTile[][] t) {
            board = new ZenTile[t.length][t[0].length];
            for (int i = 0; i < t.length; i++) {
                System.arraycopy(t[i], 0, board[i], 0, t[0].length);
            }
        }*/

        //when the user makes their move
        public boolean play(int row, int col) {
            board[row][col].spin(1);
            return isFinished();
        }

        //this generates the solution
        public void generate(int length, int width) {
            if (length >= 100 || width >= 100) {
                throw new RuntimeException("Because I don't feel like formatting it right now, I won't let you make three-digit unit long boards.");
            }
            board = new ZenTile[length][width];
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < width; j++) {
                    board[i][j] = new ZenTile();
                    //up is true if there's a tile above it that has a line facing down
                    board[i][j].up = i != 0 && board[i - 1][j].down;
                    //left is true if there's a tile to the left that has a line facing right
                    board[i][j].left = j != 0 && board[i][j - 1].right;
                    //right has a 2/3 chance of being true if it's not at the right edge.
                    board[i][j].right = (j != (width - 1)) && chance(NUMERATOR, DENOMINATOR);
                    //down has a 2/3 chance of being true if it's not at the bottom edge.
                    board[i][j].down = (i != (length - 1)) && chance(NUMERATOR, DENOMINATOR);
                }
            }
        }

        //this scrambles it
        public void scramble () {
            Random r = new Random();
            int temp;
            //spin each tile 1-4 times
            for (int i = 0; i < board[0].length; i++) {
                for (int j = 0; j < board.length; j++) {
                    temp = r.nextInt(4);
                    board[i][j].spin(temp);
                }
            }
        }

        //checks this after every play
        private boolean isFinished() {
            //iterate through every tile
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    //if the tile has a line facing down...
                    if (board[i][j].down) {
                        //and there is no tile below or no line facing up on the tile below...
                        if (i == board.length - 1 || !board[i+1][j].up) {
                            return false;
                        }
                    } else { //if there is no line facing down...
                        //and there's a tile below that has a line facing up...
                        if (i != board.length - 1 && board[i+1][j].up) {
                            return false;
                        }
                    }
                    //repeat the previous stuff, but check to see if right lines connect with the left ones.
                    if (board[i][j].right) {
                        if (j == board[0].length - 1 || !board[i][j+1].left) {
                            return false;
                        }
                    } else {
                        if (j != board[0].length - 1 && board[i][j+1].left) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            int temp;
            s.append(' ');
            //column labels
            for (int j = 0; j < board[0].length; j++) {
                s.append(' ');
                s.append(j);
            }
            s.append('\n');
            for (int i = 0; i < board.length; i++) {
                //row labels
                temp = i;
                s.append(temp);
                if (temp < 10) {
                    s.append(' ');
                }
                //tiles
                for (int j = 0; j < board[0].length; j++) {
                    s.append(board[i][j].toChar());
                    s.append(' ');
                }
                s.append('\n');
            }
            return s.toString();
        }
    }

    public static class ZenTile {
        //these booleans determine what kind of tile it is.
        boolean up;
        boolean right;
        boolean down;
        boolean left;

        public ZenTile() { }

        //imagine if there directions are matchsticks.
        //Pick up the first matchstick you find and place it at the next available space.
        //repeat until you get back to the first space.
        //should theoretically work for any number of matchsticks
        public void spin(int intensity) {
            boolean carryLine = false;
            for (int i = 0; i < intensity; i++) {
                if (up) {
                    up = false;
                    carryLine = true;
                }
                if (right) {
                    if (!carryLine) {
                        right = false;
                        carryLine = true;
                    }
                } else if (carryLine) {
                    right = true;
                    carryLine = false;
                }
                if (down) {
                    if (!carryLine) {
                        down = false;
                        carryLine = true;
                    }
                } else if (carryLine) {
                    down = true;
                    carryLine = false;
                }
                if (left) {
                    if (!carryLine) {
                        left = false;
                        carryLine = true;
                    }
                } else if (carryLine) {
                    left = true;
                    carryLine = false;
                }
                if (carryLine) {
                    up = true;
                }
            }
        }

        //represent each possible combination of directions with a box-drawing character.
        public char toChar() {
            char temp;
            int state = 0;
            if (up) {
                state++;
            }
            if (right) {
                state += 2;
            }
            if (down) {
                state += 4;
            }
            if (left) {
                state += 8;
            }
            switch(state) {
                case 0:
                    temp = ' ';
                    break;
                case 1:
                    temp = '╵';
                    break;
                case 2:
                    temp = '╶';
                    break;
                case 3:
                    temp = '└';
                    break;
                case 4:
                    temp = '╷';
                    break;
                case 5:
                    temp = '│';
                    break;
                case 6:
                    temp = '┌';
                    break;
                case 7:
                    temp = '├';
                    break;
                case 8:
                    temp = '╴';
                    break;
                case 9:
                    temp = '┘';
                    break;
                case 10:
                    temp = '─';
                    break;
                case 11:
                    temp = '┴';
                    break;
                case 12:
                    temp = '┐';
                    break;
                case 13:
                    temp = '┤';
                    break;
                case 14:
                    temp = '┬';
                    break;
                case 15:
                    temp = '┼';
                    break;
                default:
                    temp = 'e';
            }
            return temp;
        }
    }
}