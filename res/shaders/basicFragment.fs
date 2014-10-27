#version 120

varying vec2 texCoord0;
uniform vec3 color;
uniform sampler2D sampler;

void main()
{
	vec4 textureColor = texture2D(sampler, texCoord0.xy);

	gl_FragColor = textureColor * vec4(color, 1.0);
}
