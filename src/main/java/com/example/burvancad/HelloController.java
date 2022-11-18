package com.example.burvancad;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    public Canvas myCanvas;
    public TextField getPointNumber;
    public Button parTransform;
    public TextField parY;
    public TextField parX;
    public TextField parZ;
    public TextField scaleX;
    public TextField scaleY;
    public TextField scaleZ;
    public TextField turnA;
    public TextField opp;
    public TextField shiftCorner;
    Integer getPointsInLine;
    Integer quantityDots;
    Integer quantityLines;
    GraphicsContext ctx;
    private static Double[][][] pointArrayCopy;
    String[] l = new String[0];

    //for mouse
    double offsetX = 0;
    double offsetY = 0;

    //Вписывание
    double n = 1;
    double m = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void resetDataFun() {
        BufferedReader br = null;
        try {
//
            br = new BufferedReader(new FileReader("dotsTest.txt"));

            quantityDots = Integer.valueOf(br.readLine());
            System.out.println(quantityDots);

            Double[][] dotArray = new Double[quantityDots][4];
            for (int i = 0; i < quantityDots; i++) {
                String[] s;
                s = (br.readLine() + " 1").split(" ");
                System.out.println(Arrays.toString(s));
                for (int j = 0; j < s.length; j++) {
                    dotArray[i][j] = Double.valueOf(s[j]);
                }
            }
            System.out.println("Array dotArray");
            System.out.println(Arrays.deepToString(dotArray));

            quantityLines = Integer.valueOf(br.readLine());
            System.out.println(quantityLines);

            getPointsInLine = Integer.valueOf(getPointNumber.getText().trim());

            Integer[][] lineArray = new Integer[quantityLines][getPointsInLine + 1];
            for (int i = 0; i < quantityLines; i++) {

                l = (br.readLine() + " 0").split(" ");
                System.out.println(Arrays.toString(l));
                for (int j = 0; j < l.length; j++) {
                    lineArray[i][j] = Integer.valueOf(l[j]);
                }
            }
            System.out.println(Arrays.deepToString(lineArray));
            int lengthLine = l.length;
            Double[][][] dotArrayChart = new Double[quantityLines][lengthLine - 1][4];
            for (int i = 0; i < quantityLines; i++) {
                for (int j = 0; j < l.length - 1; j++) {
                    for (int k = 0; k < 4; k++) {
                        dotArrayChart[i][j][k] = dotArray[lineArray[i][j]][k];
                    }
                }
            }

            System.out.println("||NEW||");
            System.out.println(Arrays.deepToString(dotArrayChart));
            pointArrayCopy = dotArrayChart;


        } catch (IOException e) {
            System.out.print("Error: " + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                System.out.print("Error: " + e);
            }
        }
    }

    void draw() {
        //ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        double zoom = 50;


        ctx = myCanvas.getGraphicsContext2D();
        ctx.save();
        Affine transform = ctx.getTransform();

//        ctx.setFill(Color.WHITE);
//        ctx.fillRect(0,0, myCanvas.getWidth(), myCanvas.getHeight());

        transform.appendTranslation(myCanvas.getWidth() / 2 + offsetX, myCanvas.getHeight() / 2 + offsetY);

        transform.appendScale(zoom * n, -zoom * m);
        ctx.setTransform(transform);
        ctx.setLineWidth(1 / zoom);

//        for (int i = 0; i < quantityLines; i++) {
//            ctx.setStroke(Color.color(0.8, 0.3, 0.3));
//            ctx.strokeLine(pointArrayCopy[i][0][0], pointArrayCopy[i][0][1], pointArrayCopy[i][1][0], pointArrayCopy[i][1][1]);
////                ctx.strokePolyline(
////                        new double[]{pointArrayCopy[i][0][0], pointArrayCopy[j][0][0]},
////                        new double[]{pointArrayCopy[i][1][1], pointArrayCopy[j][1][1]},
////                        2
////                );
//        }

        ctx.setStroke(Color.FIREBRICK);
        for (int i = 0; i < quantityLines; i++) {
            ctx.strokePolyline(
                    new double[]{pointArrayCopy[i][0][0], pointArrayCopy[i][1][0]},
                    new double[]{pointArrayCopy[i][0][1], pointArrayCopy[i][1][1]},
                    2
            );

        }
        //System.out.println(Arrays.deepToString(pointArrayCopy));

//        ctx.setStroke(Color.FIREBRICK);
//        ctx.strokePolyline(
//                new double[]{point0[0], point1[0], point2[0], point3[0]},
//                new double[]{point0[1], point1[1], point2[1], point3[1]},
//                4
//        );

//        ctx.setFill(Color.BLUE);
//        ctx.fillPolygon(
//                new double[]{point0[0], point1[0], point2[0], point3[0]},
//                new double[]{point0[1], point1[1], point2[1], point3[1]},
//                4
//        );
        ctx.restore();

    }

    public void resetData(ActionEvent actionEvent) {
        offsetX = 0;
        offsetY = 0;
        resetDataFun();
        draw();
    }

    public void multiMat(Double[][] par) {
        double fLine;
        Double[][][] multi = new Double[quantityLines][2][4];
        for (int k = 0; k < quantityLines; k++) {
            for (int j = 0; j < 2; j++) {
                for (int i = 0; i < 4; i++) {
                    fLine = 0.0;
                    for (int t = 0; t < 4; t++) {
                        fLine += pointArrayCopy[k][j][t] * par[t][i];
                    }
                    multi[k][j][i] = fLine;
                }
            }
        }
        pointArrayCopy = multi;
    }

    public void multiMatOPP(Double[][] par, Double f) {
        double fLine;
        Double[][][] multi = new Double[quantityLines][2][4];
        for (int k = 0; k < quantityLines; k++) {
            for (int j = 0; j < getPointsInLine; j++) {
                for (int i = 0; i < 4; i++) {
                    fLine = 0.0;
                    for (int t = 0; t < 4; t++) {
                        fLine += pointArrayCopy[k][j][t] * par[t][i];
                    }
                    multi[k][j][i] = fLine;
                }
            }
        }
        for (int i = 0; i < quantityLines; i++) {
            for (int j = 0; j < getPointsInLine; j++) {
                multi[i][j][0] /= multi[i][j][3];
                multi[i][j][1] /= multi[i][j][3];
                multi[i][j][2] /= multi[i][j][3];
                multi[i][j][3] /= multi[i][j][3];
            }
        }
        pointArrayCopy = multi;
    }

    public void parTransForm(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        parallelTransfer();
        draw();
        System.out.println("||changeParallel||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void scaleTransform(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        Scaling();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void turnAroundX(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        turnAroundX();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void turnAroundY(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        turnAroundY();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void turnAroundZ(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        turnAroundZ();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void oppX(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        funFX();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void oppY(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        funFY();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void oppZ(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        funFZ();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void shiftXY(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        shiftXY();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void shiftXZ(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        shiftXZ();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void shiftYX(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        shiftYX();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void shiftYZ(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        shiftYZ();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void shiftZX(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        shiftZX();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void shiftZY(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        shiftZY();
        draw();
        System.out.println("||changeScaling||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    public void parallelTransfer() {
        try {
            Double getX = Double.valueOf(parX.getText().trim());
            Double getY = Double.valueOf(parY.getText().trim());
            Double getZ = Double.valueOf(parZ.getText().trim());
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {0.0, 1.0, 0.0, 0.0},
                    {0.0, 0.0, 1.0, 0.0},
                    {getX, getY, getZ, 1.0},
            };
            multiMat(parallel);

        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    private void Scaling() {
        try {
            Double getKx = Double.valueOf(scaleX.getText().trim());
            Double getKy = Double.valueOf(scaleY.getText().trim());
            Double getKz = Double.valueOf(scaleZ.getText().trim());
            Double[][] parallel = {
                    {getKx, 0.0, 0.0, 0.0},
                    {0.0, getKy, 0.0, 0.0},
                    {0.0, 0.0, getKz, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);

        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }

    }

    public void turnAroundX() {
        try {
            Double cornerA = Double.valueOf(turnA.getText().trim());
            double c;
            double s1;
            double s2;
            double s3;
            s2 = Math.toRadians(cornerA);
            c = Math.cos(s2);
            s1 = Math.sin(s2);
            s3 = Math.sin(s2) * -1;
            System.out.println(c);
            System.out.println(s1);
            System.out.println(s2);
            System.out.println(s3);
            System.out.println(c + s3);
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {0.0, c, s1, 0.0},
                    {0.0, -s1, c, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void turnAroundY() {
        try {
            Double cornerA = Double.valueOf(turnA.getText().trim());
            double c;
            double s1;
            double s2;
            s2 = Math.toRadians(cornerA);
            c = Math.cos(s2);
            s1 = Math.sin(s2);
            Double[][] parallel = {
                    {c, 0.0, -s1, 0.0},
                    {0.0, 1.0, 0.0, 0.0},
                    {s1, 0.0, c, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            System.out.println(c);
            System.out.println(parallel[0][0]);
            multiMat(parallel);

        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void turnAroundZ() {
        try {
            Double cornerA = Double.valueOf(turnA.getText().trim());
            double c;
            double s1;
            double s2;
            s2 = Math.toRadians(cornerA);
            c = Math.cos(s2);
            s1 = Math.sin(s2);
            Double[][] parallel = {
                    {c, s1, 0.0, 0.0},
                    {-s1, c, 0.0, 0.0},
                    {0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);

        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void funFX() {
        try {
            Double f = Double.valueOf(opp.getText().trim());
            double fTest = 1 / f;
            System.out.println(fTest);
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, fTest},
                    {0.0, 1.0, 0.0, 0.0},
                    {0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMatOPP(parallel, f);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void funFY() {
        try {
            Double f = Double.valueOf(opp.getText().trim());
            f = 1 / f;
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {0.0, 1.0, 0.0, f},
                    {0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMatOPP(parallel, f);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void funFZ() {
        try {
            Double f = Double.valueOf(opp.getText().trim());
            f = 1 / f;
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {0.0, 1.0, 0.0, 0.0},
                    {0.0, 0.0, 1.0, f},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMatOPP(parallel, f);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void shiftXY() {
        try {
            Double shift1 = Double.valueOf(shiftCorner.getText().trim());
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {shift1, 1.0, 0.0, 0.0},
                    {0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void shiftXZ() {
        try {
            Double shift1 = Double.valueOf(shiftCorner.getText().trim());
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {0.0, 1.0, 0.0, 0.0},
                    {shift1, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void shiftYX() {
        try {
            Double shift1 = Double.valueOf(shiftCorner.getText().trim());
            Double[][] parallel = {
                    {1.0, shift1, 0.0, 0.0},
                    {0.0, 1.0, 0.0, 0.0},
                    {0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void shiftYZ() {
        try {
            Double shift1 = Double.valueOf(shiftCorner.getText().trim());
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {0.0, 1.0, 0.0, 0.0},
                    {0.0, shift1, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void shiftZX() {
        try {
            Double shift1 = Double.valueOf(shiftCorner.getText().trim());
            Double[][] parallel = {
                    {1.0, 0.0, shift1, 0.0},
                    {0.0, 1.0, 0.0, 0.0},
                    {0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void shiftZY() {
        try {
            Double shift1 = Double.valueOf(shiftCorner.getText().trim());
            Double[][] parallel = {
                    {1.0, 0.0, 0.0, 0.0},
                    {0.0, 1.0, shift1, 0.0},
                    {0.0, 0.0, 1.0, 0.0},
                    {0.0, 0.0, 0.0, 1.0},
            };
            multiMat(parallel);
        } catch (EmptyStackException e) {
            System.out.println("Enter number");
        } catch (Exception e) {
            System.out.println(String.valueOf(e));
        }
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        offsetX += mouseEvent.getX() - pressedX;
        offsetY += mouseEvent.getY() - pressedY;
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        draw();
    }

    double pressedX;
    double pressedY;

    public void onMousePressed(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
        pressedY = mouseEvent.getY();
    }

    public void clearCanvas(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
    }

    public void inscribing(ActionEvent actionEvent) {
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        double getX = 0;
        double getY = 0;
        double giveX;
        double giveY;
        double sX = 0;
        double sY = 0;
        for (int i = 0; i < quantityLines; i++) {
            for (int j = 0; j < getPointsInLine; j++) {
                if (minX > pointArrayCopy[i][j][0]) {
                    minX = pointArrayCopy[i][j][0];
                }
                if (maxX < pointArrayCopy[i][j][0]) {
                    maxX = pointArrayCopy[i][j][0];
                }
                if (minY > pointArrayCopy[i][j][1]) {
                    minY = pointArrayCopy[i][j][1];
                }
                if (maxY < pointArrayCopy[i][j][1]) {
                    maxY = pointArrayCopy[i][j][1];
                }
            }
        }
        giveX = getX;
        giveY = getY;
        getX = ((minX + maxX) / 2) - giveX;
        getY = ((minY + maxY) / 2) - giveY;
        sX = (((maxX - minX) * 50) / myCanvas.getWidth()) + 1;
        sY = (((maxY - minY) * 50) / myCanvas.getHeight()) + 1;
//        n = sX;
//        m = sY;
        offsetX -= getX * 50;
        offsetY += getY * 50;
//        pressedX = mouseEvent.getX();
//        pressedY = mouseEvent.getY();
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        draw();
        myAxisLocate();
    }

    public void myAxisLocate(){
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        double getX = 0;
        double getY = 0;
        for (int i = 0; i < quantityLines; i++) {
            for (int j = 0; j < getPointsInLine; j++) {
                if (minX > pointArrayCopy[i][j][0]) {
                    minX = pointArrayCopy[i][j][0];
                }
                if (maxX < pointArrayCopy[i][j][0]) {
                    maxX = pointArrayCopy[i][j][0];
                }
                if (minY > pointArrayCopy[i][j][1]) {
                    minY = pointArrayCopy[i][j][1];
                }
                if (maxY < pointArrayCopy[i][j][1]) {
                    maxY = pointArrayCopy[i][j][1];
                }
            }
        }
        getX = (minX + maxX) / 2;
        getY = (minY + maxY) / 2;

        double x1 = myCanvas.getWidth();
        double y1 = myCanvas.getHeight();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(x1 + offsetX);
        System.out.println(y1 + offsetY);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(getX);
        System.out.println(getY);
    }
}
