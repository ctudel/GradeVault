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
            System.out.println("Please Select a Class...\n");
          else {
            System.out.println("Please wait..");
            cm.showClass();
          }
          break;

        case 4:
          if (classSelected < 1)
            System.out.println("Please Select a Class...\n");
          else {
            clearScreen();
            System.out.println("Please wait..");
            cm.getCategories();
          }
          break;

        case 5:
          if (classSelected < 1)
            System.out.println("Please Select a Class...\n");
          else {
            clearScreen();
            createCategory(scnr, cm);
            System.out.println("Successfully updated Categories\n");
          }
          break;

        case 6:
          if (classSelected < 1)
            System.out.println("Please Select a Class...\n");
          else {
            clearScreen();
            System.out.println("Please wait..");
            cm.getAssignments();
          }
          break;

        case 7:
          if (classSelected < 1)
            System.out.println("Please Select a Class...\n");
          else {
            clearScreen();
            createAssignment(scnr, cm);
            System.out.println("Successfully created Assignments\n");
          }
          break;

        case 8:
          if (classSelected < 1)
            System.out.println("Please Select a Class...\n");
          else {
            clearScreen();
            System.out.println("Hang tight, while we get  those...");
            cm.getGrades();
          }
          break;

        case 9:
          if (classSelected < 1)
            System.out.println("Please Select a Class...\n");
          else {
            clearScreen();
            getStudentGrade(scnr, cm.getCurrClass().getClassId(), sm);

          }
          break;

        case 10:
          if (classSelected < 1)
            System.out.println("Please Select a Class...\n");
          else {
            clearScreen();
            System.out.println("Please hang tight...");
            cm.getStudents();

          }
          break;

        case 11:
          clearScreen();
          findStudent(scnr, sm);
          break;

        case 12:
          clearScreen();
          createAndEnrollStudent(scnr, cm, sm);
          break;

        case 13:
          clearScreen();
          enrollStudent(scnr, cm, sm);
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
    System.out.println("1) View All Classes\n");
    if (classSelected <= 0)
      System.out.println("2) Select a Class\n");

    if (classSelected > 0) {
      System.out.println("2) Select New Class\t6) Show Assignments\t10) Show Students\n");
      System.out
          .println("3) Show Selected Class\t7) Add New Assignments\t11) Find Student\n");
      System.out.println("4) Show Categories\t8) Show Gradebook\t12) Create and Enroll Student\n");
      System.out.println("5) Add New Categories\t9) Check Student Grade\t13) Enroll Existing Student\n");
    }
  }

  public static int selectClass(Scanner scnr, ClassManager cm) {
    String course = "";
    String choice = "";

    while (course.isBlank()) {
      System.out.print("Enter the course (i.e. CS410): ");
      course = scnr.nextLine().toUpperCase();
    }

    while (!choice.equals("y") && !choice.equals("n")) {
      System.out.print("Would you like to choose the term? (y/n): ");
      choice = scnr.nextLine();
    }

    if (choice.equals("y")) {
      String[] info = selectTerm(scnr, cm); // get term and potentially section

      // Set class with additional info
      if (info[1].isBlank()) {
        System.out.println("Processing Please wait...\n");
        return cm.setClass(course, new TablePrinter().formatString(info[0]));

      } else { // section was chosen
        int section = -1;
        try {
          section = Integer.parseInt(info[1]);
        } catch (Exception e) {
          System.out.println("Error: Section must be a valid integer.");
        }

        System.out.println("Processing Please wait...\n");
        return cm.setClass(course, info[0], section);
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
      System.out.print("Select a term: ");
      term = scnr.nextLine();
    }
    info[0] = term;

    while (!choice.equals("y") && !choice.equals("n")) {
      System.out.print("Would you like to specify a section? (y/n): ");
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
      System.out.print("Select a section: ");
      section = scnr.nextLine();
    }

    return section;
  }

  public static void createCategory(Scanner scnr, ClassManager cm) {
    String category = "";
    String weightStr = "";

    while (category.isBlank()) {
      System.out.print("Category title: ");
      category = scnr.nextLine();
    }

    while (weightStr.isBlank()) {
      System.out.print("Enter weight: ");
      weightStr = scnr.nextLine();
    }

    try {
      double weight = Double.parseDouble(weightStr);
      cm.createCategory(category);
      cm.addCategory(category, weight);

    } catch (Exception e) {
      System.out.println("Invalid value for weight, please try again.");
    }

  }

  public static void createAssignment(Scanner scnr, ClassManager cm) {
    String name = "";
    String category = "";
    double points = -1.0;
    String description = "";

    while (name.isBlank()) {
      System.out.print("Assignment Title: ");
      name = scnr.nextLine();
    }

    while (category.isBlank()) {
      System.out.print("Category: ");
      category = scnr.nextLine();
    }

    while (points < 0.0) {
      try {
        System.out.print("Possible Points: ");
        points = Double.parseDouble(scnr.nextLine());
      } catch (Exception e) {
        System.out.println("Please enter a valid point value...");
      }
    }

    System.out.println("Description (optional): ");
    description = scnr.nextLine();

    if (cm.getCategory(category))
      cm.createAssignment(name, category, points, description);
    else
      System.out.println("Invalid category...");
  }

  public static void getStudentGrade(Scanner scnr, int classId, StudentManager sm) {
    String username = "";

    while (username.isBlank()) {
      System.out.print("Student username: ");
      username = scnr.nextLine();
    }

    sm.getGrades(classId, username);
  }

  public static void findStudent(Scanner scnr, StudentManager sm) {
    String searchStr = "";

    while (searchStr.isBlank()) {
      System.out.print("Search for Student by Username or Name: ");
      searchStr = scnr.nextLine();
    }

    System.out.println("Please wait while we search for your student...");
    sm.searchStudent(searchStr);
  }

  public static void createAndEnrollStudent(Scanner scnr, ClassManager cm, StudentManager sm) {
    String username = "";
    int studentID = -1;
    String firstName = "";
    String lastName = "";

    while (username.isBlank()) {
      System.out.print("Student username: ");
      username = scnr.nextLine();
    }

    while (studentID < 0) {
      try {
        System.out.print("Student ID: ");
        studentID = Integer.parseInt(scnr.nextLine());
      } catch (Exception e) {
        System.out.println("Student ID can only be numbers, with no spaces, or other characters");
      }
    }

    while (firstName.isBlank()) {
      System.out.print("Student First Name: ");
      firstName = scnr.nextLine();
    }

    while (lastName.isBlank()) {
      System.out.print("Student Last Name: ");
      lastName = scnr.nextLine();
    }

    System.out.println("Hang tight while we process...");

    if (!sm.getStudent(username, studentID)) {
      sm.createStudent(studentID, username, firstName, lastName);
      cm.addStudent(username);
      System.out.println("Successfully created and enrolled student to class!\n");
    } else {
      System.out.println("Seems like the student already exists, please try simply enrolling them...\n");
    }
  }

  public static void enrollStudent(Scanner scnr, ClassManager cm, StudentManager sm) {
    String username = "";

    while (username.isBlank()) {
      System.out.print("Student username: ");
      username = new TablePrinter().formatString(scnr.nextLine());
    }

    System.out.println("Hang tight while we process...");

    if (sm.getStudent(username) && !(sm.inClass(username, cm.getCurrClass().getClassId()))) {
      cm.addStudent(username);
      System.out.println("Successfully enrolled student to class!\n");
    } else {
      System.out.println("The student is already enrolled or does not exist...\n");
    }
  }

  public static void clearScreen() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

}
