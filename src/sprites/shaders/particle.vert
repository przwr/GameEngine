#version 330 core

in int gl_VertexID;
in vec2 position;
in vec2 parameters;

out vec2 pass_textureCoords;
out vec2 pass_textureCoords2;
out float blend;

uniform mat4 transformationMatrix;
uniform mat4 mvpMatrix;
uniform vec4 sizeModifier;

void main(void){
    gl_Position =  mvpMatrix * transformationMatrix * vec4(position.x, position.y, 0.0, 1.0);
    float stage = floor(parameters.x);
    blend = parameters.x - stage;
    float yT = floor(stage / 4);
    float xT = stage - 4 * yT;
    float yMod = floor(parameters.y / 2);
    float xMod = parameters.y - 2 * yMod;
    pass_textureCoords = vec2 ((xT + xMod) / 4, (yT + yMod) / 4);
    yT = floor((stage + 1) / 4);
    xT = stage + 1 - 4 * yT;
    pass_textureCoords2 = vec2 ((xT + xMod) / 4, (yT + yMod) / 4);
}