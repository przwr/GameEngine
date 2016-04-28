#version 330 core

in vec2 pass_textureCoords;
in vec2 pass_textureCoords2;
in float blend;

out vec4 out_color;

uniform sampler2D textureSampler;
uniform vec4 colorModifier;

void main(void){
    vec4 color1 = texture(textureSampler, pass_textureCoords) * colorModifier;
    vec4 color2 = texture(textureSampler, pass_textureCoords2) * colorModifier;
    out_color = mix(color1, color2, blend);
}