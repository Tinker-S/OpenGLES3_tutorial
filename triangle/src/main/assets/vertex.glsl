#version 300 es
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec4 aColor;
out vec4 vColor;

void main() {
    gl_Position = vec4(aPosition, 1);
    vColor = aColor;
}