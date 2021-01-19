package view;

import model.IWorld;

import javax.swing.*;
import java.awt.*;
import java.util.ConcurrentModificationException;

public class View extends JFrame {
    private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 600);
    private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(400, 400);
    private static final int BLOCK_SIZE = 10;

    private final JMenuItem mGamePlay;
    private final JMenuItem mGameStop;
    private final int movesPerSecond = 3;
    private boolean runn;
    private JGamePanel panel;

    public View(IWorld world) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Conway's Game of Life");
        setSize(DEFAULT_WINDOW_SIZE);
        setMinimumSize(MINIMUM_WINDOW_SIZE);
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
        setVisible(true);
        JMenuBar mMenu = new JMenuBar();
        setJMenuBar(mMenu);
        JMenu mFile = new JMenu("File");
        mMenu.add(mFile);
        JMenu mGame = new JMenu("Game");
        mMenu.add(mGame);
        JMenuItem mFileExit = new JMenuItem("Exit");
        mFileExit.addActionListener(e -> System.exit(0));
        mFile.add(new JSeparator());
        mFile.add(mFileExit);
        mGamePlay = new JMenuItem("Play");
        mGamePlay.addActionListener(e -> setGameBeingPlayed(true));
        mGameStop = new JMenuItem("Stop");
        mGameStop.setEnabled(false);
        mGameStop.addActionListener(e -> setGameBeingPlayed(false));
        JMenuItem mGameReset = new JMenuItem("Reset");
        mGameReset.addActionListener(e -> panel.generate());
        mGame.add(new JSeparator());
        mGame.add(mGamePlay);
        mGame.add(mGameStop);
        mGame.add(mGameReset);
        panel = new JGamePanel(world);
    }

    public void setGameBeingPlayed(boolean isBeingPlayed) {
        if (isBeingPlayed) {
            mGamePlay.setEnabled(false);
            mGameStop.setEnabled(true);
            Thread game = new Thread(panel);
            runn = true;
            game.start();
        } else {
            mGamePlay.setEnabled(true);
            mGameStop.setEnabled(false);
            runn = false;
        }
    }

    private class JGamePanel extends JPanel implements Runnable {
        private final Dimension d_gameBoardSize;

        private final IWorld world;

        public JGamePanel(IWorld world) {
            this.world = world;
            d_gameBoardSize = new Dimension(world.getStates().length, world.getStates()[0].length);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try {
                g.setColor(Color.blue);
                boolean[][] states = world.getStates();
                for (int x = 0; x < states.length; x++) {
                    for (int y = 0; y < states[x].length; y++) {
                        g.fillRect(BLOCK_SIZE + (BLOCK_SIZE * x), BLOCK_SIZE + (BLOCK_SIZE * y), BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            } catch (ConcurrentModificationException ignored) {
            }
            g.setColor(Color.BLACK);
            for (int i = 0; i <= d_gameBoardSize.width; i++) {
                g.drawLine(((i * BLOCK_SIZE) + BLOCK_SIZE), BLOCK_SIZE, (i * BLOCK_SIZE) + BLOCK_SIZE, BLOCK_SIZE + (BLOCK_SIZE * d_gameBoardSize.height));
            }
            for (int i = 0; i <= d_gameBoardSize.height; i++) {
                g.drawLine(BLOCK_SIZE, ((i * BLOCK_SIZE) + BLOCK_SIZE), BLOCK_SIZE * (d_gameBoardSize.width + 1), ((i * BLOCK_SIZE) + BLOCK_SIZE));
            }
        }

        @Override
        public void run() {
            while (runn) {
                world.step();
                repaint();
                try {
                    Thread.sleep(1000 / movesPerSecond);
                    run();
                } catch (InterruptedException ignored) {
                }
            }
        }

        public void generate() {
            world.generate();
        }
    }
}
