#version 330 core

layout (location = 0) in vec3 inPos;
layout (location = 1) in vec3 inColor;

uniform mat4 u_camera;
uniform mat4 u_proj;

uniform mat4 u_transform;

out vec3 frag_pos;
out vec3 frag_color;

void main()
{
	frag_pos = inPos;
	frag_color = inColor;
	gl_Position = u_proj * u_camera * u_transform * vec4(inPos, 1.0);
}