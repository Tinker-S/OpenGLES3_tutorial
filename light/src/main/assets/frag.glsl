#version 300 es
precision mediump float;
uniform float uR;
in vec3 vPosition;
in vec4 vAmbient;
in vec4 vDiffuse;
in vec4 vSpecular;
out vec4 fragColor;

void main() {
    vec3 color;
    float n = 8.0;
    float span = 2.0 * uR / n;

    // 每一维在立方体内的行列数
    int i = int((vPosition.x + uR) / span);
    int j = int((vPosition.y + uR) / span);
    int k = int((vPosition.z + uR) / span);

    // 计算当点应位于白色块还是黑色块中
    int whichColor = int(mod(float(i + j + k), 2.0));
    if (whichColor == 1) {
        // 奇数时为黑色
        color = vec3(0.0, 0.0, 0.0);
    } else {
        // 偶数时为白色
        color = vec3(1.0, 1.0, 1.0);
    }
    // 最终颜色
    vec4 finalColor = vec4(color, 1.0);
    // 给此片元颜色值
    fragColor = finalColor * vAmbient + finalColor * vDiffuse + finalColor * vSpecular;
}
