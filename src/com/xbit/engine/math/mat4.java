package com.xbit.engine.math;

public class mat4
{
	private float [][] matrix;
	
	public mat4()
	{
		matrix = new float[4][4];
	}

	public mat4 initIdentity()
	{
		matrix[0][0] = 1;	matrix[0][1] = 0;	matrix[0][2] = 0; matrix[0][3] = 0;
		matrix[1][0] = 0;	matrix[1][1] = 1;	matrix[1][2] = 0; matrix[1][3] = 0;
		matrix[2][0] = 0;	matrix[2][1] = 0;	matrix[2][2] = 1; matrix[2][3] = 0;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0; matrix[3][3] = 1;
		
		return this;
	}
	
	public mat4 initTranslation(float x, float y, float z)
	{
		matrix[0][0] = 1;	matrix[0][1] = 0;	matrix[0][2] = 0; matrix[0][3] = x;
		matrix[1][0] = 0;	matrix[1][1] = 1;	matrix[1][2] = 0; matrix[1][3] = y;
		matrix[2][0] = 0;	matrix[2][1] = 0;	matrix[2][2] = 1; matrix[2][3] = z;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0; matrix[3][3] = 1;
		
		return this;
	}
	
	public mat4 initRotation(float x, float y, float z)
	{
		mat4 rotationX = new mat4();
		mat4 rotationY = new mat4();
		mat4 rotationZ = new mat4();
		
		x = (float)Math.toRadians(x);
		y = (float)Math.toRadians(y);
		z = (float)Math.toRadians(z);
		
		float sinX = (float)Math.sin(x);
		float sinY = (float)Math.sin(y);
		float sinZ = (float)Math.sin(z);
		
		float cosX = (float)Math.cos(x);
		float cosY = (float)Math.cos(y);
		float cosZ = (float)Math.cos(z);
		
		rotationZ.matrix[0][0] = cosZ;	rotationZ.matrix[0][1] = -sinZ;	rotationZ.matrix[0][2] = 0; 	rotationZ.matrix[0][3] = 0;
		rotationZ.matrix[1][0] = sinZ;	rotationZ.matrix[1][1] =  cosZ;	rotationZ.matrix[1][2] = 0; 	rotationZ.matrix[1][3] = 0;
		rotationZ.matrix[2][0] = 0;		rotationZ.matrix[2][1] = 0;		rotationZ.matrix[2][2] = 1; 	rotationZ.matrix[2][3] = 0;
		rotationZ.matrix[3][0] = 0;		rotationZ.matrix[3][1] = 0;		rotationZ.matrix[3][2] = 0; 	rotationZ.matrix[3][3] = 1;
		
		rotationX.matrix[0][0] = 1;		rotationX.matrix[0][1] = 0;		rotationX.matrix[0][2] = 0; 	rotationX.matrix[0][3] = 0;
		rotationX.matrix[1][0] = 0;		rotationX.matrix[1][1] = cosX;	rotationX.matrix[1][2] = -sinX; rotationX.matrix[1][3] = 0;
		rotationX.matrix[2][0] = 0;		rotationX.matrix[2][1] = sinX;	rotationX.matrix[2][2] =  cosX; rotationX.matrix[2][3] = 0;
		rotationX.matrix[3][0] = 0;		rotationX.matrix[3][1] = 0;		rotationX.matrix[3][2] = 0; 	rotationX.matrix[3][3] = 1;
		
		rotationY.matrix[0][0] = cosY;	rotationY.matrix[0][1] = 0;		rotationY.matrix[0][2] = -sinY; rotationY.matrix[0][3] = 0;
		rotationY.matrix[1][0] = 0;		rotationY.matrix[1][1] = 1;		rotationY.matrix[1][2] = 0; 	rotationY.matrix[1][3] = 0;
		rotationY.matrix[2][0] = sinY;	rotationY.matrix[2][1] = 0;		rotationY.matrix[2][2] =  cosY; rotationY.matrix[2][3] = 0;
		rotationY.matrix[3][0] = 0;		rotationY.matrix[3][1] = 0;		rotationY.matrix[3][2] = 0; 	rotationY.matrix[3][3] = 1;
		
		matrix = rotationZ.mul(rotationY.mul(rotationX)).getMatrix();
		
		return this;
	}
	
	public mat4 initScale(float x, float y, float z)
	{
		matrix[0][0] = x;	matrix[0][1] = 0;	matrix[0][2] = 0; matrix[0][3] = 0;
		matrix[1][0] = 0;	matrix[1][1] = y;	matrix[1][2] = 0; matrix[1][3] = 0;
		matrix[2][0] = 0;	matrix[2][1] = 0;	matrix[2][2] = z; matrix[2][3] = 0;
		matrix[3][0] = 0;	matrix[3][1] = 0;	matrix[3][2] = 0; matrix[3][3] = 1;
		
		return this;
	}

	public mat4 initProjection(float fov, float width, float height, float zNear, float zFar)
	{
		float aspectRatio = width / height;
		float tanHalfFOV = (float)Math.tan(Math.toRadians(fov / 2));
		float zRange = zNear - zFar;
		
		matrix[0][0] = 1.0f / (tanHalfFOV * aspectRatio);	matrix[0][1] = 0;					matrix[0][2] = 0; 							matrix[0][3] = 0;
		matrix[1][0] = 0;									matrix[1][1] = 1.0f / tanHalfFOV;	matrix[1][2] = 0; 							matrix[1][3] = 0;
		matrix[2][0] = 0;									matrix[2][1] = 0;					matrix[2][2] = (-zNear - zFar) / zRange; 	matrix[2][3] = 2 * zFar * zNear / zRange;
		matrix[3][0] = 0;									matrix[3][1] = 0;					matrix[3][2] = 1; 							matrix[3][3] = 0;
		
		return this;
	}
	
	public mat4 initCamera(vec3 forward, vec3 up)
	{
		vec3 f = forward;
		f.normalize();
		
		vec3 r = up;
		r.normalize();
		r = r.cross(f);
		
		vec3 u = f.cross(r);
		
		matrix[0][0] = r.getX();	matrix[0][1] = r.getY();	matrix[0][2] = r.getZ(); 	matrix[0][3] = 0;
		matrix[1][0] = u.getX();	matrix[1][1] = u.getY();	matrix[1][2] = u.getZ();	matrix[1][3] = 0;
		matrix[2][0] = f.getX();	matrix[2][1] = f.getY();	matrix[2][2] = f.getZ();	matrix[2][3] = 0;
		matrix[3][0] = 0;			matrix[3][1] = 0;			matrix[3][2] = 0; 			matrix[3][3] = 1;
		
		return this;
	}
	
	public mat4 mul(mat4 matrix)
	{
		mat4 res = new mat4();
		
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				res.set(i, j,	this.matrix[i][0] * matrix.get(0, j) +
								this.matrix[i][1] * matrix.get(1, j) +
								this.matrix[i][2] * matrix.get(2, j) +
								this.matrix[i][3] * matrix.get(3, j));
			}
		}
		
		return res;
	}
	
	public float[][] getMatrix()
	{
		return matrix;
	}

	public float get(int x, int y)
	{
		return matrix[x][y];
	}
	
	public void setMatrix(float[][] matrix)
	{
		this.matrix = matrix;
	}
	
	public void set(int x, int y, float value)
	{
		this.matrix[x][y] = value; 
	}
}
