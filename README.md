# OpenGLES3_tutorial

Samples for OpenGLES 3 by kotlin.

## 模块列表

- triangle 绘制简单三角形

- rectangle 绘制矩行（分别使用glDrawArrays和glDrawElements绘制矩形）

- star 绘制五角星

- matrix 矩阵变换

最终矩阵 = projMatrix * viewMatrix * modelMatrix

    - projMatrix是投影矩阵，由Matrix.frustumM（透视投影）或者Matrix.orthoM（正交投影）设置
    - viewMatrix是摄像机位置，由Matrix.setLookAtM设置
    - modelMatrix是模型矩阵，默认设置成单位矩阵，可以对其做平移，旋转，缩放等变换
