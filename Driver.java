import java.io.Console;
import java.util.Scanner;

public class Driver {

  public static void main(String[] args) {
    run();
  }

  public static void run() {
    ClassManager cm = new ClassManager();
    StudentManager sm = new StudentManager();

    Scanner scnr = new Scanner(System.in);
    int classSelected = 0;
    int option = 0;

    while (true) {
      switch (option) {
        case 0:
          break;

        case 1:
          clearScreen();
          System.out.println("Retrieving Classes...\n\n");
          cm.getAllClasses();
          break;

        case 2:
          classSelected = selectClass(scnr, cm);
          break;

        case 3:
          if (classSelected < 1)
            System.out.println("Please select a class...\n");
          else {
            System.out.println("Please wait..");
            cm.showClass();
          }
          break;

        default:
          System.out.println("Invalid choice, type the number for the option you would like to choose..\n");
      }

      printMenu(classSelected);
      option = -1;
      while (option < 0) {
        try {
          option = Integer.parseInt(scnr.nextLine());

        } catch (Exception e) {
          System.out.println("Invalid choice, please choose a number option from the menu...\n");
        }
      }
      System.out.println(option);
      System.out.print("\n");
    }
  }

  public static void printMenu(int classSelected) {
    System.out.println("Welcome to the grade book");
    System.out.println("-------------------------");
    System.out.println("1) View all classes\n");
    System.out.println("2) Select a Class\n");

    if (classSelected > 0) {
      System.out.println("3) Show class\n");
    }
  }

  public static int selectClass(Scanner scnr, ClassManager cm) {
    String course = "";
    String choice = "";

    while (course.isBlank()) {
      System.out.print("Enter the course (i.e. CS410): ");
      course = scnr.nextLine();
    }

    while (!choice.equals("y") && !choice.equals("n")) {
      System.out.println("Would you like to choose the term? (y/n)");
      choice = scnr.nextLine();
    }

    if (choice.equals("y")) {
      String[] info = selectTerm(scnr, cm); // get term and potentially section

      // Set class with additional info
      if (info[1].isBlank()) {
        System.out.println("Processing Please wait...\n");
        return cm.setClass(course, info[0]);

      } else { // section was chosen
        int section = -1;
        try {
          section = Integer.parseInt(info[1]);
        } catch (Exception e) {
          System.out.println("Error: Section must be a valid integer.");
        }

        System.out.println("Processing Please wait...\n");
        return cm.setClass(course, info[0], Integer.parseInt(info[1]));
      }

    } else { // only course provided
      System.out.println("Processing Please wait...\n");
      return cm.setClass(course);
    }
  }

  public static String[] selectTerm(Scanner scnr, ClassManager cm) {
    String[] info = { "", "" };
    String term = "";
    String choice = "";

    while (term.isBlank()) {
      System.out.println("Select a term: ");
      term = scnr.nextLine();
    }
    info[0] = term;

    while (!choice.equals("y") && !choice.equals("n")) {
      System.out.println("Would you like to specify a section? (y/n)");
      choice = scnr.nextLine();
    }

    if (choice.equals("y")) {
      info[1] = selectSection(scnr, cm); // section
    }

    return info;
  }

  public static String selectSection(Scanner scnr, ClassManager cm) {
    String section = "";

    while (section.isBlank()) {
      System.out.println("Select a section: ");
      section = scnr.nextLine();
    }

    return section;
  }

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

}
