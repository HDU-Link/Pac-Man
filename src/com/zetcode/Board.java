package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.zetcode.sprite.Blinky;
import com.zetcode.sprite.Clyde;
import com.zetcode.sprite.Inky;
import com.zetcode.sprite.Pacman;
import com.zetcode.sprite.Pinky;
import com.zetcode.sprite.Sprite;

public class Board extends JPanel {
	private Dimension d;
	private Blinky blinky;
	private Pinky pinky;
	private Inky inky;
	private Clyde clyde;
	private Pacman pacman;
	private HashSet<Sprite> ghosts = new HashSet();
	private HashSet<Sprite> pills = new HashSet();
	private HashSet<Sprite> powerpills = new HashSet();
	private Sprite cherry = new Sprite();
	private Sprite banana = new Sprite();
	public static Board instance;
	private int lives = 4;
	private int score = 0;
	private int count = 0;
	private boolean inGame = true;
	private boolean scream = false;
	private boolean musicPlayed = false;
	private boolean newGame = false;
	private boolean gamePaused = false;
	private long startTime;
	private long whiteTime;
	private Timer timer;
	private int screamDuration = 10000;
	private Timer screamTimer = new Timer(screamDuration, e -> {
		scream = false;
		((Timer) e.getSource()).stop();
	});
	private Random random = new Random();

	public Board() {
		initBoard();
		instance = this;
	}

	private void mapInit() {
		for (int y = 0; y < Commons.HEIGHT; y++) {
			String row = Commons.MAP[y];
			for (int x = 0; x < Commons.WIDTH; x++) {
				Commons.walls[x][y] = (row.charAt(x) != 'X' && row.charAt(x) != 'd');
			}
		}
		for (int x = 0; x < Commons.HEIGHT; x++) {
			for (int y = 0; y < Commons.WIDTH; y++) {
				Sprite s = new Sprite();
				if (Commons.MAP[x].charAt(y) == '.') {
					s.setX(y * Commons.TILE_SIZE + 4);
					s.setY(x * Commons.TILE_SIZE + 4);
					pills.add(s);
				} else if (Commons.MAP[x].charAt(y) == 'B') {
					s.setX(y * Commons.TILE_SIZE - 3);
					s.setY(x * Commons.TILE_SIZE - 3);
					powerpills.add(s);
				} else if (Commons.MAP[x].charAt(y) == 'r') {
					blinky = new Blinky(y * Commons.TILE_SIZE + 2, x * Commons.TILE_SIZE - 3);
					ghosts.add(blinky);
				} else if (Commons.MAP[x].charAt(y) == 'b') {
					inky = new Inky(y * Commons.TILE_SIZE + 2, x * Commons.TILE_SIZE - 6);
					ghosts.add(inky);
				} else if (Commons.MAP[x].charAt(y) == 'p') {
					pinky = new Pinky(y * Commons.TILE_SIZE + 2, x * Commons.TILE_SIZE - 6);
					ghosts.add(pinky);
				} else if (Commons.MAP[x].charAt(y) == 'o') {
					clyde = new Clyde(y * Commons.TILE_SIZE + 2, x * Commons.TILE_SIZE - 6);
					ghosts.add(clyde);
				} else if (Commons.MAP[x].charAt(y) == 'P')
					pacman = new Pacman(y * Commons.TILE_SIZE + 2, x * Commons.TILE_SIZE - 8);
			}
		}
	}

	private void initBoard() {
		addKeyListener(new TAdapter());
		setFocusable(true);
		d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
		setBackground(Color.black);
		new AudioPlayer().Music("src/music/开始游戏音效.wav");
		timer = new Timer(Commons.DELAY, new GameCycle());
		timer.start();
		startTime = System.currentTimeMillis();
		mapInit();
	}

	private void gameInit() {
		for (int x = 0; x < Commons.HEIGHT; x++) {
			for (int y = 0; y < Commons.WIDTH; y++) {
				if (Commons.MAP[x].charAt(y) == 'r') {
					blinky.setX(y * Commons.TILE_SIZE + 2);
					blinky.setY(x * Commons.TILE_SIZE - 2);
				} else if (Commons.MAP[x].charAt(y) == 'b') {
					inky.setX(y * Commons.TILE_SIZE + 2);
					inky.setY(x * Commons.TILE_SIZE - 2);
				} else if (Commons.MAP[x].charAt(y) == 'p') {
					pinky.setX(y * Commons.TILE_SIZE + 2);
					pinky.setY(x * Commons.TILE_SIZE - 2);
				} else if (Commons.MAP[x].charAt(y) == 'o') {
					clyde.setX(y * Commons.TILE_SIZE + 2);
					clyde.setY(x * Commons.TILE_SIZE - 2);
				} else if (Commons.MAP[x].charAt(y) == 'P') {
					pacman.setX(y * Commons.TILE_SIZE + 2);
					pacman.setY(x * Commons.TILE_SIZE - 8);
				}
			}
		}
	}

	private void drawEyes(Graphics g, Sprite sprite) {
		switch (sprite.getDirection()) {
		case (0):
			g.drawImage(sprite.eyeImage(), sprite.getX() + 2, sprite.getY() + 12, this);
			break;
		case (1):
			g.drawImage(sprite.eyeImage(), sprite.getX() + 6, sprite.getY() + 12, this);
			break;
		case (2):
			g.drawImage(sprite.eyeImage(), sprite.getX() + 4, sprite.getY() + 8, this);
			break;
		case (3):
			g.drawImage(sprite.eyeImage(), sprite.getX() + 4, sprite.getY() + 16, this);
			break;
		}
	}

	private void drawGhosts(Graphics g) {
		for (Sprite ghost : ghosts) {
			if (ghost.isVisible()) {
				if (scream) {
					ghost.speed = 2;
					if (ghost.collision(pacman)) {
						gamePaused = true;
						double bonus = 200 * Math.pow(2, (count++));
						this.score += bonus;
						g.setColor(Color.cyan);
						g.setFont(new Font("Helvetica", Font.BOLD, 16));
						g.drawString((int) bonus + "", ghost.getX() - 25, ghost.getY() - 25);
						new Timer(3500, e -> gamePaused = false).start();
						ghost.die();
						ghost.setX(13 * Commons.TILE_SIZE);
						ghost.setY(13 * Commons.TILE_SIZE);
						ghost.setVisible(true);
						new AudioPlayer().Music("src/music/吃幽灵.wav");
						break;
					}
					if (System.currentTimeMillis() - whiteTime < 8000) {
						if (System.currentTimeMillis() % 6 < 3) {
							g.drawImage(new ImageIcon("src/images/ghost1.png").getImage(), ghost.getX(), ghost.getY(),
									this);
						} else {
							g.drawImage(new ImageIcon("src/images/ghost2.png").getImage(), ghost.getX(), ghost.getY(),
									this);
						}
					} else {
						if (System.currentTimeMillis() % 500 < 125) {
							g.drawImage(new ImageIcon("src/images/ghost3.png").getImage(), ghost.getX(), ghost.getY(),
									this);
						} else if (System.currentTimeMillis() % 500 < 250) {
							g.drawImage(new ImageIcon("src/images/ghost4.png").getImage(), ghost.getX(), ghost.getY(),
									this);
						} else if (System.currentTimeMillis() % 500 < 375) {
							g.drawImage(new ImageIcon("src/images/ghost1.png").getImage(), ghost.getX(), ghost.getY(),
									this);
						} else {
							g.drawImage(new ImageIcon("src/images/ghost2.png").getImage(), ghost.getX(), ghost.getY(),
									this);
						}
					}

				} else {
					ghost.speed = Commons.SPEED;
					if (System.currentTimeMillis() % 250 < 125) {
						g.drawImage(ghost.getImage(0), ghost.getX(), ghost.getY(), this);
					} else {
						g.drawImage(ghost.getImage(1), ghost.getX(), ghost.getY(), this);
					}
					drawEyes(g, ghost);
				}
				if (ghost.isDying()) {
					ghost.die();
				}
			}
		}
	}

	private void drawPlayer(Graphics g) {
		if (pacman.isVisible()) {
			if (System.currentTimeMillis() % 500 < 125) {
				g.drawImage(pacman.getImage(0), pacman.getX(), pacman.getY(), this);
			} else if (System.currentTimeMillis() % 500 < 250) {
				g.drawImage(pacman.getImage(pacman.getDirection() + 1), pacman.getX(), pacman.getY(), this);
			} else if (System.currentTimeMillis() % 500 < 375) {
				g.drawImage(pacman.getImage(pacman.getDirection() + 5), pacman.getX(), pacman.getY(), this);
			} else {
				g.drawImage(pacman.getImage(pacman.getDirection() + 1), pacman.getX(), pacman.getY(), this);
			}
		}

		if (pacman.isDying()) {
			pacman.die();
			inGame = false;
		}
	}

	private void drawPill(Graphics g) {
		Iterator<Sprite> iterator = pills.iterator();
		while (iterator.hasNext()) {
			Sprite pill = iterator.next();
			if (pill != null && (pill.getX() - 8) / Commons.TILE_SIZE == pacman.getX() / Commons.TILE_SIZE
					&& (pill.getY() - 8) / Commons.TILE_SIZE == pacman.getY() / Commons.TILE_SIZE) {
				iterator.remove();
				score += 10;
				if (System.currentTimeMillis() % 10 < 5)
					new AudioPlayer().Music("src/music/pill.wav");
			}
			var i = new ImageIcon("src/images/pill.png");
			g.drawImage(i.getImage(), pill.getX(), pill.getY(), this);
		}
		iterator = powerpills.iterator();
		while (iterator.hasNext()) {
			Sprite powerpill = iterator.next();
			if (powerpill != null && (powerpill.getX() - 8) / Commons.TILE_SIZE == pacman.getX() / Commons.TILE_SIZE
					&& (powerpill.getY() - 8) / Commons.TILE_SIZE == pacman.getY() / Commons.TILE_SIZE) {
				iterator.remove();
				score += 50;
				new AudioPlayer().Music("src/music/大力丸.wav");
				count = 0;
				scream = true;
				whiteTime = System.currentTimeMillis();
			}
			var i = new ImageIcon("src/images/powerpill.png");
			g.drawImage(i.getImage(), powerpill.getX(), powerpill.getY(), this);
		}
		if (powerpills.isEmpty() && pills.isEmpty()) {
			newGame = true;
			whiteTime = System.currentTimeMillis();
		}
	}

	private void drawLives(Graphics g) {
		var i = new ImageIcon("src/images/pacman1.png");
		for (int j = 1; j <= lives; j++)
			g.drawImage(i.getImage(), 30 * j, Commons.TILE_SIZE * 30 + 20, this);
	}

	private void drawScore(Graphics g) {
		g.setColor(Color.white);
		g.setFont(new Font("Helvetica", Font.BOLD, 20));
		g.drawString("Score：" + score, Commons.BOARD_WIDTH / 2 - 20, Commons.BOARD_HEIGHT - 45);
	}

	private void drawCherry(Graphics g) {
		var i = new ImageIcon("src/images/cherry.png");
		cherry.setX(Commons.TILE_SIZE * 13);
		cherry.setY(300);
		g.drawImage(i.getImage(), Commons.TILE_SIZE * 13, 300, this);
	}

	private void drawBanana(Graphics g) {
		var i = new ImageIcon("src/images/banana.png");
		banana.setX(Commons.TILE_SIZE * 13);
		banana.setY(240);
		g.drawImage(i.getImage(), Commons.TILE_SIZE * 13, 240, this);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	private void doDrawing(Graphics g) {
		BufferedImage bg = null;
		try {
			bg = ImageIO.read(new File("src/images/background.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		g.drawImage(bg, 0, 0, null);
		if (newGame) {
			try {
				bg = ImageIO.read(new File("src/images/background1.png"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			g.drawImage(bg, 0, 0, null);
			gameWin(g);
		} else if (inGame) {
			if ((System.currentTimeMillis() - startTime) > 10000 && cherry != null)
				if (cherry.collision(pacman)) {
					new AudioPlayer().Music("src/music/樱桃.wav");
					score += 100;
					cherry = null;
				} else
					drawCherry(g);
			if ((System.currentTimeMillis() - startTime) > 15000 && banana != null)
				if (banana.collision(pacman)) {
					new AudioPlayer().Music("src/music/樱桃.wav");
					score += 200;
					banana = null;
				} else
					drawBanana(g);
			drawPill(g);
			drawGhosts(g);
			drawPlayer(g);
			drawLives(g);
			drawScore(g);
			if (System.currentTimeMillis() - startTime < 5000) {
				g.setColor(Color.yellow);
				g.setFont(new Font("Britannic Bold", Font.BOLD, 30));
				g.drawString("READY!", Commons.BOARD_WIDTH / 2 - 60, Commons.BOARD_HEIGHT / 2 + 10);
			}
		} else {
			if (timer.isRunning())
				timer.stop();
			gameOver(g);
		}
		Toolkit.getDefaultToolkit().sync();
	}

	private void gameOver(Graphics g) {
		g.setColor(Color.red);
		g.setFont(new Font("Britannic Bold", Font.PLAIN, 30));
		g.drawString("Game Over", Commons.BOARD_WIDTH / 2 - 80, Commons.BOARD_HEIGHT / 2 + 10);
	}

	private void gameWin(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(160, Commons.BOARD_WIDTH / 2 - 50, Commons.BOARD_WIDTH / 2 - 90, 100);
		g.drawImage(pacman.getImage(2), Commons.BOARD_WIDTH / 2 - 95, Commons.BOARD_HEIGHT / 2 - 90, this);
		g.drawImage(blinky.getImage(1), Commons.BOARD_WIDTH / 2 - 57, Commons.BOARD_HEIGHT / 2 - 90, this);
		g.drawImage(blinky.eyeImage(), Commons.BOARD_WIDTH / 2 - 55, Commons.BOARD_HEIGHT / 2 - 78, this);
		g.drawImage(pinky.getImage(1), Commons.BOARD_WIDTH / 2 - 19, Commons.BOARD_HEIGHT / 2 - 90, this);
		g.drawImage(pinky.eyeImage(), Commons.BOARD_WIDTH / 2 - 18, Commons.BOARD_HEIGHT / 2 - 78, this);
		g.drawImage(inky.getImage(1), Commons.BOARD_WIDTH / 2 + 19, Commons.BOARD_HEIGHT / 2 - 90, this);
		g.drawImage(inky.eyeImage(), Commons.BOARD_WIDTH / 2 + 20, Commons.BOARD_HEIGHT / 2 - 78, this);
		g.drawImage(clyde.getImage(1), Commons.BOARD_WIDTH / 2 + 55, Commons.BOARD_HEIGHT / 2 - 90, this);
		g.drawImage(clyde.eyeImage(), Commons.BOARD_WIDTH / 2 + 56, Commons.BOARD_HEIGHT / 2 - 78, this);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		g.drawString("Pacman", Commons.BOARD_WIDTH / 2 - 100, Commons.BOARD_HEIGHT / 2 - 100);
		g.drawString("Blinky", Commons.BOARD_WIDTH / 2 - 58, Commons.BOARD_HEIGHT / 2 - 100);
		g.drawString("Pinky", Commons.BOARD_WIDTH / 2 - 18, Commons.BOARD_HEIGHT / 2 - 100);
		g.drawString("Inky", Commons.BOARD_WIDTH / 2 + 25, Commons.BOARD_HEIGHT / 2 - 100);
		g.drawString("Clyde", Commons.BOARD_WIDTH / 2 + 55, Commons.BOARD_HEIGHT / 2 - 100);
		g.setColor(Color.white);
		g.setFont(new Font("隶书", Font.PLAIN, 20));
		g.drawString("流光容易把人抛", Commons.BOARD_WIDTH / 2 - 75, Commons.BOARD_HEIGHT / 2 - 30);
		g.setFont(new Font("隶书", Font.PLAIN, 20));
		g.drawString("红了樱桃 绿了芭蕉", Commons.BOARD_WIDTH / 2 - 90, Commons.BOARD_HEIGHT / 2 - 5);
	}

	private void update() {
		if (System.currentTimeMillis() - startTime > 10000) {
			Commons.walls[14][12] = true;
			Commons.walls[13][12] = true;
		}
		if (newGame) {
			new AudioPlayer().Music("src/music/转场音乐.wav");
			if (timer.isRunning())
				timer.stop();
		} else if (System.currentTimeMillis() - startTime > 5000) {
			pacman.act();
			if (scream == false) {
				if (!musicPlayed) {
					new AudioPlayer().Music("src/music/游戏音乐.wav");
					musicPlayed = true;
				}
				for (Sprite ghost : ghosts) {
					if (ghost.collision(pacman)) {
						pacman.die();
						lives--;
						if (lives == 0)
							inGame = false;
						gamePaused = true;
						new Timer(2000, _ -> gamePaused = false).start();
						gameInit();
						break;
					}
				}
				if (System.currentTimeMillis() % 10000 < 2000) {
					// 随机游走模式
					for (Sprite ghost : ghosts) {
						if (System.currentTimeMillis() % 100 == 0) {
							ghost.setDirection(random.nextInt(4));
						}
						ghost.randomwalk();
					}
				} else {
					// 追赶模式
					blinky.trace(pacman);
					pinky.trace(pacman);
					inky.trace(pacman, blinky);
					clyde.trace(pacman);
				}
			} else {
				// 设置该模式10秒后进行切换
				screamTimer.start();
				Iterator<Sprite> iterator = ghosts.iterator();
				while (iterator.hasNext()) {
					Sprite ghost = iterator.next();
					if (ghost.getX() == pacman.getX() && ghost.getY() == pacman.getY()) {
						score = score + 200;
					}
					if (ghost.Scared(pacman) == false) {
						// 在幽灵与吃豆人距离在八格之外时，随机游走
						if (System.currentTimeMillis() % 100 == 0) {
							ghost.setDirection(random.nextInt(4));
						}
						ghost.randomwalk();
					} else {
						ghost.Runaway(pacman);
					}
				}
			}
		}
	}

	private void doGameCycle() {
		if (!gamePaused) {
			update();
			repaint();
		}
	}

	private class GameCycle implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			doGameCycle();
		}
	}

	private class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			pacman.keyPressed(e);
			if (e.getKeyCode() == KeyEvent.VK_P) {
				gamePaused = !gamePaused;
			}
		}
	}
}