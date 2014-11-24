package conways;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/*
 * @author Violet
 */

public class ConwayPanel extends JPanel {
    Logger logger = Logger.getLogger(ConwayPanel.class.getName());
    
    private boolean step;
    private boolean paused;
    
    private final int startX, startY, cellNum;
    private int zoom;
    private boolean[][] cells;
    private ArrayList<Integer> xDrag = new ArrayList<>(), yDrag = new ArrayList<>();
    
    private final Map<Key, Boolean> keyMap = new HashMap<>();
    private final Map<Direction, Boolean> directionMap = new HashMap<>();
    
    private int currentX, currentY;
    
    enum Key {
        c(KeyEvent.VK_C),
        f(KeyEvent.VK_F),
        i(KeyEvent.VK_I),
        p(KeyEvent.VK_P),
        s(KeyEvent.VK_S),
        space(KeyEvent.VK_SPACE);
        private final int keyCode;

        private Key(int keyCode) {
            this.keyCode = keyCode;
        }

        public int getKeyCode() {
            return keyCode;
        }
    }
    
    enum Direction {
        up(KeyEvent.VK_UP),
        down(KeyEvent.VK_DOWN),
        left(KeyEvent.VK_LEFT),
        right(KeyEvent.VK_RIGHT);
        private final int keyCode;

        private Direction(int keyCode) {
            this.keyCode = keyCode;
        }

        public int getKeyCode() {
            return keyCode;
        }
    }
    
    public ConwayPanel() {
        super();
        
        setBackground(new Color(245, 255, 245, 255));
        
        paused = true;
        
        startX = 0;
        startY = 0;
        zoom = 15;
        cellNum = 200;
        
        cells = new boolean[cellNum][cellNum];
        
        currentX = 0;
        currentY = 0;
        
        defineMaps();
        setKeyBindings();
        Timer timer = new Timer(100, new keyListener());
        timer.start();
        
        setupMouseListeners();
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void defineMaps() {
        for (Key key : Key.values()) {
            keyMap.put(key, false);
        }
        for (Direction direction : Direction.values()) {
            directionMap.put(direction, false);
        }
    }
    
    private void setKeyBindings() {
        InputMap inMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actMap = getActionMap();
        for (final Key key : Key.values()) {
//            KeyStroke pressed = KeyStroke.getKeyStroke(key.getKeyCode(), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false);
//            KeyStroke released = KeyStroke.getKeyStroke(key.getKeyCode(), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true);
            KeyStroke pressed = KeyStroke.getKeyStroke(key.getKeyCode(), 0, false);
            KeyStroke released = KeyStroke.getKeyStroke(key.getKeyCode(), 0, true);
            inMap.put(pressed, key.toString() + "pressed");
            inMap.put(released, key.toString() + "released");
            actMap.put(key.toString() + "pressed", new AbstractAction() {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    keyMap.put(key, true);
                }
            });
            actMap.put(key.toString() + "released", new AbstractAction() {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    keyMap.put(key, false);
                }
            });
        }
        
        for (final Direction direction : Direction.values()) {
            KeyStroke pressed = KeyStroke.getKeyStroke(direction.getKeyCode(), 0, false);
            KeyStroke released = KeyStroke.getKeyStroke(direction.getKeyCode(), 0, true);
            inMap.put(pressed, direction.toString() + "pressed");
            inMap.put(released, direction.toString() + "released");
            actMap.put(direction.toString() + "pressed", new AbstractAction() {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    directionMap.put(direction, true);
                }
            });
            actMap.put(direction.toString() + "released", new AbstractAction() {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    directionMap.put(direction, false);
                }
            });
        }
    }
    
    private class keyListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (Key key : Key.values()) {
                if (keyMap.get(key)) {
                    switch(key.toString()) {
                        case "c":
                            for (int y = 0; y < cellNum; y++) {
                                for (int x = 0; x < cellNum; x++) {
                                    cells[x][y] = false;
                                }
                            }
                            if (!paused) {
                                paused = true;
                            }
                            break;
                        case "f":
                            for (int y = 0; y < cellNum; y++) {
                                for (int x = 0; x < cellNum; x++) {
                                    cells[x][y] = true;
                                }
                                if (!paused) {
                                    paused = true;
                                }
                            }
                            break;
                        case "i":
                            for (int y = 0; y < cellNum; y++) {
                                for (int x = 0; x < cellNum; x++) {
                                    cells[x][y] = !cells[x][y];
                                }
                                if (!paused) {
                                    paused = true;
                                }
                            }
                            break;
                        case "l":
                            for (int y = 0; y < cellNum; y++) {
                                for (int x = 0; x < cellNum; x++) {
                                    cells[x][y] = !cells[x][y];
                                }
                            }
                            break;
                        case "p":
                            paused = !paused;
                            break;
                        case "s":
                            step = true;
                            break;
                        case "space":
                            flipCell(currentX, currentY);
                            if (!paused) {
                                paused = true;
                            }
//                        default:
                    }
                }
            }
            
            for (Direction direction : Direction.values()) {
                if (directionMap.get(direction)) {
                    switch(direction.toString()) {
                        case "down":
                            currentY += currentY == cellNum - 1 ? 0 : 1;
                            if (!paused) {
                                paused = true;
                            }
                            break;
                        case "up":
                            currentY -= currentY == 0 ? 0 : 1;
                            if (!paused) {
                                paused = true;
                            }
                            break;
                        case "left":
                            currentX -= currentX == 0 ? 0 : 1;
                            if (!paused) {
                                paused = true;
                            }
                            
                            break;
                        case "right":
                            currentX += currentX == cellNum - 1 ? 0 : 1;
                            if (!paused) {
                                paused = true;
                            }
                            break;
//                        default:
                    }
                }
            }
        }
    }
    
    private void setupMouseListeners() {
        addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();
                int x = p.x / (zoom + 1);
                int y = p.y / (zoom + 1);
                
                boolean flipped = false;
                for (int i = 0; i < xDrag.size(); i++) {
                    if (x == xDrag.get(i) && y == yDrag.get(i)) {
                        flipped = true;
                        break;
                    }
                }
                if (y >= 0 && y < cellNum && x >= 0 && x < cellNum && !flipped) {
                    flipCell(x, y);
                    xDrag.add(x);
                    yDrag.add(y);
                }
                if (!paused) {
                    paused = true;
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                int x = p.x / (zoom + 1);
                int y = p.y / (zoom + 1);

                if (y >= 0 && y < cellNum && x >= 0 && x < cellNum) {
                    flipCell(x, y);
                    xDrag.add(x);
                    yDrag.add(y);
                }
                if (!paused) {
                    paused = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                xDrag = new ArrayList<>();
                yDrag = new ArrayList<>();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(255, 255, 255, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(new Color(245, 245, 255, 245));
                getGraphics().drawRect(2, 2, getWidth() - 2, getHeight() - 2);
            }
        });
        
        addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoom(e.getWheelRotation());
            }
        });
    }
    
    public void run() {
        boolean[][] nextCells;
        while (true) {
            if (!paused || step) {
                nextCells = new boolean[cellNum][cellNum];
                for (int y = 0; y < cellNum; y++) {
                    nextCells[y] = Arrays.copyOf(cells[y], cells[y].length);
                }
//                nextCells = (boolean[][])cells.clone();
                for (int y = 0; y < cellNum; y++) {
                    for (int x = 0; x < cellNum; x++) {
                        boolean cell = cells[x][y];
                        if (cell) {
                            if (!(getNumLiveNeighbors(x, y) == 2 || getNumLiveNeighbors(x, y) == 3)) {
                                nextCells[x][y] = false;
                            }
                        } else {
                            if (getNumLiveNeighbors(x, y) == 3) {
                                nextCells[x][y] = true;
                            }
                        }
                    }
                }
                cells = nextCells;
                if (step) {
                    step = false;
                }
            }
            try {
                repaint();
                Thread.sleep(10);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in ConwayPanel.run():", e);
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(new Color(0, 0, 255, 2));
        
        g.fillRect(0, 0, zoom * cellNum + cellNum, zoom * cellNum + cellNum);
        
        g.setColor(new Color(0, 0, 0, 20));
        
        for (int y = 0; y <= zoom * cellNum + cellNum; y += zoom + 1) {
            g.drawLine(0, y, zoom * cellNum + cellNum, y);
        }
        
        for (int x = 0; x <= zoom * cellNum + cellNum; x += zoom + 1) {
            g.drawLine(x, 0, x, zoom * cellNum + cellNum);
        }
        
        g.setColor(new Color(0, 240, 190, 150));
        
        for (int y = 0; y < cellNum; y++) {
            for (int x = 0; x < cellNum; x++) {
                if (cells[x][y]) {
                    g.fillRect(x * zoom + x + 1, y * zoom + y + 1, zoom, zoom);
                }
            }
        }
        
        g.setColor(Color.black);
        
        if (paused) {
            g.drawRect(currentX * zoom + currentX + 1, currentY * zoom + currentY + 1, zoom, zoom);
        }
    }
    
    public void zoom(int wheelRot) {
        zoom -= wheelRot;
        if (zoom < 3) {
            zoom = 3;
        }
        if (zoom * cellNum + 1 > getWidth() * 4 || zoom * cellNum + 1 > getHeight() * 4) {
            zoom = zoom + wheelRot;
            
        }
    }
    
    public void flipCell(int x, int y) {
        cells[x][y] = !cells[x][y];
    }
    
    public int getNumLiveNeighbors(int xInd, int yInd) {
        int num = 0;
        for (int y = (yInd > 0 ? yInd - 1 : 0); y <= (yInd < cellNum - 1 ? yInd + 1 : cellNum - 1); y++) {
            for (int x = (xInd > 0 ? xInd - 1 : 0); x <= (xInd < cellNum - 1 ? xInd + 1 : cellNum - 1); x++) {
                if (x == xInd && y == yInd) {
                    continue;
                }
                num += (cells[x][y] ? 1 : 0);
            }
        }
        return num;
    }
}
