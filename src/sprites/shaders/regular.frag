#version 330 core

in vec2 pass_textureCoords;
in vec3 o_color;

out vec4 out_color;

uniform sampler2D textureSampler;
uniform vec4 colorModifier;
uniform float useTexture;
uniform float useColor;

void main(void){
    if (useTexture == 1.0) {
        out_color = texture(textureSampler, pass_textureCoords) * colorModifier;
    } else {
        if (useColor == 1.0){
            out_color = vec4(o_color, 1);
        } else {
            out_color = colorModifier;
        }
    }
}