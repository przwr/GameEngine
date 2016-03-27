#version 330 core

in vec2 pass_textureCoords;
in vec3 o_colour;

out vec4 out_colour;

uniform sampler2D textureSampler;
uniform vec4 colourModifier;
uniform float useTexture;
uniform float useColour;

void main(void){
    if (useTexture == 1.0) {
        out_colour = texture(textureSampler, pass_textureCoords) * colourModifier;
    } else {
        if (useColour == 1.0){
            out_colour = vec4(o_colour, 1);
        } else {
            out_colour = colourModifier;
        }
    }
}