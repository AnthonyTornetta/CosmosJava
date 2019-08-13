#version 400 core

uniform float u_time;

uniform vec3 chunkLocation;
uniform mat4 projection;
uniform mat4 view;

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_color;
layout (location = 2) in vec2 in_uv;
layout (location = 3) in vec3 in_translation;

out vec3 color;
out vec2 uv;

void main()
{
	uv = in_uv;
	
	gl_Position = projection * view * vec4(in_position + in_translation + chunkLocation, 1.0);
	color = in_color;
}