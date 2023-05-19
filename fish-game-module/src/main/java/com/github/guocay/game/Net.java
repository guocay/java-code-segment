package com.github.guocay.game;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.github.guocay.game.Util.getFile;

@Data
public class Net {
	private int x;
	private int y;
	private int width;
	private int height;
	private BufferedImage image;
	private boolean show;

	public Net(String img) throws Exception {
		//从文件系统加载图片.
		image = ImageIO.read(getFile(img));
		width = image.getWidth();
		height = image.getHeight();
		show = false;
	}

}
