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

    private Font _axisFont;

    private double[][] viewport = new double[2][2];

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
        _axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(ArrayList<Double[]> graphicsData) {
        _graphicsData = graphicsData;
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

    public void setTurnGraphics(double angel){
        _angel = angel;
        repaint();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

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
                minX = _graphicsData.get(i)[1];
            if (_graphicsData.get(i)[0] > maxX) {
                maxX = _graphicsData.get(i)[1];
            }
        }

        scaleX = getSize().getWidth() / (maxX - minX);
        scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

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

        Graphics2D canvas = (Graphics2D) graphics;
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

    private Point2D.Double shiftPoint (Point2D.Double src,double deltaX, double deltaY){
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

}
