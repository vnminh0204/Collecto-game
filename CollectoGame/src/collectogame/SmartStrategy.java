package collectogame;

import java.util.ArrayList;
import java.util.List;

public class SmartStrategy implements Strategy {
	
	/**
	 * @invariant name - The name of the strategy
	 * @invariant myCollection - the balls we collected
	 * @invariant opponentCollection - the balls the opponent collected
	 * @invariant boardCollections - the balls left on the board
	 * @invariant copyBoard - a copy of the board
	 * @invariant allChoice - the possible moves
	 * @invariant twothreeMove - move that gets two or three balls, because they have one 
	 * @invariant fourMove1 - you get 4 balls, so can get up to 2 colors (first color)
	 * @invariant fourmove2 - you get 4 balls, so can get up to 2 colors (second color)
	 * @invariant gainScore - the score you gain for the move
	 * @invariant gainBalls - the balls you gain for the move
	 * @invariant opponentBestGain - The max amount of balls the opponent can get after this move
	 * @invariant opponentGainScore - The max score the opponent can get after this move
	 */
	private String name = "Smart";
	private int[] myCollection, boardCollection, opponentCollection;
	Board copyBoard;
	private int myScore, oppScore;
	private List<Integer> allChoice;
	private Ball[] twothreeMove;
	private Ball[] fourMove1, fourMove2;
	private int[] gainScore, gainBalls, opponentBestGain, opponentGainScore;
	
	/**
	 * This method will find the best move of opponent 
	 * if we choose this move's indice (for both single or double move).
	 * @param board - the current board
	 * @param moveIndice - the tested move's indice
	 * @requires board != null;
	 * @requires isValidMove(moveIndice) == true;
	 */
	public void updateOppBestMove(Board board, int moveIndice) {
		int maxGainBalls = -1;
		int bestScore = -1;
		if (board.isStillPossibleSingleMove()) {
			for (int indice = 0; indice <= 27; indice++) {
				if (board.isValidSingleMove(indice)) {
					Board testBoard = board.deepCopy();
					testBoard.makeSingleMove(indice);
					List<Ball> collectList = testBoard.collectBall();
					int possibleGain = collectList.size();
					
					int oppGainScore = 0;
					int[] oppGainCollection = new int[7];
					oppGainCollection = this.getCopyCollection(opponentCollection);
					for (Ball ball : collectList) {
						oppGainCollection[ball.convertTo()]++;
			    	}
					int oppNewScore = this.calculateScore(oppGainCollection);
					oppGainScore = oppNewScore - oppScore;
					
					if (possibleGain > maxGainBalls) {
						maxGainBalls = possibleGain;
						bestScore = oppGainScore;
					} else if ((possibleGain == maxGainBalls) && (oppGainScore > bestScore)) {
						maxGainBalls = possibleGain;
						bestScore = oppGainScore;
					}
				}
			}
			
			opponentGainScore[moveIndice] = bestScore;
			opponentBestGain[moveIndice] =  maxGainBalls;
		} else if (board.isStillPossibleDoubleMove()) {
			for (int indice = 0; indice < 28 * 28; indice++) {
				int move1 = indice / 28;
				int move2 = indice % 28;
				if (board.isValidDoubleMove(move1, move2)) {
					Board testBoard = board.deepCopy();
					testBoard.makeDoubleMove(move1, move2);
					List<Ball> collectList = testBoard.collectBall();
					int possibleGain = collectList.size();
					
					int oppGainScore = 0;
					int[] oppGainCollection = new int[7];
					oppGainCollection = this.getCopyCollection(opponentCollection);
					for (Ball ball : collectList) {
						oppGainCollection[ball.convertTo()]++;
			    	}
					int oppNewScore = this.calculateScore(oppGainCollection);
					oppGainScore = oppNewScore - oppScore;
					
					if (possibleGain > maxGainBalls) {
						maxGainBalls = possibleGain;
						bestScore = oppGainScore;
					} else if ((possibleGain == maxGainBalls) && (oppGainScore > bestScore)) {
						maxGainBalls = possibleGain;
						bestScore = oppGainScore;
					}
				}
			}
			
			opponentGainScore[moveIndice] = bestScore;
			opponentBestGain[moveIndice] =  maxGainBalls;
		}
		
	}
    
	/**
	 * With the list of all possible single move.
	 * this method will calculate your possible gained balls and scores for that move
	 * it also classify the move get 4 ball (with 1-2 color)
	 * and the move get 2-3 ball (with only 1 color)
	 * because these are the most common situation
	 */
	public void calculateAllSingleMove() {
		gainScore = new int[28];
		gainBalls = new int[28];
		opponentBestGain = new int[28];
		opponentGainScore = new int[28];
		twothreeMove = new Ball[28];
		fourMove1 = new Ball[28];
		fourMove2 = new Ball[28];
 		for (int moveIndice : allChoice) {
			
			Board testMoveBoard = copyBoard.deepCopy();
			testMoveBoard.makeSingleMove(moveIndice);
			
			List<Ball> possibleCollectedBalls = testMoveBoard.collectBall();
			
			gainBalls[moveIndice] = possibleCollectedBalls.size();
			
			if (possibleCollectedBalls.size() <= 3) {
				twothreeMove[moveIndice] = possibleCollectedBalls.get(0);
			}
			
			if (possibleCollectedBalls.size() == 4) {
				fourMove1[moveIndice] = possibleCollectedBalls.get(0);
				fourMove2[moveIndice] = possibleCollectedBalls.get(1);
				for (Ball ball : possibleCollectedBalls) {
					if (!ball.equals(fourMove1[moveIndice])) {
						fourMove2[moveIndice] = ball;
					}
				}
			}
			
			int[] possibleNewCollection = new int[7];
			possibleNewCollection = getCopyCollection(this.myCollection);
			
			for (Ball ball : possibleCollectedBalls) {
				possibleNewCollection[ball.convertTo()]++;
			}
			
			gainScore[moveIndice] = calculateScore(possibleNewCollection) - myScore;
			
			this.updateOppBestMove(testMoveBoard, moveIndice);
		}
	}
	
	/**
	 * With the list of all possible double move.
	 * this method will calculate your possible gained balls and scores for that move
	 * it also classify the move get 4 ball (with 1-2 color)
	 * and the move get 2-3 ball (with only 1 color)
	 * because these are the most common situation
	 */
	public void calculateAllDoubleMove() {
		gainScore = new int[28 * 28];
		gainBalls = new int[28 * 28];
		opponentBestGain = new int[28 * 28];
		opponentGainScore = new int[28 * 28];
		twothreeMove = new Ball[28 * 28];
		fourMove1 = new Ball[28 * 28];
		fourMove2 = new Ball[28 * 28];
		
 		for (int moveIndice : allChoice) {
			
			Board testMoveBoard = copyBoard.deepCopy();
			int move1 = moveIndice / 28;
			int move2 = moveIndice % 28;
			testMoveBoard.makeDoubleMove(move1, move2);
			
			List<Ball> possibleCollectedBalls = testMoveBoard.collectBall();
			
			gainBalls[moveIndice] = possibleCollectedBalls.size();
			
			if (possibleCollectedBalls.size() <= 3) {
				twothreeMove[moveIndice] = possibleCollectedBalls.get(0);
			}
			
			if (possibleCollectedBalls.size() == 4) {
				fourMove1[moveIndice] = possibleCollectedBalls.get(0);
				fourMove2[moveIndice] = possibleCollectedBalls.get(1);
				for (Ball ball : possibleCollectedBalls) {
					if (!ball.equals(fourMove1[moveIndice])) {
						fourMove2[moveIndice] = ball;
					}
				}
			}
			
			int[] possibleNewCollection = new int[7];
			possibleNewCollection = getCopyCollection(this.myCollection);
			
			for (Ball ball : possibleCollectedBalls) {
				possibleNewCollection[ball.convertTo()]++;
			}
			
			gainScore[moveIndice] = calculateScore(possibleNewCollection) - myScore;
			
			this.updateOppBestMove(testMoveBoard, moveIndice);
		}
	}
	


	/**
	 * This method is choose the best move among those move that get 2,3,4 balls.
	 * @param list - the list of move get 2,3,4 balls
	 * @return the priority list of move
	 */
	public List<Integer> sortPriority(List<Integer> list) {
		List<Integer> priorList = new ArrayList<Integer>();
		List<Integer> oppGain1 = new ArrayList<Integer>();
		List<Integer> oppGain2 = new ArrayList<Integer>();
		List<Integer> oppGain3 = new ArrayList<Integer>();
		//opponentGainScore[move]
		// 1 move gain 2 score
		for (int move : list) {
			if (gainScore[move] == 2) {
				if  (opponentGainScore[move] == 0) {
					priorList.add(move);
				} else if (opponentGainScore[move] == 1) {
					oppGain1.add(move);
				} else if (opponentGainScore[move] == 2) {
					oppGain2.add(move);
				} else if (opponentGainScore[move] == 3) {
					oppGain3.add(move);
				}
			}
		}
		priorList.addAll(oppGain1);
		oppGain1.clear();

		// 2 4 ball at opp = 2, you = 4
		for (int move : list) {
			if (gainBalls[move] == 4) {
				Ball ball = fourMove1[move];
				if ((!priorList.contains(move)) && (opponentCollection[ball.convertTo()] == 2) 
						&& (myCollection[ball.convertTo()] == 4)) {
					if  (opponentGainScore[move] == 0) {
						priorList.add(move);
					} else if (opponentGainScore[move] == 1) {
						oppGain1.add(move);
					} else if (opponentGainScore[move] == 2) {
						oppGain2.add(move);
					} else if (opponentGainScore[move] == 3) {
						oppGain3.add(move);
					}
				} else {
					ball = fourMove2[move];
					if ((opponentCollection[ball.convertTo()] == 2) 
						&& (myCollection[ball.convertTo()] == 4)) {
						if  (opponentGainScore[move] == 0) {
							priorList.add(move);
						} else if (opponentGainScore[move] == 1) {
							oppGain1.add(move);
						} else if (opponentGainScore[move] == 2) {
							oppGain2.add(move);
						} else if (opponentGainScore[move] == 3) {
							oppGain3.add(move);
						}
					}
				}
			}
		}
		priorList.addAll(oppGain1);
		oppGain1.clear();
		
		// 3 4 ball at opp = 4, you = 2
		for (int move : list) {
			if (gainBalls[move] == 4) {
				Ball ball = fourMove1[move];
				if ((!priorList.contains(move)) && (opponentCollection[ball.convertTo()] == 4) 
						&& (myCollection[ball.convertTo()] == 2)) {
					if  (opponentGainScore[move] == 0) {
						priorList.add(move);
					} else if (opponentGainScore[move] == 1) {
						oppGain1.add(move);
					} else if (opponentGainScore[move] == 2) {
						oppGain2.add(move);
					} else if (opponentGainScore[move] == 3) {
						oppGain3.add(move);
					}
				} else {
					ball = fourMove2[move];
					if ((opponentCollection[ball.convertTo()] == 4) 
						&& (myCollection[ball.convertTo()] == 2)) {
						if  (opponentGainScore[move] == 0) {
							priorList.add(move);
						} else if (opponentGainScore[move] == 1) {
							oppGain1.add(move);
						} else if (opponentGainScore[move] == 2) {
							oppGain2.add(move);
						} else if (opponentGainScore[move] == 3) {
							oppGain3.add(move);
						}
					}
				}
			}
		}

		
		// 4 3 ball at opp = 4, you = 0 
		for (int move : list) {
			if (gainBalls[move] == 3) {
				Ball ball = twothreeMove[move];
				if ((!priorList.contains(move)) && (opponentCollection[ball.convertTo()] == 4) 
						&& (myCollection[ball.convertTo()] == 0)) {
					if  (opponentGainScore[move] == 0) {
						priorList.add(move);
					} else if (opponentGainScore[move] == 1) {
						oppGain1.add(move);
					} else if (opponentGainScore[move] == 2) {
						oppGain2.add(move);
					} else if (opponentGainScore[move] == 3) {
						oppGain3.add(move);
					}
				}
			}
		}
		priorList.addAll(oppGain1);
		oppGain1.clear();
		
		// 5 2 ball at opp = 2, you = 4 
		for (int move : list) {
			if (gainBalls[move] == 2) {
				Ball ball = twothreeMove[move];
				if ((!priorList.contains(move)) && (opponentCollection[ball.convertTo()] == 2) 
						&& (myCollection[ball.convertTo()] == 4)) {
					if  (opponentGainScore[move] == 0) {
						priorList.add(move);
					} else if (opponentGainScore[move] == 1) {
						oppGain1.add(move);
					} else if (opponentGainScore[move] == 2) {
						oppGain2.add(move);
					} else if (opponentGainScore[move] == 3) {
						oppGain3.add(move);
					}
				}
			}
		}
		priorList.addAll(oppGain1);
		oppGain1.clear();
		
		// 6 3 ball at you = 0
		for (int move : list) {
            if (gainBalls[move] == 3) {
                Ball ball = twothreeMove[move];
                if ((!priorList.contains(move)) && (myCollection[ball.convertTo()] == 0)) {
                    if  (opponentGainScore[move] == 0) {
                        priorList.add(move);
                    } else if (opponentGainScore[move] == 1) {
                        oppGain1.add(move);
                    } else if (opponentGainScore[move] == 2) {
                        oppGain2.add(move);
                    } else if (opponentGainScore[move] == 3) {
                        oppGain3.add(move);
                    }
                }
            }
        }

		
		// 7 2 ball at opp = 4, you = 2 
		for (int move : list) {
			if (gainBalls[move] == 2) {
				Ball ball = twothreeMove[move];
				if ((!priorList.contains(move)) && (opponentCollection[ball.convertTo()] == 4) 
						&& (myCollection[ball.convertTo()] == 2)) {
					if  (opponentGainScore[move] == 0) {
						priorList.add(move);
					} else if (opponentGainScore[move] == 1) {
						oppGain1.add(move);
					} else if (opponentGainScore[move] == 2) {
						oppGain2.add(move);
					} else if (opponentGainScore[move] == 3) {
						oppGain3.add(move);
					}
				}
			}
		}

		//8 gain 1 score but opp gain 0 scores
		for (int move : list) {
			if ((!priorList.contains(move)) && (gainScore[move] == 1)) {
				if  (opponentGainScore[move] == 0) {
					priorList.add(move);
				} else if (opponentGainScore[move] == 1) {
					oppGain1.add(move);
				} else if (opponentGainScore[move] == 2) {
					oppGain2.add(move);
				} else if (opponentGainScore[move] == 3) {
					oppGain3.add(move);
				}
			}
		}
		priorList.addAll(oppGain1);
		oppGain1.clear();
		
		//9 other moves except opp 6 you 0
		for (int move : list) {
			if (!priorList.contains(move)) {
				Ball ball;
				if (gainBalls[move] == 4) {
					ball = fourMove1[move];
					if (opponentCollection[ball.convertTo()] != 6) {
						ball = fourMove2[move];
						if (opponentCollection[ball.convertTo()] != 6) {
							if  (opponentGainScore[move] == 0) {
								priorList.add(move);
							} else if (opponentGainScore[move] == 1) {
								oppGain1.add(move);
							} else if (opponentGainScore[move] == 2) {
								oppGain2.add(move);
							} else if (opponentGainScore[move] == 3) {
								oppGain3.add(move);
							}
						}
					}
				} else {
					ball = twothreeMove[move];
					if (opponentCollection[ball.convertTo()] != 6) {
						if  (opponentGainScore[move] == 0) {
							priorList.add(move);
						} else if (opponentGainScore[move] == 1) {
							oppGain1.add(move);
						} else if (opponentGainScore[move] == 2) {
							oppGain2.add(move);
						} else if (opponentGainScore[move] == 3) {
							oppGain3.add(move);
						}
					}
				}
			}
		}
		priorList.addAll(oppGain1);
		oppGain1.clear();
		
		//10 your opponent get 1 ball higher
		for (int move : list) {
			if (!priorList.contains(move)) {
				if  (opponentGainScore[move] == 0) {
					priorList.add(move);
				} else if (opponentGainScore[move] == 1) {
					oppGain1.add(move);
				} else if (opponentGainScore[move] == 2) {
					oppGain2.add(move);
				} else if (opponentGainScore[move] == 3) {
					oppGain3.add(move);
				}
			}
		}
		priorList.addAll(oppGain1);
		priorList.addAll(oppGain2);
		priorList.addAll(oppGain3);
		return priorList;
	}
	
	/**
	 * This method will divide the move get 2,3,4 ball into 2 sublist.
	 * @param list - list of moves that get 2,3,4 balls
	 * @requires list != null
	 * @return 1st list if move you get more or equal balls || 2nd list if you get less balls
	 */
	public int handle(List<Integer> list) {
		List<Integer> equalList = new ArrayList<Integer>();
		List<Integer> lessList = new ArrayList<Integer>();
		for (int move : list) {
			if (gainBalls[move] >= opponentBestGain[move]) {
				equalList.add(move);
			} else {
				lessList.add(move);
			}
		}
		List<Integer> subList;
		if (equalList.size() > 0) {
			subList = new ArrayList<>(equalList.subList(0, equalList.size()));
			equalList = sortPriority(subList);
			return equalList.get(0);
		} else {
			subList = new ArrayList<>(lessList.subList(0, lessList.size()));
			lessList = sortPriority(subList);
			return lessList.get(0);
		}
	}
	
	/**
	 * Find the best move among the list of move you have more ball than opponent.
	 * @param list - the list of move that player will gain more balls
	 * @requires list != null;
	 * @return the best move among move that you have more ball
	 */
	public int findBestMoreMove(List<Integer> list) {
		int maxBall = -1;
		int maxDifferGain = -1;
		int bestMove = -1;
		int maxScore = -1;
		int leastOppGainScore = 100;
		for (int move : list) {
			if (gainBalls[move] <= opponentBestGain[move]) {
				continue;
			}
			
			if ((gainBalls[move] - opponentBestGain[move]) > maxDifferGain) {
				bestMove = move;
				maxBall = gainBalls[move];
				maxDifferGain = gainBalls[move] - opponentBestGain[move];
				maxScore = gainScore[move];
				leastOppGainScore = opponentGainScore[move];
			} else if (((gainBalls[move] - opponentBestGain[move]) == maxDifferGain) 
					&& (gainBalls[move] > maxBall)) {
				bestMove = move;
				maxBall = gainBalls[move];
				maxDifferGain = gainBalls[move] - opponentBestGain[move];
				maxScore = gainScore[move];
				leastOppGainScore = opponentGainScore[move];
			} else if (((gainBalls[move] - opponentBestGain[move]) == maxDifferGain) 
				&& (gainBalls[move] == maxBall) 
				&& (gainScore[move] > maxScore)) {
				bestMove = move;
				maxBall = gainBalls[move];
				maxDifferGain = gainBalls[move] - opponentBestGain[move];
				maxScore = gainScore[move];
				leastOppGainScore = opponentGainScore[move];
			} else if (((gainBalls[move] - opponentBestGain[move]) == maxDifferGain) 
					&& (gainBalls[move] == maxBall) 
					&& (gainScore[move] == maxScore)
					&& (opponentGainScore[move] < leastOppGainScore)) {
				bestMove = move;
				maxBall = gainBalls[move];
				maxDifferGain = gainBalls[move] - opponentBestGain[move];
				maxScore = gainScore[move];
			}
		}
		return bestMove;
	}

	/**
	 * Find the best move among the list of move you have less balls than opponent.
	 * @param list the list of move that player will gain less balls
	 * @requires list != null;
	 * @return the best move among move that you have less ball
	 */
	public int findBestLessMove(List<Integer> list) {
		int leastDiff = 100;
		int leastOppGainScore = 100;
		int leastBall = 100;
		int maxScore = -1;
		int bestMove = -1;
		for (int move : list) {
			if ((opponentBestGain[move] - gainBalls[move]) < leastDiff) {
				leastDiff = opponentBestGain[move] - gainBalls[move];
				leastBall = opponentBestGain[move];
				bestMove = move;
				maxScore = gainScore[move];
				leastOppGainScore = opponentGainScore[move];
			} else if (((opponentBestGain[move] - gainBalls[move]) == leastDiff) 
					&& (gainScore[move] > maxScore)) {
				leastDiff = opponentBestGain[move] - gainBalls[move];
				leastBall = opponentBestGain[move];
				bestMove = move;
				maxScore = gainScore[move];
				leastOppGainScore = opponentGainScore[move];
			} else if (((opponentBestGain[move] - gainBalls[move]) == leastDiff) 
					&& (gainScore[move] == maxScore) 
					&& (opponentBestGain[move] < leastBall)) {
				leastDiff = opponentBestGain[move] - gainBalls[move];
				leastBall = opponentBestGain[move];
				bestMove = move;
				maxScore = gainScore[move];
				leastOppGainScore = opponentGainScore[move];
			} else if (((opponentBestGain[move] - gainBalls[move]) == leastDiff) 
					&& (gainScore[move] == maxScore) 
					&& (opponentBestGain[move] == leastBall)
					&& (opponentGainScore[move] < leastOppGainScore)) {
				leastDiff = opponentBestGain[move] - gainBalls[move];
				leastBall = opponentBestGain[move];
				bestMove = move;
				maxScore = gainScore[move];
				leastOppGainScore = opponentGainScore[move];
			}
		}
		
		return bestMove;
	}
	
	@Override
	public int[] determineMove(Board board, int[] collection) {
		int[] choice = new int[0];
		allChoice = new ArrayList<Integer>();
		myCollection = this.getCopyCollection(collection);
		myScore = this.calculateScore(collection);
		copyBoard = board.deepCopy();
		calculateCollection();
		oppScore = this.calculateScore(opponentCollection);
		if (board.isStillPossibleSingleMove()) {
			choice = new int[1];
			//get All possible moves
			for (int indice = 0; indice <= 27; indice++) {
				if (copyBoard.isValidSingleMove(indice)) {
					allChoice.add(indice);
				}
			}
			
			// set random move to make sure at least 1 move will be return
			int random = (int) (Math.random() * allChoice.size());
			choice[0] = allChoice.get(random);
			
			//calculate possible score, balls and opponent's best move
			calculateAllSingleMove();
			for (int move : allChoice) {
				System.out.println("With move " + move + " you gain " + gainBalls[move] 
						+ " balls and " + gainScore[move] + " score, but opponent can gain " 
						+ opponentBestGain[move] + "balls and " 
						+ opponentGainScore[move] + "scores");
			}
			
			//in case no equal or more move
			int possibleMove = findBestLessMove(allChoice);
			List<Integer> subChoice = new ArrayList<Integer>();
			for (Integer move : allChoice) {
				if (opponentBestGain[move] - gainBalls[move] <= 2) {
					subChoice.add(move);
				}
			}
			if (subChoice.size() > 0) {
				allChoice = new ArrayList<Integer>(subChoice.subList(0, subChoice.size()));
			
				//check if there is a move you will gain more than opponent
				int bestMoreMove =  findBestMoreMove(allChoice);
			
				if (bestMoreMove != -1) {
					possibleMove = bestMoreMove;
				} else {
			
					// next filer if a move gain >=5 => take it
					int maxBall = -1;
					int bestMove = -1;
					int maxScore = -1;
					for (int move : allChoice) {
						if (gainBalls[move] > maxBall) {
							bestMove = move;
							maxBall = gainBalls[move];
							maxScore = gainScore[move];
						} else if ((gainBalls[move] == maxBall) && (gainScore[move] > maxScore)) {
							bestMove = move;
							maxBall = gainBalls[move];
							maxScore = gainScore[move];
						}
					}
				
					if ((bestMove != -1) && (maxBall >= 5)) {
						possibleMove = bestMove;
					} else {
						// method to handle 2,3,4 ball
						possibleMove = handle(allChoice);
					}
				}
			}
			choice[0] = possibleMove;
			System.out.println("Best choice is" + choice[0]);
		
		} else if (board.isStillPossibleDoubleMove()) {
			choice = new int[2];
			for (int indice = 0; indice < 28 * 28; indice++) {
				int move1 = indice / 28;
				int move2 = indice % 28;
				if (copyBoard.isValidDoubleMove(move1, move2)) {
					allChoice.add(indice);
				}
			}
			calculateAllDoubleMove();
			for (int move : allChoice) {
				int move1 = move / 28;
				int move2 = move % 28;
				System.out.println("With move " + move1 + "-> " 
						+ move2 + " you gain " + gainBalls[move] 
						+ " balls and " + gainScore[move] + " score, but opponent can gain " 
						+ opponentBestGain[move] + "balls and " 
						+ opponentGainScore[move] + "scores");
			}
			int random = (int) (Math.random() * allChoice.size());
			int possibleMove = allChoice.get(random);
			
			List<Integer> subChoice = new ArrayList<Integer>();
			for (Integer move : allChoice) {
				if (opponentBestGain[move] - gainBalls[move] <= 2) {
					subChoice.add(move);
				}
			}
			
			if (subChoice.size() > 0) {
				allChoice = new ArrayList<Integer>(subChoice.subList(0, subChoice.size()));
			
				//check if there is a move you will gain more than opponent
				int bestMoreMove =  findBestMoreMove(allChoice);
			
				if (bestMoreMove != -1) {
					possibleMove = bestMoreMove;
				} else {
			
					// next filer if a move gain >=5 => take it
					int maxBall = -1;
					int bestMove = -1;
					int maxScore = -1;
					for (int move : allChoice) {
						if (gainBalls[move] > maxBall) {
							bestMove = move;
							maxBall = gainBalls[move];
							maxScore = gainScore[move];
						} else if ((gainBalls[move] == maxBall) && (gainScore[move] > maxScore)) {
							bestMove = move;
							maxBall = gainBalls[move];
							maxScore = gainScore[move];
						}
					}
				
					if ((bestMove != -1) && (maxBall >= 5)) {
						possibleMove = bestMove;
					} else {
						// method to handle 2,3,4 ball
						possibleMove = handle(allChoice);
					}
				}
			}
			System.out.println("My best move is " + possibleMove);
			choice[0] = possibleMove / 28;
			choice[1] = possibleMove % 28;
		}
        return choice;
	}
	
	/**
	 * Return the deep copy of given collection.
	 * @param collection - the player's ball collection
	 * @return the copy of given collection.
	 */
	public int[] getCopyCollection(int[] collection) {
		int[] copy = new int[7];
		for (int i = 1; i < 7; i++) {
			copy[i] = collection[i] + 0;
		}
		return copy;
	}
	
	/**
	 * Calculate the opponent's collection. 
	 * based on player and the number of ball in board.
	 */
	public void calculateCollection() {
		boardCollection = new int[7];
		opponentCollection = new int[7];
		for (int i = 0; i < Board.DIM; i++) {
			for (int j = 0; j < Board.DIM; j++) {
				boardCollection[copyBoard.getField(i, j).convertTo()]++;
			}
		}
		
		for (int i = 1; i < 7; i++) {
			opponentCollection[i] = 8 - boardCollection[i] - myCollection[i];
		}
	}
	
	/**
	 * Calculate the score of given collection.
	 * @param collection - the list of balls
	 * @return the score of the given collection
	 */
    public int calculateScore(int[] collection) {
    	int newScore = 0;
    	for (int i = 1; i < collection.length; i++) {
    		newScore = newScore + (collection[i] / 3);
    	}
    	return newScore;
    }
    
	@Override
	public String getName() {
		return this.name;
	}
}
