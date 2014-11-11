
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Target;
import Util.LogUtil;
import Util.Pinger;
import Util.SysUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author root
 */
public class MainViewFXMLController implements Initializable {

    ExecutorService exec;
    Runnable r;
    LogUtil logUtil;
    boolean globalPingerStatus = true;

    SysUtil sysUtil;
    long lastLabelUpdate = 0;
    long lastCycleInterval = 0;
    int cycle = 0;

    static ObservableList<Target> targets = FXCollections.observableArrayList();
    public List<Target> safeTargets;

    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis timeAxis;
    XYChart.Series series;

    @FXML
    private TableView target_table;

    @FXML
    private BarChart<String, Number> cpu_graph;
    XYChart.Series cpuSeries;

    @FXML
    private PieChart storage_chart;
    XYChart.Series storageSeries;

    @FXML
    private AnchorPane overviewAnchorPane;
    @FXML
    TitledPane systemTitledPane;
    @FXML
    AnchorPane systemAnchorPane;
    @FXML
    VBox systemVBox;
    @FXML
    HBox systemHBox;
    @FXML
    VBox system_resourcesVBox;
    @FXML
    VBox system_FileSystemVBox;
    @FXML
    HBox system_FileSystemHBox;
    @FXML
    Pane system_resourcesPane;

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
    TitledPane monitoringTitledPane;
    @FXML
    AnchorPane monitoringAnchorPane;
    @FXML
    Pane infoPane;
    @FXML
    GridPane infoGridPane;

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
    private Label monitored_infoLabel;
    @FXML
    private Label paused_infoLabel;
    @FXML
    private Label lastupdate_infoLabel;

    @FXML
    private LineChart<Number, Number> cycleChart;
    @FXML
    private NumberAxis secondAxis;
    XYChart.Series cycleSeries;

    @FXML
    TextArea output_console;
    @FXML
    TitledPane output_TitledPane;
    @FXML
    AnchorPane output_AnchorPane;

    @FXML
    Button toolbar_tooglePingButton;
    @FXML
    Label toolbar_statusLabel;
    @FXML
    Button toolbar_addPeerButton;
    @FXML
    Button toolbar_removePeersButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sysUtil = new SysUtil();
        initToolbar();
        initLogger();
        createTargets();
        initTable();
        initChart();
        initSystemGraph();
        initCycleChart();
        setLastLabelUpdate();
        startLabelUpdateCounter();
        beginPing();
        updatePingChart();

    }

    public void initToolbar() {
        Image playDark = new Image(getClass().getResourceAsStream("/images/play_dark.png"));
        Image playLight = new Image(getClass().getResourceAsStream("/images/play_light.png"));
        Image pauseDark = new Image(getClass().getResourceAsStream("/images/pause_dark.png"));
        Image pauseLight = new Image(getClass().getResourceAsStream("/images/pause_light.png"));
        Image xDark = new Image(getClass().getResourceAsStream("/images/x_dark.png"));
        Image xLight = new Image(getClass().getResourceAsStream("/images/x_light.png"));
        Image plusDark = new Image(getClass().getResourceAsStream("/images/plus_dark.png"));
        Image plusLight = new Image(getClass().getResourceAsStream("/images/plus_light.png"));

        ImageView playImageView = new ImageView(pauseDark);
        playImageView.setPreserveRatio(true);
        playImageView.setFitHeight(15.00);

        ImageView addPeerImageView = new ImageView(plusDark);
        ImageView removePeersImageView = new ImageView(xDark);
        addPeerImageView.setPreserveRatio(true);
        removePeersImageView.setPreserveRatio(true);
        addPeerImageView.setFitHeight(15.00);
        removePeersImageView.setFitHeight(15.00);

        toolbar_tooglePingButton.setText("");
        toolbar_tooglePingButton.setGraphic(playImageView);

        toolbar_addPeerButton.setGraphic(addPeerImageView);
        toolbar_removePeersButton.setGraphic(removePeersImageView);
        toolbar_addPeerButton.prefWidthProperty().bind(toolbar_removePeersButton.widthProperty());

        toolbar_tooglePingButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (playImageView.getImage() == playDark) {
                    playImageView.setImage(playLight);
                } else {
                    playImageView.setImage(pauseLight);
                }
            }
        });
        toolbar_tooglePingButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (playImageView.getImage() == pauseLight) {
                    playImageView.setImage(playDark);
                    globalPingerStatus = false;
                    if (!exec.isShutdown()) {
                        updateLabel(toolbar_statusLabel, "Finishing cycle");
                        colorLabel(toolbar_statusLabel, Color.ORANGE);
                        exec.shutdown();
                    }
                } else {
                    playImageView.setImage(pauseDark);
                    globalPingerStatus = true;
                    beginPing();
                }

            }
        });

        toolbar_addPeerButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (addPeerImageView.getImage() == plusDark) {
                    addPeerImageView.setImage(plusLight);
                } else {
                    addPeerImageView.setImage(plusLight);
                }
            }
        });
        toolbar_addPeerButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (addPeerImageView.getImage() == plusLight) {
                    addPeerImageView.setImage(plusDark);
                } else {
                    addPeerImageView.setImage(plusDark);
                }
                showTargetEditor();
            }
        });

        toolbar_removePeersButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (removePeersImageView.getImage() == xDark) {
                    removePeersImageView.setImage(xLight);
                } else {
                    removePeersImageView.setImage(xDark);
                }
            }
        });
        toolbar_removePeersButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (removePeersImageView.getImage() == xLight) {
                    removePeersImageView.setImage(xDark);
                } else {
                    removePeersImageView.setImage(xLight);
                }

            }
        });
    }

    public void showTargetEditor() {
        final Stage dialog = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/servmonfx/TargetEditorFXML.fxml"));
            dialog.initModality(Modality.APPLICATION_MODAL);
            Scene dialogScene = new Scene(root);
            dialogScene.getStylesheets().add("/servmonfx/TargetEditorFXML.fxml");
            dialog.setScene(dialogScene);
            dialog.resizableProperty().set(false);
            dialog.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTargets() {
        Target t1 = new Target("PENDING", "Serveriai.lt", null, "79.98.29.20", null, 0, "A", 0, false);
        Target t2 = new Target("PENDING", "Delfi", "www.delfi.lt", null, null, 0, "A", 0, false);
        Target t3 = new Target("PENDING", "Google.org", "www.google.org", null, null, 0, "A", 0, false);
        Target t4 = new Target("PENDING", "Local peer", null, "192.168.1.140", null, 0, "A", 0, false);
        Target t5 = new Target("PENDING", "Amazon.co.uk", "www.amazon.co.uk", null, null, 0, "A", 0, false);
        Target t6 = new Target("PENDING", "NASA", "www.nasa.gov", null, null, 0, "A", 0, false);
        Target t7 = new Target("PENDING", "BBC", "www.bbc.co.uk", null, null, 0, "A", 0, false);
        Target t8 = new Target("PENDING", "FakeWebsite.com", "www.longfakewebsitename.com", "123.456.789.245", null, 0, "A", 0, false);
        Target t9 = new Target("PENDING", "Linux Mint", "www.linuxmint.com", null, null, 0, "A", 0, false);
        Target t0 = new Target("PENDING", "Facebook", "www.facebook.com", null, null, 0, "A", 0, false);
        Target t10 = new Target("PENDING", "Youtube", "www.youtube.com", null, null, 0, "A", 0, false);
        Target t11 = new Target("PENDING", "W3Schools", "www.w3schools.com", null, null, 0, "A", 0, false);
        Target t12 = new Target("PENDING", "Docs.Oracle", "docs.oracle.com", null, null, 0, "A", 0, false);
        Target t13 = new Target("PENDING", "StackOverflow", "stackoverflow.com", null, null, 0, "A", 0, false);
        Target t14 = new Target("PENDING", "ClassicRock.fm", null, "5.20.233.18", null, 0, "A", 0, false);
        Target t15 = new Target("PENDING", "GMail", "mail.google.com", null, null, 0, "A", 0, false);
        Target t16 = new Target("PENDING", "Mail.com", "www.mail.com", null, null, 0, "A", 0, false);
        Target t17 = new Target("PENDING", "Tutorialspoint", "www.tutorialspoint.com", null, null, 0, "A", 0, false);
        Target t18 = new Target("PENDING", "Swedbank.lt", "www.swedbank.lt", null, null, 0, "A", 0, false);
        Target t19 = new Target("PENDING", "localhost", null, "127.0.0.1", null, 0, "AM", 0, false);
        Target t20 = new Target("PENDING", "Simulated UNREACHABLE", null, "192.168.2.123", null, 0, "A", 0, false);
        Target t21 = new Target("PENDING", "Australia DNS", "ns1.telstra.net", "139.130.4.5", null, 0, "A", 0, false);
        Target t22 = new Target("PENDING", "Google DNS 1", "google-public-dns-a.google.com.", "8.8.8.8", null, 0, "A", 0, false);
        Target t23 = new Target("PENDING", "Google DNS 2", "google-public-dns-b.google.com.", "8.8.4.4", null, 0, "A", 0, false);
        Target t24 = new Target("PENDING", "LjreMC", "www.lejremc.dk", null, null, 0, "A", 0, false);

        targets.add(t1);
        targets.add(t2);
        targets.add(t3);
        targets.add(t4);
        targets.add(t5);
        targets.add(t6);
        targets.add(t7);
        targets.add(t8);
        targets.add(t9);
        targets.add(t0);
        targets.add(t10);
        targets.add(t11);
        targets.add(t12);
        targets.add(t13);
        targets.add(t14);
        targets.add(t15);
        targets.add(t16);
        targets.add(t17);
        targets.add(t18);
        targets.add(t19);
        targets.add(t20);
        targets.add(t21);
        targets.add(t22);
        targets.add(t23);
        targets.add(t24);

    }

    public void beginPing() {
        safeTargets = new ArrayList<>();
        for (Target t : targets) {
            safeTargets.add(t);
        }
        safeTargets = Collections.synchronizedList(targets);

        exec = Executors.newCachedThreadPool();

        for (int i = 0; i < 4; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        for (Target t : safeTargets) {
                            String ping = null;
                            if (t.isActive() && !t.isIsBeingPinged()) {
                                t.setIsBeingPinged(true);
                                t.setPinged(t.getPinged() + 1);
                                t.setStatus("PINGING");
                                try {
                                    Callable c = new Pinger(t);
                                    ping = c.call().toString();
                                    switch (ping) {
                                        case "TIME_OUT":
                                            t.setStatus("TIME OUT");
                                            t.setLastrtt("TIME_OUT");
                                            t.setTimeouts(t.timeoutsProperty().get() + 1);
                                            logUtil.log(LogUtil.INFO, t.nameProperty().get() + " - timed out!");
                                            t.setIsBeingPinged(false);
                                            break;
                                        case "UNKNOWN_HOST":
                                            t.setStatus("ERROR");
                                            t.setLastrtt("UNKNOWN HOST");
                                            logUtil.log(LogUtil.WARNING, t.nameProperty().get() + " - unknown host!");
                                            t.setIsBeingPinged(false);
                                            break;
                                        case "UNREACHABLE":
                                            t.setStatus("ERROR");
                                            t.setLastrtt("UNREACHABLE HOST");
                                            logUtil.log(LogUtil.WARNING, t.nameProperty().get() + " - is unreachable!");
                                            t.setIsBeingPinged(false);
                                            break;
                                        default:
                                            t.setLastrtt(ping);
                                            t.setStatus("ACTIVE");
                                            t.setIsBeingPinged(false);
                                            break;
                                    }
                                    System.out.println("C=" + t.getPinged() + " - " + t.nameProperty().get());
                                } catch (Exception e) {
                                    logUtil.log(LogUtil.CRITICAL, e.getMessage() + ", " + e.getCause());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void initChart() {
        lineChart.setCreateSymbols(false);
        timeAxis.setLowerBound(00.00);
        timeAxis.setUpperBound(100.00);
        for (Target t : targets) {

            if (t.flagsProperty().get().contains("M")) {
                series = new XYChart.Series();
                series.setName(t.nameProperty().get());
                lineChart.getData().add(series);
            }
        }
    }

    public void initCycleChart() {
        monitoringTitledPane.setExpanded(true);
        infoPane.prefWidthProperty().bind(monitoringTitledPane.widthProperty().subtract(monitoringTitledPane.widthProperty()).add(260.00));
        infoPane.prefHeightProperty().bind(monitoringTitledPane.heightProperty().subtract(50));
        infoGridPane.prefWidthProperty().bind(infoPane.widthProperty());
        infoGridPane.prefHeightProperty().bind(infoPane.heightProperty().subtract(15));
        cycleChart.prefWidthProperty().bind(monitoringTitledPane.widthProperty().subtract(infoPane.widthProperty()).subtract(30));

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
        TableColumn flagsCol = new TableColumn();
        TableColumn nameCol = new TableColumn();
        TableColumn domainCol = new TableColumn();
        TableColumn addressCol = new TableColumn();
        TableColumn pingCol = new TableColumn();
        TableColumn timeoutsCol = new TableColumn();

        statusCol.setText("Status");
        flagsCol.setText("F");
        nameCol.setText("Name");
        domainCol.setText("Domain");
        addressCol.setText("Address");
        pingCol.setText("Ping");
        timeoutsCol.setText("Timeouts");

        setTableListeners();

        statusCol.prefWidthProperty().bind(target_table.widthProperty().divide(10));
        flagsCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(0.5));
        nameCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(2));
        domainCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(2));
        addressCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(2));
        pingCol.prefWidthProperty().bind(target_table.widthProperty().divide(10).multiply(1.5));
        timeoutsCol.prefWidthProperty().bind(target_table.widthProperty().divide(10));

        target_table.getColumns().addAll(statusCol, flagsCol, nameCol, domainCol, addressCol, pingCol, timeoutsCol);
        target_table.setItems(targets);

        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        flagsCol.setCellValueFactory(new PropertyValueFactory<>("flags"));
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
                            String colorBG = null;
                            Color colorFG = null;
                            switch (String.valueOf(item)) {
                                case "ERROR":
                                    colorBG = "red";
                                    colorFG = Color.BLACK;
                                    break;
                                case "ACTIVE":
                                    colorBG = "green";
                                    colorFG = Color.WHITE;
                                    break;
                                case "TIME OUT":
                                    colorBG = "yellow";
                                    colorFG = Color.BLACK;
                                    break;
                                case "PAUSED":
                                    colorBG = "grey";
                                    colorFG = Color.WHITE;
                                    break;
                                case "PINGING":
                                    colorBG = "white";
                                    colorFG = Color.RED;
                                    break;
                                default:
                                    colorBG = "white";
                                    colorFG = Color.BLACK;
                                    break;
                            }
                            this.setStyle("-fx-background-color:" + colorBG);
                            this.setTextFill(colorFG);
                            setText(item);
                        }
                    }
                };
            }
        });
    }

    public void setTableListeners() {
        target_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final ContextMenu rightClickMenu = new ContextMenu();
        final MenuItem addToMonitoringItem = new MenuItem("Add to monitoring");
        final MenuItem removeFromMonitoringItem = new MenuItem("Remove from monitoring");
        final MenuItem pausePingItem = new MenuItem("Halt ping");
        final MenuItem resumePingItem = new MenuItem("Resume ping");

        addToMonitoringItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Target> selectedList = target_table.getSelectionModel().getSelectedItems();
                for (Target t : selectedList) {
                    t.addFlag("M");
                    series = new XYChart.Series();
                    series.setName(t.nameProperty().get());
                    lineChart.getData().add(series);
                    logUtil.log(LogUtil.INFO, t.nameProperty().get() + " - added to ping chart.");
                }
            }
        });
        removeFromMonitoringItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<Target> selectedList = new ArrayList<>(target_table.getSelectionModel().getSelectedItems());
                for (Target t : selectedList) {
                    t.removeFlag("M");
                    ObservableList<XYChart.Series<Number, Number>> theSeries = lineChart.getData();
                    for (XYChart.Series s : theSeries) {
                        if (s.getName().equals(t.nameProperty().get())) {
                            removeSeries(s);
                            logUtil.log(LogUtil.INFO, t.nameProperty().get() + " - removed from ping chart.");
                        }
                    }

                }
            }
        });

        pausePingItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Target> selectedList = target_table.getSelectionModel().getSelectedItems();
                for (Target t : selectedList) {
                    t.removeFlag("A");
                    t.setStatus("PAUSED");
                    logUtil.log(LogUtil.INFO, t.nameProperty().get() + " - paused ping.");
                }
            }
        });

        resumePingItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Target> selectedList = target_table.getSelectionModel().getSelectedItems();
                for (Target t : selectedList) {
                    t.addFlag("A");
                    t.setStatus("PENDING");
                    t.setLastrtt("");
                    logUtil.log(LogUtil.INFO, t.nameProperty().get() + " - resumed ping.");
                }
            }
        });

        removeFromMonitoringItem.disableProperty().bind(Bindings.isEmpty(target_table.getSelectionModel().getSelectedItems()));
        addToMonitoringItem.disableProperty().bind(Bindings.isEmpty(target_table.getSelectionModel().getSelectedItems()));
        rightClickMenu.getItems().addAll(addToMonitoringItem, removeFromMonitoringItem, pausePingItem, resumePingItem);
        target_table.setContextMenu(rightClickMenu);
    }

    public synchronized void addToChart(XYChart.Series s, int pos, Double value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                s.getData().add(new XYChart.Data(pos, value));
            }
        });
    }

    public synchronized void removeSeries(XYChart.Series s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lineChart.getData().remove(s);
            }
        });
    }

    public void initSystemGraph() {
        cpu_graph.setCategoryGap(50);
        cpu_graph.setBarGap(0);
        systemTitledPane.prefHeightProperty().bind(overviewAnchorPane.heightProperty());
        systemAnchorPane.prefHeightProperty().bind(systemTitledPane.heightProperty());

        systemVBox.prefHeightProperty().bind(systemTitledPane.heightProperty());
        systemHBox.prefHeightProperty().bind(systemTitledPane.heightProperty());

        system_resourcesVBox.prefHeightProperty().bind(systemTitledPane.heightProperty());
        system_FileSystemVBox.prefHeightProperty().bind(systemTitledPane.heightProperty());
        system_FileSystemHBox.prefHeightProperty().bind(system_resourcesPane.heightProperty());

        cpu_graph.prefWidthProperty().bind(systemTitledPane.widthProperty().divide(2));
        cpu_graph.prefHeightProperty().bind(systemTitledPane.heightProperty().divide(10).multiply(8));
        storage_chart.prefWidthProperty().bind(systemTitledPane.widthProperty().divide(2));
        storage_chart.prefHeightProperty().bind(systemTitledPane.heightProperty().divide(10).multiply(8));

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
                                updatePieLabel(d, forSwitch + " " + (Math.round((value1 * 100) * 100.00) / 100.00) + "%");
                                long usedspace = sysUtil.getUsedSpaceKbLinux();
                                String label1 = String.valueOf(usedspace / 1024) + " Mb" + " (" + (usedspace / 1024 / 1024) + " Gb)";
                                updateLabel(usedspace_label, label1);
                                break;
                            case "Free":
                                Double value2 = 1 - sysUtil.getUsedSpacePercentLinux();
                                d.setPieValue(value2 * 100);
                                updatePieLabel(d, forSwitch + " " + (Math.round((value2 * 100) * 100.00) / 100.00) + "%");
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

    public void colorLabel(Label l, Color c) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                l.setTextFill(c);
            }
        });
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
        int monitored = 0;
        int paused = 0;

        for (Target t : targets) {
            if (t.lastrttProperty().get() != null) {
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
            if (t.flagsProperty().get().contains("M")) {
                monitored++;
            }
            if (t.flagsProperty().get().contains("P")) {
                paused++;
            }
        }
        updateLabel(targets_infoLabel, String.valueOf(total));
        updateLabel(active_infoLabel, String.valueOf(active));
        updateLabel(timedout_infoLabel, String.valueOf(timeouts));
        updateLabel(unreachable_infoLabel, String.valueOf(unreachable));
        updateLabel(unknownhosts_infoLabel, String.valueOf(unknown));
        updateLabel(monitored_infoLabel, String.valueOf(monitored));
        updateLabel(paused_infoLabel, String.valueOf(paused));
        lastCycleInterval = (System.currentTimeMillis() - lastLabelUpdate) / 1000;
        updateLabel(lastupdate_infoLabel, String.valueOf(lastCycleInterval + "s"));
        if (!globalPingerStatus) {
            updateLabel(toolbar_statusLabel, "Paused");
            colorLabel(toolbar_statusLabel, Color.BLUE);
        } else {
            updateLabel(toolbar_statusLabel, "Running");
            colorLabel(toolbar_statusLabel, Color.GREEN);
        }
        setLastLabelUpdate();
    }

    public void startLabelUpdateCounter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (cycle + 5 > secondAxis.getUpperBound()) {
                            secondAxis.setUpperBound(cycle + 5);
                            secondAxis.setLowerBound(cycle - 50);
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

    public void initLogger() {
        logUtil = new LogUtil(output_console);

        output_AnchorPane.prefHeightProperty().bind(output_TitledPane.heightProperty());

        output_console.prefWidthProperty().bind(monitoringTitledPane.widthProperty().subtract(1));
        output_console.prefHeightProperty().bind(output_TitledPane.heightProperty().subtract(27));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                output_console.setText("  #############################################");
                output_console.appendText('\n' + "  #|        SERVER MONITORING CONSOLE        |#");
                output_console.appendText('\n' + "  #|                  v0.1                   |#");
                output_console.appendText('\n' + "  #|-----------------------------------------|#");
                output_console.appendText('\n' + "  #|               INFO LEVELS               |#");
                output_console.appendText('\n' + "  #|                                         |#");
                output_console.appendText('\n' + "  #| [INFO] - Information                    |#");
                output_console.appendText('\n' + "  #| [WARN] - Warning                        |#");
                output_console.appendText('\n' + "  #| [SEVE] - Severe                         |#");
                output_console.appendText('\n' + "  #| [CRIT] - Critical                       |#");
                output_console.appendText('\n' + "  #|                                         |#");
                output_console.appendText('\n' + "  #############################################");
                output_console.appendText('\n' + " ");
            }
        });

    }

    public void updatePingChart() {
        exec.execute(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    for (Target t : targets) {
                        if (t.lastrttProperty().get() != null && !t.lastrttProperty().get().isEmpty()) {
                            switch (t.lastrttProperty().get()) {
                                case "TIME_OUT":
                                    for (XYChart.Series s : lineChart.getData()) {
                                        if (s.getName().equals(t.nameProperty().get())) {
                                            addToChart(s, cycle, 00.00);
                                        }
                                    }
                                    break;
                                case "UNKNOWN HOST":
                                    break;
                                case "UNREACHABLE":
                                    break;
                                default:
                                    for (XYChart.Series s : lineChart.getData()) {
                                        if (s.getName().equals(t.nameProperty().get())) {
                                            addToChart(s, cycle, Double.valueOf(t.lastrttProperty().get()));
                                        }
                                    }
                                    break;
                            }
                        }

                    }

                    try {
                        rangeChart(cycle);
                        updateInfo();
                        cycle++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public boolean getGlobalPingerStatus() {
        return globalPingerStatus;
    }

    public void setGlobalPingerStatus(boolean globalPingerStatus) {
        this.globalPingerStatus = globalPingerStatus;
    }

}
