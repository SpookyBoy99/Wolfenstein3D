package com.xbit.engine.base;

import static org.lwjgl.opengl.GL11.GL_NEAREST;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.TextureLoader;

import com.xbit.engine.math.vec3;
import com.xbit.engine.render.Bitmap;
import com.xbit.engine.render.Mesh;
import com.xbit.engine.render.Texture;
import com.xbit.engine.render.Vertex;

public class ResourceLoader
{
	public static Texture loadTexture(String fileName)
	{
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];
		
		try
		{
			int id = TextureLoader.getTexture(ext, new FileInputStream(new File("./res/textures/" + fileName)), GL_NEAREST).getTextureID();
			
			return new Texture(id);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	
	public static Bitmap loadBitmap(String fileName)
	{
		try
		{
			BufferedImage image = ImageIO.read(new File("./res/bitmaps/" + fileName));
			
			int width = image.getWidth();
			int height = image.getHeight();
			int pixels[] = new int[width * height];
			
			image.getRGB(0, 0, width, height, pixels, 0, width);
			
			return new Bitmap(width, height, pixels);
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
		
		return null;
	}
	
	public static String loadShader(String fileName)
	{
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader shaderReader = null;
		
		try
		{
			shaderReader = new BufferedReader(new FileReader("./res/shaders/" + fileName));
			String line;
			
			while ((line = shaderReader.readLine()) != null)
				shaderSource.append(line).append("\n");
			
			shaderReader.close();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		return shaderSource.toString();
	}
	
	public static Mesh loadMesh(String fileName)
	{
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];
		
		if (!ext.equals("obj"))
		{
			System.err.println("Error: File format not supported for mesh data: " + ext);
			System.err.println("(Or you just misspelled the extension)");
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		BufferedReader meshReader = null;
		
		try
		{
			meshReader = new BufferedReader(new FileReader("./res/models/" + fileName));
			String line;
			
			while ((line = meshReader.readLine()) != null)
			{
				String[] tokens = line.split(" ");
				tokens = Util.removeEmptyStrings(tokens);
				
				if (tokens.length == 0 || tokens[0].equals("#"))
				{
					continue;
				}
				else if (tokens[0].equals("v"))
				{
					vertices.add(new Vertex(new vec3(Float.valueOf(tokens[1]), Float.valueOf(tokens[2]), Float.valueOf(tokens[3]))));
				}
				else if (tokens[0].equals("f"))
				{
					indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
					indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
					indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
					
					if (tokens.length > 4)
					{
						indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
						indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
					}
				}
					
			}
			
			meshReader.close();
			
			Mesh res = new Mesh();
			Vertex[] vertexData = new Vertex[vertices.size()];
			vertices.toArray(vertexData);
			
			Integer[] indexData = new Integer[indices.size()];
			indices.toArray(indexData);
			
			res.addVertices(vertexData, Util.toIntArray(indexData), true);
			
			return res;
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
}
