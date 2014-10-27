package com.xbit.engine.render;

public class Bitmap
{
	private int width;
	private int height;
	private int[] pixels;
	
	public Bitmap(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.pixels = new int[width * height];
	}

	public Bitmap(int width, int height, int[] pixels)
	{
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}
	
	public Bitmap flipY()
	{
		int[] temp = new int [pixels.length];
		
		for(int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				temp[i + j * width] = pixels[i + (height - j - 1) * width];
			}
		}
		
		pixels = temp;
		
		return this;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int[] getPixels()
	{
		return pixels;
	}
	
	public int getPixel(int x, int y)
	{
		return pixels[x + y * width];
	}
	
	public void setPixel(int x, int y, int value)
	{
		pixels[x + y * width] = value;
	}
}
