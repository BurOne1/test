package com.example.burvancad;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    public TextField numberLab;
    Integer getPointsInLine;
    Integer quantityDots;
    Integer quantityLines;
    GraphicsContext ctx;
    // Основной массив
    private static Double[][][] pointArrayCopy;
    // Массив нормалей
    double[][] normal;
    double[] kD;

    boolean[] visibilityPolygon;
    double[][] minMaxPolyPoint;
    String[] l = new String[0];

    //for mouse
    double offsetX = 0;
    double offsetY = 0;

    double[] pointInCenterFigure = new double[3];

    double[][] zBufferArr;
    Color[][] kadBuffer;
    int numLab;

    Color[] myColors = new Color[]{
            Color.rgb(255, 0, 0), Color.rgb(255, 70, 70), Color.rgb(255, 138, 138), Color.rgb(155, 0, 0),
            Color.rgb(150, 58, 58), Color.rgb(95, 15, 15), Color.rgb(255, 61, 0), Color.rgb(254, 142, 106),
            Color.rgb(199, 48, 0), Color.rgb(195, 108, 80), Color.rgb(233, 38, 85), Color.rgb(225, 117, 162),
            Color.rgb(106, 240, 0), Color.rgb(41, 93, 0), Color.rgb(182, 223, 16), Color.rgb(210, 236, 108),
            Color.rgb(93, 109, 28), Color.rgb(54, 194, 59),
            Color.rgb(54, 110, 194), Color.rgb(0, 102, 255), Color.rgb(173, 201, 243), Color.rgb(31, 72, 133),
            Color.rgb(55, 149, 178), Color.rgb(0, 180, 237),
            Color.rgb(255, 230, 0), Color.rgb(255, 238, 88), Color.rgb(255, 247, 172), Color.rgb(169, 152, 3),
            Color.rgb(255, 199, 0), Color.rgb(178, 161, 100),
            Color.PLUM};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int a = (int) (myCanvas.getWidth());
        int b = (int) (myCanvas.getHeight());
        zBufferArr = new double[b][a];
        kadBuffer = new Color[b][a];
        for (int q = 0; q < myCanvas.getHeight(); q++) {
            for (int j = 0; j < myCanvas.getWidth(); j++) {
                zBufferArr[q][j] = -1000000;
                kadBuffer[q][j] = Color.WHITESMOKE;
            }
        }
    }

    // Считывает данные из текстового файла
    public void resetDataFun() {
        numLab = Integer.parseInt(numberLab.getText().trim());
        BufferedReader br = null;
        try {
//
            br = new BufferedReader(new FileReader("rombDodecAndCube.txt"));

            quantityDots = Integer.valueOf(br.readLine());
            System.out.println(quantityDots);

            // массив всех точек
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
            // значения локального массива присваеваем глобальному массиву
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

        ctx.setFill(Color.color(0.1, 0.1, 0.5, 0.1));
        ctx.fillRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());

        transform.appendTranslation(myCanvas.getWidth() / 2 + offsetX, myCanvas.getHeight() / 2 + offsetY);

        transform.appendScale(zoom, -zoom);
        ctx.setTransform(transform);
        ctx.setLineWidth(2 / zoom);
        PixelWriter pixelWriter = ctx.getPixelWriter();


//        for (int i = 0; i < quantityLines; i++) {
//            ctx.setStroke(Color.color(0.8, 0.3, 0.3));
//            ctx.strokeLine(pointArrayCopy[i][0][0], pointArrayCopy[i][0][1], pointArrayCopy[i][1][0], pointArrayCopy[i][1][1]);
////                ctx.strokePolyline(
////                        new double[]{pointArrayCopy[i][0][0], pointArrayCopy[j][0][0]},
////                        new double[]{pointArrayCopy[i][1][1], pointArrayCopy[j][1][1]},
////                        2
////                );
//        }
        //centerFigure();
//        PixelWriter pixelWriter = ctx.getPixelWriter();
//        for(int i = (int) X_MIN*50; i < X_MAX*50; i++){
//            for (int j = (int) Y_MIN*50; j < Y_MAX*50; j++){
//                ctx.getPixelWriter().setColor(
//                        //10,10, Color.RED
//                        (int) (((myCanvas.getWidth()) / 2) + i),(int) (((myCanvas.getHeight()) / 2) - j), Color.FIREBRICK
//                );
//            }
//        }


        if (numLab == 1) {
            int u = 0;
            for (int i = 0; i < quantityLines; i++) {
                u += 20;
                ctx.setFill(Color.rgb(0, 194, 255, 0.5));

                ctx.fillPolygon(
                        new double[]{pointArrayCopy[i][0][0], pointArrayCopy[i][1][0], pointArrayCopy[i][2][0], pointArrayCopy[i][3][0]},
                        new double[]{pointArrayCopy[i][0][1], pointArrayCopy[i][1][1], pointArrayCopy[i][2][1], pointArrayCopy[i][3][1]},
                        4
                );
                ctx.setStroke(Color.rgb(207, 84, 31));
                ctx.strokePolygon(
                        new double[]{pointArrayCopy[i][0][0], pointArrayCopy[i][1][0], pointArrayCopy[i][2][0], pointArrayCopy[i][3][0]},
                        new double[]{pointArrayCopy[i][0][1], pointArrayCopy[i][1][1], pointArrayCopy[i][2][1], pointArrayCopy[i][3][1]},
                        4
                );
            }
        } else if (numLab == 2) {
            Robertson();
            int u = 0;
            int c = 30;
            for (int i = 0; i < quantityLines; i++) {
                if (visibilityPolygon[i]) {

                    u += 30;
                    ctx.setFill(Color.rgb(0, 194, 255, 0.7));

                    ctx.fillPolygon(
                            new double[]{pointArrayCopy[i][0][0], pointArrayCopy[i][1][0], pointArrayCopy[i][2][0], pointArrayCopy[i][3][0]},
                            new double[]{pointArrayCopy[i][0][1], pointArrayCopy[i][1][1], pointArrayCopy[i][2][1], pointArrayCopy[i][3][1]},
                            4
                    );
                    ctx.setStroke(Color.rgb(207, 84, 31));
                    ctx.strokePolygon(
                            new double[]{pointArrayCopy[i][0][0], pointArrayCopy[i][1][0], pointArrayCopy[i][2][0], pointArrayCopy[i][3][0]},
                            new double[]{pointArrayCopy[i][0][1], pointArrayCopy[i][1][1], pointArrayCopy[i][2][1], pointArrayCopy[i][3][1]},
                            4
                    );
                }
            }
        } else if (numLab == 3) {
            centerFigure();
            Robertson();
            max_minPolygonPoint();

            for (int q = 0; q < myCanvas.getHeight(); q++) {
                for (int j = 0; j < myCanvas.getWidth(); j++) {
                    kadBuffer[q][j] = Color.WHITESMOKE;
                }
            }

            int one;
            int two;
            int three;
            int four;
            int otherOneFirst;
            int otherTwoFirst;
            int otherOneSecond;
            int otherTwoSecond;
            int sY;
            int sX;
            double deltaY;
            double deltaX;
            ArrayList<Integer> pointR = new ArrayList<Integer>(); //Динамический массив
//            Color[] colorPoly = new Color[]{Color.DARKBLUE, Color.RED, Color.GOLD, Color.LIME, Color.FIREBRICK,
//                    Color.KHAKI, Color.ORANGE, Color.BLACK, Color.GREEN, Color.BLUE, Color.CRIMSON, Color.BLUEVIOLET, Color.BROWN, Color.BURLYWOOD, Color.YELLOW,
//                    Color.CADETBLUE, Color.DARKRED, Color.CHOCOLATE, Color.CORAL, Color.DARKKHAKI, Color.CYAN, Color.GREENYELLOW, Color.ORCHID, Color.FORESTGREEN, Color.PLUM};

            for (int i = 0; i < quantityLines; i++) {
                for (int y = (int) (myCanvas.getHeight() / 2 - minMaxPolyPoint[i][3] * 50), h = 0; y < myCanvas.getHeight() / 2 - minMaxPolyPoint[i][2] * 50; y++,h++) {
                    for (int x = (int) (myCanvas.getWidth() / 2 + minMaxPolyPoint[i][0] * 50); x < myCanvas.getWidth() / 2 + minMaxPolyPoint[i][1] * 50; x++) {
                        for (int d = 0, f = 1, v, g, l, k, w=0; d < 4; d++, f++) {
                            if (f == 4) {
                                f = 0;
                            }
                            v = d + 1;
                            if (v == 4) {
                                v = 0;
                            }
                            g = f + 1;
                            if (g == 4) {
                                g = 0;
                            }
                            l = d - 1;
                            if (l == -1) {
                                l = 3;
                            }
                            k = f - 1;
                            if (k == -1) {
                                k = 3;
                            }
                            if (w == 2) {
                                w = 0;
                            }
                            one = (int) (myCanvas.getHeight() / 2 - pointArrayCopy[i][d][1] * 50);
                            two = (int) (myCanvas.getHeight() / 2 - pointArrayCopy[i][f][1] * 50);
                            otherOneFirst = (int) (myCanvas.getHeight() / 2 - pointArrayCopy[i][v][1] * 50);
                            otherTwoFirst = (int) (myCanvas.getHeight() / 2 - pointArrayCopy[i][g][1] * 50);
                            otherOneSecond = (int) (myCanvas.getHeight() / 2 - pointArrayCopy[i][l][1] * 50);
                            otherTwoSecond = (int) (myCanvas.getHeight() / 2 - pointArrayCopy[i][k][1] * 50);
                            three = (int) (myCanvas.getWidth() / 2 + pointArrayCopy[i][d][0] * 50);
                            four = (int) (myCanvas.getWidth() / 2 + pointArrayCopy[i][f][0] * 50);
                            sY = Math.abs(two - one);
                            sX = Math.abs(three - four);
                            if (one < two) {
                                deltaY = Math.abs(one - y);
                                if ((y >= one && y <= two) && ((y + 1 >= otherOneFirst && y + 1 <= otherTwoFirst) || (y + 1 >= otherOneSecond && y + 1 <= otherTwoSecond))) {
                                    deltaX = Math.abs((sX * deltaY) / sY);
                                    if (three <= four) {
                                        //pointArrPoly[h][w] = (int) (three + deltaX);
                                        pointR.add((int) (three + deltaX));
                                    } else {
                                       // pointArrPoly[h][w] = (int) (three - deltaX);
                                        pointR.add((int) (three - deltaX));
                                    }
                                    w++;
                                } else if (y > one && y < two) {
                                    deltaX = Math.abs((sX * deltaY) / sY);
                                    if (three <= four) {
                                       // pointArrPoly[h][w] = (int) (three + deltaX);
                                        pointR.add((int) (three + deltaX));
                                    } else {
                                       // pointArrPoly[h][w] = (int) (three - deltaX);
                                        pointR.add((int) (three - deltaX));
                                    }
                                    w++;
                                }
                            } else {
                                deltaY = Math.abs(two - y);
                                if ((y < one && y > two) && (y + 0.5 <= one && y + 0.5 >= two) || (y - 0.5 <= one && y - 0.5 >= two)) {
                                    deltaX = Math.abs((sX * deltaY) / sY);
                                    if (three <= four) {
                                       // pointArrPoly[h][w] = (int) (four - deltaX);
                                        pointR.add((int) (four - deltaX));
                                    } else {
                                       // pointArrPoly[h][w] = (int) (four + deltaX);
                                        pointR.add((int) (four + deltaX));
                                    }
                                    w++;
                                }
                            }
                        }
                    }
                    try {
                        int n = pointR.get(0);
                        int m = pointR.get(1);
                        double z;
                        if (n <= m) {
                            for (int l = n; l < m; l++) {
                                z = (-(normal[i][0] * ((l - myCanvas.getWidth()/2)/50)) - (normal[i][1] * ((myCanvas.getHeight()/2 - y)/50)) - kD[i] ) / normal[i][2];
                                if (z >= zBufferArr[y][l]) {
                                    kadBuffer[y][l] = myColors[i];
                                    zBufferArr[y][l] = z;
                                }
                                else {
                                    ctx.getPixelWriter().setColor(
                                            l, y, Color.PINK
                                    );
                                }
                            }
                        } else {
                            for (int l = m; l < n; l++) {
                                z = (-(normal[i][0] * ((l - myCanvas.getWidth()/2)/50)) - (normal[i][1] * ((myCanvas.getHeight()/2 - y)/50)) - kD[i] ) / normal[i][2];
                                if (z >= zBufferArr[y][l]) {
                                    kadBuffer[y][l] = myColors[i];
                                    zBufferArr[y][l] = z;
                                }
                                else {
                                    ctx.getPixelWriter().setColor(
                                            l, y, Color.WHITESMOKE
                                    );
                                }
                            }
                        }
                        pointR.clear();
                    } catch (Exception e) {
                        //System.out.print(" Empty line Rast Error: " + e);
                    }
                }
            }
            for (int q = 0; q < myCanvas.getHeight(); q++) {
                for (int j = 0; j < myCanvas.getWidth(); j++) {
                    ctx.getPixelWriter().setColor(
                            j, q, kadBuffer[q][j]
                    );
                    zBufferArr[q][j] = -1000000;
                }
            }
        }
        ctx.restore();
    }

    //Кнопка старт
    public void resetData(ActionEvent actionEvent) {
        offsetX = 0;
        offsetY = 0;
        resetDataFun();
        draw();
    }

    //Умножение Точек на матрицу
    public void multiMat(Double[][] par) {
        double fLine;
        Double[][][] multi = new Double[quantityLines][getPointsInLine][4];
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
        pointArrayCopy = multi;
    }

    //Умножение Точек на матрицу для ОПП
    public void multiMatOPP(Double[][] par, Double f) {
        double fLine;
        Double[][][] multi = new Double[quantityLines][getPointsInLine][4];
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

    // кнопка паралельного переноса
    public void parTransForm(ActionEvent actionEvent) {
        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        parallelTransfer();
        draw();
        System.out.println("||changeParallel||");
        System.out.println(Arrays.deepToString(pointArrayCopy));
    }

    //Кнопка маштабирования
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

    //Функция паралельного переноса
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

    //функция маштабирования
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
        double minX = pointArrayCopy[0][0][0];
        double maxX = pointArrayCopy[0][0][0];
        double minY = pointArrayCopy[0][0][1];
        double maxY = pointArrayCopy[0][0][1];
        double minZ = pointArrayCopy[0][0][2];
        double maxZ = pointArrayCopy[0][0][2];
        double getX;
        double getY;
        double centerX;
        double centerY;
        double centerZ;
        double scaling;
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
        //Переменные для вписывания
        getX = (myCanvas.getWidth() / ((maxX - minX) * 50));
        getY = (myCanvas.getHeight() / ((maxY - minY) * 50));
        if (getX < getY) {
            scaling = getX;
        } else {
            scaling = getY;
        }
        Double[][] mat1 = {
                {scaling, 0.0, 0.0, 0.0},
                {0.0, scaling, 0.0, 0.0},
                {0.0, 0.0, scaling, 0.0},
                {0.0, 0.0, 0.0, 1.0},
        };
        multiMat(mat1);

        minX = pointArrayCopy[0][0][0];
        maxX = pointArrayCopy[0][0][0];
        minY = pointArrayCopy[0][0][1];
        maxY = pointArrayCopy[0][0][1];
        minZ = pointArrayCopy[0][0][2];
        maxZ = pointArrayCopy[0][0][2];
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
                if (minZ > pointArrayCopy[i][j][2]) {
                    minZ = pointArrayCopy[i][j][2];
                }
                if (maxZ < pointArrayCopy[i][j][2]) {
                    maxZ = pointArrayCopy[i][j][2];
                }
            }
        }
        centerX = (maxX + minX) / 2;
        centerY = (maxY + minY) / 2;
        centerZ = (maxZ + minZ) / 2;
        Double[][] mat2 = {
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0},
                {-centerX, -centerY, -centerZ, 1.0},
        };
        multiMat(mat2);

        System.out.println(minX);
        System.out.println(maxX);

        ctx.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        draw();
    }

    double X_MIN;
    double Y_MIN;
    double X_MAX;
    double Y_MAX;

    public void centerFigure() {
        double minX = pointArrayCopy[0][0][0];
        double maxX = pointArrayCopy[0][0][0];
        double minY = pointArrayCopy[0][0][1];
        double maxY = pointArrayCopy[0][0][1];
        double minZ = pointArrayCopy[0][0][2];
        double maxZ = pointArrayCopy[0][0][2];
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
                if (minZ > pointArrayCopy[i][j][2]) {
                    minZ = pointArrayCopy[i][j][2];
                }
                if (maxZ < pointArrayCopy[i][j][2]) {
                    maxZ = pointArrayCopy[i][j][2];
                }
            }
        }
        X_MAX = maxX;
        Y_MAX = maxY;
        X_MIN = minX;
        Y_MIN = minY;
        pointInCenterFigure[0] = (maxX + minX) / 2;
        pointInCenterFigure[1] = (maxY + minY) / 2;
        pointInCenterFigure[2] = (maxZ + minZ) / 2;
    }

    public void Robertson() {
        visibilityPolygon = new boolean[quantityLines];//хранит значение если true то рисуем
        double[][] startPoint = new double[quantityLines][3];//начало двух не колинеарных векторов каждого полигона
        double[][][] vectors = new double[quantityLines][2][3];//по два вектора на полигон
        normal = new double[quantityLines][3];//массив векторов нормали
        kD = new double[quantityLines];
        for (int k = 0; k < quantityLines; k++) {
            for (int j = 0; j < 3; j++) {
                startPoint[k][j] = pointArrayCopy[k][0][j];
            }
        }

        //формируем вектора
        for (int k = 0; k < quantityLines; k++) {
            System.out.println("|-| New polygon |-|");
            for (int j = 1, m = 0; j < getPointsInLine - 1; j++, m++) {
                for (int i = 0; i < 3; i++) {
                    vectors[k][m][i] = pointArrayCopy[k][j][i] - startPoint[k][i];
                }
            }
        }

        //формируем нормали векторов
        for (int k = 0; k < quantityLines; k++) {
            normal[k][0] = (vectors[k][0][1] * vectors[k][1][2]) - (vectors[k][0][2] * vectors[k][1][1]);
            normal[k][1] = (vectors[k][0][2] * vectors[k][1][0]) - (vectors[k][0][0] * vectors[k][1][2]);
            normal[k][2] = (vectors[k][0][0] * vectors[k][1][1]) - (vectors[k][0][1] * vectors[k][1][0]);
        }

        // находим D для уравнения плоскости для zBuffer
        for (int a = 0; a < quantityLines; a++) {
            kD[a] = -1 * ((normal[a][0] * pointArrayCopy[a][0][0]) + (normal[a][1] * pointArrayCopy[a][0][1]) + (normal[a][2] * pointArrayCopy[a][0][2]));
            //kD[a] = (-(normal[a][0] * pointArrayCopy[a][0][0]) - (normal[a][1] * pointArrayCopy[a][0][1]) - (normal[a][2] * pointArrayCopy[a][0][2]));
        }

        if(numLab == 2){
            double[] e = new double[3];
            e[0] = 0.0;
            e[1] = 0.0;
            e[2] = -1.0;
            centerFigure();
            double normalTest;
            double visYesOrNo;//скалярное произведение вектора наблюдателя на вектор нормали
            for (int k = 0; k < quantityLines; k++) {
                double[] test = new double[3];
                normalTest = 0;
                visYesOrNo = 0;
                for (int i = 0; i < 3; i++) {
                    test[i] = pointInCenterFigure[i] - startPoint[k][i];
                    normalTest += test[i] * normal[k][i];
                }
                if (normalTest > 0) {
                    for (int j = 0; j < 3; j++) {
                        normal[k][j] = normal[k][j] * -1;
                    }
                }
                for (int n = 0; n < 3; n++) {
                    visYesOrNo += e[n] * normal[k][n];
                }
                if (visYesOrNo < 0) {
                    visibilityPolygon[k] = true;
                } else {
                    visibilityPolygon[k] = false;
                }
            }
            System.out.println(Arrays.toString(visibilityPolygon));
        }


    }
    public void max_minPolygonPoint() {
        double minPolyX = pointArrayCopy[0][0][0];
        double maxPolyX = pointArrayCopy[0][0][0];
        double minPolyY = pointArrayCopy[0][0][1];
        double maxPolyY = pointArrayCopy[0][0][1];
        minMaxPolyPoint = new double[quantityLines][4];
        for (int i = 0; i < quantityLines; i++) {
            for (int j = 0; j < getPointsInLine; j++) {
                if (minPolyX > pointArrayCopy[i][j][0]) {
                    minPolyX = pointArrayCopy[i][j][0];
                }
                if (maxPolyX < pointArrayCopy[i][j][0]) {
                    maxPolyX = pointArrayCopy[i][j][0];
                }
                if (minPolyY > pointArrayCopy[i][j][1]) {
                    minPolyY = pointArrayCopy[i][j][1];
                }
                if (maxPolyY < pointArrayCopy[i][j][1]) {
                    maxPolyY = pointArrayCopy[i][j][1];
                }
            }
            minMaxPolyPoint[i][0]=minPolyX;
            minMaxPolyPoint[i][1]=maxPolyX;
            minMaxPolyPoint[i][2]=minPolyY;
            minMaxPolyPoint[i][3]=maxPolyY;
        }
    }


    public void colorsSplitButton(ActionEvent actionEvent) {

    }
}
