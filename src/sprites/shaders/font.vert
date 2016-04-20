#version 330 core

in vec2 position;
in vec2 textureCoords;

uniform vec2 translation;
uniform vec2 scale;

out vec2 pass_textureCoords;

void main(void){
    vec2 pos = (position + translation * vec2(2.0, -2.0)) * vec2(1.0 + scale.x, 1.0 + scale.y);
	gl_Position = vec4(pos.x + scale.x, pos.y - scale.y, 0.0, 1.0);
	pass_textureCoords = textureCoords;
}