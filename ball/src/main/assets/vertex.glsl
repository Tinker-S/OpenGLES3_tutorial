#version 300 es
layout (location = 0) uniform mat4 uMVPMatrix;
layout (location = 0) in vec3 aPosition;
out vec4 vColor;

void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 1);
    float color;
    if (aPosition.x > 0.0) {
        color = aPosition.x;
    } else {
        color = -aPosition.x;
    }
    vColor = vec4(color, color, color, 1.0);
}
