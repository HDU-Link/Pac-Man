package com.zetcode.sprite;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.ImageIcon;

import com.zetcode.Commons;

public class Sprite {
	private boolean visible;
	private List<Image> image = new ArrayList<>();
	private boolean dying;
	int direction;
	int x, y;
	public int speed = Commons.SPEED;

	public Sprite() {
		visible = true;
	}

	public void die() {
		visible = false;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setImage(int i, Image image) {
		this.image.add(i, image);
	}

	public Image getImage(int i) {
		return this.image.get(i);
	}

	public Image eyeImage() {
		var eyeImg = "src/images/eye1.png";
		var ii = new ImageIcon(eyeImg);
		return ii.getImage();
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int d) {
		this.direction = d;
	}

	public void setDying(boolean dying) {
		this.dying = dying;
	}

	public boolean isDying() {
		return this.dying;
	}

	public boolean collision(Pacman pacman) {
		return (int) (this.x / 18) == (int) (pacman.x / 18) && (int) (this.y / 18) == (int) (pacman.y / 18);
	}

	// 随机游走模式
	public void randomwalk() {
		Point Ghostpos0 = new Point((this.x + 18) / 18 - 1, (this.y + 18) / 18);
		Point Ghostpos1 = new Point((this.x + 18) / 18 + 1, (this.y + 18) / 18);
		Point Ghostpos2 = new Point((this.x + 18) / 18, (this.y + 18) / 18 - 1);
		Point Ghostpos3 = new Point((this.x + 18) / 18, (this.y + 18) / 18 + 1);
		switch (direction) {
		case (0):
			if (isValidPosition(Ghostpos0)) {
				this.x -= speed;
				break;
			} else {
				if (isValidPosition(Ghostpos2)) {
					this.direction = 2;
					break;
				} else {
					this.direction = 3;
					break;
				}
			}
		case (1):
			if (isValidPosition(Ghostpos1)) {
				this.x += speed;
				break;
			} else {
				if (isValidPosition(Ghostpos3)) {
					this.direction = 3;
					break;
				} else {
					this.direction = 2;
					break;
				}
			}
		case (2):
			if (isValidPosition(Ghostpos2)) {
				this.y -= speed;
				break;
			} else {
				if (isValidPosition(Ghostpos0)) {
					this.direction = 0;
					break;
				} else {
					this.direction = 1;
					break;
				}
			}
		case (3):
			if (isValidPosition(Ghostpos3)) {
				this.y += speed;
				break;
			} else {
				if (isValidPosition(Ghostpos1)) {
					this.direction = 1;
					break;
				} else {
					this.direction = 0;
					break;
				}
			}
		}
		if (x >= Commons.BOARD_WIDTH - 40)
			this.direction = 0;
		if (x <= 0)
			this.direction = 1;
		if (y >= Commons.BOARD_HEIGHT - 110 && this.direction != 2)
			this.direction = 2;
		if (y <= 0 && this.direction != 3)
			this.direction = 3;
	}

	// 追逐模式
	public void trace(Pacman pacman) {
		Point Ghostpos = new Point((this.x + 16) / 18, (this.y + 16) / 18);
		Point Pacman = new Point((pacman.getX() + 16) / 18, (pacman.getY() + 16) / 18);
		List<Point> path = findPath(Ghostpos, Pacman);
		if (path != null && path.size() > 1) {
			Point nextPos = path.get(1);
			if (isValidPosition(nextPos)) {
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
			}
		}
	}

	// 受惊模式
	public boolean Scared(Pacman pacman) {
		Point Ghostpos = new Point((this.getX() + 16) / 18, (this.getY() + 16) / 18);
		Point Pacman = new Point((pacman.getX() + 16) / 18, (pacman.getY() + 16) / 18);
		if (manhattan(Ghostpos, Pacman) >= 9) {
			return false;
		} else {
			return true;
		}
	}

	public void Runaway(Pacman pacman) {
		Point Ghostpos = new Point((this.getX() + 16) / 18, (this.getY() + 16) / 18);
		Point Pacman = new Point((pacman.getX() + 16) / 18, (pacman.getY() + 16) / 18);
		int manh = 10 - manhattan(Ghostpos, Pacman);
		Point p1 = new Point((this.getX() + 16) / 18 + manh / 2, (this.getY() + 16) / 18 + manh - manh / 2);
		Point p2 = new Point((this.getX() + 16) / 18 - manh / 2, (this.getY() + 16) / 18 - manh + manh / 2);
		Point p3 = new Point((this.getX() + 16) / 18 + manh / 2, (this.getY() + 16) / 18 - manh + manh / 2);
		Point p4 = new Point((this.getX() + 16) / 18 - manh / 2, (this.getY() + 16) / 18 + manh - manh / 2);
		Point p = p1;
		int flag = 1;
		if (Pacman.x > Ghostpos.x && Pacman.y > Ghostpos.y) {
			if (p2.x >= 1 && p2.y >= 1) {
				p = p2;
				flag = 2;
			} else if (p3.x <= 26 && p3.y >= 1) {
				p = p3;
				flag = 3;
			} else if (p4.x >= 1 && p4.y <= 29) {
				p = p4;
				flag = 4;
			}
		} else if (Pacman.x <= Ghostpos.x && Pacman.y > Ghostpos.y) {
			if (p3.x <= 26 && p3.y >= 1) {
				p = p3;
				flag = 3;
			} else if (p2.x >= 1 && p2.y >= 1) {
				p = p2;
				flag = 2;
			} else if (p1.x <= 26 && p1.y <= 29) {
				p = p1;
				flag = 1;
			} else {
				p = p4;
				flag = 4;
			}
		} else if (Pacman.x > Ghostpos.x && Pacman.y <= Ghostpos.y) {
			if (p4.x >= 1 && p4.y <= 29) {
				p = p4;
				flag = 4;
			} else if (p2.x >= 1 && p2.y >= 1) {
				p = p2;
				flag = 2;
			} else if (p1.x <= 26 && p1.y <= 29) {
				p = p1;
				flag = 1;
			} else {
				p = p3;
				flag = 3;
			}
		} else {
			if (p2.x <= 26 && p2.y <= 29) {
				p = p1;
				flag = 1;
			} else if (p3.x <= 26 && p3.y >= 1) {
				p = p3;
				flag = 3;
			} else if (p4.x >= 1 && p4.y <= 29) {
				p = p4;
				flag = 4;
			} else {
				p = p2;
				flag = 2;
			}
		}
		if (!isValidPosition(p)) {
			switch (flag) {
			case 1:
				p.x = 26;
				p.y = 29;
				break;
			case 2:
				p.x = 1;
				p.y = 1;
				break;
			case 3:
				p.x = 26;
				p.y = 1;
				break;
			case 4:
				p.x = 1;
				p.y = 29;
				break;
			}
		}
		List<Point> path = findPath(Ghostpos, p);
		if (path != null && path.size() > 1) {
			Point nextPos = path.get(1);
			if (isValidPosition(nextPos)) {
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
			}
		}
	}

	// A*算法
	public List<Point> findPath(Point start, Point target) {
		PriorityQueue<Node> openSet = new PriorityQueue<>();
		HashMap<Point, Node> allNodes = new HashMap<>();

		Node startNode = new Node(start, null, 0, manhattan(start, target));
		openSet.add(startNode);
		allNodes.put(start, startNode);

		while (!openSet.isEmpty()) {
			Node current = openSet.poll();

			if (current.position.equals(target)) {
				return buildPath(current);
			}

			for (Point neighbor : getNeighbors(current.position)) {
				int tentativeG = current.g + 1;
				Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor));

				if (tentativeG < neighborNode.g) {
					neighborNode.parent = current;
					neighborNode.g = tentativeG;
					neighborNode.f = tentativeG + manhattan(neighbor, target);

					if (!openSet.contains(neighborNode)) {
						openSet.add(neighborNode);
					}
					allNodes.put(neighbor, neighborNode);
				}
			}
		}
		return null;
	}

	private List<Point> getNeighbors(Point pos) {
		List<Point> neighbors = new ArrayList<>();
		int[][] directions = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

		for (int[] dir : directions) {
			Point neighbor = new Point(pos.x + dir[0], pos.y + dir[1]);

			if (isValidPosition(neighbor)) {
				neighbors.add(neighbor);
			}

		}
		return neighbors;
	}

	private List<Point> buildPath(Node endNode) {
		LinkedList<Point> path = new LinkedList<>();
		Node current = endNode;

		while (current != null) {
			path.addFirst(current.position);
			current = current.parent;
		}
		return path;
	}

	public int manhattan(Point a, Point b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}

	protected boolean isValidPosition(Point pos) {
		return pos.x >= 0 && pos.x < Commons.WIDTH && pos.y >= 0 && pos.y < Commons.HEIGHT
				&& Commons.walls[pos.x][pos.y];
	}

	private static class Node implements Comparable<Node> {
		Point position;
		Node parent;
		int f, g;

		public Node(Point position, Node parent, int f, int g) {
			this.position = position;
			this.parent = parent;
			this.f = f;
			this.g = g;
		}

		public Node(Point position) {
			this(position, null, Integer.MAX_VALUE, Integer.MAX_VALUE);
		}

		@Override
		public int compareTo(Node other) {
			return Integer.compare(this.f, other.f);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Node node = (Node) obj;
			return position.equals(node.position);
		}

		@Override
		public int hashCode() {
			return position.hashCode();
		}
	}

}