#version 400 compatibility

in vec2 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform vec2 textureShift;

void main(void){
    gl_Position =  gl_ModelViewProjectionMatrix * transformationMatrix *  vec4(position, 0.0, 1.0);
    pass_textureCoords = vec2 (textureCoords.x + textureShift.x, textureCoords.y + textureShift.y);
}