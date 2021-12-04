package MainPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class MyMenuBar extends JMenuBar implements ActionListener {
    JMenu fileMenu = new JMenu("Файл");
    JMenu graphicMenu = new JMenu("График");

    JMenuItem loadItem = new JMenuItem("Загрузить");
    JMenuItem saveItem = new JMenuItem("Сохранить");
    JMenuItem exitItem = new JMenuItem("Выход");

    JCheckBoxMenuItem showAxes = new JCheckBoxMenuItem("Показать оси координат",true);
    JCheckBoxMenuItem showPoints = new JCheckBoxMenuItem("Показать маркеры точек",true);
    JCheckBoxMenuItem showLabels = new JCheckBoxMenuItem("Показать подписи координат",false);
    JMenuItem turnGraphics = new JMenuItem("Повернуть графикa");
    JMenuItem turnAtZero = new JMenuItem("Вернуть график в исходное положение");

    private GraphicsDisplay _display;
    JFileChooser fileChooser = null;

    private ArrayList<Double[]> graphicsData = new ArrayList<>();

    public MyMenuBar(GraphicsDisplay display, boolean visible){
        _display = display;

        loadItem.addActionListener(this);
        saveItem.addActionListener(e -> saveGraphics(new File("OutCoordinates.txt")));
        exitItem.addActionListener(e ->  System.exit(0));

        showPoints.addActionListener(e -> _display.setShowMarkers(showPoints.getState()));
        showAxes.addActionListener(e -> _display.setShowAxis(showAxes.getState()));
        showLabels.addActionListener(e -> _display.setShowLabels(showLabels.getState()));
        turnGraphics.addActionListener(this);
        turnAtZero.addActionListener(e ->{
            _display.setTurnGraphics(0, false);
            _display.showGraphics(graphicsData);
        });

        graphicMenu.add(showAxes);
        graphicMenu.add(showPoints);
        graphicMenu.add(showLabels);
        graphicMenu.add(turnGraphics);
        graphicMenu.add(turnAtZero);

        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        add(fileMenu);
        add(graphicMenu);

        setVisible(visible);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadItem){
            try {
                if(fileChooser==null){
                    fileChooser=new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));

                    if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
                        openGraphics(fileChooser.getSelectedFile());
                    }
                }

                setVisible(true);
            }
            catch (NullPointerException ex){
                System.out.println(ex.getMessage());
            }
        }
        if (e.getSource() == turnGraphics){
            String result = JOptionPane.showInputDialog(this, "Введите угол");

            double angel = Double.parseDouble(result);

            _display.setTurnGraphics(angel, false);
        }
    }

    private void openGraphics(File file){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String line;
            String[] strings = null;

            while((line = reader.readLine()) != null){
                strings = line.split(" ");
            }

            Double x = 0d;
            Double y = 0d;
            for (int  i = 0; i < strings.length;i++){
                if (i%2==0) {
                    x = Double.parseDouble(strings[i]);
                }
                if (i%2!=0){
                    y = Double.parseDouble(strings[i]);
                    graphicsData.add(new Double[]{x,y});
                }
            }

            _display.showGraphics(graphicsData);

            reader.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Указанный файл не найден",
                    "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка чтения координат точек из файла",
                    "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка чтения координат точек из файла",
                    "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    private void saveGraphics(File file){
        try {
            FileWriter writer = new FileWriter(file.getAbsolutePath());

            for (int i = 0; i < graphicsData.size();i++){
                writer.write(graphicsData.get(i)[0] + " ");
                writer.write(graphicsData.get(i)[1] + " ");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVisible(boolean visible){
        showAxes.setEnabled(visible);
        showPoints.setEnabled(visible);
        showLabels.setEnabled(visible);
        saveItem.setEnabled(visible);
        turnGraphics.setEnabled(visible);
        turnAtZero.setEnabled(visible);

    }
}
