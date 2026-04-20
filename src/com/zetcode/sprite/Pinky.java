package com.zetcode.sprite;

import java.awt.Point;
import java.util.List;

import javax.swing.ImageIcon;

public class Pinky extends Sprite {
	public Pinky(int x, int y) {
		initPinky(x, y);
	}

	private void initPinky(int x, int y) {
		this.x = x;
		this.y = y;
		this.direction = 3;
		var ghostImg1 = "src/images/pinky1.png";
		var ghostImg2 = "src/images/pinky2.png";
		var i1 = new ImageIcon(ghostImg1);
		var i2 = new ImageIcon(ghostImg2);
		setImage(0, i1.getImage());
		setImage(1, i2.getImage());
	}

	public void trace(Pacman pacman) {
		Point Ghostpos = new Point((this.x + 16) / 18, (this.y + 16) / 18);
		Point Pacman = new Point((pacman.getX() + 16) / 18, (pacman.getY() + 16) / 18);
		switch (pacman.direction) {
		case (0):
			Pacman.x -= 4;
			break;
		case (1):
			Pacman.x += 4;
			break;
		case (2):
			Pacman.y -= 4;
			break;
		case (3):
			Pacman.y += 4;
			break;
		}
		List<Point> path = findPath(Ghostpos, Pacman);
		if (path != null && path.size() > 1) {
			Point nextPos = path.get(1);
			if (nextPos.x - Ghostpos.x < 0) {
				direction = 0;
				this.x -= speed;
			} else if (nextPos.x - Ghostpos.x > 0) {
				direction = 1;
				this.x += speed;
			} else if (nextPos.y - Ghostpos.y < 0) {
				direction = 2;
				this.y -= speed;
			} else if (nextPos.y - Ghostpos.y > 0) {
				direction = 3;
				this.y += speed;
			}
		} else {
			randomwalk();
		}
	}
}