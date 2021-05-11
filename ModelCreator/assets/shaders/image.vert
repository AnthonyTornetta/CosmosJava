#version 330 core

layout (location = 0) in vec3 inPos;
layout (location = 2) in vec2 inUv;

uniform mat4 u_transform;
uniform mat4 u_projection;
uniform mat4 u_camera;

out vec2 frag_uv;

void main()
{
	frag_uv = inUv;
	
	gl_Position = u_projection * u_camera * u_transform * vec4(inPos, 1.0);
}