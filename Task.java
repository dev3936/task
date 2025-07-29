package task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.PriorityQueue;

public class Task extends JFrame implements Comparable<Task> {
    private String title;
    private String priority;
    private LocalDate deadline;

    private static PriorityQueue<Task> taskQueue = new PriorityQueue<>();
    private static DefaultListModel<String> listModel = new DefaultListModel<>();

    public Task(String title, String priority, LocalDate deadline) {
        this.title = title;
        this.priority = priority;
        this.deadline = deadline;
    }

    public String getTitle() { return title; }
    public String getPriority() { return priority; }
    public LocalDate getDeadline() { return deadline; }

    private int priorityValue(String p) {
        return switch (p.toLowerCase()) {
            case "high" -> 1;
            case "medium" -> 2;
            case "low" -> 3;
            default -> 4;
        };
    }

    @Override
    public int compareTo(Task other) {
        int p1 = priorityValue(this.priority);
        int p2 = priorityValue(other.priority);
        if (p1 != p2) return Integer.compare(p1, p2);
        return this.deadline.compareTo(other.deadline);
    }

    @Override
    public String toString() {
        return title + " (" + priority + ") - Due: " + deadline;
    }

    // ===================== GUI =====================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskGUI().setVisible(true));
    }

    static class TaskGUI extends JFrame {
        private JTextField titleField;
        private JComboBox<String> priorityBox;
        private JTextField deadlineField;
        private JList<String> taskList;

        public TaskGUI() {
            setTitle("🎯 Smart Task Scheduler");
            setSize(600, 500);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            Color backgroundColor = new Color(245, 245, 255);
            Color panelColor = new Color(220, 235, 255);
            Font font = new Font("Segoe UI", Font.BOLD, 16);

            // Top Panel - Input
            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 15, 15));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            inputPanel.setBackground(panelColor);

            JLabel titleLabel = new JLabel("Task Title:");
            titleLabel.setFont(font);
            inputPanel.add(titleLabel);

            titleField = new JTextField();
            titleField.setFont(font);
            inputPanel.add(titleField);

            JLabel priorityLabel = new JLabel("Priority:");
            priorityLabel.setFont(font);
            inputPanel.add(priorityLabel);

            priorityBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
            priorityBox.setFont(font);
            inputPanel.add(priorityBox);

            JLabel deadlineLabel = new JLabel("Deadline (yyyy-mm-dd):");
            deadlineLabel.setFont(font);
            inputPanel.add(deadlineLabel);

            deadlineField = new JTextField();
            deadlineField.setFont(font);
            inputPanel.add(deadlineField);

            JButton addButton = new JButton("➕ Add Task");
            addButton.setFont(font);
            addButton.setBackground(new Color(76, 175, 80));
            addButton.setForeground(Color.WHITE);
            inputPanel.add(addButton);

            JButton clearButton = new JButton("🧹 Clear");
            clearButton.setFont(font);
            clearButton.setBackground(new Color(244, 67, 54));
            clearButton.setForeground(Color.WHITE);
            inputPanel.add(clearButton);

            add(inputPanel, BorderLayout.NORTH);

            // Center Panel - Task List
            taskList = new JList<>(listModel);
            taskList.setFont(new Font("Consolas", Font.PLAIN, 16));
            taskList.setBackground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(taskList);
            scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Scheduled Tasks"));
            scrollPane.setBackground(backgroundColor);
            add(scrollPane, BorderLayout.CENTER);

            // Background color for main content
            getContentPane().setBackground(backgroundColor);

            // Button Action
            addButton.addActionListener(e -> addTask());
            clearButton.addActionListener(e -> clearInputs());
        }

        private void addTask() {
            String title = titleField.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();
            String deadlineStr = deadlineField.getText().trim();

            if (title.isEmpty() || deadlineStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate deadline = LocalDate.parse(deadlineStr);
                Task task = new Task(title, priority, deadline);
                taskQueue.offer(task);
                updateTaskList();
                clearInputs();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid date format. Use yyyy-mm-dd.", "Date Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateTaskList() {
            listModel.clear();
            PriorityQueue<Task> temp = new PriorityQueue<>(taskQueue);
            while (!temp.isEmpty()) {
                listModel.addElement(temp.poll().toString());
            }
        }

        private void clearInputs() {
            titleField.setText("");
            deadlineField.setText("");
            priorityBox.setSelectedIndex(0);
        }
    }
}

    
