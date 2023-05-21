package calculator;
import calculator.Calculator;
class QuickStart {
    public static void main (String[] args) {
        
        Calculator calc = new Calculator();

        while(true) {
            System.out.print("\033[H\033[2J");
            System.out.println("SCREEN: " + calc.getScreen());

            String key = System.console().readLine();
            calc.Press(key);

            if (key.equals("q")) {
                break;
            }
        }
    }
}