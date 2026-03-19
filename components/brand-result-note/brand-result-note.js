Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    variant: {
      type: String,
      value: 'result'
    },
    title: {
      type: String,
      value: '本次结果由青青课堂自适应测评生成'
    },
    description: {
      type: String,
      value: '结合短回合题目表现、能力维度和难度变化，帮助家长更清楚地理解当前词汇面。'
    },
    mode: {
      type: String,
      value: 'student'
    }
  }
});
