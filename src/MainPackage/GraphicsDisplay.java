package MainPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class GraphicsDisplay extends JPanel {

    private boolean _showAxis = true;
    private boolean _showMarkers = true;
    private boolean _showLabels = false;
    private boolean _turn = false;

    private int selectedMarker = -1;

    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    private double scaleX;
    private double scaleY;
    private double scale;

    private double _angel;

    private BasicStroke _graphicsStroke;
    private BasicStroke _axisStroke;
    private BasicStroke _markerStroke;
    private BasicStroke _gridStroke;

    private Font _axisFont;
    private Font _labelsFont;

    ArrayList<Double[]> _graphicsData;

    private static DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();

    public GraphicsDisplay() {
        setBackground(Color.WHITE);

        _graphicsStroke = new BasicStroke(2f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10f, new float[]{8,2,2,2,4,2,2,2,8}, 0f);
        _axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f,null , 0.0f);
        _markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        _gridStroke = new BasicStroke(1.0F, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0F, new float[] { 4.0F, 4.0F }, 0.0F);
        _axisFont = new Font("Serif", Font.BOLD, 36);
        _labelsFont = new Font("Serif", 0, 10);
    }

    public void showGraphics(ArrayList<Double[]> graphicsData) {
        _graphicsData = new ArrayList<>(graphicsData.size());
        for (Double[] point : graphicsData){
            _graphicsData.add(point.clone());
        }
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        _showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        _showMarkers = showMarkers;
        repaint();
    }

    public void setShowLabels(boolean showLabels) {
        _showLabels = showLabels;
        repaint();
    }

    public void setTurnGraphics(double angel, boolean turn){
        _turn = turn;
        _angel = angel;
        repaint();
    }



    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (_turn) turnGraphics();

        if (_angel != 0) turnGraphics();

        if (_graphicsData == null || _graphicsData.size() == 0) return;

        minX = _graphicsData.get(0)[0];
        maxX = _graphicsData.get(_graphicsData.size() - 1)[0];
        minY = _graphicsData.get(0)[1];
        maxY = _graphicsData.get(_graphicsData.size() - 1)[1];;

        for (int i = 0; i < _graphicsData.size(); i++) {
            if (_graphicsData.get(i)[1] < minY)
                minY = _graphicsData.get(i)[1];
            if (_graphicsData.get(i)[1] > maxY) {
                maxY = _graphicsData.get(i)[1];
            }
            if (_graphicsData.get(i)[0] < minX)
                minX = _graphicsData.get(i)[0];
            if (_graphicsData.get(i)[0] > maxX) {
                maxX = _graphicsData.get(i)[0];
            }
        }


        scaleX = getSize().getWidth() / (maxX - minX);
        scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

        Graphics2D canvas = (Graphics2D) graphics;
        if (_showLabels) paintLabels(canvas);
        paintGrid(canvas);

        if (scale == scaleX) {
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();


        if (_showAxis) paintAxis(canvas);
        paintGraphics(canvas);
        if (_showMarkers) paintMarkers(canvas);


        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);

    }

    public void turnGraphics(){
        for(int i =0; i < _graphicsData.size();i++){
            double x = _graphicsData.get(i)[0];
            double y = _graphicsData.get(i)[1];

            _graphicsData.get(i)[0] = x * Math.cos(Math.toRadians(_angel)) - y * Math.sin(Math.toRadians(_angel));
            _graphicsData.get(i)[1] = x * Math.sin(Math.toRadians(_angel)) + y * Math.cos(Math.toRadians(_angel));
        }

    }

    private void paintAxis(Graphics2D canvas) {
        canvas.setStroke(_axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(_axisFont);

        FontRenderContext context = canvas.getFontRenderContext();

        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));

            GeneralPath arrowY = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrowY.moveTo(lineEnd.getX(), lineEnd.getY());
            arrowY.lineTo(arrowY.getCurrentPoint().getX() + 10, arrowY.getCurrentPoint().getY() + 25);
            arrowY.moveTo(lineEnd.getX(), lineEnd.getY());
            arrowY.lineTo(arrowY.getCurrentPoint().getX() - 10 , arrowY.getCurrentPoint().getY() + 25);
            canvas.draw(arrowY);

            Rectangle2D bounds = _axisFont.getStringBounds("Y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);

            canvas.drawString("Y", (float) labelPos.getX() + 10,
                    (float) (labelPos.getY() - bounds.getY()));
        }

        if (minY <= 0.0 && maxY >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));

            GeneralPath arrowX = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrowX.moveTo(lineEnd.getX(), lineEnd.getY());
            arrowX.lineTo(arrowX.getCurrentPoint().getX() - 25, arrowX.getCurrentPoint().getY() - 10);
            arrowX.moveTo(lineEnd.getX(), lineEnd.getY());
            arrowX.lineTo(arrowX.getCurrentPoint().getX() - 25, arrowX.getCurrentPoint().getY() + 10);
            canvas.draw(arrowX);

            Rectangle2D bounds = _axisFont.getStringBounds("X", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);

            canvas.drawString("X", (float) (labelPos.getX() - bounds.getWidth() - 10),
                    (float) (labelPos.getY() + bounds.getY()));
        }
    }

    private void paintGrid(Graphics2D canvas) {
        canvas.setStroke(_gridStroke);
        canvas.setColor(Color.GRAY);
        double pos = minX;
        double step = (maxX - minX) / 10.0D;
        while (pos < maxX) {
            canvas.draw(new Line2D.Double(translateXYtoPoint(pos, minY),
                    translateXYtoPoint(pos, maxY)));
            pos += step;
        }
        canvas.draw(new Line2D.Double(translateXYtoPoint(maxX, maxY),
                translateXYtoPoint(maxX, minY)));
        pos = minY;
        step = (maxY - minY) / 10.0D;
        while (pos < maxY) {
            canvas.draw(new Line2D.Double(translateXYtoPoint(minX, pos),
                    translateXYtoPoint(maxX, pos)));
            pos += step;
        }
        canvas.draw(new Line2D.Double(translateXYtoPoint(minX, maxY),
                translateXYtoPoint(maxX, maxY)));
    }

    private void paintMarkers (Graphics2D canvas){
        canvas.setStroke(_markerStroke);
        canvas.setColor(Color.BLUE);
        canvas.setPaint(Color.BLUE);

        for (Double[] point : _graphicsData) {
            GeneralPath marker = new GeneralPath();
            Point2D.Double center = xyToPoint(point[0], point[1]);

            marker.moveTo(center.getX() - 3, center.getY() - 6);
            marker.lineTo(center.getX() + 3, center.getY() - 6);
            marker.moveTo(center.getX() - 3, center.getY() + 6);
            marker.lineTo(center.getX() + 3, center.getY() + 6);//
            marker.moveTo(center.getX() - 6, center.getY() + 3);
            marker.lineTo(center.getX() - 6, center.getY() - 3);
            marker.moveTo(center.getX() + 6, center.getY() + 3);
            marker.lineTo(center.getX() + 6, center.getY() - 3);//
            marker.moveTo(center.getX(), center.getY() + 6);
            marker.lineTo(center.getX(), center.getY() - 6);
            marker.moveTo(center.getX() - 6, center.getY());
            marker.lineTo(center.getX() + 6, center.getY());
            canvas.draw(marker);
        }
    }

    private void paintLabels(Graphics2D canvas) {

        double labelXPos, labelYPos;
        canvas.setColor(Color.BLACK);
        canvas.setFont(_labelsFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (minY < 0.0D && maxY > 0.0D) {
            labelYPos = 0.0D;
        } else {
            labelYPos = minY;
        }
        if (minX < 0.0D && maxX > 0.0D) {
            labelXPos = 0.0D;
        } else {
            labelXPos = minX;
        }

        double pos = minX;
        double step = (maxX - minX) / 10.0D;
        while (pos < maxX) {
            Point2D.Double point = translateXYtoPoint(pos, labelYPos);
            String label = formatter.format(pos);
            Rectangle2D bounds = _labelsFont.getStringBounds(label, context);
            canvas.drawString(label, (float)(point.getX() + 5.0D), (float)(point.getY() - bounds.getHeight()));
            pos += step;
        }
        pos = minY;
        step = (maxY - minY) / 10.0D;
        while (pos < maxY) {
            Point2D.Double point = translateXYtoPoint(labelXPos, pos);
            String label = formatter.format(pos);
            Rectangle2D bounds = _labelsFont.getStringBounds(label, context);
            canvas.drawString(label, (float)(point.getX() + 5.0D), (float)(point.getY() - bounds.getHeight()));
            pos += step;
        }
        if (selectedMarker >= 0) {
            Point2D.Double point = translateXYtoPoint(_graphicsData.get(selectedMarker)[0], (_graphicsData.get(selectedMarker))[1]);
            String label = "X=" + formatter.format(_graphicsData.get(selectedMarker)[0]) + ", Y=" + formatter.format(_graphicsData.get(selectedMarker)[1]);
            Rectangle2D bounds = _labelsFont.getStringBounds(label, context);
            canvas.setColor(Color.BLUE);
            canvas.drawString(label, (float)(point.getX() + 5.0D), (float)(point.getY() - bounds.getHeight()));
        }
    }

    protected Point2D.Double translateXYtoPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scaleX, deltaY * scaleY);
    }

    private void paintGraphics (Graphics2D canvas){
        canvas.setStroke(_graphicsStroke);
        canvas.setColor(Color.RED);

        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < _graphicsData.size(); i++) {
            Point2D.Double point = xyToPoint(_graphicsData.get(i)[0], _graphicsData.get(i)[1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(graphics);
    }

    private Point2D.Double xyToPoint ( double x, double y){
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }

}
