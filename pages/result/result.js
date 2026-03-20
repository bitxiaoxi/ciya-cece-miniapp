const { getAssessmentResult } = require('../../utils/api');
const { clearSession, getSession, patchSession } = require('../../utils/assessment-session');
const { getStagePresentation } = require('../../utils/stage-helper');

function showError(message) {
  wx.showToast({
    title: message,
    icon: 'none'
  });
}

function formatDateTime(value) {
  if (!value) {
    return '未完成';
  }
  return String(value).replace('T', ' ').slice(0, 16);
}

function formatScore(value, digits = 0) {
  const numeric = Number(value);
  if (Number.isNaN(numeric)) {
    return '--';
  }
  return numeric.toFixed(digits);
}

function formatPercent(value) {
  const numeric = Number(value);
  if (Number.isNaN(numeric)) {
    return '--';
  }
  return `${numeric.toFixed(0)}%`;
}

function buildAbilityDescription(score, correctCount, totalCount) {
  if (!totalCount) {
    return '本次该维度题量较少，建议结合下次复测继续观察。';
  }
  if (score >= 80) {
    return `本次 ${correctCount}/${totalCount} 题表现稳定，说明这一维度掌握相对扎实。`;
  }
  if (score >= 60) {
    return `本次 ${correctCount}/${totalCount} 题整体可用，继续巩固会更稳。`;
  }
  return `本次 ${correctCount}/${totalCount} 题波动偏大，建议优先加强这一维度。`;
}

function splitRecommendation(text) {
  return String(text || '')
    .split(/[；。]/)
    .map((item) => item.trim())
    .filter(Boolean);
}

function buildAbilityItems(result) {
  let source = Array.isArray(result.abilityScores) ? result.abilityScores : [];
  if (!source.length) {
    source = [
      {
        abilityType: 'READING',
        abilityLabel: '阅读词汇',
        score: result.readingScore,
        correctCount: 0,
        totalCount: 0
      },
      {
        abilityType: 'LISTENING',
        abilityLabel: '听辨词汇',
        score: result.listeningScore,
        correctCount: 0,
        totalCount: 0
      },
      {
        abilityType: 'CONTEXT',
        abilityLabel: '语境理解',
        score: result.contextScore,
        correctCount: 0,
        totalCount: 0
      }
    ];
  }

  return source.map((item) => {
    const score = Number(item.score || 0);
    const correctCount = Number(item.correctCount || 0);
    const totalCount = Number(item.totalCount || 0);
    return {
      key: item.abilityType,
      label: item.abilityLabel || item.abilityType,
      score: Math.max(0, Math.min(100, Number(score.toFixed(0)))),
      description: buildAbilityDescription(score, correctCount, totalCount),
      correctCount,
      totalCount
    };
  });
}

function getConfidenceTag(score) {
  const numeric = Number(score || 0);
  if (numeric >= 85) {
    return '结果稳定度高';
  }
  if (numeric >= 70) {
    return '结果参考度较高';
  }
  if (numeric >= 55) {
    return '建议近期复测';
  }
  return '结果波动偏大';
}

function getStrongWeakAbilities(abilities) {
  if (!abilities.length) {
    return {
      strongest: null,
      weakest: null
    };
  }
  const sorted = abilities.slice().sort((left, right) => left.score - right.score);
  return {
    weakest: sorted[0],
    strongest: sorted[sorted.length - 1]
  };
}

function buildSuggestionTags(result, weakestAbility) {
  const basis = result.basis || {};
  const tags = splitRecommendation(result.recommendationText);

  if (weakestAbility) {
    tags.push(`优先巩固${weakestAbility.label}相关高频词和短练习。`);
  }
  if (Number(result.confidenceScore || 0) < 70) {
    tags.push('建议 1 到 2 周后用相近学段再测一次，观察结果是否稳定。');
  }
  if (Number(basis.uncertainCount || 0) + Number(basis.skipCount || 0) >= 2) {
    tags.push('下次测评可尽量减少“不确定”和“跳过”，这样结果会更稳。');
  }

  const filtered = tags.filter(Boolean);
  if (!filtered.length) {
    filtered.push('继续保持高频词巩固和短场景练习，逐步扩展稳定词汇面。');
  }
  return filtered.slice(0, 4);
}

function buildReferenceTags(result, strongestAbility, weakestAbility, estimatedStage) {
  const tags = [];
  if (estimatedStage) {
    tags.push(`结果更接近${estimatedStage.shortLabel}`);
  }
  if (strongestAbility) {
    tags.push(`${strongestAbility.label}更稳`);
  }
  if (weakestAbility && (!strongestAbility || weakestAbility.key !== strongestAbility.key)) {
    tags.push(`${weakestAbility.label}待加强`);
  }
  tags.push(getConfidenceTag(result.confidenceScore));
  if (result.session && result.session.aiEnabled) {
    tags.push('含 AI 解释留痕');
  }
  return tags.slice(0, 4);
}

function buildBasisItems(result) {
  const basis = result.basis || {};
  const finalDifficultyBand = basis.finalDifficultyBand || {};
  const anchorPerformance = basis.anchorPerformance || {};

  return [
    {
      label: '规则版本',
      value: basis.ruleVersion || (result.session && result.session.ruleVersion) || '--'
    },
    {
      label: '题库版本',
      value: basis.bankVersion || (result.session && result.session.bankVersion) || '--'
    },
    {
      label: '正确率',
      value: formatPercent(basis.correctRate)
    },
    {
      label: '总体得分',
      value: formatScore(basis.overallScore, 2)
    },
    {
      label: '难度带位置',
      value: formatPercent(finalDifficultyBand.normalizedRate)
    },
    {
      label: '锚点稳定度',
      value: formatPercent(anchorPerformance.weightedRate)
    }
  ];
}

function buildSessionItems(result, selectedStage, estimatedStage) {
  const session = result.session || {};
  return [
    {
      label: '测评编号',
      value: session.sessionNo || '--'
    },
    {
      label: '起测学段',
      value: selectedStage ? selectedStage.shortLabel : (session.selectedStageCode || '--')
    },
    {
      label: '估计学段',
      value: estimatedStage ? estimatedStage.shortLabel : (result.estimatedStageCode || '--')
    },
    {
      label: '作答情况',
      value: `${session.correctCount || 0}/${session.answeredCount || 0} 题正确`
    },
    {
      label: '开始时间',
      value: formatDateTime(session.startedAt)
    },
    {
      label: '完成时间',
      value: formatDateTime(session.finishedAt)
    }
  ];
}

function buildResultView(result) {
  const session = result.session || {};
  const selectedStage = getStagePresentation(session.selectedStageCode, session.selectedStageCode);
  const estimatedStage = getStagePresentation(result.estimatedStageCode, result.estimatedStageCode);
  const abilities = buildAbilityItems(result);
  const abilityState = getStrongWeakAbilities(abilities);
  const basis = result.basis || {};
  const basisItems = buildBasisItems(result);
  const sessionItems = buildSessionItems(result, selectedStage, estimatedStage);
  const anchorPerformance = basis.anchorPerformance || {};
  const finalDifficultyBand = basis.finalDifficultyBand || {};
  const totalAnswered = Number(basis.totalAnswered || session.answeredCount || 0);
  const confidenceScore = Number(result.confidenceScore || 0);

  return {
    themeMode: selectedStage.mode || 'student',
    stageLabel: `词芽测测 · ${selectedStage.shortLabel}`,
    resultCard: {
      vocabEstimate: result.vocabEstimateMid,
      vocabRange: `${result.vocabEstimateMin} - ${result.vocabEstimateMax}`,
      summary: result.summaryText || '本次结果已生成。',
      note: `规则 ${basis.ruleVersion || session.ruleVersion || '--'} · 题库 ${basis.bankVersion || session.bankVersion || '--'} · 可信度 ${formatScore(confidenceScore, 2)}`
    },
    stageReference: `本次从${selectedStage.shortLabel}起测，综合作答路径后，结果更接近${estimatedStage.shortLabel}。`,
    abilities,
    referenceTags: buildReferenceTags(result, abilityState.strongest, abilityState.weakest, estimatedStage),
    suggestionTags: buildSuggestionTags(result, abilityState.weakest),
    basisItems,
    basisSummary: `本次共作答 ${totalAnswered} 题，正确率 ${formatPercent(basis.correctRate)}，锚点题稳定度 ${formatPercent(anchorPerformance.weightedRate)}，当前难度位于本学段带宽的 ${formatPercent(finalDifficultyBand.normalizedRate)}。`,
    sessionItems
  };
}

Page({
  data: {
    themeMode: 'student',
    stageLabel: '词芽测测 · 测评结果',
    result: null,
    referenceTags: [],
    suggestionTags: [],
    abilities: [],
    stageReference: '',
    basisItems: [],
    basisSummary: '',
    sessionItems: [],
    loading: true,
    footerActions: [
      { key: 'retry', label: '再测一次' }
    ]
  },

  onLoad(options) {
    const session = getSession();
    this.sessionNo = options.sessionNo || (session && session.sessionNo) || '';
    this.loadResult();
  },

  async loadResult() {
    if (!this.sessionNo) {
      showError('缺少测评编号');
      wx.redirectTo({
        url: '/pages/home/home'
      });
      return;
    }

    this.setData({
      loading: true
    });

    wx.showLoading({
      title: '加载结果'
    });

    try {
      const result = await getAssessmentResult(this.sessionNo);
      const view = buildResultView(result);
      const session = getSession();
      if (session && session.sessionNo === this.sessionNo) {
        patchSession({
          latestResult: result
        });
      }

      this.setData({
        loading: false,
        themeMode: view.themeMode,
        stageLabel: view.stageLabel,
        result: view.resultCard,
        referenceTags: view.referenceTags,
        suggestionTags: view.suggestionTags,
        abilities: view.abilities,
        stageReference: view.stageReference,
        basisItems: view.basisItems,
        basisSummary: view.basisSummary,
        sessionItems: view.sessionItems
      });
    } catch (error) {
      showError(error.message || '加载结果失败');
      this.setData({
        loading: false
      });
    } finally {
      wx.hideLoading();
    }
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
