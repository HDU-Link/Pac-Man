package com.zetcode.sprite;

import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

import com.zetcode.AudioPlayer;
import com.zetcode.Commons;

public class Pacman extends Sprite {
	private double vx = -Commons.SPEED * 1.2, vy = 0;

	public Pacman(int x, int y) {
		this.direction = 0;
		initPlayer(x, y);
	}

	private void initPlayer(int x, int y) {
		this.x = x;
		this.y = y;
		for (int i = 0; i < 9; i++) {
			setImage(i, new ImageIcon("src/images/pacman" + (i == 0 ? "" : i) + ".png").getImage());
		}
	}

	public void act() {
		switch (direction) {
		case (0):
			Point Pos0 = new Point((int) ((this.x + vx + 6) / Commons.TILE_SIZE), (this.y + 16) / Commons.TILE_SIZE);
			if (isValidPosition(Pos0)) {
				this.x += vx;
			} else {
				this.x = (int) (this.x / Commons.TILE_SIZE) * Commons.TILE_SIZE + 14;
			}
			break;
		case (1):
			Point Pos1 = new Point((int) ((this.x + vx + 23) / Commons.TILE_SIZE), (this.y + 16) / Commons.TILE_SIZE);
			if (isValidPosition(Pos1)) {
				this.x += vx;
			} else {
				this.x = (int) (this.x / Commons.TILE_SIZE) * Commons.TILE_SIZE + 10;
			}
			break;
		case (2):
			Point Pos2 = new Point((this.x + 16) / Commons.TILE_SIZE, (int) ((this.y + vy + 10) / Commons.TILE_SIZE));
			if (isValidPosition(Pos2)) {
				this.y += vy;
			} else {
				this.y = (int) (this.y / Commons.TILE_SIZE) * Commons.TILE_SIZE + 8;
			}
			break;
		case (3):
			Point Pos3 = new Point((this.x + 16) / Commons.TILE_SIZE, (int) ((this.y + vy + 23) / Commons.TILE_SIZE));
			if (isValidPosition(Pos3)) {
				this.y += vy;
			} else {
				this.y = (int) (this.y / Commons.TILE_SIZE) * Commons.TILE_SIZE + 13;
			}
			break;
		}
		if (x <= 0)
			if ((this.y + 16) / Commons.TILE_SIZE == 14) {
				x = 468;
			} else {
				x = 0;
			}
		if (y <= 2)
			y = 2;
		if (x >= 470)
			if ((this.y + 16) / Commons.TILE_SIZE == 14) {
				x = 2;
			} else {
				x = 470;
			}
		if (y >= Commons.BOARD_HEIGHT - 16)
			y = Commons.BOARD_HEIGHT - 16;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case (KeyEvent.VK_LEFT):
			this.direction = 0;
			vx = -Commons.SPEED;
			vy = 0;
			break;
		case (KeyEvent.VK_RIGHT):
			this.direction = 1;
			vx = Commons.SPEED;
			vy = 0;
			break;
		case (KeyEvent.VK_UP):
			this.direction = 2;
			vx = 0;
			vy = -Commons.SPEED;
			break;
		case (KeyEvent.VK_DOWN):
			this.direction = 3;
			vx = 0;
			vy = Commons.SPEED;
			break;
		case (KeyEvent.VK_1):
			Point Pos = new Point(this.x / 18, this.y / 18);
			System.out.println(Pos);
			break;
		case (KeyEvent.VK_2):
			Pos = new Point(this.x, this.y);
			System.out.println(Pos);
		}
	}

	public void die() {
		new AudioPlayer().Music("src/music/死亡音效.wav");
	}
}