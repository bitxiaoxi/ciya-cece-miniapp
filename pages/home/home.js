Page({
  data: {
    highlights: [
      '3-5分钟完成',
      'AI 自适应难度',
      '不会也能跳过'
    ],
    trustPoints: [
      '不是传统考试，更像一段轻松的小挑战。',
      '系统会根据回答自动调整题目，减少挫败感。',
      '结果会给出估算区间、能力拆解和下一步建议。'
    ]
  },

  handleStart() {
    wx.navigateTo({
      url: '/pages/segment/segment'
    });
  },

  handlePreviewResult() {
    wx.navigateTo({
      url: '/pages/result/result?sample=1'
    });
  }
});
