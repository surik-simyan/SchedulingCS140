import java.util.Arrays;

public class Autoassociator {
	private int weights[][];
	private int trainingCapacity;
	
	public Autoassociator(CourseArray courses) {
		int numCourses = courses.length() - 1;
		weights = new int[numCourses][numCourses];
		trainingCapacity = (int) (0.138 * numCourses); // Hebbian rule capacity
		for (int i = 0; i < numCourses; i++) {
			for (int j = i; j < numCourses; j++) {
				weights[i][j] = weights[j][i] = 0;
			}
		}
	}
	
	public int getTrainingCapacity() {
		return trainingCapacity;
	}
	
	public void training(int pattern[]) {
		for (int i = 0; i < pattern.length; i++) {
			for (int j = i; j < pattern.length; j++) {
				weights[i][j] += pattern[i] * pattern[j];
				weights[j][i] = weights[i][j];
			}
		}
	}
	
	public int unitUpdate(int neurons[]) {
		int index = (int) (Math.random() * neurons.length);
		unitUpdate(neurons, index);
		return index;
	}
	
	public void unitUpdate(int neurons[], int index) {
		int activation = 0;
		for (int i = 0; i < neurons.length; i++) {
			activation += weights[index][i] * neurons[i];
		}
		neurons[index] = (activation >= 0) ? 1 : -1;
	}
	
	public void chainUpdate(int neurons[], int steps) {
		for (int i = 0; i < steps; i++) {
			unitUpdate(neurons);
		}
	}
	
	public void fullUpdate(int neurons[]) {
		int[] previousState = neurons.clone();
		do {
			previousState = neurons.clone();
			chainUpdate(neurons, neurons.length);
		} while (!Arrays.equals(neurons, previousState));
	}
}
