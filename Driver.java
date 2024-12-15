import java.util.Scanner;

public class Driver {

  public static void main(String[] args) {
    ClassManager cm = new ClassManager();
    StudentManager sm = new StudentManager();

    Scanner scnr = new Scanner(System.in);
    int option = 0;

    while (true) {
      switch (option) {
        case 0:
          break;
        case 1:
          System.out.println("Retrieving Classes...\n\n");
          cm.getAllClasses();
          break;
        case 2:
          System.out.println();
        default:
          System.out.println("Invalid choice, type the number for the option you would like to choose..\n");
      }

      printMenu();
      option = Integer.parseInt(scnr.nextLine());
      System.out.println(option);
      System.out.print("\n");
    }
  }

  public static void printMenu() {
    System.out.println("Welcome to the grade book");
    System.out.println("-------------------------");
    System.out.println("1) View all classes\n");
  }
}
