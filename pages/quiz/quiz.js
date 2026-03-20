const { finishAssessment, getNextQuestion, submitAnswer } = require('../../utils/api');
const { getSession, patchSession } = require('../../utils/assessment-session');
const { getStagePresentation } = require('../../utils/stage-helper');

const QUESTION_TYPE_LABELS = {
  WORD_TO_CN: '英文词义',
  IMAGE_TO_WORD: '看图识词',
  AUDIO_CHOICE: '听音辨词',
  CONTEXT_CHOICE: '语境判断'
};

const QUESTION_PROMPTS = {
  WORD_TO_CN: '看到英文单词后，选出最合适的中文意思。',
  IMAGE_TO_WORD: '结合图片线索，选出最合适的英文单词。',
  AUDIO_CHOICE: '先听音频，再选出最合适的答案。',
  CONTEXT_CHOICE: '结合句子语境，选出最合适的词。'
};

const PHASE_LABELS = {
  ROUTE: '起始路由题',
  CORE: '核心测评题',
  ANCHOR: '锚点校准题'
};

function showError(message) {
  wx.showToast({
    title: message,
    icon: 'none'
  });
}

function formatDifficulty(value) {
  if (value === null || value === undefined || value === '') {
    return '--';
  }
  const numeric = Number(value);
  if (Number.isNaN(numeric)) {
    return String(value);
  }
  return numeric.toFixed(2);
}

function isExampleAsset(url) {
  return typeof url === 'string' && url.indexOf('example.com/') > -1;
}

function buildOptionView(option) {
  const helperParts = [];
  if (option.optionImageUrl && !isExampleAsset(option.optionImageUrl)) {
    helperParts.push('图片选项');
  }
  if (option.optionAudioUrl) {
    helperParts.push(isExampleAsset(option.optionAudioUrl) ? '示例音频' : '含音频');
  }

  return {
    id: option.id,
    letter: option.optionKey || String(option.sortNo || ''),
    label: option.optionText || '查看选项',
    helper: helperParts.join(' · '),
    imageUrl: option.optionImageUrl && !isExampleAsset(option.optionImageUrl) ? option.optionImageUrl : '',
    audioUrl: option.optionAudioUrl || '',
    showMeta: helperParts.length > 0 || !!option.optionAudioUrl
  };
}

function buildQuestionView(rawQuestion, stage) {
  const questionType = rawQuestion.questionType || '';
  const mediaTone = stage && stage.mode === 'child' ? 'sun' : 'teal';
  const question = {
    id: rawQuestion.itemId,
    itemId: rawQuestion.itemId,
    questionNo: rawQuestion.questionNo,
    questionType,
    phaseType: rawQuestion.phaseType || '',
    typeLabel: QUESTION_TYPE_LABELS[questionType] || '词汇挑战',
    phaseLabel: PHASE_LABELS[rawQuestion.phaseType] || '',
    prompt: QUESTION_PROMPTS[questionType] || '请选择最合适的答案。',
    stem: rawQuestion.stemText || '请选择最合适的答案。',
    stemNote: '',
    imageUrl: '',
    imageEmoji: '',
    imageHint: '',
    showIllustration: false,
    mediaTone,
    audioUrl: rawQuestion.stemAudioUrl || '',
    audioHint: rawQuestion.stemAudioUrl
      ? (isExampleAsset(rawQuestion.stemAudioUrl) ? '示例音频资源已接入，当前使用占位播放。' : '点击播放题干音频。')
      : '',
    options: (rawQuestion.options || []).map(buildOptionView)
  };

  if (questionType === 'WORD_TO_CN') {
    question.stem = rawQuestion.wordText || rawQuestion.stemText || '请选择词义';
    question.stemNote = rawQuestion.stemText || '请选择最合适的中文意思。';
  } else if (questionType === 'IMAGE_TO_WORD') {
    question.stem = rawQuestion.stemText || '请根据图片选择正确单词。';
    question.imageEmoji = '🖼';
    question.imageHint = isExampleAsset(rawQuestion.stemImageUrl)
      ? '图片字段已接入，当前题库使用示例图片地址。'
      : '请观察图片后作答。';
    question.imageUrl = rawQuestion.stemImageUrl && !isExampleAsset(rawQuestion.stemImageUrl) ? rawQuestion.stemImageUrl : '';
    question.showIllustration = !!(question.imageUrl || question.imageHint);
  } else if (questionType === 'AUDIO_CHOICE') {
    question.stem = rawQuestion.stemText || '请先播放音频，再选择答案。';
    question.stemNote = '如果不确定，可以选择“不确定”或“跳过”。';
  } else if (questionType === 'CONTEXT_CHOICE') {
    question.stem = rawQuestion.stemText || '请结合语境判断最合适的词。';
    question.stemNote = '重点看句子线索，不需要逐词翻译。';
  }

  question.showIllustration = !!(question.imageUrl || question.imageHint);

  return question;
}

function buildOptions(question, state) {
  return (question.options || []).map((option) => {
    let visualState = 'default';

    if (!state.revealAnswer && state.selectedOptionId && option.id === state.selectedOptionId) {
      visualState = 'selected';
    }

    if (state.revealAnswer) {
      if (state.submittedStatus === 'CORRECT' && option.id === state.selectedOptionId) {
        visualState = 'correct';
      } else if (state.submittedStatus === 'WRONG' && option.id === state.selectedOptionId) {
        visualState = 'wrong';
      } else {
        visualState = 'disabled-next';
      }
    }

    return Object.assign({}, option, {
      visualState
    });
  });
}

function buildFeedback(status, nextDifficulty) {
  const suffix = nextDifficulty ? ` 当前难度调整到 ${formatDifficulty(nextDifficulty)}。` : '';

  if (status === 'CORRECT') {
    return {
      tone: 'success',
      text: `这题答得比较稳，系统会继续往上探一点。${suffix}`
    };
  }
  if (status === 'WRONG') {
    return {
      tone: 'warning',
      text: `这题先记住也可以，系统会适当回调难度。${suffix}`
    };
  }
  if (status === 'UNCERTAIN') {
    return {
      tone: 'neutral',
      text: `已记录为不确定，后续题目会略微放缓。${suffix}`
    };
  }
  return {
    tone: 'neutral',
    text: `这一题已跳过，系统会继续给出合适题目。${suffix}`
  };
}

Page({
  data: {
    themeMode: 'student',
    stage: null,
    question: null,
    currentIndex: 1,
    total: 8,
    optionsView: [],
    isPlayingAudio: false,
    selectedOptionId: '',
    revealAnswer: false,
    answered: false,
    isTransitioning: false,
    questionLoading: true,
    primaryText: '请选择答案',
    primaryDisabled: true,
    secondaryActions: [
      { key: 'unsure', label: '不确定' },
      { key: 'skip', label: '跳过' }
    ],
    feedbackText: '',
    feedbackTone: 'neutral',
    pendingShouldFinish: false,
    submittedStatus: '',
    progress: null,
    midCheckpointShown: false
  },

  onLoad(options) {
    this.sessionNo = options.sessionNo || '';
    this.questionStartedAt = 0;
    this.bootstrap();
  },

  onUnload() {
    if (this.audioTimer) {
      clearTimeout(this.audioTimer);
    }
    if (this.audioContext) {
      this.audioContext.destroy();
      this.audioContext = null;
    }
  },

  async bootstrap() {
    const session = getSession();
    const sessionNo = this.sessionNo || (session && session.sessionNo) || '';
    if (!session || !sessionNo) {
      wx.redirectTo({
        url: '/pages/home/home'
      });
      return;
    }

    const stage = session.stage || getStagePresentation(session.selectedStageCode);
    this.sessionNo = sessionNo;
    this.setData({
      themeMode: stage.mode || 'student',
      stage,
      total: session.totalQuestionCount || this.data.total,
      midCheckpointShown: !!session.midCheckpointShown
    });

    if (session.pendingQuestion) {
      const progress = {
        answeredCount: 0,
        totalCount: session.totalQuestionCount || this.data.total,
        currentPhase: session.pendingQuestion.phaseType || 'ROUTE',
        correctCount: 0,
        uncertainCount: 0
      };
      patchSession({
        pendingQuestion: null
      });
      this.applyQuestion(session.pendingQuestion, progress);
      return;
    }

    await this.fetchNextQuestion();
  },

  applyQuestion(rawQuestion, progress) {
    const question = buildQuestionView(rawQuestion, this.data.stage);
    const total = (progress && progress.totalCount) || this.data.total;
    const currentIndex = question.questionNo || ((progress && progress.answeredCount) || 0) + 1;
    this.questionStartedAt = Date.now();

    patchSession({
      currentQuestion: rawQuestion,
      currentProgress: progress || null,
      totalQuestionCount: total
    });

    this.setData({
      questionLoading: false,
      question,
      progress: progress || null,
      currentIndex,
      total,
      selectedOptionId: '',
      revealAnswer: false,
      answered: false,
      isTransitioning: false,
      primaryText: '请选择答案',
      primaryDisabled: true,
      feedbackText: '',
      feedbackTone: 'neutral',
      pendingShouldFinish: false,
      submittedStatus: '',
      optionsView: buildOptions(question, {
        selectedOptionId: '',
        revealAnswer: false,
        submittedStatus: ''
      })
    });
  },

  async fetchNextQuestion() {
    this.setData({
      questionLoading: true,
      isTransitioning: true,
      primaryDisabled: true
    });

    try {
      const response = await getNextQuestion(this.sessionNo);
      if (response.shouldFinish || !response.question) {
        await this.finishAssessmentFlow();
        return;
      }

      patchSession({
        currentProgress: response.progress || null,
        totalQuestionCount: response.progress ? response.progress.totalCount : this.data.total
      });

      this.applyQuestion(response.question, response.progress);
    } catch (error) {
      showError(error.message || '获取下一题失败');
      this.setData({
        questionLoading: false,
        isTransitioning: false,
        primaryText: this.data.pendingShouldFinish ? '查看结果' : '下一题',
        primaryDisabled: !this.data.revealAnswer
      });
    }
  },

  async submitCurrentAnswer(payload) {
    if (!this.data.question || this.data.isTransitioning || this.data.revealAnswer) {
      return;
    }

    const responseTimeMs = Math.max(Date.now() - this.questionStartedAt, 0);
    const requestData = {
      itemId: this.data.question.itemId,
      responseTimeMs
    };

    if (payload.selectedOptionId) {
      requestData.selectedOptionId = payload.selectedOptionId;
    }

    if (payload.answerStatus) {
      requestData.answerStatus = payload.answerStatus;
    }

    this.setData({
      isTransitioning: true,
      primaryText: '判定中...',
      primaryDisabled: true
    });

    try {
      const response = await submitAnswer(this.sessionNo, requestData);
      const submittedStatus = payload.answerStatus || (response.isCorrect ? 'CORRECT' : 'WRONG');
      const feedback = buildFeedback(submittedStatus, response.nextDifficulty);
      const selectedOptionId = payload.selectedOptionId || '';

      patchSession({
        currentQuestion: null,
        currentProgress: response.progress || null
      });

      this.setData({
        isTransitioning: false,
        revealAnswer: true,
        answered: true,
        selectedOptionId,
        pendingShouldFinish: !!response.shouldFinish,
        submittedStatus,
        progress: response.progress || null,
        primaryText: response.shouldFinish ? '查看结果' : '下一题',
        primaryDisabled: false,
        feedbackText: feedback.text,
        feedbackTone: feedback.tone,
        optionsView: buildOptions(this.data.question, {
          selectedOptionId,
          revealAnswer: true,
          submittedStatus
        })
      });
    } catch (error) {
      showError(error.message || '提交答案失败');
      this.setData({
        isTransitioning: false,
        primaryText: '请选择答案',
        primaryDisabled: true,
        selectedOptionId: '',
        optionsView: buildOptions(this.data.question, {
          selectedOptionId: '',
          revealAnswer: false,
          submittedStatus: ''
        })
      });
    }
  },

  handleSelectOption(event) {
    if (this.data.revealAnswer || this.data.isTransitioning || !this.data.question) {
      return;
    }

    const optionId = Number(event.detail.optionId || 0);
    this.setData({
      selectedOptionId: optionId,
      optionsView: buildOptions(this.data.question, {
        selectedOptionId: optionId,
        revealAnswer: false,
        submittedStatus: ''
      })
    });

    this.submitCurrentAnswer({
      selectedOptionId: optionId
    });
  },

  handlePlayAudio() {
    const question = this.data.question;
    if (!question || !question.audioUrl || this.data.isPlayingAudio) {
      return;
    }

    if (isExampleAsset(question.audioUrl) || !wx.createInnerAudioContext) {
      this.simulateAudio();
      return;
    }

    if (!this.audioContext) {
      this.audioContext = wx.createInnerAudioContext();
      this.audioContext.onPlay(() => {
        this.setData({
          isPlayingAudio: true
        });
      });
      this.audioContext.onStop(() => {
        this.setData({
          isPlayingAudio: false
        });
      });
      this.audioContext.onEnded(() => {
        this.setData({
          isPlayingAudio: false
        });
      });
      this.audioContext.onError(() => {
        this.setData({
          isPlayingAudio: false
        });
        showError('当前音频资源暂不可播放');
      });
    }

    this.audioContext.stop();
    this.audioContext.src = question.audioUrl;
    this.audioContext.play();
  },

  simulateAudio() {
    this.setData({
      isPlayingAudio: true
    });

    if (this.audioTimer) {
      clearTimeout(this.audioTimer);
    }

    this.audioTimer = setTimeout(() => {
      this.setData({
        isPlayingAudio: false
      });
    }, 1100);
  },

  async handlePrimaryTap() {
    if (this.data.primaryDisabled || !this.data.revealAnswer || this.data.isTransitioning) {
      return;
    }

    if (this.data.pendingShouldFinish) {
      await this.finishAssessmentFlow();
      return;
    }

    const progress = this.data.progress || {};
    const total = progress.totalCount || this.data.total || 0;
    const halfCount = Math.ceil(total / 2);

    if (!this.data.midCheckpointShown && progress.answeredCount >= halfCount && progress.answeredCount < total) {
      patchSession({
        midCheckpointShown: true
      });
      this.setData({
        midCheckpointShown: true
      });
      wx.redirectTo({
        url: `/pages/checkpoint/checkpoint?sessionNo=${this.sessionNo}&milestone=${progress.answeredCount}`
      });
      return;
    }

    await this.fetchNextQuestion();
  },

  handleSecondaryTap(event) {
    const { key } = event.detail;
    if (this.data.isTransitioning || this.data.revealAnswer || !this.data.question) {
      return;
    }

    this.submitCurrentAnswer({
      answerStatus: key === 'unsure' ? 'UNCERTAIN' : 'SKIP'
    });
  },

  async finishAssessmentFlow() {
    if (this.finishing) {
      return;
    }

    this.finishing = true;
    wx.showLoading({
      title: '生成结果'
    });

    try {
      const result = await finishAssessment(this.sessionNo);
      patchSession({
        latestResult: result,
        currentQuestion: null,
        pendingQuestion: null
      });

      wx.redirectTo({
        url: `/pages/result/result?sessionNo=${this.sessionNo}`
      });
    } catch (error) {
      showError(error.message || '生成结果失败');
      this.setData({
        isTransitioning: false,
        questionLoading: false,
        primaryText: '查看结果',
        primaryDisabled: false
      });
    } finally {
      wx.hideLoading();
      this.finishing = false;
    }
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
