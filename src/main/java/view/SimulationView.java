package view;

import controller.Simulator;
import model.Body;
import model.Boundary;
import model.DrawPositionWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simulation view
 *
 * @author aricci
 */
public class SimulationView {

    private final VisualiserFrame frame;

    /**
     * Creates a view of the specified size (in pixels)
     */
    public SimulationView(int w, int h, Simulator simulator) {
        frame = new VisualiserFrame(w, h, simulator);
    }

    public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds) {
        frame.display(bodies, vt, iter, bounds);
    }

    public static class VisualiserFrame extends JFrame {

        private final VisualiserPanel panel;

        public VisualiserFrame(int w, int h, Simulator simulator) {
            setTitle("Bodies Simulation");
            setSize(w, h);
            setResizable(false);
            JPanel mainJPanel = new JPanel(new BorderLayout());
            JPanel btnJPanel = new JPanel();
            getContentPane().add(mainJPanel);
            JButton btnStart = new JButton("START");
            btnStart.addActionListener(e -> simulator.setBtnClicked(true));
            JButton btnStop = new JButton("STOP");
            btnStop.addActionListener(e -> simulator.setBtnClicked(false));
            panel = new VisualiserPanel(w, h);
            mainJPanel.add(BorderLayout.SOUTH, btnJPanel);
            mainJPanel.add(BorderLayout.CENTER, panel);
            btnJPanel.add(btnStart);
            btnJPanel.add(btnStop);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    System.exit(0);
                }

                public void windowClosed(WindowEvent ev) {
                    System.exit(0);
                }
            });
            this.setVisible(true);
        }

        public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                panel.display(bodies, vt, iter, bounds);
                                repaint();
                                return null;
                            }
                        }.doInBackground();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            } catch (Exception ex) {
                //silently ignored
            }
        }

    }

    public static class VisualiserPanel extends JPanel implements KeyListener {

        private CopyOnWriteArrayList<Body> bodies;
        private Boundary bounds;

        private long nIter;
        private double vt;
        private double scale = 1;

        private final long dx;
        private final long dy;

        public VisualiserPanel(int w, int h) {
            setSize(w, h);
            dx = w / 2 - 20;
            dy = h / 2 - 20;
            this.addKeyListener(this);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
            requestFocusInWindow();
        }

        public void paint(Graphics g) {
            if (bodies != null) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g2.clearRect(0, 0, this.getWidth(), this.getHeight());


                int x0 = getXCord(bounds.getX0());
                int y0 = getYCord(bounds.getY0());

                int wd = getXCord(bounds.getX1()) - x0;
                int ht = y0 - getYCord(bounds.getY1());

                g2.drawRect(x0, y0 - ht, wd, ht);

                bodies.forEach(b -> {
                    try {
                        new DrawPositionWorker(g2, b, scale, dx, dy);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                String time = String.format("%.2f", vt);
                g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " (UP for zoom in, DOWN for zoom out)", 2, 20);
            }
        }


        private int getXCord(double x) {
            return (int) (dx + x * dx * scale);
        }

        private int getYCord(double y) {
            return (int) (dy - y * dy * scale);
        }

        public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds) {
            this.bodies = new CopyOnWriteArrayList<>(bodies);
            this.bounds = bounds;
            this.vt = vt;
            this.nIter = iter;
        }

        public void updateScale(double k) {
            scale *= k;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 38) {        /* KEY UP */
                scale *= 1.1;
            } else if (e.getKeyCode() == 40) {    /* KEY DOWN */
                scale *= 0.9;
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }
    }
}
