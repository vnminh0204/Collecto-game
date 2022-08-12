package collectogame;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class NaiveStrategy implements Strategy {
	private String name = "Naive";
	
	//Simple strategy that picks a random possible valid move
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int[] determineMove(Board board, int[] collection) {
		int[] choice = new int[0];
		
		if (board.isStillPossibleSingleMove()) {
			List<Integer> allChoice = new ArrayList<Integer>();
			
			for (int indice = 0; indice <= 27; indice++) {
				if (board.isValidSingleMove(indice)) {
					allChoice.add(indice);
				}
			}
			int random = (int) (Math.random() * allChoice.size());
			choice = new int[1];
			choice[0] = allChoice.get(random);
		} else if (board.isStillPossibleDoubleMove()) {
			List<SimpleEntry<Integer, Integer>> allChoice = 
					new ArrayList<SimpleEntry<Integer, Integer>>();
			
			for (int indice1 = 0; indice1 <= 27; indice1++) {
				for (int indice2 = 0; indice2 <= 27; indice2++) {
					if (board.isValidDoubleMove(indice1, indice2)) {
						SimpleEntry<Integer, Integer> possibleChoice 
								= new SimpleEntry<>(indice1, indice2);
			
						allChoice.add(possibleChoice);
					}
				}
			}
			int random = (int) (Math.random() * allChoice.size());
			SimpleEntry<Integer, Integer> randomChoice = allChoice.get(random);
			choice = new int[2];
			choice[0] = randomChoice.getKey();
			choice[1] = randomChoice.getValue();
		}
		return choice;
	}

}
