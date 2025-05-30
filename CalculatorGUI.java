import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CalculatorGUI {
    private static JTextField display;
    private static double firstNumber = 0;
    private static String operation = "";
    private static boolean startNewNumber = true;

    public static void main(String[] args) {
        // 1. 设置窗口基础
        JFrame frame = new JFrame("全功能计算器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 500);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(240, 240, 245));

        // 2. 显示框配置
        display = new JTextField("0");
        display.setFont(new Font("Segoe UI", Font.BOLD, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        frame.add(display, BorderLayout.NORTH);

        // 3. 按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 8, 8));
        buttonPanel.setBackground(new Color(220, 220, 230));

        // 按钮定义
        String[][] buttons = {
            {"C", "CE", "⌫", "/"},
            {"7", "8", "9", "*"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"±", "0", ".", "="}
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

        // 5. 键盘支持
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char keyChar = e.getKeyChar();
                int keyCode = e.getKeyCode();
                
                // 映射键盘按键
                String command = switch (keyCode) {
                    case KeyEvent.VK_ENTER -> "=";
                    case KeyEvent.VK_ESCAPE -> "C";
                    case KeyEvent.VK_BACK_SPACE -> "⌫";
                    case KeyEvent.VK_MINUS -> "-";
                    case KeyEvent.VK_EQUALS -> "+";
                    default -> Character.isDigit(keyChar) ? String.valueOf(keyChar) : 
                           (keyChar == '*' ? "*" : 
                           (keyChar == '/' ? "/" : 
                           (keyChar == '.' ? "." : null)));
                };
                
                if (command != null) {
                    handleButtonAction(command);
                }
            }
        });

        // 6. 显示窗口
        frame.setVisible(true);
        frame.requestFocus(); // 确保键盘输入可用
    }

    // 创建带样式的按钮
    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        // 按钮颜色设置
        if (text.matches("[0-9.]")) {
            button.setBackground(new Color(240, 240, 250));
        } else if (text.equals("=")) {
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(220, 220, 230));
        }
        
        // 悬停效果
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(button.getBackground().brighter());
            }
        });
        
        return button;
    }

    // 处理所有按钮动作
    private static void handleButtonAction(String command) {
        switch (command) {
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> {
                if (startNewNumber) {
                    display.setText(command);
                    startNewNumber = false;
                } else {
                    display.setText(display.getText() + command);
                }
            }
            case "." -> {
                if (startNewNumber) {
                    display.setText("0.");
                    startNewNumber = false;
                } else if (!display.getText().contains(".")) {
                    display.setText(display.getText() + ".");
                }
            }
            case "±" -> {
                if (!display.getText().equals("0")) {
                    display.setText(display.getText().startsWith("-") ? 
                        display.getText().substring(1) : "-" + display.getText());
                }
            }
            case "⌫" -> {
                if (display.getText().length() > 1) {
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
            }
            case "CE" -> {
                display.setText("0");
                startNewNumber = true;
            }
            case "+", "-", "*", "/" -> {
                if (!startNewNumber) {
                    firstNumber = Double.parseDouble(display.getText());
                }
                operation = command;
                startNewNumber = true;
            }
            case "=" -> {
                if (!operation.isEmpty() && !startNewNumber) {
                    double secondNumber = Double.parseDouble(display.getText());
                    double result = switch (operation) {
                        case "+" -> firstNumber + secondNumber;
                        case "-" -> firstNumber - secondNumber;
                        case "*" -> firstNumber * secondNumber;
                        case "/" -> secondNumber == 0 ? 
                            (Double.isInfinite(firstNumber / 0.0) ? 0 : firstNumber) : // 处理除零
                            firstNumber / secondNumber;
                        default -> secondNumber;
                    };
                    display.setText(String.valueOf(result));
                    operation = "";
                    startNewNumber = true;
                }
            }
        }
    }
}