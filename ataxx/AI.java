/* Skeleton code copyright (C) 2008, 2022 Paul N. Hilfinger and the
 * Regents of the University of California.  Do not distribute this or any
 * derivative work without permission. */

package ataxx;

import java.util.ArrayList;
import java.util.Random;

import static ataxx.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Manu John
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. SEED is used to initialize
     *  a random-number generator for use in move computations.  Identical
     *  seeds produce identical behaviour. */
    AI(Game game, PieceColor myColor, long seed) {
        super(game, myColor);
        _random = new Random(seed);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getMove() {
        if (!getBoard().canMove(myColor())) {
            game().reportMove(Move.pass(), myColor());
            return "-";
        }
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        game().reportMove(move, myColor());
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(getBoard());
        _lastFoundMove = null;
        if (myColor() == RED) {
            minMax(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            minMax(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to the findMove method
     *  above. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove, int sense,
                       int alpha, int beta) {
        /* We use WINNING_VALUE + depth as the winning value so as to favor
         * wins that happen sooner rather than later (depth is larger the
         * fewer moves have been made. */
        if (depth == 0 || board.getWinner() != null) {
            return staticScore(board, WINNING_VALUE + depth);
        }

        Move best;
        best = null;
        int bestScore = 0;

        if (sense == 1) {
            bestScore = -INFTY;
            for (Move m : findLegalMoves(board)) {
                Board b = new Board(board);
                if (b.legalMove(m)) {
                    b.makeMove(m);
                    int response = minMax(b, depth - 1, false, -1, alpha, beta);
                    if (response > bestScore) {
                        bestScore = response;
                        best = m;
                        alpha = Math.max(alpha, bestScore);
                        if (alpha >= beta) {
                            return bestScore;
                        }
                    }
                }
            }
        } else if (sense == -1) {
            bestScore = INFTY;
            for (Move m : findLegalMoves(board)) {
                Board b = new Board(board);
                if (b.legalMove(m)) {
                    b.makeMove(m);
                    int response = minMax(b, depth - 1, false, 1, alpha, beta);
                    if (response < bestScore) {
                        bestScore = response;
                        best = m;
                        alpha = Math.min(alpha, bestScore);
                        if (alpha >= beta) {
                            return bestScore;
                        }
                    }
                }
            }
        }



        if (saveMove) {
            _lastFoundMove = best;
        }
        return bestScore;
    }


    /** Return a heuristic value for BOARD.  This value is +- WINNINGVALUE in
     *  won positions, and 0 for ties. */
    private int staticScore(Board board, int winningValue) {
        PieceColor winner = board.getWinner();
        if (winner != null) {
            return switch (winner) {
            case RED -> winningValue;
            case BLUE -> -winningValue;
            default -> 0;
            };
        }

        return board.redPieces() - board.bluePieces();
    }

    /**
     iterates through 7*7 board then it checks if the.
     given piece is the current player's piece.
     then it iterates through if it is.
     possible to jump or extend for that piece--basically.
     it finds what the legal moves are.
     @param board
     @return ArrayList
     */
    private ArrayList<Move> findLegalMoves(Board board) {
        ArrayList<Move> legalMoves = new ArrayList<>();
        for (char i = 'a'; i <= 'g'; i++) {
            for (char j = '1'; j <= '7'; j++) {
                if (board.get(i, j).compareTo(board.whoseMove()) == 0) {
                    for (int m = -2; m < 3; m++) {
                        for (int n = -2; n < 3; n++) {
                            if (!(m == 0 && n == 0)) {
                                if (board.legalMove(i, j, (char)
                                        (i + m), (char) (j + n))) {
                                    legalMoves.add(Move.move(i, j,
                                            (char) (i + m), (char) (j + n)));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (legalMoves.isEmpty()) {
            legalMoves.add(Move.pass());
        }
        return legalMoves;
    }

    /** Pseudo-random number generator for move computation. */
    private Random _random = new Random();
}
