#version 300 es
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTextureCoordinate1;
layout (location = 2) in vec2 aTextureCoordinate2;
out vec2 vTextureCoordinate1;
out vec2 vTextureCoordinate2;

void main() {
    gl_Position = vec4(aPosition, 1);
    vTextureCoordinate1 = aTextureCoordinate1;
    vTextureCoordinate2 = aTextureCoordinate2;
}