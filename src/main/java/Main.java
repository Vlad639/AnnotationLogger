public class Main {

	@Trace
	public static void emptyMethod(){
	}

	@Trace
	public static int sum(int a, int b){
		return a + b;
	}

	@Trace
	private static void method1000() throws InterruptedException {
		Thread.sleep(1000);
	}

	public static void main(String[] args) throws InterruptedException {
		LoggerMethods.loggingMode = 2;

		for (int i = 0; i < 5; i++){
			emptyMethod();
		}

		System.out.println(sum(12, 38));

		for (int i = 0; i < 5; i++){
			Thread.sleep(10);
			method1000();
		}

	}
}
