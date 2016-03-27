#version 330 compatibility

in vec2 position;
in vec2 textureCoords;
in int gl_VertexID;
in float shade;

out vec2 pass_textureCoords;
out float o_colour;

uniform mat4 transformationMatrix;
uniform vec2 textureShift;
uniform vec4 sizeModifier;
uniform float useTexture;

void main(void){
    if(useTexture == 1.0) {
        switch (gl_VertexID % 4){
            case 0:
            case 1:
                gl_Position =  gl_ModelViewProjectionMatrix * transformationMatrix *  vec4(position.x + sizeModifier.x, position.y, 0.0, 1.0);
                pass_textureCoords = vec2 (textureCoords.x + textureShift.x + sizeModifier.z, textureCoords.y + textureShift.y);
                break;
            case 2:
            case 3:
                gl_Position =  gl_ModelViewProjectionMatrix * transformationMatrix *  vec4(position.x + sizeModifier.y, position.y, 0.0, 1.0);
                pass_textureCoords = vec2 (textureCoords.x + textureShift.x + sizeModifier.w, textureCoords.y + textureShift.y);
                break;
        }
    } else {
        gl_Position =  gl_ModelViewProjectionMatrix * transformationMatrix *  vec4(position, 0.0, 1.0);
        o_colour = shade;
    }
}
