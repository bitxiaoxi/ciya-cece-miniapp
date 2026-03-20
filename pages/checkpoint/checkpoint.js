const { getSession } = require('../../utils/assessment-session');
const { getStagePresentation } = require('../../utils/stage-helper');

function buildPanel(mode, milestone, total) {
  if (milestone >= Math.max(total - 2, 6)) {
    return {
      kicker: '最后一段冲刺',
      title: '你已经完成大半啦',
      description: '后面的题会继续根据刚才的回答微调难度，保持现在的节奏就可以。',
      emoji: mode === 'child' ? '🚀' : '✨',
      tone: mode === 'child' ? 'warm' : 'deep'
    };
  }

  return {
    kicker: '进度过半',
    title: '先歇一口气，再继续',
    description: '后面的题会更贴合前面的作答表现，不需要每一题都完全确定。',
    emoji: mode === 'child' ? '🎈' : '🌿',
    tone: mode === 'child' ? 'sun' : 'teal'
  };
}

Page({
  data: {
    themeMode: 'student',
    stage: null,
    milestone: 4,
    total: 8,
    panel: {}
  },

  onLoad(options) {
    const session = getSession();
    const stage = session && session.selectedStageCode
      ? (session.stage || getStagePresentation(session.selectedStageCode))
      : getStagePresentation(options.stageCode || 'P3_4');
    const milestone = Number(options.milestone || 4);
    const total = Number((session && session.totalQuestionCount) || 8);

    this.sessionNo = options.sessionNo || (session && session.sessionNo) || '';

    this.setData({
      themeMode: stage.mode || 'student',
      stage,
      milestone,
      total,
      panel: buildPanel(stage.mode || 'student', milestone, total)
    });
  },

  handleContinue() {
    if (!this.sessionNo) {
      wx.redirectTo({
        url: '/pages/segment/segment'
      });
      return;
    }

    wx.redirectTo({
      url: `/pages/quiz/quiz?sessionNo=${this.sessionNo}`
    });
  }
});
