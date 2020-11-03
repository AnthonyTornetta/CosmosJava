#version 330 core

in vec3 frag_pos;
in vec3 frag_color;
in vec4 frag_uv;

uniform sampler2D sampler;

out vec4 FragColor;

uniform float time;

float mod(float a, float b)
{
	return a - b * int(a / b);
}

void main()
{
	float xd = 16.0f / 256.0f; // the width of each texture in the atlas - make me a uniform later
	
	vec4 textColor = texture(sampler, vec2(frag_uv.z + mod(frag_uv.x, xd), frag_uv.w + mod(frag_uv.y, xd)));

	FragColor = textColor;
}