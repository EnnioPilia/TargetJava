package TargetJava;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel {
    // UI elements
    private final JTextField angleField = new JTextField(5);
    private final JTextField speedField = new JTextField(5);
    private final JButton shootButton = new JButton("Tirer");
    private final JButton resetButton = new JButton("Nouvelle position");

    // Logic & game state
    private final Timer timer;
    private final Random random = new Random();

    private int angle;
    private int speed;
    private int wind;
    private double vx, vy;
    private double projX, projY;
    private Point cannonPos, targetPos;
    private final List<Point> trajectory = new ArrayList<>();
    private int score = 0;
    private final int gravity = 1;

    public GamePanel() {
        setLayout(new FlowLayout());
        add(new JLabel("Angle (0-90):"));
        add(angleField);
        add(new JLabel("Vitesse (10-100):"));
        add(speedField);
        add(shootButton);
        add(resetButton);

        shootButton.addActionListener(e -> startShooting());
        resetButton.addActionListener(e -> placeCannonAndTarget());

        placeCannonAndTarget();

        timer = new Timer(50, e -> updateProjectile());
        timer.setRepeats(true);
    }

   private void placeCannonAndTarget() {
    int margin = 50;
    int panelHeight = Math.max(getHeight(), 200); // Pour éviter getHeight() == 0

    cannonPos = new Point(random.nextInt(100), panelHeight - margin);

    int targetYMax = Math.max(panelHeight - 100, 1); // au moins 1
    targetPos = new Point(400 + random.nextInt(100), random.nextInt(targetYMax));

    wind = random.nextInt(11) - 5; // [-5, +5]
    trajectory.clear();
    projX = cannonPos.x;
    projY = cannonPos.y;
    repaint();
}


    private void startShooting() {
        try {
            angle = Integer.parseInt(angleField.getText());
            speed = Integer.parseInt(speedField.getText());

            if (angle < 0 || angle > 90 || speed < 10 || speed > 100) {
                throw new NumberFormatException();
            }

            double radians = Math.toRadians(angle);
            vx = Math.cos(radians) * speed / 2;
            vy = -Math.sin(radians) * speed / 2;
            projX = cannonPos.x;
            projY = cannonPos.y;
            trajectory.clear();
            timer.start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un angle (0-90) et une vitesse (10-100) valides.");
        }
    }

    private void updateProjectile() {
        projX += vx;
        vx += wind / 10.0;
        projY += vy;
        vy += gravity;

        trajectory.add(new Point((int) projX, (int) projY));

        // Collision check
        Rectangle projectileRect = new Rectangle((int) projX, (int) projY, 5, 5);
        Rectangle targetRect = new Rectangle(targetPos.x, targetPos.y, 20, 20);

        if (projectileRect.intersects(targetRect)) {
            timer.stop();
            score++;
            JOptionPane.showMessageDialog(this, "Cible touchée !");
        }

        // Out of bounds
        if (projX < 0 || projX > getWidth() || projY > getHeight()) {
            timer.stop();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw cannon
        g.setColor(Color.BLUE);
        g.fillRect(cannonPos.x, cannonPos.y, 20, 20);

        // Draw target
        g.setColor(Color.RED);
        g.fillRect(targetPos.x, targetPos.y, 20, 20);

        // Draw projectile
        g.setColor(Color.BLACK);
        g.fillOval((int) projX, (int) projY, 5, 5);

        // Draw trajectory
        g.setColor(Color.GRAY);
        for (Point p : trajectory) {
            g.fillOval(p.x, p.y, 2, 2);
        }

        // Draw score
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Vent: " + wind, 10, 40);
    }
}
