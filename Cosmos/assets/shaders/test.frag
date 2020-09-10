#version 330 core

in vec3 frag_pos;
in vec3 frag_color;

out vec4 FragColor;

uniform float time;

float rando(vec2 co)
{
	return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main()
{
	float rng = rando(vec2(frag_pos.x, frag_pos.y));

	FragColor = vec4(
		rng * abs(sin(frag_pos.x + time)), 
		rng * abs(cos(frag_pos.y + time)), 
		rng * abs(tan(frag_pos.z + time)), 
		1.0f);
		
	//FragColor = vec4(frag_color, 1.0);
	
	/*
	float rng2 = rando(vec2(frag_pos.x * time * 2, frag_pos.y * time));
	float rng3 = rando(vec2(frag_pos.x * time, frag_pos.y * time * 2));
	
	FragColor = vec4(rng, rng2, rng3, 1.0f);
	*/
}