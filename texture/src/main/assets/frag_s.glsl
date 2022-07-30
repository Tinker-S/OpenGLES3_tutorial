#version 300 es
precision mediump float;
in vec2 vTextureCoordinate;
uniform sampler2D sTexture;
out vec4 fragColor;

void main() {
    vec4 color = texture(sTexture, vTextureCoordinate);
    fragColor = color;
}
