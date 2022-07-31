#version 300 es
layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTextureCoordinate;
layout (location = 0) uniform mat4 uTextureTransformMatrix;
out vec2 vTextureCoordinate;

void main() {
    // surfaceTexture.getTransformMatrix目前没去研究
    // gl_Position = uTextureTransformMatrix * vec4(aPosition, 1);
    gl_Position = vec4(aPosition, 1);
    vTextureCoordinate = aTextureCoordinate;
}
