package com.github.guocay.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static com.github.guocay.game.Util.getFile;

public class Pool extends JPanel{

	private final BufferedImage background;

	private final Net net;

	private final Fish[] all;

	public Pool() throws Exception {
		background = ImageIO.read(getFile("background-image.jpg"));
		net = new Net("fishing-net.png");
		all = new Fish[]{
				new Fish("fish01"),
				new Fish("fish02"),
				new Fish("fish03"),
				new Fish("fish04"),
				new Fish("fish05"),
				new Fish("fish06"),
				new Fish("fish07"),
				new Fish("fish08"),
				new Fish("fish09"),
				new Fish("fish10"),
				new Fish("fish11")
		};
	}

	public void paint(Graphics g) {
		g.drawImage(background, 0,0,null);
		for (Fish	fish: all) {
			int x = fish.getX();
			int y = fish.getY();
			g.drawImage(fish.getImage(), x, y, null);
		}
		if(net.isShow()){
			Image img = net.getImage();
			int x = net.getX() - net.getWidth()/2;
			int y = net.getY() - net.getHeight()/2;
			g.drawImage(img, x, y, null);
		}
	}

	public void action() throws Exception {
		//启动每条鱼,让鱼自己去游动(run)
		for (Fish fish : all) {
			//Thread 线程API, 可以启动鱼自己去游动
			Thread t = new Thread(fish);
			t.start();//启动(start)鱼自己去游动(run())
		}

		MouseAdapter l = new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				catchFish();//抓鱼
			}
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				net.setX(x);
				net.setY(y);
			}
			public void mouseEntered(MouseEvent e) {
				net.setShow(true);//Show 显示
			}
			public void mouseExited(MouseEvent e) {
				net.setShow(false);
			}
		};
		this.addMouseListener(l);
		this.addMouseMotionListener(l);

		while(true){
			repaint();//观察鱼去哪里了!
			Thread.sleep(1000/24);
		}
	}

	protected void catchFish() {
		for(int i=all.length-1; i>=0; i--){
			Fish fish = all[i];
			if(fish.catchBy(net)){
				fish.getOut();
				break;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("捕鱼达人");
		frame.setSize(800, 520);
		frame.setLocationRelativeTo(null);
		Pool pool = new Pool();
		frame.add(pool);
		frame.setVisible(true);
		pool.action();
	}
}







