#version 330 compatibility

in vec2 position;

out vec3 colour;

void main(void){
    gl_Position = gl_ModelViewProjectionMatrix * vec4(position, 0.0, 1.0);
    colour = vec3(0.1, 0.1, 1.0);
}
