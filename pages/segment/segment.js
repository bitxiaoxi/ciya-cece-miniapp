const { STAGE_CONFIGS, getStageById } = require('../../mock/stages');
const { clearSession } = require('../../utils/assessment-engine');

Page({
  data: {
    stages: STAGE_CONFIGS,
    selectedId: '',
    selectedStage: null
  },

  handleSelectStage(event) {
    const { stageId } = event.detail;
    this.setData({
      selectedId: stageId,
      selectedStage: getStageById(stageId)
    });
  },

  handleContinue() {
    if (!this.data.selectedId) {
      return;
    }

    clearSession();

    wx.navigateTo({
      url: `/pages/quiz/quiz?stageId=${this.data.selectedId}`
    });
  },

  handleBack() {
    wx.navigateBack({
      delta: 1,
      fail() {
        wx.redirectTo({
          url: '/pages/home/home'
        });
      }
    });
  }
});
