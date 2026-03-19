const { ensureSession, getSession, getCurrentQuestion, submitCurrentAnswer } = require('../../utils/assessment-engine');
const { getStageById } = require('../../mock/stages');

const TYPE_LABEL_MAP = {
  word_to_cn: '英文词义',
  image_to_word: '看图识词',
  audio_choice: '听音辨词'
};

Page({
  data: {
    themeMode: 'student',
    stage: null,
    question: null,
    currentIndex: 1,
    total: 12,
    optionsView: [],
    isPlayingAudio: false,
    selectedOptionId: '',
    revealAnswer: false,
    answered: false,
    isTransitioning: false,
    primaryText: '请选择答案',
    primaryDisabled: true,
    secondaryActions: [
      { key: 'unsure', label: '不确定' },
      { key: 'skip', label: '跳过' }
    ],
    feedbackText: '',
    feedbackTone: 'neutral'
  },

  onLoad(options) {
    this.stageId = options.stageId || '';
    this.bootstrap(this.stageId);
  },

  onUnload() {
    if (this.revealTimer) {
      clearTimeout(this.revealTimer);
    }
    if (this.audioTimer) {
      clearTimeout(this.audioTimer);
    }
  },

  bootstrap(stageId) {
    const session = ensureSession(stageId);
    this.stageId = session.stageId;
    this.syncQuestion();
  },

  syncQuestion() {
    const session = getSession();
    const question = getCurrentQuestion();
    if (!session) {
      wx.redirectTo({
        url: '/pages/home/home'
      });
      return;
    }

    if (!question) {
      if (session.result) {
        wx.redirectTo({
          url: '/pages/result/result'
        });
        return;
      }
      wx.redirectTo({
        url: '/pages/home/home'
      });
      return;
    }

    const stage = getStageById(session.stageId);
    const preparedQuestion = {
      ...question,
      typeLabel: TYPE_LABEL_MAP[question.type] || '词汇挑战'
    };

    this.setData({
      themeMode: stage.mode,
      stage,
      question: preparedQuestion,
      currentIndex: session.answers.length + 1,
      total: session.totalQuestions,
      selectedOptionId: '',
      revealAnswer: false,
      answered: false,
      isTransitioning: false,
      isPlayingAudio: false,
      primaryText: '请选择答案',
      primaryDisabled: true,
      feedbackText: '',
      feedbackTone: 'neutral',
      optionsView: this.buildOptions(preparedQuestion, {
        selectedOptionId: '',
        revealAnswer: false
      })
    });
  },

  buildOptions(question, state) {
    return (question.options || []).map((option) => {
      let visualState = 'default';

      if (state.selectedOptionId && !state.revealAnswer && option.id === state.selectedOptionId) {
        visualState = 'selected';
      }

      if (state.revealAnswer) {
        if (option.id === question.answerId) {
          visualState = 'correct';
        } else if (option.id === state.selectedOptionId) {
          visualState = 'wrong';
        } else {
          visualState = 'disabled-next';
        }
      }

      return {
        ...option,
        visualState
      };
    });
  },

  handleSelectOption(event) {
    if (this.data.revealAnswer || this.data.isTransitioning) {
      return;
    }

    const { optionId } = event.detail;
    const { question } = this.data;

    if (!question) {
      return;
    }

    if (this.revealTimer) {
      clearTimeout(this.revealTimer);
    }

    this.setData({
      selectedOptionId: optionId,
      primaryText: '判定中...',
      primaryDisabled: true,
      optionsView: this.buildOptions(question, {
        selectedOptionId: optionId,
        revealAnswer: false
      })
    });

    this.revealTimer = setTimeout(() => {
      const isCorrect = optionId === question.answerId;
      this.setData({
        revealAnswer: true,
        answered: true,
        primaryText: '下一题',
        primaryDisabled: false,
        feedbackText: isCorrect ? '这题答得很稳，继续保持。' : '这一题先记住也很好，后面会继续调整难度。',
        feedbackTone: isCorrect ? 'success' : 'warning',
        optionsView: this.buildOptions(question, {
          selectedOptionId: optionId,
          revealAnswer: true
        })
      });
    }, 220);
  },

  handlePlayAudio() {
    if (this.data.isPlayingAudio) {
      return;
    }

    this.setData({
      isPlayingAudio: true
    });

    this.audioTimer = setTimeout(() => {
      this.setData({
        isPlayingAudio: false
      });
    }, 980);
  },

  handlePrimaryTap() {
    if (this.data.primaryDisabled || !this.data.revealAnswer || this.data.isTransitioning) {
      return;
    }
    this.advanceFlow({
      action: 'answer',
      optionId: this.data.selectedOptionId
    });
  },

  handleSecondaryTap(event) {
    const { key } = event.detail;
    if (this.data.isTransitioning || this.data.revealAnswer) {
      return;
    }
    this.advanceFlow({
      action: key
    });
  },

  advanceFlow(payload) {
    this.setData({
      isTransitioning: true,
      primaryDisabled: true,
      primaryText: payload.action === 'answer' ? '下一题中...' : '继续中...'
    });

    const route = submitCurrentAnswer(payload);
    setTimeout(() => {
      this.navigateByRoute(route);
    }, 180);
  },

  navigateByRoute(route) {
    if (route.type === 'checkpoint') {
      wx.redirectTo({
        url: `/pages/checkpoint/checkpoint?stageId=${this.stageId}&milestone=${route.milestone}`
      });
      return;
    }

    if (route.type === 'result') {
      wx.redirectTo({
        url: '/pages/result/result'
      });
      return;
    }

    if (route.type === 'question') {
      this.syncQuestion();
      return;
    }

    wx.redirectTo({
      url: '/pages/home/home'
    });
  },

  handleBack() {
    if (this.data.isTransitioning) {
      return;
    }

    wx.redirectTo({
      url: '/pages/segment/segment'
    });
  }
});
