#version 330 core

in vec2 pass_textureCoords;
in float o_color;

out vec4 out_color;

uniform sampler2D textureSampler;
uniform float colorModifier;
uniform float useTexture;

void main(void){
    if(useTexture == 1.0) {
        out_color = texture(textureSampler, pass_textureCoords);
        if(out_color.w > 0){
            out_color = vec4(colorModifier, colorModifier, colorModifier, out_color.w);
        }
    } else {
        out_color = vec4(o_color, o_color, o_color, 1.0);
    }
}