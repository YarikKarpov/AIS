//для работы программы необходимо скачать библиотеку JDBC SQLITE
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class АИС {
    //подключаем базу данных к java, указывая путь к базе данных на компьютере
    private static final String url = "jdbc:sqlite:C:/Users/mx060/OneDrive/Документы/sql database/database";
    private static JComboBox<String> tableSelector;
    private static JTable table;
    private static DefaultTableModel model;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(АИС::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("бд");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // создаем панель для выбора таблицы и кнопок
        JPanel panel = new JPanel();
        tableSelector = new JComboBox<>();
        JButton loadButton = new JButton("загрузить данные");
        JButton createTableButton = new JButton("создать таблицу");
        JButton addbutton = new JButton("добавить данные в таблицу");
        JButton removebutton = new JButton("удалить данные из таблицы");

        loadButton.addActionListener(e -> loadTableData((String) tableSelector.getSelectedItem()));
        createTableButton.addActionListener(e -> createTable());
        addbutton.addActionListener(e -> addDataToTable());
        removebutton.addActionListener(e->removedatas());

        panel.add(tableSelector);
        panel.add(loadButton);
        panel.add(createTableButton);
        panel.add(addbutton);
        panel.add(removebutton);

        // создание таблицы
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // загружаем доступные таблицы из базы данных
        loadTableNames();

        frame.setVisible(true);
    }

    private static void loadTableNames() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            while (rs.next()) {
                String tableName = rs.getString("name");
                tableSelector.addItem(tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ошибка при загрузке таблиц: " + e.getMessage());
        }
    }

    private static void loadTableData(String tableName) {
        if (tableName == null) {
            return; // если таблица не выбрана, ничего не делаем
        }

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);

            // получаем метаданные
            int columnCount = rs.getMetaData().getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = rs.getMetaData().getColumnName(i);
            }

            // создаем модель таблицы
            model = new DefaultTableModel(columnNames, 0);

            // заполняем модель данными
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            // устанавливаем модель таблицы
            table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ошибка при загрузке данных: " + e.getMessage());
        }
    }

    private static void createTable() {
        // открываем диалоговое окно для ввода имени таблицы и названий столбцов
        String tableName = JOptionPane.showInputDialog("введите имя таблицы:");
        String columns = JOptionPane.showInputDialog("введите названия столбцов через запятую");
        if (tableName != null && columns != null) {
            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {

                // создаем SQL-запрос для создания таблицы
                String sql = "CREATE TABLE " + tableName + " (" + columns + ")";
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "таблица " + tableName + " успешно создана.");

                // обновляем список таблиц
                loadTableNames();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "ошибка при создании таблицы: " + e.getMessage());
            }
        }
    }

    //метод добавления данных в таблицу
    private static void addDataToTable() {
        String table = JOptionPane.showInputDialog("введите таблицу");
        String column = JOptionPane.showInputDialog("введите столбец");
        String data = JOptionPane.showInputDialog("введите данные");
        if (table != null && column != null && data != null) {
            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                String sql = "INSERT INTO " + table + " (" + column + ") VALUES ('" + data + "')";
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "данные в таблицу " + table + " добавлены.");
                loadTableNames();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //Метод удаления данных из таблицы
    private static void removedatas() {
        String table = JOptionPane.showInputDialog("введите таблицу");
        String column = JOptionPane.showInputDialog("введите столбец");
        String data = JOptionPane.showInputDialog("введите данные");
        if (table != null && column != null && data != null) {
            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                String sql = "DELETE FROM " + table + " WHERE " + column + " = '" + data + "'";
                stmt.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "данные из таблицы " + table + " удалены.");
                loadTableNames();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

