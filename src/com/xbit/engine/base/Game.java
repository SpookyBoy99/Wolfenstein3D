package com.xbit.engine.base;

import com.xbit.engine.math.Transform;

public class Game
{
	private static Level level;
	private static boolean isRunning;
	private static int levelNum = 0;
	
	public Game()
	{
		loadNextLevel();
	}
	
	public void input()
	{
		level.input();
	}
	
	public void update()
	{
		if (isRunning)
			level.update();
	}
	
	public void render()
	{
		if (isRunning)
			level.render();
	}
	
	public static void loadNextLevel()
	{
		levelNum++;
		level = new Level("level" + levelNum + ".png", "WolfCollectionHD.png");
		
		Transform.setProjection(70.0f, Window.getWidth(), Window.getHeight(), 0.1f, 1000.0f);
		Transform.setCamera(level.getPlayer().getCamera());
		isRunning = true;
	}
	
	public static Level getLevel()
	{
		return level;
	}
	
	public static boolean setIsRunning(boolean value)
	{
		return isRunning = value;
	}
}