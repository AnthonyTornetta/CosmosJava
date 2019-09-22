#version 400 core

uniform mat4 projection;
uniform mat4 view;

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_color;
layout (location = 3) in vec3 in_translation;

out vec3 color;

void main()
{
	gl_Position = projection * view * vec4(in_position + in_translation, 1.0);
	color = in_color;
}