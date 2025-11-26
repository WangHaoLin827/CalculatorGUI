/*
 * Copyright (c) 2025 WangHaoLin827
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class CalculatorGUI {
    private static JTextField display;
    private static double firstNumber = 0;
    private static String operation = "";
    private static boolean startNewNumber = true;
    private static int MAX_LEN = 24;
    private static String lastOperation = "";
    private static Double lastSecondNumber = null;
    private static JTextArea historyDisplay; // New: History display area
    private static final int HISTORY_LIMIT = 10; // New: Limit history entries
    private static java.util.List<String> history = new java.util.ArrayList<>(); // New: History list

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 1. 设置窗口基础
        JFrame frame = new JFrame("全功能计算器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(230, 230, 235));

        // 2. 显示框配置
        display = new JTextField("0");
        display.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Slightly larger font
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setForeground(Color.BLACK); // Ensure text is black
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        frame.add(display, BorderLayout.NORTH);

        String ml = System.getProperty("calc.maxlen");
        if (ml != null) {
            try { setMaxLen(Integer.parseInt(ml)); } catch (Exception ignored) {}
        }

        // 3. 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(6, 4, 10, 10)); // Slightly larger gaps, now 6 rows
        buttonPanel.setBackground(new Color(210, 210, 215));

        // 3.1. 按钮定义
        String[][] buttons = {
            {"C", "CE", "⌫", "/"},
            {"7", "8", "9", "*"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"√", "^", "±", "="},
            {"0", ".", "E", "Ans"} // Placeholder for future use or advanced features
        };

        // 4. 创建按钮并绑定事件
        for (String[] row : buttons) {
            for (String text : row) {
                JButton button = createButton(text);
                button.addActionListener(e -> handleButtonAction(text));
                buttonPanel.add(button);
            }
        }

        frame.add(buttonPanel, BorderLayout.CENTER);

        // 5. 历史记录面板
        historyDisplay = new JTextArea();
        historyDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        historyDisplay.setEditable(false);
        historyDisplay.setBackground(new Color(245, 245, 250));
        historyDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        historyDisplay.setLineWrap(true);
        historyDisplay.setWrapStyleWord(true);

        JScrollPane historyScrollPane = new JScrollPane(historyDisplay);
        historyScrollPane.setPreferredSize(new Dimension(120, 0)); // Adjust width as needed
        historyScrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        frame.add(historyScrollPane, BorderLayout.EAST);

        JRootPane root = frame.getRootPane();
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "=");
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "C");
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "⌫");
        bindKey(root, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "-");
        bindKey(root, KeyStroke.getKeyStroke('+'), "+");
        bindKey(root, KeyStroke.getKeyStroke('*'), "*");
        bindKey(root, KeyStroke.getKeyStroke('/'), "/");
        bindKey(root, KeyStroke.getKeyStroke('.'), ".");
        bindKey(root, KeyStroke.getKeyStroke('='), "=");
        for (char c = '0'; c <= '9'; c++) {
            bindKey(root, KeyStroke.getKeyStroke(c), String.valueOf(c));
        }

        // 6. 显示窗口
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    // 创建带样式的按钮
    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        
        // 按钮颜色设置
        if (text.matches("[0-9.]")) {
            button.setBackground(new Color(248, 248, 255)); // Lightest gray for numbers/dot
        } else if (text.equals("=")) {
            button.setBackground(new Color(50, 100, 150)); // Darker blue for equals
            button.setForeground(Color.WHITE);
        } else if (text.equals("C") || text.equals("CE") || text.equals("⌫")) {
            button.setBackground(new Color(235, 235, 240)); // Slightly darker for clear/backspace
        } else {
            button.setBackground(new Color(225, 230, 235)); // Lighter gray for operators
        }
        
        Color normal = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(normal.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }
        });
        
        return button;
    }

    // 处理所有按钮动作
    private static void handleButtonAction(String command) {
        String current = display.getText();
        boolean isError = "Error".equals(current);
        switch (command) {
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> {
                if (startNewNumber || isError) {
                    display.setText(command);
                    startNewNumber = false;
                } else if (display.getText().length() < MAX_LEN) {
                    display.setText(display.getText() + command);
                }
            }
            case "." -> {
                if (isError) {
                    display.setText("0.");
                    startNewNumber = false;
                } else if (startNewNumber) {
                    display.setText("0.");
                    startNewNumber = false;
                } else if (!display.getText().contains(".") && display.getText().length() < MAX_LEN) {
                    display.setText(display.getText() + ".");
                }
            }
            case "±" -> {
                if (!isError && !display.getText().equals("0")) {
                    display.setText(display.getText().startsWith("-") ? 
                        display.getText().substring(1) : "-" + display.getText());
                }
            }
            case "⌫" -> {
                if (isError) {
                    display.setText("0");
                    startNewNumber = true;
                } else if (display.getText().length() > 1) {
                    display.setText(display.getText().substring(0, display.getText().length() - 1));
                } else {
                    display.setText("0");
                    startNewNumber = true;
                }
            }
            case "C" -> {
                display.setText("0");
                firstNumber = 0;
                operation = "";
                startNewNumber = true;
                lastOperation = "";
                lastSecondNumber = null;
            }
            case "CE" -> {
                display.setText("0");
                startNewNumber = true;
            }
            case "+", "-", "*", "/", "^" -> {
                if (!isError) {
                    if (!startNewNumber && operation.isEmpty()) { // If a number is just entered, and no operation is set yet
                        firstNumber = Double.parseDouble(display.getText());
                        operation = command;
                        startNewNumber = true;
                        lastOperation = "";
                        lastSecondNumber = null;
                    } else if (!operation.isEmpty() && startNewNumber) { // If an operation is already set, just update it
                        operation = command;
                    } else if (!startNewNumber && !operation.isEmpty()) { // If a number is entered after an operation, calculate first
                        handleButtonAction("="); // Evaluate previous operation
                        firstNumber = Double.parseDouble(display.getText());
                        operation = command;
                        startNewNumber = true;
                        lastOperation = "";
                        lastSecondNumber = null;
                    }
                }
            }
            case "√" -> {
                if (!isError) {
                    double value = Double.parseDouble(display.getText());
                    if (value >= 0) {
                        double result = Math.sqrt(value);
                        display.setText(formatDisplay(result));
                        firstNumber = result;
                        startNewNumber = true;
                        operation = ""; // Clear operation after unary calculation
                    } else {
                        display.setText("Error");
                        startNewNumber = true;
                        operation = "";
                    }
                }
            }
            case "=" -> {
                if (!operation.isEmpty() && !startNewNumber && !isError) {
                    double secondNumber = Double.parseDouble(display.getText());
                    if (operation.equals("/") && secondNumber == 0) {
                        display.setText("Division by zero");
                        operation = "";
                        startNewNumber = true;
                        return;
                    }
                    double result = switch (operation) {
                        case "+" -> firstNumber + secondNumber;
                        case "-" -> firstNumber - secondNumber;
                        case "*" -> firstNumber * secondNumber;
                        case "/" -> firstNumber / secondNumber;
                        case "^" -> Math.pow(firstNumber, secondNumber);
                        default -> secondNumber;
                    };
                    String formatted = formatDisplay(result);
                    display.setText(formatted);
                    lastOperation = operation;
                    lastSecondNumber = secondNumber;
                    operation = "";
                    startNewNumber = true;
                    if (!"Error".equals(formatted) && !"Division by zero".equals(formatted) && !"Invalid input for √".equals(formatted)) {
                        firstNumber = result;
                        addToHistory(firstNumber + " " + operation + " " + secondNumber + " = " + formatted);
                    }
                } else if (operation.isEmpty() && startNewNumber && !isError && lastSecondNumber != null && !lastOperation.isEmpty()) {
                    double base = Double.parseDouble(display.getText());
                    double secondNumber = lastSecondNumber;
                    String op = lastOperation;
                    if (op.equals("/") && secondNumber == 0) {
                        display.setText("Division by zero");
                        startNewNumber = true;
                        return;
                    }
                    double result = switch (op) {
                        case "+" -> base + secondNumber;
                        case "-" -> base - secondNumber;
                        case "*" -> base * secondNumber;
                        case "/" -> base / secondNumber;
                        case "^" -> Math.pow(base, secondNumber);
                        default -> base;
                    };
                    String formatted = formatDisplay(result);
                    display.setText(formatted);
                    startNewNumber = true;
                    if (!"Error".equals(formatted) && !"Division by zero".equals(formatted) && !"Invalid input for √".equals(formatted)) {
                        firstNumber = result;
                        addToHistory(base + " " + op + " " + secondNumber + " = " + formatted);
                    }
                }
            }
        }
    }

    private static void addToHistory(String entry) {
        history.add(0, entry); // Add to the beginning
        if (history.size() > HISTORY_LIMIT) {
            history.remove(history.size() - 1); // Remove oldest entry
        }
        updateHistoryDisplay();
    }

    private static void updateHistoryDisplay() {
        StringBuilder sb = new StringBuilder();
        for (String entry : history) {
            sb.append(entry).append("\n");
        }
        historyDisplay.setText(sb.toString());
    }

    private static void bindKey(JComponent c, KeyStroke ks, String command) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, command);
        c.getActionMap().put(command, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonAction(command);
            }
        });
    }

    private static String formatDisplay(double value) {
        if (Double.isNaN(value)) {
            return "Invalid calculation";
        } else if (Double.isInfinite(value)) {
            return "Result too large/small";
        } else if (value < 0 && operation.equals("√")) {
            return "Invalid input for √";
        }
        DecimalFormat df = new DecimalFormat("0.############");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value);
    }

    private static void setMaxLen(int value) {
        if (value > 0) {
            MAX_LEN = value;
        }
    }
}