#version 400 core

uniform mat4 u_proj;
uniform mat4 u_camera;

uniform mat4 u_transform;

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_color;

out vec3 color;

void main()
{
	gl_Position = u_proj * u_camera * u_transform * vec4(in_position, 1.0);

	color = in_color;
}
