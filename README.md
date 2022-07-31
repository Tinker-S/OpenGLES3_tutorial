# OpenGLES3_tutorial

Samples for OpenGLES 3 by kotlin.

## 模块列表

- triangle 绘制简单三角形

- rectangle 绘制矩行（分别使用glDrawArrays和glDrawElements绘制矩形）

- star 绘制五角星

- matrix 矩阵变换
    - projMatrix：投影矩阵，由Matrix.frustumM（透视投影）或者Matrix.orthoM（正交投影）设置
    - viewMatrix：摄像机位置，由Matrix.setLookAtM设置
    - modelMatrix：模型矩阵，默认设置成单位矩阵，可以对其做平移，旋转，缩放等变换

    最终矩阵 = projMatrix * viewMatrix * modelMatrix
    
- circle 绘制圆（多边形）

- cube 绘制立方体

- ball 绘制球

- cone 绘制圆锥体

- cylinder 绘制圆柱体

- light 光照模型（OpenGL采用冯氏光照模型）

    - 环境光照射结果 = 材质的反射系数 * 环境光强度
    - 散射光照射结果 = 材质的反射系数 * 散射光强度 * max(cos(入射角), 0)
    - 镜面光照射结果 = 材质的反射系数 * 镜面光强度 * max(0, (cos(半向量与法向量的夹角))粗糙度)
    
- texture 绘制纹理

- teapot 从模型文件加载一个茶壶

- camera 实现相机预览效果（实时滤镜）
