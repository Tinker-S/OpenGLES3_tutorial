#version 300 es
uniform mat4 uMVPMatrix;
uniform mat4 uModelMatrix;
uniform vec3 uLightLocation;//光源位置
uniform vec3 uCamera;//摄像机位置
in vec3 aPosition;//顶点位置
in vec3 aNormal;//法向量
out vec3 vPosition;
out vec4 vAmbient;//用于传递给片元着色器的环境光最终强度
out vec4 vDiffuse;//用于传递给片元着色器的散射光最终强度
out vec4 vSpecular;//用于传递给片元着色器的镜面光最终强度

// 定位光光照计算的方法
// normal: 法向量
// ambient: 环境光最终强度
// difuse: 散射光最终强度
// specular: 镜面光最终强度
// lightLocation: 光源位置
// lightAmbient: 环境光强度
// lightDiffuse: 散射光强度
// lightSPecular: 镜面光强度
void pointLight(
    in vec3 normal, inout vec4 ambient, inout vec4 diffuse,
    inout vec4 specular, in vec3 lightLocation, in vec4 lightAmbient,
    in vec4 lightDiffuse, in vec4 lightSpecular
) {
    // 直接得出环境光的最终强度
    ambient = lightAmbient;
    // 计算变换后的法向量
    vec3 normalTarget = aPosition + normal;
    vec3 newNormal = (uModelMatrix * vec4(normalTarget, 1)).xyz - (uModelMatrix * vec4(aPosition, 1)).xyz;
    // 对法向量规格化
    newNormal = normalize(newNormal);
    // 计算从表面点到摄像机的向量
    vec3 eye = normalize(uCamera - (uModelMatrix * vec4(aPosition, 1)).xyz);
    // 计算从表面点到光源位置的向量vp
    vec3 vp = normalize(lightLocation - (uModelMatrix * vec4(aPosition, 1)).xyz);
    // 格式化vp
    vp = normalize(vp);
    // 求视线与光线的半向量
    vec3 halfVector = normalize(vp + eye);
    // 粗糙度，越小越光滑
    float shininess = 50.0;
    // 求法向量与vp的点积与0的最大值
    float nDotViewPosition = max(0.0, dot(newNormal, vp));
    // 计算散射光的最终强度
    diffuse = lightDiffuse * nDotViewPosition;
    // 法线与半向量的点积
    float nDotViewHalfVector = dot(newNormal, halfVector);
    // 镜面反射光强度因子
    float powerFactor = max(0.0, pow(nDotViewHalfVector, shininess));
    // 计算镜面光的最终强度
    specular = lightSpecular * powerFactor;
}

void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 1);
    // 用来接收三个通道最终强度的变量
    vec4 ambientTemp, diffuseTemp, specularTemp;
    pointLight(normalize(aNormal), ambientTemp, diffuseTemp, specularTemp, uLightLocation,
        vec4(0.15, 0.15, 0.15, 1.0), vec4(0.8, 0.8, 0.8, 1.0), vec4(0.7, 0.7, 0.7, 1.0));
    vAmbient = ambientTemp;
    vDiffuse = diffuseTemp;
    vSpecular = specularTemp;
    vPosition = aPosition;
}
