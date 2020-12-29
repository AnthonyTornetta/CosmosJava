#version 330 core

in vec3 frag_pos;
in vec3 frag_color;
in vec2 frag_uv;
in vec3 frag_light;

uniform sampler2D sampler;

out vec4 FragColor;

uniform float time;

float ambient = 0.2;

void main()
{
	vec4 textColor = 
		vec4(max(frag_light.x, ambient), max(frag_light.y, ambient), max(frag_light.z, ambient), 1)
		* texture(sampler, vec2(frag_uv.x, frag_uv.y));
	
	FragColor = textColor;
}