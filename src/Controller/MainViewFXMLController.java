
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Target;
import Model.TargetList;
import Util.LogUtil;
import Util.Looper;
import Util.SysUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.chart.*;
import javafx.scene.control.*;
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
 * @author Linas Martusevicius
 *
 * Main controller of the MainViewFXML.fxml.
 */
public class MainViewFXMLController implements Initializable {

    ExecutorService exec;
    Runnable r;
    LogUtil logUtil;
    boolean globalPingerStatus = true;
    TargetList tl;
    ArrayList<Looper> looperList;

    SysUtil sysUtil;
    long lastLabelUpdate = 0;
    long lastCycleInterval = 0;
    int cycle = 0;

    static ObservableList<Target> targets;
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
        exec = Executors.newCachedThreadPool();
        tl = TargetList.getInstance(exec);
        targets = tl.getTargetList();
        initToolbar();
        initLogger();
        initTable();
        initChart();
        initSystemGraph();
        initCycleChart();
        setLastLabelUpdate();
        startLabelUpdateCounter();
        beginPing();
        updatePingChart();
    }

    /**
     * Initializes the toolbar located in the Peers tab
     */
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
                    pausePing();
                } else {
                    playImageView.setImage(pauseDark);
                    tl.setIsPaused(false);
                    resumePing();
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
                ObservableList<Target> selectedList = target_table.getSelectionModel().getSelectedItems();
                if (removePeersImageView.getImage() == xLight) {
                    removePeersImageView.setImage(xDark);
                } else {
                    removePeersImageView.setImage(xLight);
                }
                if (selectedList.size() > 0) {
                    for (Target ts : selectedList) {
                        for (Target tl : targets) {
                            if (ts.nameProperty().get().equals(tl.nameProperty().get())) {
                                targets.remove(tl);
                            }
                        }
                    }
                }

            }
        });
    }

    /**
     * Creates a new stage with the TargetEditorFXML.fxml layout and displays it
     * for the user.
     */
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

    /**
     * Begins the pinging loop.
     */
    public void beginPing() {
        looperList = tl.getLooperList();
        for (int i = 0; i < 8; i++) {
            looperList.add(new Looper(i, targets, tl, logUtil));
        }

        for (Looper looper : looperList) {
            exec.execute(looper);
        }
    }

    /**
     * Initializes the peer chart in the Graph tab.
     */
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

    /**
     * Initializes the cycle chart in the Overview tab's monitoring segment.
     */
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

    /**
     * Ranges the chart in accordance to the number of cycles done by the pinger
     * loop.
     *
     * @param i int representation of the current cycle.
     */
    public void rangeChart(int i) {
        if (i >= timeAxis.getUpperBound()) {
            timeAxis.setLowerBound(timeAxis.getLowerBound() + 20);
            timeAxis.setUpperBound(timeAxis.getUpperBound() + 20);
        }

    }

    /**
     * Initializes the peer table in the Peers tab. Binds the width properties
     * of the columns to the size of the window. Binds their values to the peer
     * list. Sets a custom cell factory for the status column.
     */
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

    /**
     * Sets right-click listeners and their actions on the peer table in the
     * Peers tab.
     */
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

    /**
     * Adds a given data point to the peer chart in the Graph tab.
     *
     * @param s the XYChart.Series object to add to.
     * @param pos the position on the chart (X coordinate)
     * @param value the value of the data (Y coordinate)
     */
    public synchronized void addToChart(XYChart.Series s, int pos, Double value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                s.getData().add(new XYChart.Data(pos, value));
            }
        });
    }

    /**
     * Removes a series from the ping chart in the Graph tab.
     *
     * @param s XYChart.Series object to be removed.
     */
    public synchronized void removeSeries(XYChart.Series s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lineChart.getData().remove(s);
            }
        });
    }

    /**
     * Initializes the local system load monitoring charts in the Overview tab's
     * System segment.
     */
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

    /**
     * Sets a label to the desired color. Reusable method to deal with updating
     * elements on JavaFX's UI thread.
     *
     * @param l the Label object
     * @param c the color
     */
    public void colorLabel(Label l, Color c) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                l.setTextFill(c);
            }
        });
    }

    /**
     * Sets a label's text value. Reusable method to deal with updating elements
     * on JavaFX's UI thread.
     *
     * @param l the Label object
     * @param s the String value
     */
    public void updateLabel(Label l, String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                l.setText(s);
            }
        });
    }

    /**
     * Sets a PieChart's label's text value. Reusable method to deal with
     * updating elements on JavaFX's UI thread.
     *
     * @param d the PieChart.Data object
     * @param s the String value
     */
    public void updatePieLabel(PieChart.Data d, String s) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                d.setName(s);
            }
        });
    }

    /**
     * Updates the general information panel in the Overview tab's Monitoring
     * section's left hand side.
     */
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

    /**
     * A counter used to continuously update the cycle chart and general
     * information in the Overview tab's Monitoring section with a one second
     * delay.
     */
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

    /**
     * Sets the lastLabelUpdate value to the current system time.
     */
    public void setLastLabelUpdate() {
        lastLabelUpdate = System.currentTimeMillis();
    }

    /**
     * Initializes the custom logger and Output section of the Overview tab.
     */
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

    /**
     * Continuously updates the ping chart every second.
     */
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

    /**
     * Pauses the pingers.
     */
    public void pausePing() {
        globalPingerStatus = false;
        for (Looper l : looperList) {
            l.suspend();
        }

    }

    /**
     * Resumes the pingers
     */
    public void resumePing() {
        globalPingerStatus = true;
        for (Looper l : looperList) {
            l.resume();
        }
    }

}
