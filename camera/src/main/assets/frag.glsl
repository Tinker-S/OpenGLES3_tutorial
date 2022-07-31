#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;
in vec2 vTextureCoordinate;
uniform samplerExternalOES sTexture;
out vec4 fragColor;

void main() {
    vec4 color = texture(sTexture, vTextureCoordinate);
    fragColor = color;

    // 这里稍加变化就可以实现各种滤镜效果，例如实现一个简单的黑白滤镜
    // 滤镜的原理是每个片元的像素值取r,g,b三个分量的平均值
    // float grey = (color.r + color.g + color.b) / 3.0;
    // fragColor = vec4(grey, grey, grey, 1.0);
}