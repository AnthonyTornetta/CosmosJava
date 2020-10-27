#version 330 core

in vec3 frag_pos;
in vec3 frag_color;

out vec4 FragColor;

uniform float time;

void main()
{
	FragColor = vec4(0, 0.4, 0.7, 0);
}