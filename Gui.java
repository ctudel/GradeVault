import javax.swing.*;

public class Gui {

  public static void main(String[] args) {
    JFrame jframe = new JFrame("GUI Screen");

    jframe.setSize(400, 400);

    JButton pressButton = new JButton("Press");

    jframe.getContentPane().add(pressButton);

    jframe.setVisible(true);
  }

}
