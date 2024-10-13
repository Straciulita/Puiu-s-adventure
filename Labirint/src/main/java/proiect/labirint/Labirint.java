package proiect.labirint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Labirint {

    public static void main(String[] args) {
        Fereastra();
    }

    private static void Fereastra() {
        // Dimensiunea labirintului
        int numCells = 21;
        int cellSize = 40;

        // Calculăm dimensiunea ferestrei în funcție de numărul de celule și dimensiunea fiecărei celule
        int windowSize = Math.max(numCells * cellSize, 840); // Asigură-te că dimensiunea ferestrei este cel puțin 840

        // Creăm frame-ul principal
        JFrame frame = new JFrame("Aventura lui PIU");

        // Creăm un layout pentru a comuta între panouri
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Creăm panourile individuale
        MenuPanel menuPanel = new MenuPanel(cardLayout, mainPanel); // Meniul principal
        MazePanel mazePanel = new MazePanel(numCells, numCells, cellSize); // Panoul labirintului
        LoadingPanel loadingPanel = new LoadingPanel(cardLayout, mainPanel); // Panoul de încărcare

        // Adăugăm panourile în CardLayout
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(loadingPanel, "Loading");
        mainPanel.add(mazePanel, "Game");

        // Afișăm meniul la început
        cardLayout.show(mainPanel, "Menu");

        // Configurăm frame-ul
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mainPanel);
        frame.setSize(windowSize+20, windowSize + 40);  // Setăm dimensiunea ferestrei
       
        frame.setVisible(true);
    }
}

// Panoul de meniu principal
// Panoul de meniu principal
class MenuPanel extends JPanel {
    private Image backgroundImage;

    public MenuPanel(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(null); // Setăm layout-ul null pentru a putea poziționa elementele manual

        // Încarcă imaginea de fundal (înlocuiește calea cu cea a imaginii tale)
        backgroundImage = new ImageIcon("E:\\facultate\\anul 3\\IP\\Labirint\\src\\main\\java\\proiect\\labirint\\WhatsApp Image 2024-10-13 at 21.48.40.jpeg").getImage();

        // Titlul jocului
        JLabel titleLabel = new JLabel("Aventura lui PIU");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40)); 
        titleLabel.setForeground(Color.decode("#241b03")); // Schimbă culoarea titlului

        titleLabel.setBounds(250, 100, 400, 60); // Poziția și dimensiunea titlului
        add(titleLabel);

        // Butonul Play
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.PLAIN, 30));
        playButton.setBackground(Color.decode("#734300")); // Setează culoarea de fundal a butonului
        playButton.setForeground(Color.WHITE); // Setează culoarea textului butonului
        playButton.setBounds(350, 400, 200, 60); // Poziția și dimensiunea butonului Play
        add(playButton);

        // Acțiunea butonului Play
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Când apasă Play, comutăm la panoul de încărcare
                cardLayout.show(mainPanel, "Loading");

                // Simulăm încărcarea și după câteva secunde comutăm la joc
                Timer timer = new Timer(2000, new ActionListener() { // Întârziere de 2 secunde
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cardLayout.show(mainPanel, "Game");

                        // Dăm focus la MazePanel pentru a putea primi evenimentele tastaturii
                        mainPanel.getComponent(2).requestFocusInWindow();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Desenăm imaginea de fundal pentru a se potrivi ferestrei
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}


// Panoul de încărcare
class LoadingPanel extends JPanel {
    public LoadingPanel(CardLayout cardLayout, JPanel mainPanel) {
        setBackground(Color.decode("#ffd8a1")); // Setează culoarea de fundal a panoului de încărcare
        setLayout(new BorderLayout());

        JLabel loadingLabel = new JLabel("Loading...", JLabel.CENTER);
        loadingLabel.setForeground(Color.decode("#241b03")); // Schimbă culoarea titlului
        
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 50));
        add(loadingLabel, BorderLayout.CENTER);
    }
}

// Panoul labirintului (folosim codul tău original pentru MazePanel)
// Panoul labirintului (MazePanel)
class MazePanel extends MazeGenerator {

    public MazePanel(int width, int height, int cellSize) {
        super(width, height, cellSize);

        // Setăm focusabilitatea pentru a capta evenimentele tastaturii
        setFocusable(true);
        requestFocusInWindow();

        // Adăugăm un listener pentru evenimentele tastaturii
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //movePlayer(e.getKeyCode());
            }
        });
    }
    
}


