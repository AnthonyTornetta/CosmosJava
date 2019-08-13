#version 400 core

uniform float u_time;
uniform sampler2D sampler;

in vec3 color;
in vec2 uv;

out vec4 out_color;

void main()
{
	out_color = texture(sampler, uv) * vec4(color, 1.0); 
}