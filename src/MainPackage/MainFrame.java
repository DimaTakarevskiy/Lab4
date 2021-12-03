package MainPackage;

import javax.swing.*;

public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private GraphicsDisplay display = new GraphicsDisplay();

    MainFrame(){
        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setExtendedState(MAXIMIZED_BOTH);

        setJMenuBar(new MyMenuBar(display, false));

        add(display);

        setVisible(true);
    }
}