#version 400 core

uniform float u_time;

uniform vec3 chunkLocation;
uniform mat4 projection;
uniform mat4 view;

uniform mat4 u_rotation_x;
uniform mat4 u_rotation_y;
uniform mat4 u_rotation_z;

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_color;
layout (location = 2) in vec2 in_uv;
layout (location = 3) in vec3 in_translation;

out vec3 color;
out vec2 uv;

void main()
{
	uv = in_uv;
	
	gl_Position = projection * view * u_rotation_x * u_rotation_y * u_rotation_z * vec4(in_translation + in_position + chunkLocation, 1.0);
	color = in_color;
}

