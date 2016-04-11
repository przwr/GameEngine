#version 330 core

in vec2 pass_textureCoords;

out vec4 out_color;

uniform vec4 color;
uniform sampler2D fontAtlas;

uniform float width;
uniform float edge;

uniform float borderWidth;
uniform float borderEdge;

uniform vec2 offset;
uniform vec4 borderColor;

float smoothStep(float width, float edge, float distance){
    if (distance < width) {
        return 1.0;
    } else if (distance < width + edge) {
        float alpha = (distance - width) / edge;
        return 1.0 - alpha * alpha * (3.0 - 2.0 * alpha);
    } else {
        return 0.0;
    }
}

void main(void){
    float distance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
	float alpha = smoothStep(width, edge, distance);
	float borderAlpha = alpha;
	if (borderWidth > 0.0) {
	    float borderDistance = 1.0 - texture(fontAtlas, pass_textureCoords - offset).a;
	    borderAlpha = smoothStep(borderWidth, borderEdge, borderDistance);
	}
	out_color = vec4(mix(vec3(borderColor.x, borderColor.y, borderColor.z), vec3(color.x, color.y, color.z), alpha/borderAlpha)
	, alpha * color.w + (1.0 - alpha) * borderAlpha * borderColor.w * color.w);
}