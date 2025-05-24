package Calculator;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private JTextField display;
    private double num1 = 0, num2 = 0, result = 0;
    private String operator = "";

    private final Color darkBg = new Color(30, 30, 30);
    private final Color darkBtnNum = new Color(60, 60, 60);
    private final Color darkBtnOp = new Color(80, 80, 80);
    private final Color darkFg = Color.WHITE;
    private final Color darkDisplayBg = new Color(45, 45, 45);

    private final Color lightBg = new Color(245, 245, 245);
    private final Color lightBtnNum = new Color(220, 220, 220);
    private final Color lightBtnOp = new Color(200, 200, 200);
    private final Color lightFg = Color.BLACK;
    private final Color lightDisplayBg = Color.WHITE;

    private boolean darkMode = true;

    private JPanel btnPanel;
    private JButton toggleThemeBtn;

    public Calculator() {
        setTitle("Calculator");
        setSize(360, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(darkBg);

        display = new JTextField();
        display.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        display.setEditable(false);
        display.setBackground(darkDisplayBg);
        display.setForeground(darkFg);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(display, BorderLayout.NORTH);

        String[] buttons = {
            "AC", "√", "x²", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", ""
        };

        btnPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        btnPanel.setBackground(darkBg);

        for (String text : buttons) {
            if (text.isEmpty()) {
                btnPanel.add(new JLabel());
                continue;
            }
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setBackground(text.matches("[0-9.]") ? darkBtnNum : darkBtnOp);
            btn.setForeground(darkFg);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(this);

            // Hover effect
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    btn.setBackground(btn.getBackground().brighter());
                }
                public void mouseExited(MouseEvent evt) {
                    btn.setBackground(text.matches("[0-9.]") ? (darkMode ? darkBtnNum : lightBtnNum) : (darkMode ? darkBtnOp : lightBtnOp));
                }
            });

            btnPanel.add(btn);
        }
        add(btnPanel, BorderLayout.CENTER);

        toggleThemeBtn = new JButton("Switch to Light Mode");
        toggleThemeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        toggleThemeBtn.setFocusPainted(false);
        toggleThemeBtn.setBackground(darkBtnOp);
        toggleThemeBtn.setForeground(darkFg);
        toggleThemeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleThemeBtn.addActionListener(e -> toggleTheme());
        add(toggleThemeBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceBtn = (JButton) e.getSource();

        // Animate button press (no sound)
        animateButtonPress(sourceBtn);

        String input = e.getActionCommand();

        try {
            switch (input) {
                case "AC":
                    display.setText("");
                    num1 = num2 = result = 0;
                    operator = "";
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    num1 = Double.parseDouble(display.getText());
                    operator = input;
                    display.setText("");
                    break;
                case "=":
                    num2 = Double.parseDouble(display.getText());
                    switch (operator) {
                        case "+": result = num1 + num2; break;
                        case "-": result = num1 - num2; break;
                        case "*": result = num1 * num2; break;
                        case "/": result = num2 != 0 ? num1 / num2 : 0; break;
                    }
                    display.setText(String.valueOf(result));
                    break;
                case "√":
                    double value1 = Double.parseDouble(display.getText());
                    display.setText(String.valueOf(Math.sqrt(value1)));
                    break;
                case "x²":
                    double value2 = Double.parseDouble(display.getText());
                    display.setText(String.valueOf(Math.pow(value2, 2)));
                    break;
                default:
                    display.setText(display.getText() + input);
            }
        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    private void animateButtonPress(JButton btn) {
        final int frames = 15;
        final int delay = 15;
        final float scaleStart = 1.0f;
        final float scaleEnd = 0.85f;

        btn.setEnabled(false);

        Color originalBg = btn.getBackground();
        Color targetBg = originalBg.brighter().brighter();

        Border originalBorder = btn.getBorder();
        Border glowBorder = BorderFactory.createLineBorder(new Color(255, 215, 0), 3, true);

        Timer timerShrink = new Timer(delay, null);
        Timer timerExpand = new Timer(delay, null);

        final int[] count = {0};

        timerShrink.addActionListener(evt -> {
            count[0]++;
            float progress = count[0] / (float) frames;

            float scale = scaleStart - (scaleStart - scaleEnd) * progress;
            setButtonScale(btn, scale);

            btn.setBackground(interpolateColor(originalBg, targetBg, progress));

            if (progress > 0.3) {
                btn.setBorder(glowBorder);
            }

            if (count[0] >= frames) {
                timerShrink.stop();
                count[0] = 0;
                timerExpand.start();
            }
        });

        timerExpand.addActionListener(evt -> {
            count[0]++;
            float progress = count[0] / (float) frames;

            float scale = scaleEnd + (scaleStart - scaleEnd) * progress;
            setButtonScale(btn, scale);

            btn.setBackground(interpolateColor(targetBg, originalBg, progress));

            if (progress > 0.7) {
                btn.setBorder(originalBorder);
            }

            if (count[0] >= frames) {
                timerExpand.stop();
                btn.setEnabled(true);
            }
        });

        timerShrink.start();
    }

    private Color interpolateColor(Color c1, Color c2, float t) {
        t = Math.min(1f, Math.max(0f, t));
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
        return new Color(r, g, b);
    }

    private void setButtonScale(JButton btn, float scale) {
        int w = btn.getWidth();
        int h = btn.getHeight();

        int newW = (int) (w * scale);
        int newH = (int) (h * scale);

        int marginW = (w - newW) / 2;
        int marginH = (h - newH) / 2;

        btn.setMargin(new Insets(marginH, marginW, marginH, marginW));
        btn.repaint();
    }

    private void toggleTheme() {
        darkMode = !darkMode;

        if (darkMode) {
            getContentPane().setBackground(darkBg);
            display.setBackground(darkDisplayBg);
            display.setForeground(darkFg);
            btnPanel.setBackground(darkBg);

            for (Component c : btnPanel.getComponents()) {
                if (c instanceof JButton) {
                    JButton btn = (JButton) c;
                    String txt = btn.getText();
                    btn.setForeground(darkFg);
                    btn.setBackground(txt.matches("[0-9.]") ? darkBtnNum : darkBtnOp);
                    btn.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                }
            }
            toggleThemeBtn.setText("Switch to Light Mode");
            toggleThemeBtn.setBackground(darkBtnOp);
            toggleThemeBtn.setForeground(darkFg);
        } else {
            getContentPane().setBackground(lightBg);
            display.setBackground(lightDisplayBg);
            display.setForeground(lightFg);
            btnPanel.setBackground(lightBg);

            for (Component c : btnPanel.getComponents()) {
                if (c instanceof JButton) {
                    JButton btn = (JButton) c;
                    String txt = btn.getText();
                    btn.setForeground(lightFg);
                    btn.setBackground(txt.matches("[0-9.]") ? lightBtnNum : lightBtnOp);
                    btn.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                }
            }
            toggleThemeBtn.setText("Switch to Dark Mode");
            toggleThemeBtn.setBackground(lightBtnOp);
            toggleThemeBtn.setForeground(lightFg);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}
