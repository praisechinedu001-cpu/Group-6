package com.example.mainapp4;// =============================================
// SMART HOME LOAD MONITOR – COMPLETE VERSION
// Java + JavaFX (Mini Project – Ghana 13A Sockets)
// =============================================

import javafx.application.*;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;



// ---------------- ENUMS ----------------
enum Status { OK, WARNING, DANGER, SURGE, INVALID }
enum Priority { ESSENTIAL, NON_ESSENTIAL }

// ---------------- APPLIANCE ----------------
class Appliance {
    String name, location, group;
    double current, prevCurrent, maxCurrent;
    Priority priority;
    Status status = Status.OK;

    Appliance(String n, String l, String g, double m, Priority p) {
        name=n; location=l; group=g; maxCurrent=m; priority=p;
    }
}

// ---------------- MAIN APP ----------------
public class MainApp extends Application {

    // SETTINGS
    double voltage = 230;
    double mainLimit = 40;
    double surgeThreshold = 4;
    double tariff = 1.5;

    // ENERGY
    double energyKWh = 0;

    ObservableList<Appliance> appliances = FXCollections.observableArrayList();
    ObservableList<String> alerts = FXCollections.observableArrayList();
    ObservableList<String> recommendations = FXCollections.observableArrayList();

    Timer timer;

    @Override
    public void start(Stage stage) {
        initAppliances();

        TableView<Appliance> table = applianceTable();
        ListView<String> alertList = new ListView<>(alerts);
        ListView<String> recList = new ListView<>(recommendations);

        Label totalLbl = new Label();
        Label powerLbl = new Label();
        Label energyLbl = new Label();
        Label costLbl = new Label();
        Label statusLbl = new Label();

        VBox summary = new VBox(8, totalLbl, powerLbl, energyLbl, costLbl, statusLbl);
        summary.setPadding(new Insets(10));
        summary.setStyle("-fx-border-color:black");

        Button start = new Button("Start");
        Button stop = new Button("Stop");
        Button settings = new Button("Settings");

        start.setOnAction(e-> {
            startSim(totalLbl, powerLbl, energyLbl, costLbl, statusLbl);
        });
        stop.setOnAction(e-> {
            stopSim();
        });
        settings.setOnAction(e-> {
            openSettings();
        });

        HBox top = new HBox(10,start,stop,settings);
        top.setPadding(new Insets(10));

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Alerts",alertList));
        tabs.getTabs().add(new Tab("Recommendations",recList));

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(table);
        root.setRight(summary);
        root.setBottom(tabs);

        stage.setScene(new Scene(root,1000,600));
        stage.setTitle("Smart Home Load Monitor");
        stage.show();
    }

    // ---------------- INITIAL DATA ----------------
    void initAppliances(){
        appliances.add(new Appliance("Fridge", "Kitchen", "Kitchen", 2, Priority.ESSENTIAL));
        appliances.add(new Appliance("Microwave", "Kitchen", "Kitchen", 8, Priority.NON_ESSENTIAL));
        appliances.add(new Appliance("Kettle", "Kitchen", "Kitchen", 10, Priority.NON_ESSENTIAL));
        appliances.add(new Appliance("AC", "Living", "Living", 12, Priority.NON_ESSENTIAL));
        appliances.add(new Appliance("TV", "Living", "Living", 1.5, Priority.NON_ESSENTIAL));
        appliances.add(new Appliance("Lights", "House", "General", 2, Priority.ESSENTIAL));
    }

public static class Appliance{
        private final String name;
        private final String group;
        private final double current;
        private final Priority priority;
        private Status status;

        public Appliance(String name, String Status, String group, double current, Priority priority){
            this.name = name;
            this.group = group;
            this.current = current;
            this.priority = priority;

        }
        public String getName(){return name;}
        public String getGroup(){return group;}
        public double getCurrent(){return current;}
        public Priority getPriority(){return priority;}
        public Status getStatus(){return status;}

}

    // ---------------- TABLE ----------------
    TableView<Appliance> applianceTable(){
        TableView<Appliance> t=new TableView<>(appliances);
        t.getColumns().add(col("Appliance",a -> new SimpleStringProperty(a.getValue().getName())));
        t.getColumns().add(col("Group",a-> new SimpleStringProperty(a.getValue().getGroup())));
        t.getColumns().add(col("Current(A)",a->new SimpleStringProperty(String.valueOf(a.getValue().getCurrent()))));
        t.getColumns().add(col("Priority",a-> new SimpleStringProperty(a.getValue().getPriority().toString())));
        t.getColumns().add(col("Status",a-> new SimpleStringProperty(a.getValue().getStatus().toString())));
        return t;
    }
    TableColumn<Appliance,String> col(String n, javafx.util.Callback<TableColumn.CellDataFeatures<Appliance,String>, javafx.beans.value.ObservableValue<String>> v){
        TableColumn<Appliance,String> c=new TableColumn<>(n);
        c.setCellValueFactory(v);
        return c;
    }

    // ---------------- SIMULATION ----------------
    void startSim(Label t,Label p,Label e,Label c,Label s){
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run(){Platform.runLater(()-> {
                update(t, p, e, c, s);
            });}
        },0,1000);
    }
    void stopSim(){ if(timer!=null)timer.cancel(); }

    void update(Label totalLbl,Label powerLbl,Label energyLbl,Label costLbl,Label statusLbl){
        Random r=new Random();
        double total=0;
        Map<String,Double> groupSum=new HashMap<>();
        recommendations.clear();

       class Appliance {
        private String name;
        private String group;
        private double current;
        private double prevCurrent;
        private double maxCurrent;


           public void setPrevCurrent(double prevCurrent){this.prevCurrent = prevCurrent;
            }
            public void setCurrent(double val) {this.current = val;
            }
            public void setStatus(Status status) {
            }
            public String getName(){return name;}
            public double getCurrent(){return current;}
            public double getPrevCurrent(){return prevCurrent;}
            public double getMaxCurrent(){return maxCurrent;}
           public String getGroup(){return group;}
        }


        ObservableList<Appliance> appliances = FXCollections.observableArrayList();
         {
            for (Appliance a : appliances) {
                a.setPrevCurrent(a.getCurrent());

                double nextVal = r.nextDouble() * a.getCurrent() * 1.3;
                a.setCurrent(nextVal);

                if (a.getCurrent() <= 0 || a.getCurrent() > a.getMaxCurrent()) {
                    a.setStatus(Status.INVALID);
                    alert("Sensor fault on " + a.getName());
                    continue;
                }
                if (a.getCurrent() - a.getPrevCurrent() >= surgeThreshold) {
                    a.setStatus(Status.SURGE);
                    alert("Surge on " + a.getName());
                } else {
                    a.setStatus(Status.OK);
                }

                total += a.getCurrent();
                groupSum.put(a.getGroup(), groupSum.getOrDefault(a.getGroup(), 0.0) + a.getCurrent());
            }
        }


        // SOCKET GROUP CHECK
        for(String g:groupSum.keySet()){
            double v=groupSum.get(g);
            if(v>13) alert(g+" socket overloaded (>13A)");
            else if(v>=10) alert(g+" socket high load (warning)");
        }

        // HOUSE STATUS
        Status hs;
        if(total>mainLimit){
            hs=Status.DANGER;
            loadShedding(total);
        } else if(total>=0.8*mainLimit) hs=Status.WARNING;
        else hs=Status.OK;

        energyKWh+= (total*voltage/1000)/3600;

        totalLbl.setText(String.format("Total Current: %.2f A",total));
        powerLbl.setText(String.format("Power: %.0f W",total*voltage));
        energyLbl.setText(String.format("Energy: %.3f kWh",energyKWh));
        costLbl.setText(String.format("Cost: %.2f",energyKWh*tariff));
        statusLbl.setText("House Status: "+hs);
    }

    // ---------------- LOAD SHEDDING ----------------
    void loadShedding(double total){
        recommendations.clear();
        List<Appliance> nonEssentials = appliances.stream()
                .filter(a -> a.getPriority() == Priority.NON_ESSENTIAL)
                .sorted((a, b) -> Double.compare(b.getCurrent(), a.getCurrent()))
                .toList();

        for (Appliance a : nonEssentials){
            if (total > mainLimit){
                recommendations.add("Switch off" + a.getName());
                total -= a.getCurrent();

                    }
                }
    }

    // ---------------- ALERT ----------------
    void alert(String m){
        alerts.add("["+LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+" ] "+m);
    }

    // ---------------- SETTINGS ----------------
    void openSettings(){
        Stage s=new Stage();
        TextField v=new TextField(""+voltage);
        TextField m=new TextField(""+mainLimit);
        TextField st=new TextField(""+surgeThreshold);
        TextField t=new TextField(""+tariff);
        Button save=new Button("Save");
        save.setOnAction(e->{
            voltage=Double.parseDouble(v.getText());
            mainLimit=Double.parseDouble(m.getText());
            surgeThreshold=Double.parseDouble(st.getText());
            tariff=Double.parseDouble(t.getText());
            s.close();
        });
        VBox box=new VBox(10,new Label("Voltage"),v,new Label("Main Limit"),m,new Label("Surge Threshold"),st,new Label("Tariff"),t,save);
        box.setPadding(new Insets(10));
        s.setScene(new Scene(box,250,300));
        s.setTitle("Settings");
        s.show();
    }

    public static void main(String[] args){ launch(); }
}
