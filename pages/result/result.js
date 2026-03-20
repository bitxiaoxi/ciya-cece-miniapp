const { SAMPLE_RESULT } = require('../../mock/results');
const { getStageById } = require('../../mock/stages');
const { getLatestResult, getSession, clearSession } = require('../../utils/assessment-engine');

Page({
  data: {
    themeMode: 'student',
    stageLabel: '词芽测测 · 结果示例',
    result: SAMPLE_RESULT,
    referenceTags: [],
    suggestionTags: [],
    footerActions: [
      { key: 'retry', label: '再测一次' }
    ]
  },

  onLoad(options) {
    const session = getSession();
    const isSample = options.sample === '1';
    const stage = session ? getStageById(session.stageId) : null;
    const result = isSample ? SAMPLE_RESULT : getLatestResult();

    this.setData({
      themeMode: isSample ? 'student' : stage ? stage.mode : 'student',
      stageLabel: isSample
        ? '词芽测测 · 结果示例'
        : `词芽测测 · ${stage ? stage.shortLabel : '测评结果'}`,
      result,
      referenceTags: (result.strengths || []).concat(result.growthFocus || []).slice(0, 4),
      suggestionTags: result.suggestions || []
    });
  },

  handleSecondaryTap(event) {
    if (event.detail.key !== 'retry') {
      return;
    }

    clearSession();
    wx.reLaunch({
      url: '/pages/home/home'
    });
  }
});
