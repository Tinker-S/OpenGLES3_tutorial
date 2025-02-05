#version 300 es
precision mediump float;
//接收从顶点着色器过来的参数
in vec4 ambientZM;//接收从顶点着色器过来的正面环境光最终强度
in vec4 diffuseZM;//接收从顶点着色器过来的正面散射光最终强度
in vec4 specularZM;//接收从顶点着色器过来的正面镜面反射光最终强度
in vec4 ambientFM;//接收从顶点着色器过来的反面环境光最终强度
in vec4 diffuseFM;//接收从顶点着色器过来的反面散射光最终强度
in vec4 specularFM;//接收从顶点着色器过来的反面镜面反射光最终强度
out vec4 fragColor;//输出到片元的颜色

void main() {
    vec4 finalColor = vec4(0.9, 0.9, 0.9, 1.0);
    //判断是物体的正面还是反面
    if (gl_FrontFacing) {
        // 正面
        fragColor = finalColor * ambientZM + finalColor * specularZM + finalColor * diffuseZM;
    } else {
        // 反面
        fragColor = finalColor * ambientFM + finalColor * specularFM + finalColor * diffuseFM;
    }
}
