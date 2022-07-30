#version 300 es
precision mediump float;
in vec2 vTextureCoordinate1;
in vec2 vTextureCoordinate2;
uniform sampler2D sTexture1;
uniform sampler2D sTexture2;
out vec4 fragColor;

void main() {
    vec4 color1 = texture(sTexture1, vTextureCoordinate1);
    vec4 color2 = texture(sTexture2, vTextureCoordinate2);

    if (vTextureCoordinate2.x < 0.0 || vTextureCoordinate2.x > 1.0 ||
            vTextureCoordinate2.y < 0.0 || vTextureCoordinate2.y > 1.0) {
        fragColor = color1;
    } else {
        fragColor = color2;
    }
}
