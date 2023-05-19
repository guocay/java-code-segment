package com.github.guocay.game;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Random;

import static com.github.guocay.game.Util.getFile;

@Data
public class Fish implements Runnable {

	private int x;
	private int y;
	private int width;
	private int height;
	private int index=0;
	private BufferedImage[] images;
	private BufferedImage image;
	private int step;//移动步伐

	public Fish(String prefix) throws Exception {
		// prefix = "fish02"
		// fish01_00.png ~ fish01_09.png -> images
		images = new BufferedImage[10];
		for(int i=0; i<10; i++){
			//i= 0 1 2 ... 9
			String file = prefix + "_0" + i + ".png";
			images[i] = ImageIO.read(getFile(file));
		}
		image = images[0];
		width = image.getWidth();
		height = image.getHeight();
		Random random = new Random();
		x = random.nextInt(801-width);
		y = random.nextInt(481-height);
		step = random.nextInt(4)+2;
	}

	/** 在Runnable 中定义的抽象方法 */
	public void run() {
		while(true){
			move();
			try {
				Thread.sleep(1000/10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void move(){
		x-=step;
		if(x<-width){
			getOut();
		}
		//更换图片
		image = images[index++%images.length];
	}
	/** 滚蛋 */
	public void getOut(){
		Random random = new Random();
		x = 800;
		y = random.nextInt(480-height);
		step = random.nextInt(4)+2;
	}
	/** 检查当前鱼是否被网抓到 */
	public boolean catchBy(Net net){
		int dx = net.getX() - this.x;
		int dy = net.getY() - this.y;
		return dx>=0 && dx <width && dy>=0 && dy<height;
	}

}





