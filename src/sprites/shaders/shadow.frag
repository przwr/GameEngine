#version 330 core

in vec2 pass_textureCoords;
in float o_colour;

out vec4 out_colour;

uniform sampler2D textureSampler;
uniform vec4 colourModifier;
uniform float useTexture;

void main(void){
    if(useTexture == 1.0) {
        out_colour = texture(textureSampler, pass_textureCoords);
        if(out_colour.w > 0){
            out_colour = colourModifier;
        }
    } else {
        out_colour = vec4(o_colour, o_colour, o_colour, 1);
    }
}