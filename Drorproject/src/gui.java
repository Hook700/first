import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;
import java.util.Date;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;

public class gui {
    private JFrame frame;
    private JButton button;
    private JButton button2;
    private JButton button3;
    private JButton searchButton;
    private JTextField startDateField;
    private JTextField endDateField;;
    private JButton button5;
    private JLabel labelForButton1;
    private JLabel labelForButton2;
    private JLabel labelForButton3;
    private JLabel labelForButton4;
    private JLabel labelForButton5;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;


    private JTextArea textArea;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    gui window = new gui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public gui() {

        initialize();
    }

    private void initialize() {
        frame = new JFrame("SQL Database GUI");
        frame.setBounds(500, 500, 1000, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        labelForButton1 = new JLabel("Max LoggedValue");
        labelForButton1.setBounds(10, 50, 200, 20);
        frame.getContentPane().add(labelForButton1);
        button = new JButton("Load Query 1");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        button.setBounds(10, 70, 120, 23);
        frame.getContentPane().add(button);
        labelForButton2 = new JLabel("Sum LoggedValue");
        labelForButton2.setBounds(130, 50, 200, 20);
        frame.getContentPane().add(labelForButton2);
        button2 = new JButton("Load Query 2");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Load the data in a separate thread
                new Thread(() -> {
                    loadDatatwo();
                }).start();
            }
        });

        button2.setBounds(130, 70, 120, 23);
        frame.getContentPane().add(button2);
        labelForButton3 = new JLabel("Min LoggedValue");
        labelForButton3.setBounds(250, 50, 200, 20);
        frame.getContentPane().add(labelForButton3);
        button3 = new JButton("Load Query 3");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadDatathree();
            }
        });

        button3.setBounds(250, 70, 120, 23);
        frame.getContentPane().add(button3);
        labelForButton5 = new JLabel("Where LoggedValue = 0");
        labelForButton5.setBounds(370, 50, 200, 20);
        frame.getContentPane().add(labelForButton5);
        button5 = new JButton("Load Query 5");
        button5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData5();
            }
        });
        button5.setBounds(370, 70, 120, 23);
        frame.getContentPane().add(button5);

        labelForButton4 = new JLabel("Write the date's for Max LoggedValue");
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd/MM/yyyy"); // Set the date format
        startDateChooser.setBounds(490, 70, 120, 20);
        frame.getContentPane().add(startDateChooser);

        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd/MM/yyyy"); // Set the date format
        endDateChooser.setBounds(610, 70, 120, 20);
        frame.getContentPane().add(endDateChooser);
        searchButton = new JButton("Search date for Query 4");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchDatabase();
            }
        });
        searchButton.setBounds(730, 70, 180, 20);
        frame.getContentPane().add(searchButton);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea); // Added a scroll pane
        scrollPane.setBounds(10, 100, 950, 300);
        frame.getContentPane().add(scrollPane);
    }

    private void loadData() {
        try {
            String query = "SELECT LogID, lineID, logTime, Max(LoggedValue) as Maximum FROM `linesproductivity_updated (1)` GROUP BY LogID, lineID, logTime"; // Adjust the query as needed
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/drorproject1", "root", "Aa123456");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Adjust the column widths as necessary
            String headerFormat = "%-10s | %-10s | %-15s\n";
            String rowFormat = "%-10s | %-10s | %-15s\n";

            // Set headers for the columns
            textArea.setText(String.format(headerFormat, "LogID", "LineID", "LogTime"));
            textArea.append("-------------------------------------------------\n"); // Separator for headers and data

            while (resultSet.next()) {
                String logID = resultSet.getString("LogID");
                String lineID = resultSet.getString("lineID");
                String logTime = resultSet.getString("logTime");
                String loggedValue= resultSet.getString("Maximum");
                // Format and append each row's data using rowFormat
                textArea.append(String.format(rowFormat, logID, lineID, logTime));

            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data");
        }

}

    private void loadDatatwo() {
    try {
        String query = "SELECT logID, SUM(loggedValue) as sum_of_log FROM `linesproductivity_updated (1)` GROUP BY logID";
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/drorproject1", "root", "Aa123456");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        // Adjust the column widths as necessary for these specific columns
        String headerFormat = "%-10s | %-15s\n"; // Adjusted for LogID and sum_of_log
        String rowFormat = "%-10s | %-15s\n"; // Adjusted for LogID and sum_of_log
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        // Set headers for the columns - adjusted to match the data being displayed
        textArea.setText(String.format(headerFormat, "LogID", "Sum of Log"));
        textArea.append("------------------------------\n"); // Adjusted separator for headers and data

        while (resultSet.next()) {
            String logID = resultSet.getString("logID");
            // Correctly fetch the sum_of_log value from the result set
            String sumOfLog = resultSet.getString("sum_of_log");
            // Format and append each row's data using rowFormat
            textArea.append(String.format(rowFormat, logID, sumOfLog));
            xData.add(Double.valueOf(logID));
            yData.add(Double.valueOf(sumOfLog));
        }

        connection.close();
        displayChart2(xData, yData);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading data");
    }
}

private void loadDatathree() {
    try {
        String query = "SELECT LineID, MIN(loggedValue) as min_of_log FROM `linesproductivity_updated (1)` GROUP BY LineID";
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/drorproject1", "root", "Aa123456");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        // Adjust the column widths as necessary for these specific columns
        String headerFormat = "%-10s | %-15s\n"; // Adjusted for LogID and sum_of_log
        String rowFormat = "%-10s | %-15s\n"; // Adjusted for LogID and sum_of_log

        // Set headers for the columns - adjusted to match the data being displayed
        textArea.setText(String.format(headerFormat, "LineID", "Min of Log"));
        textArea.append("------------------------------\n"); // Adjusted separator for headers and data

        while (resultSet.next()) {
            String lineID = resultSet.getString("lineID");
            // Correctly fetch the sum_of_log value from the result set
            String minOfLog = resultSet.getString("min_of_log");
            // Format and append each row's data using rowFormat
            textArea.append(String.format(rowFormat, lineID, minOfLog));

        }

        connection.close();

    } catch (Exception e)
    {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading data");
    }
}
private void loadData5() {
    try {
        String query = "SELECT LogID, lineID, logTime, LoggedValue ,CmdType,Description,UnitType FROM `linesproductivity_updated (1)` WHERE LoggedValue = 0"; // Adjust the query as needed
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/drorproject1", "root", "Aa123456");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        // Adjust the column widths as necessary
        String headerFormat = "%-10s | %-10s | %-10s | %-10s | %-10s | %-10s | %-15s\n";
        String rowFormat = "%-10s | %-10s | %-10s | %-10s | %-10s | %-10s | %-15s\n";

        // Set headers for the columns
        textArea.setText(String.format(headerFormat, "LogID", "LineID", "LogTime", "LoggedValue", "CmdType", "Description", "UintType"));
        textArea.append("-------------------------------------------------\n"); // Separator for headers and data

        while (resultSet.next()) {
            String logID = resultSet.getString("LogID");
            String lineID = resultSet.getString("lineID");
            String logTime = resultSet.getString("logTime");
            String loggedValue = resultSet.getString("loggedValue");
            String cmdType = resultSet.getString("cmdType");
            String description = resultSet.getString("description");
            String unitType = resultSet.getString("unitType");
            // Format and append each row's data using rowFormat
            textArea.append(String.format(rowFormat, logID, lineID, logTime, loggedValue, cmdType, description, unitType));
        }

        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading data");
    }
}
    private void searchDatabase() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        String formattedStartDate = dateFormat.format(startDate);
        String formattedEndDate = dateFormat.format(endDate);


        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/drorproject1", "root", "Aa123456");
            Statement statement = connection.createStatement();

            // Replace this query with your actual query
            String query = "SELECT LineID,max(LoggedValue) FROM `linesproductivity_updated (1)` WHERE LogTime BETWEEN '" + formattedStartDate + "' AND '" + formattedEndDate + "'"+"GROUP BY LineID";
            ResultSet resultSet = statement.executeQuery(query);

            // Process and display the results
            textArea.setText("");
            while (resultSet.next()) {
                // Adjust this to match your actual column names and data processing needs
                String result = resultSet.getString("LineID") + "\n";
                textArea.append(result);
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error in database query");
        }
    }
    private void displayChart2(List<Double> xData, List<Double> yData) {
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Sum LoggedValue").xAxisTitle("LogID").yAxisTitle("Sum").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        // Add series to the chart
        chart.addSeries("Sum", xData, yData);
        new SwingWrapper<>(chart).displayChart();
    }
}