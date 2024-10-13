package proiect.labirint;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MazeGenerator extends JPanel {

    private int width;
    private int height;
    private int[][] maze;
    private int cellSize; // Dimensiunea celulei în pixeli
    private Image backgroundImage; // Imaginea de fundal
    private Image Image;
    private Image Pui;
    private Image graunte;
    private Clip collectSound;

    // Coordonatele jucătorului
    private int playerX = 0;
    private int playerY = 0;

    // Puncte (graunte)
    private ArrayList<Point> graunteList;  // Listă cu pozițiile grăunțelor

    // Scorul jucătorului
    private int score = 0;
    private JLabel scoreLabel;  // Label pentru scor

    // Mesajul "Good Job!"
    private String message = "";  // Mesajul de feedback
    private long messageStartTime;  // Timpul la care mesajul a fost afișat
    private static final long MESSAGE_DURATION = 2000;  // Durata în milisecunde

    // Butonul de restart
    private JButton restartButton;

    private final Random random = new Random();

    // Constructor pentru dimensiunile labirintului
    public MazeGenerator(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        // Încărcăm imaginile
        this.backgroundImage = new ImageIcon("E:\\facultate\\anul 3\\IP\\Labirint\\src\\main\\java\\proiect\\labirint\\WhatsApp Image 2024-10-13 at 20.38.05.jpeg").getImage();
        this.Image = new ImageIcon("E:\\facultate\\anul 3\\IP\\Labirint\\src\\main\\java\\proiect\\labirint\\WhatsApp Image 2024-10-13 at 17.32.38.jpeg").getImage();
        this.Pui = new ImageIcon("E:\\facultate\\anul 3\\IP\\Labirint\\src\\main\\java\\proiect\\labirint\\pui.png").getImage();
        this.graunte = new ImageIcon("E:\\facultate\\anul 3\\IP\\Labirint\\src\\main\\java\\proiect\\labirint\\graunte.png").getImage();
        
        this.maze = new int[height][width];
        this.graunteList = new ArrayList<>();  // Inițializăm lista de grăunțe
        generateMaze(0, 0);

        // Setăm jucătorul la poziția de start
        playerX = 0;
        playerY = 0;
        
        
        // Generăm aleatoriu grăunțele în labirint
        spawnGraunte(10);  // Plasăm 10 grăunțe (poți modifica numărul)
        loadSounds();
        // Adăugăm key listener pentru mișcarea jucătorului
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                movePlayer(e.getKeyCode());
            }
        });

        // Creăm și adăugăm un label pentru scor
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);  // Setăm textul să fie alb
        this.setLayout(null);  // Setăm layout-ul ca null pentru a poziționa label-ul manual
        scoreLabel.setBounds(10, 10, 200, 30);  // Setăm poziția și dimensiunea label-ului
        this.add(scoreLabel);

        // Creăm butonul de restart
        restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setBackground(Color.decode("#734300")); // Setează culoarea de fundal a butonului
        restartButton.setForeground(Color.WHITE); // Setează culoarea textului butonului
        restartButton.setBounds((width * cellSize) / 2 - 100, (height * cellSize) / 2 + 30, 200, 50);
        restartButton.setVisible(false);  // Îl ascundem inițial
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();  // Resetăm jocul când este apăsat butonul
            }
        });
        this.add(restartButton);
    }

    // Generare labirint folosind backtracking recursiv
    private void generateMaze(int x, int y) {
        Integer[] directions = new Integer[]{0, 1, 2, 3};
        Collections.shuffle(Arrays.asList(directions), random);

        for (int direction : directions) {
            int nx = x, ny = y;

            switch (direction) {
                case 0: ny = y - 2; break; // Sus
                case 1: nx = x + 2; break; // Dreapta
                case 2: ny = y + 2; break; // Jos
                case 3: nx = x - 2; break; // Stânga
            }

            // Verifică dacă noua poziție este în interiorul granițelor labirintului și dacă este neexplorată
            if (nx >= 0 && nx < width && ny >= 0 && ny < height && maze[ny][nx] == 0) {
                maze[ny][nx] = 1;   // Marchez celula ca parte din labirint

                // Elimin peretele dintre celule
                int wallY = y + (ny - y) / 2;
                int wallX = x + (nx - x) / 2;

                if (wallY >= 0 && wallY < height && wallX >= 0 && wallX < width) {
                    maze[wallY][wallX] = 1; // Marchez ca liber
                }

                generateMaze(nx, ny); // Continui generarea recursivă
            }
        }
    }

    // Generăm grăunțele în poziții aleatorii în labirint
    private void spawnGraunte(int count) {
        int graunteCount = 0;
        while (graunteCount < count) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            // Plasăm grăunțele doar pe celulele libere (valori de 1 în labirint)
            if (maze[y][x] == 1 && (x != playerX || y != playerY)) {
                graunteList.add(new Point(x, y));
                graunteCount++;
            }
        }
    }

    // Metoda pentru a muta jucătorul în funcție de direcția aleasă
    public void movePlayer(int keyCode) {
        int nextX = playerX;
        int nextY = playerY;

        // Verificăm tasta apăsată și calculăm noile coordonate
        switch (keyCode) {
            case KeyEvent.VK_UP:
                nextY -= 1; // Sus
                break;
            case KeyEvent.VK_DOWN:
                nextY += 1; // Jos
                break;
            case KeyEvent.VK_LEFT:
                nextX -= 1; // Stânga
                break;
            case KeyEvent.VK_RIGHT:
                nextX += 1; // Dreapta
                break;
        }

        // Verificăm dacă noua poziție este validă și liberă (celulă cu valoarea 1)
        if (nextX >= 0 && nextX < width && nextY >= 0 && nextY < height && maze[nextY][nextX] == 1) {
            playerX = nextX;
            playerY = nextY;

            // Verificăm dacă jucătorul a colectat un graunte
            for (int i = 0; i < graunteList.size(); i++) {
                Point grauntePos = graunteList.get(i);
                if (grauntePos.x == playerX && grauntePos.y == playerY) {
                    graunteList.remove(i);  // Eliminăm grăuntele colectat
                    score++;  // Actualizăm scorul
                    scoreLabel.setText("Score: " + score);  // Actualizăm textul din label
                    
                if (collectSound != null) {
    System.out.println("Redare sunet de colectare...");
    collectSound.setFramePosition(0); // Resetează clipul pentru a începe de la început
    collectSound.start(); // Redăm sunetul
}
                    if (score >= 10) {
                        message = "Good Job!";
                        messageStartTime = System.currentTimeMillis();
                        restartButton.setVisible(true); // Afișăm butonul de restart
                    }
                }
            }

            repaint(); // Redesenăm componenta pentru a actualiza poziția jucătorului și graunțele
        }
    }

    // Metoda de restartare a jocului
    private void restartGame() {
        score = 0; // Resetăm scorul
        scoreLabel.setText("Score: 0"); // Resetăm textul scorului
        message = ""; // Resetăm mesajul
        graunteList.clear(); // Golim lista de grăunțe
        generateMaze(0, 0); // Generăm din nou labirintul
        spawnGraunte(10); // Plasăm din nou grăunțe
        playerX = 0; // Resetăm poziția jucătorului
        playerY = 0; // Resetăm poziția jucătorului
        restartButton.setVisible(false); // Ascundem butonul de restart
        repaint(); // Redesenăm componenta
    }

    // Metoda de desenare a labirintului
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Asigurăm ștergerea corectă a componentei

        // Parcurgem fiecare celulă a labirintului și desenăm doar celulele libere
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maze[y][x] == 1) {
                    // Desenăm imaginea în locul celulei libere
                    g.drawImage(backgroundImage, x * cellSize, y * cellSize,
                                cellSize, cellSize, this);
                } else {
                    // Desenăm imaginea pentru zonele de pereți
                    g.drawImage(Image, x * cellSize, y * cellSize,
                                cellSize, cellSize, this);
                }
            }
        }

        // Desenăm grăunțele
        for (Point grauntePos : graunteList) {
            g.drawImage(graunte, grauntePos.x * cellSize, grauntePos.y * cellSize, cellSize, cellSize, this);
        }

        // Desenăm jucătorul (Pui) la poziția curentă
        g.drawImage(Pui, playerX * cellSize, playerY * cellSize, cellSize, cellSize, this);

        // Desenăm mesajul "Good Job!" dacă a fost colectat un graunte
        if (!message.isEmpty() && (System.currentTimeMillis() - messageStartTime < MESSAGE_DURATION)) {
            g.setColor(Color.YELLOW);  // Setăm culoarea textului
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString(message, (width * cellSize) / 2 - 50, (height * cellSize) / 2);  // Poziționăm mesajul
        }
    }
private void loadSounds() {
    try {
        URL soundURL = new File("C:\\Users\\ioio\\Downloads\\pollitos-chiken-lillttle-bird-ken-34358-[AudioTrimmer.com].wav").toURI().toURL();

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
        collectSound = AudioSystem.getClip(); // Asigură-te că folosești javax.sound.sampled.Clip
        collectSound.open(audioInputStream); // Încărcăm fișierul audio
    } catch (Exception e) {
        System.err.println("Could not load sound: " + e.getMessage());
    }
}
}
