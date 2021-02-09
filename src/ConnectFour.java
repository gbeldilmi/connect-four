import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
public class ConnectFour extends JFrame {
  private JButton gridButtons[][];
  private JLabel topLabels[];
  private JPanel mainPanel, topPanel, gridPanel;
  private int t;
  public static void main(String args[]) {
    ConnectFour game;
    game = new ConnectFour();
  }
  public ConnectFour() {
    initComponents();
    resetComponents();
  }
  private void initComponents() {
    int i, j;
    gridButtons = new JButton[7][6];
    topLabels = new JLabel[2];
    // set up the window
    setTitle("Connect Four");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(350, 350));
    setMinimumSize(new Dimension(350, 350));
    setLocationRelativeTo(null); // Center the window on the screen
    setVisible(true); // Displays the window
    // set up content
    mainPanel = (JPanel) getContentPane();
    mainPanel.setLayout(new BorderLayout());
    // set up the top panel
    topPanel = new JPanel();
    topPanel.setLayout(new GridLayout(1, 2));
    for (i = 0; i < 2; i++) {
      topLabels[i] = new JLabel((i == 0)? "Player" : "Bot");
      topLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
      topPanel.add(topLabels[i]);
    }
    mainPanel.add(topPanel, BorderLayout.NORTH);
    // set up the grid panel
    gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(6, 7));
    for (i = 0; i < 6; i++) {
      for (j = 0; j < 7; j++) {
        gridButtons[j][i] = new JButton();
        gridButtons[j][i].addActionListener( (e) -> play(e) );
        gridPanel.add(gridButtons[j][i]);
      }
    }
    mainPanel.add(gridPanel, BorderLayout.CENTER);
    pack();
  }
  private void resetComponents() {
    // Reset and restart a new game
    int i, j;
    t = 0;
    for (i = 0; i < 2; i++) {
      topLabels[i].setForeground((i == 0)? Color.blue : Color.black);
    }
    for (i = 0; i < 6; i++) {
      for (j = 0; j < 7; j++) {
        gridButtons[j][i].setText("");
        gridButtons[j][i].setBackground(new Color(-1));
        gridButtons[j][i].setEnabled(!(i < 5));
      }
    }
  }
  private int[] getCoordinatesInGrid(JButton o) {
    // Search an element in gridButtons[][] and return its coordinates if found
    int c[], i, j;
    c = new int[]{-1, -1};
    for (i = 0; i < 6; i++) {
      for (j = 0; j < 7; j++) {
        if (o == gridButtons[j][i]) {
          c = new int[]{j, i};
        }
      }
    }
    return c;
  }
  private int searchToken(String token, int x, int y, int dx, int dy) {
    /* Count the number of same token in one direction  */
    int n;
    n = 1;
    while (0 <= x + (n * dx) && x + (n * dx) < 7 && 0 <= y + (n * dy) && y + (n * dy) < 6 &&
        token.equals(gridButtons[x + (n * dx)][y + (n * dy)].getText())) {
      n++;
    }
    return n - 1;
  }
  private void changePlayer() {
    // Alternate between player's and bot's turn
    topLabels[0].setForeground((t % 2 == 0)? Color.blue : Color.black);
    topLabels[1].setForeground((t % 2 == 0)? Color.black : Color.red);
  }
  private boolean gameIsEnded(JButton last) {
    // Check if the game is over
    boolean f, w;
    int i, l[];
    // check if the grid is full
    f = true;
    for (i = 0; i < 7; i++) {
      f = f && !gridButtons[i][0].getText().equals("");
    }
    // check if a player won
    w = false;
    l = getCoordinatesInGrid(last);
    for (int d[] : new int[][]{{0, 1}, {1, 1}, {1, 0}, {1, -1}}) {
      w = w || (searchToken(gridButtons[l[0]][l[1]].getText(), l[0], l[1], d[0], d[1]) +
          searchToken(gridButtons[l[0]][l[1]].getText(), l[0], l[1], -d[0], -d[1]) >= 3);
    }
    if (w) {
      JOptionPane.showMessageDialog(this, (t % 2 == 0)? "You won." : "Bot won.");
    }
    return f || w;
  }
  private void play(ActionEvent event) {
    // Perform player's action
    int c[];
    // perform some actions to the clicked button
    ((JButton) event.getSource()).setEnabled(false);
    ((JButton) event.getSource()).setText((t % 2 == 0)? "O" : "X");
    ((JButton) event.getSource()).setBackground((t % 2 == 0)? Color.blue : Color.red);
    // enable the button above if there is one
    c = getCoordinatesInGrid((JButton) event.getSource());
    if (c[1] > 0) {
      gridButtons[c[0]][c[1] - 1].setEnabled(true);
    }
    if (!gameIsEnded((JButton) event.getSource())) {
      t++;
      changePlayer();
      if (t % 2 == 1) {
        simulateBot();
      }
    } else {
      c[0] = JOptionPane.showConfirmDialog(this, "Would you like to play another game ?",
          "New game ?", JOptionPane.YES_NO_OPTION);
      if (c[0] == JOptionPane.YES_OPTION) {
        resetComponents();
      } else {
        dispose();
      }
    }
  }
  private void simulateBot() {
    // Simulate bot's reflection
    int c[], p[], s[], b, i, j, k;
    p = new int[7];
    s = new int[7];
    b = 0;
    for (i = 0; i < 7; i++) {
      p[i] = -1;
      s[i] = -1;
      // search where bot can play
      for (j = 0; j < 6; j++) {
        if (gridButtons[i][j].isEnabled()) {
          p[i] = j;
        }
      }
      if (p[i] != -1) {
        // attribute score for it
        for (String u : new String[] {"O", "X"} ) {
          c = new int[4];
          j = 0;
          for (int d[] : new int[][]{{0, 1}, {1, 1}, {1, 0}, {1, -1}}) {
            c[j] = searchToken(u, i, p[i], d[0], d[1]) + searchToken(u, i, p[i], -d[0], -d[1]);
            j++;
          }
          for (int d : c) {
            k = d;
            for (j = 1; j <= d; j++) {
              k *= j * 10;
            }
            s[i] += k;
          }
        }
        // save the biggest score
        b = (s[i] >= b)? s[i] : b;
      }
    }
    // remove scores under the biggest
    for (i = 1; i < 7; i++) {
      s[i] = (s[i] == b)? b : -1;
    }
    // choose one of the biggest scores randomly
    do {
      i = new Random().nextInt(7);
    } while (p[i] == -1 || s[i] == -1);
    // trigger play(ActionEvent)
    play(new ActionEvent(gridButtons[i][p[i]], 1, ""));
  }
}
