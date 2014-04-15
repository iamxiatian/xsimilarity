错误修订
=====================

1. 第三章概念词语的相似度计算部分的公式：
Sim(C1, C2) = β1 Sim1 (C1, C2) + ∑ β1 βi Sim i (C1, C2)
应为： Sim(C1, C2) = β1 Sim1 (C1, C2) + ∑ Sim1(C1, C2) βi Sim i (C1, C2)
可参考以下代码实现：           i
    @Override
    protected double calculate(double sim_v1, double sim_v2, double sim_v3, double sim_v4) {
        return beta1 * sim_v1 + beta2 * sim_v1 * sim_v2 + beta3 * sim_v1 * sim_v3 + beta4 * sim_v1 * sim_v4;
    }
