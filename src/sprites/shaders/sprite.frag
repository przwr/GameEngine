#version 400 core

in vec2 pass_textureCoords;

out vec4 out_colour;

uniform sampler2D textureSampler;
uniform vec4 colourModifier;

void main(void){
    out_colour = texture(textureSampler, pass_textureCoords) * colourModifier;
}