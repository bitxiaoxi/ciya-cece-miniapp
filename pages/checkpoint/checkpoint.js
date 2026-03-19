const { getStageById } = require('../../mock/stages');

function buildPanel(mode, milestone) {
  if (milestone >= 8) {
    return {
      kicker: '最后一段冲刺',
      title: '你已经完成大半啦',
      description: '后面的题会继续根据你的回答自动调整，不需要每一题都完全确定。',
      emoji: mode === 'child' ? '🚀' : '✨',
      tone: mode === 'child' ? 'warm' : 'deep'
    };
  }

  return {
    kicker: '进度过半',
    title: '你已经完成一半啦',
    description: '后面的题会更贴合刚刚的回答节奏，保持现在的感觉继续就好。',
    emoji: mode === 'child' ? '🎈' : '🌿',
    tone: mode === 'child' ? 'sun' : 'teal'
  };
}

Page({
  data: {
    themeMode: 'student',
    stage: null,
    milestone: 4,
    panel: {}
  },

  onLoad(options) {
    const stage = getStageById(options.stageId || 'stage-1');
    const milestone = Number(options.milestone || 4);

    this.setData({
      themeMode: stage.mode,
      stage,
      milestone,
      panel: buildPanel(stage.mode, milestone)
    });
  },

  handleContinue() {
    wx.redirectTo({
      url: `/pages/quiz/quiz?stageId=${this.data.stage.id}`
    });
  }
});
