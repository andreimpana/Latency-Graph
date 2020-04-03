package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.print.PageLayout;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.net.InetAddress;
import java.util.GregorianCalendar;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Latency over WiFi");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis(); // we are gonna plot against time
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Ping(ms)");
        yAxis.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Network Latency");
        lineChart.setAnimated(false); // disable animations

        //define a series to display data
        XYChart.Series<String, Number> current_ping = new XYChart.Series<>();
        current_ping.setName("Latency");

        //define average series
        XYChart.Series<String, Number> avg = new XYChart.Series<>();
        avg.setName("Average latency ");

        // add series to chart
        lineChart.getData().add(current_ping);
        lineChart.getData().add(avg);

        // setup scene
        Scene scene = new Scene(lineChart, 800, 400);
        //Scene SuspendButton = new Scene(new Button("Suspend"), 100, 100);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
        }
        });


        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


        final int[] count = {0};
        final long[] totalPing = {0};
        final double[] avergaePing = {0};

        // Put latency into Graph
        if(primaryStage.isShowing()) {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                Platform.runLater(() -> {
                    try {
                        System.out.println(count[0]);
                        Date now = new Date();

                        String ipAddress = "www.google.ca";
                        InetAddress inet = InetAddress.getByName(ipAddress);

                        System.out.println("Sending Ping Request to: " + ipAddress);

                        long finish = 0;
                        long start = new GregorianCalendar().getTimeInMillis();


                        if (inet.isReachable(5000)) {
                            finish = new GregorianCalendar().getTimeInMillis();
                            System.out.println("Ping RTT: " + (finish - start + "ms") + " Time: " + now);
                            current_ping.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), finish - start));



                            totalPing[0] += finish - start;
                        } else {
                            System.out.println(ipAddress + " NOT reachable.");
                            current_ping.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), 0));
                        }

                        if(count[0] == 5){
                            avergaePing[0] = totalPing[0] / 5;
                            System.out.println(avergaePing[0]);
                            avg.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), avergaePing[0]));
                            count[0] = 0;
                            avergaePing[0] = 0;
                            totalPing[0] = 0;
                        }

                        count[0]++;
                        


                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                    }
                });
            }, 0, 1, TimeUnit.SECONDS);
        }
    }
}


