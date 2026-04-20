package com.zetcode.sprite;

import javax.swing.ImageIcon;

public class Blinky extends Sprite {
	public Blinky(int x, int y) {
		initBlinky(x, y);
	}

	private void initBlinky(int x, int y) {
		this.x = x;
		this.y = y;
		this.direction = 2;
		var ghostImg1 = "src/images/blinky1.png";
		var ghostImg2 = "src/images/blinky2.png";
		var i1 = new ImageIcon(ghostImg1);
		var i2 = new ImageIcon(ghostImg2);
		setImage(0, i1.getImage());
		setImage(1, i2.getImage());
	}
}