# 计算器 GUI

一个简单、可用的桌面计算器，使用 Java Swing 构建。

## 功能
- 四则运算，支持连续运算
- 重复等号行为：等号后再次按 `=` 会用上次的操作数继续计算
- 键盘输入支持：`Enter`、`Escape`、`Backspace`、`+ - * / .`、`0–9`
- 错误处理：除以 `0` 显示 `Error`，输入数字后恢复
- 结果格式化：去除尾随 `.0` 与多余零

## 运行
- 直接运行已打包的 JAR：
  - `java -jar CalculatorGUI.jar`
- 配置最大输入长度（默认 24）：
  - `java -Dcalc.maxlen=32 -jar CalculatorGUI.jar`

## 环境要求
- JDK 8+，Windows/macOS/Linux 任意平台

## 许可
- 见 `LICENSE`
