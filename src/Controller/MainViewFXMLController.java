
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Target;
import Util.Pinger;
import Util.SysUtil;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 *
 * @author root
 */
public class MainViewFXMLController implements Initializable {

    SysUtil sysUtil;
    long lastLabelUpdate = 0;
    long lastCycleInterval = 0;
    int cycle = 0;

    static ObservableList<Target> targets = FXCollections.observableArrayList();

    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis timeAxis;
    XYChart.Series series;

    @FXML
    private TableView target_table;

    @FXML
    private BarChart<String, Number> cpu_graph;
    @FXML
    private CategoryAxis categoryAxis;
    XYChart.Series cpuSeries;

    @FXML
    private PieChart storage_chart;
    XYChart.Series storageSeries;

    @FXML
    private Label cpu_label;
    @FXML
    private Label ram_label;
    @FXML
    private Label net_label;
    @FXML
    private Label freespace_label;
    @FXML
    private Label usedspace_label;
    @FXML
    private Label totalspace_label;

    @FXML
    private Label targets_infoLabel;
    @FXML
    private Label active_infoLabel;
    @FXML
    private Label timedout_infoLabel;
    @FXML
    private Label unreachable_infoLabel;
    @FXML
    private Label unknownhosts_infoLabel;
    @FXML
    private Label lastupdate_infoLabel;
    
    @FXML private LineChart<Number,Number> cycleChart;
    @FXML private NumberAxis secondAxis;
    XYChart.Series cycleSeries;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sysUtil = new SysUtil();
        createTargets();
        initTable();
        initChart();
        initCPUgraph();
        initCycleChart();
        setLastLabelUpdate();
        startLabelUpdateCounter();
        beginPing();
    }

    public static void createTargets() {
        Target t1 = new Target("PENDING", "Serveriai.lt", null, "79.98.29.20", null, 0);
        Target t2 = new Target("PENDING", "Delfi", "www.delfi.lt", null, null, 0);
        Target t3 = new Target("PENDING", "Google.org", "www.google.org", null, null, 0);
        Target t4 = new Target("PENDING", "Local peer", null, "192.168.1.140", null, 0);
        Target t5 = new Target("PENDING", "Amazon.co.uk", "www.amazon.co.uk", null, null, 0);
        Target t6 = new Target("PENDING", "NASA", "www.nasa.gov", null, null, 0);
        Target t7 = new Target("PENDING", "BBC", "www.bbc.co.uk", null, null, 0);
        Target t8 = new Target("PENDING", "FakeWebsite.com", "www.longfakewebsitename.com", "123.456.789.245", null, 0);
        Target t9 = new Target("PENDING", "Linux Mint", "www.linuxmint.com", null, null, 0);
        Target t0 = new Target("PENDING", "Facebook", "www.facebook.com", null, null, 0);
        Target t10 = new Target("PENDING", "Youtube", "www.youtube.com", null, null, 0);
        Target t11 = new Target("PENDING", "W3Schools", "www.w3schools.com", null, null, 0);
        Target t12 = new Target("PENDING", "Docs.Oracle", "docs.oracle.com", null, null, 0);
        Target t13 = new Target("PENDING", "StackOverflow", "stackoverflow.com", null, null, 0);
        Target t14 = new Target("PENDING", "ClassicRock.fm", null, "5.20.233.18", null, 0);
        Target t15 = new Target("PENDING", "GMail", "mail.google.com", null, null, 0);
        Target t16 = new Target("PENDING", "Mail.com", "www.mail.com", null, null, 0);
        Target t17 = new Target("PENDING", "Tutorialspoint", "www.tutorialspoint.com", null, null, 0);
        Target t18 = new Target("PENDING", "Swedbank.lt", "www.swedbank.lt", null, null, 0);
        Target t19 = new Target("PENDING", "localhost", null, "127.0.0.1", null, 0);
        Target t20 = new Target("PENDING", "Simulated UNREACHABLE", null, "192.168.2.123", null, 0);
        Target t21 = new Target("PENDING", "Australia DNS", "ns1.telstra.net", "139.130.4.5",null,0);
        Target t22 = new Target("PENDING", "Google DNS 1", "google-public-dns-a.google.com.", "8.8.8.8",null,0);
        Target t23 = new Target("PENDING", "Google DNS 2", "google-public-dns-b.google.com.", "8.8.4.4",null,0);
        Target t24 = new Target("PENDING", "LjreMC", "www.lejremc.dk",null, null,0);

        targets.add(t1);targets.add(t2);targets.add(t3);targets.add(t4);targets.add(t5);targets.add(t6);targets.add(t7);targets.add(t8);targets.add(t9);
        targets.add(t0);targets.add(t10);targets.add(t11);targets.add(t12);targets.add(t13);targets.add(t14);targets.add(t15);targets.add(t16);
        targets.add(t17);targets.add(t18);targets.add(t19);targets.add(t20);targets.add(t21);targets.add(t22);targets.add(t23);targets.add(t24);

    }

    public void beginPing() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                while (true) {
                    for (Target t : targets) {
                        for (XYChart.Series s : lineChart.getData()) {
                            String ping = null;
                            try {
                                
                                if (s.getName().equals(t.nameProperty().get())) {
                                    Callable c = new Pinger(t);
                                    ping = c.call().toString();
                                    switch (ping) {
                                        case "TIME_OUT":
                                            addToChart(s, cycle, 00.00);
                                            t.setStatus("TIME OUT");
                                            t.setLastrtt("TIME_OUT");
                                            t.setTimeouts(t.timeoutsProperty().get() + 1);
                                            break;
                                        case "UNKNOWN_HOST":
                                            t.setStatus("ERROR");
                                            t.setLastrtt("UNKNOWN HOST");
                                            break;
                                        case "UNREACHABLE":
                                            t.setStatus("ERROR");
                                            t.setLastrtt("UNREACHABLE HOST");
                                            break;
                                        default:
                                            t.setLastrtt(ping);
                                            t.setStatus("ACTIVE");
                                            addToChart(s, cycle, Double.valueOf(ping));
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println(ping);
                                e.printStackTrace();
                            }
                        }
                    }
                    cycle++;
                    rangeChart(cycle);
                    updateInfo();
                }
            }
        }).start();
    }

    public void initChart() {
        lineChart.setCreateSymbols(false);
        timeAxis.setLowerBound(00.00);
        timeAxis.setUpperBound(100.00);
        for (Target t : targets) {
            series = new XYChart.Series();
            series.setName(t.nameProperty().get());
            lineChart.getData().add(series);
        }
    }
    
    public void initCycleChart(){
        cycleChart.setCreateSymbols(false);
        cycleChart.setLegendVisible(false);
        secondAxis.setLowerBound(00.00);
        secondAxis.setUpperBound(50.00);
        cycleSeries = new XYChart.Series();
        cycleSeries.setName("Interval");
        cycleChart.getData().add(cycleSeries);
    }

    public void rangeChart(int i) {
        if (i >= timeAxis.getUpperBound()) {
            timeAxis.setLowerBound(timeAxis.getLowerBound() + 20);
            timeAxis.setUpperBound(timeAxis.getUpperBound() + 20);
        }
    }

    public void initTable() {

        TableColumn statusCol = new TableColumn();
        TableColumn nameCol = new TableColumn();
        TableColumn domainCol = new TableColumn();
        TableColumn addressCol = new TableColumn();
        TableColumn pingCol = new TableColumn();
        TableColumn timeoutsCol = new TableColumn();

        statusCol.setText("Status");
        nameCol.setText("Name");
        domainCol.setText("Domain");
        addressCol.setText("Address");
        pingCol.setText("Ping");
        timeoutsCol.setText("Timeouts");

        statusCol.prefWidthProperty().bind(target_table.widthProperty().divide(10));
        nameCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(2));
        domainCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(2));
        addressCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(2));
        pingCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(1.5));
        timeoutsCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(1.5));

        target_table.getColumns().addAll(statusCol, nameCol, domainCol, addressCol, pingCol, timeoutsCol);
        target_table.setItems(targets);

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        domainCol.setCellValueFactory(new PropertyValueFactory<>("domain"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        pingCol.setCellValueFactory(new PropertyValueFactory<>("lastrtt"));
        timeoutsCol.setCellValueFactory(new PropertyValueFactory<>("timeouts"));

        statusCol.setCellFactory(new Callback<TableColumn<Target, String>, TableCell<Target, String>>() {

            @Override
            public TableCell<Target, String> call(TableColumn<Target, String> p) {

                return new TableCell<Target, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            String color = null;
                            switch (String.valueOf(item)) {
                                case "ERROR":
                                    color = "red";
                                    break;
                                case "ACTIVE":
                                    color = "green";
                                    break;
                                case "TIME OUT":
                                    color = "yellow";
                                    break;
                            }
                            this.setStyle("-fx-background-color:" + color);
                            setText(item);
                        }
                    }
                };
            }
        });
    }

    public synchronized void addToChart(XYChart.Series s, int pos, Double value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                s.getData().add(new XYChart.Data(pos, value));
            }
        });
    }

    public void initCPUgraph() {
        cpu_graph.setCategoryGap(10);

        cpuSeries = new XYChart.Series();
        cpu_graph.getData().add(cpuSeries);

        cpu_graph.setLegendVisible(false);
        ArrayList<XYChart.Data> data = new ArrayList();

        XYChart.Data dataObj0 = new XYChart.Data<>("CPU", 00.00);
        XYChart.Data dataObj1 = new XYChart.Data<>("RAM", 00.00);
        XYChart.Data dataObj2 = new XYChart.Data<>("NET", 00.00);

        data.add(dataObj0);
        data.add(dataObj1);
        data.add(dataObj2);
        cpuSeries.getData().addAll(data);
        dataObj0.getNode().setStyle("-fx-bar-fill: #006BB2;");
        dataObj1.getNode().setStyle("-fx-bar-fill: #0099FF;");
        dataObj2.getNode().setStyle("-fx-bar-fill: #66C2FF;");
        sysUtil.setMaxTraffic(100);

        storage_chart.setTitle("Space");
        storage_chart.setLegendVisible(false);

        PieChart.Data pcd1 = new PieChart.Data("Used", sysUtil.getUsedSpacePercentLinux() * 100);
        PieChart.Data pcd2 = new PieChart.Data("Free", 100 - sysUtil.getUsedSpacePercentLinux() * 100);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(pcd1, pcd2);

        storage_chart.setData(pieChartData);
        pcd1.getNode().setStyle("-fx-pie-color: #006BB2;");
        pcd2.getNode().setStyle("-fx-pie-color: #0099FF;");

        new Thread(new Runnable() {
            @Override
            public void run() {
                long totalspace = sysUtil.getTotalSpaceKbLinux();
                String totalString = String.valueOf(totalspace / 1024) + " Mb" + " (" + (totalspace / 1024 / 1024) + " Gb)";
                updateLabel(totalspace_label, totalString);
                while (true) {
                    for (XYChart.Data dataObj : data) {
                        switch (dataObj.getXValue().toString()) {
                            case "CPU":
                                Double dub1 = sysUtil.getCPULoad();
                                if (dub1 != null) {
                                    dataObj.setYValue(dub1);
                                    updateLabel(cpu_label, String.valueOf(dub1) + "%");
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case "RAM":
                                Double dub2 = sysUtil.getMemoryUsage();
                                dub2 = (double) Math.round(dub2 * 100) / 100;
                                if (dub2 != null) {
                                    dataObj.setYValue(dub2);
                                    updateLabel(ram_label, String.valueOf(dub2) + "%");
                                }
                                break;
                            case "NET":
                                Double dub3 = sysUtil.getNetworkUsage();
                                dub3 = (double) Math.round(dub3 * 100) / 100;
                                if (dub3 > sysUtil.getMaxTraffic()) {
                                    sysUtil.setMaxTraffic(dub3.intValue() + 100);
                                }
                                if (dub3 != null) {
                                    dataObj.setYValue(dub3);
                                    updateLabel(net_label, String.valueOf(dub3) + "%");
                                }
                                break;
                        }
                    }
                    for (PieChart.Data d : storage_chart.getData()) {
                        String forSwitch = d.getName().substring(0, 4);
                        switch (forSwitch) {
                            case "Used":
                                Double value1 = sysUtil.getUsedSpacePercentLinux();
                                d.setPieValue(value1 * 100);
                                updatePieLabel(d, forSwitch + " " + (value1 * 100) + "%");
                                long usedspace = sysUtil.getUsedSpaceKbLinux();
                                String label1 = String.valueOf(usedspace / 1024) + " Mb" + " (" + (usedspace / 1024 / 1024) + " Gb)";
                                updateLabel(usedspace_label, label1);
                                break;
                            case "Free":
                                Double value2 = 1 - sysUtil.getUsedSpacePercentLinux();
                                d.setPieValue(value2 * 100);
                                updatePieLabel(d, forSwitch + " " + (value2 * 100) + "%");
                                long freespacekb = sysUtil.getFreeSpaceKbLinux();
                                String label2 = String.valueOf(freespacekb / 1024) + " Mb" + " (" + (freespacekb / 1024 / 1024) + " Gb)";
                                updateLabel(freespace_label, label2);
                                break;
                        }
                    }
                }
            }
        }).start();
    }

    public void updateLabel(Label l, String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                l.setText(s);
            }
        });
    }

    public void updatePieLabel(PieChart.Data d, String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                d.setName(s);
            }
        });
    }

    public void updateInfo() {
        int total = targets.size();
        int timeouts = 0;
        int unreachable = 0;
        int unknown = 0;
        int active = 0;

        for (Target t : targets) {
            switch (t.lastrttProperty().get()) {
                case "TIME_OUT":
                    timeouts++;
                    break;
                case "UNREACHABLE HOST":
                    unreachable++;
                    break;
                case "UNKNOWN HOST":
                    unknown++;
                    break;
                default:
                    active++;
                    break;
            }
        }
        updateLabel(targets_infoLabel, String.valueOf(total));
        updateLabel(active_infoLabel, String.valueOf(active));
        updateLabel(timedout_infoLabel, String.valueOf(timeouts));
        updateLabel(unreachable_infoLabel, String.valueOf(unreachable));
        updateLabel(unknownhosts_infoLabel, String.valueOf(unknown));
        lastCycleInterval = (System.currentTimeMillis()-lastLabelUpdate)/1000;
        updateLabel(lastupdate_infoLabel, String.valueOf(lastCycleInterval+"s"));
        setLastLabelUpdate();
    }

    public void startLabelUpdateCounter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if(cycle+5>secondAxis.getUpperBound()){
                            secondAxis.setUpperBound(cycle+5);
                            secondAxis.setLowerBound(cycle-50);
                        }
                        addToChart(cycleSeries, cycle, Double.valueOf(lastCycleInterval));
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void setLastLabelUpdate() {
        lastLabelUpdate = System.currentTimeMillis();
    }
}
