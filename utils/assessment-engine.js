const { getStageById } = require('../mock/stages');
const { getQuestionBankByStage } = require('../mock/questions');
const { getAbilityLabel, getStageReferenceByEstimate, SAMPLE_RESULT } = require('../mock/results');

const STORAGE_KEY = 'vocab_seed_assessment_session';
const TOTAL_QUESTIONS = 12;
const CHECKPOINTS = [4, 8];

let memorySession = null;

function hasWxStorage() {
  return typeof wx !== 'undefined' && typeof wx.getStorageSync === 'function';
}

function readStorage() {
  if (hasWxStorage()) {
    return wx.getStorageSync(STORAGE_KEY);
  }
  return memorySession;
}

function writeStorage(session) {
  memorySession = session;
  if (hasWxStorage()) {
    wx.setStorageSync(STORAGE_KEY, session);
  }
}

function removeStorage() {
  memorySession = null;
  if (hasWxStorage()) {
    wx.removeStorageSync(STORAGE_KEY);
  }
}

function clone(data) {
  return JSON.parse(JSON.stringify(data));
}

function getSession() {
  const session = readStorage();
  if (!session) {
    return null;
  }
  memorySession = session;
  return clone(session);
}

function getQuestionById(stageId, questionId) {
  const bank = getQuestionBankByStage(stageId);
  return bank.find((item) => item.id === questionId) || null;
}

function pickNextQuestion(session) {
  const bank = getQuestionBankByStage(session.stageId);
  const usedQuestionIds = session.answers.map((item) => item.questionId);
  if (session.currentQuestionId) {
    usedQuestionIds.push(session.currentQuestionId);
  }
  const lastQuestionType = session.answers.length ? session.answers[session.answers.length - 1].type : '';
  const available = bank.filter((question) => usedQuestionIds.indexOf(question.id) === -1);

  if (!available.length) {
    return null;
  }

  return available
    .slice()
    .sort((questionA, questionB) => {
      const diffA = Math.abs(questionA.difficulty - session.currentDifficulty);
      const diffB = Math.abs(questionB.difficulty - session.currentDifficulty);
      if (diffA !== diffB) {
        return diffA - diffB;
      }

      const typePenaltyA = questionA.type === lastQuestionType ? 1 : 0;
      const typePenaltyB = questionB.type === lastQuestionType ? 1 : 0;
      if (typePenaltyA !== typePenaltyB) {
        return typePenaltyA - typePenaltyB;
      }

      return questionA.id.localeCompare(questionB.id);
    })[0];
}

function createAbilityBuckets() {
  return {
    recognition: { correct: 0, total: 0 },
    listening: { correct: 0, total: 0 },
    context: { correct: 0, total: 0 }
  };
}

function createSession(stageId) {
  const stage = getStageById(stageId);
  const session = {
    id: `session_${Date.now()}`,
    stageId: stage.id,
    mode: stage.mode,
    totalQuestions: TOTAL_QUESTIONS,
    checkpoints: clone(CHECKPOINTS),
    shownCheckpoints: [],
    currentDifficulty: stage.startDifficulty || 2,
    currentQuestionId: '',
    answers: [],
    abilityBuckets: createAbilityBuckets(),
    result: null,
    startedAt: Date.now()
  };

  const firstQuestion = pickNextQuestion(session);
  session.currentQuestionId = firstQuestion ? firstQuestion.id : '';
  writeStorage(session);
  return clone(session);
}

function ensureSession(stageId) {
  const session = getSession();
  if (session && (!stageId || session.stageId === stageId)) {
    return session;
  }
  return createSession(stageId || 'stage-1');
}

function updateDifficulty(currentDifficulty, answerPayload) {
  if (answerPayload.action === 'skip') {
    return Math.max(1, currentDifficulty - 1);
  }
  if (answerPayload.action === 'unsure') {
    return currentDifficulty;
  }
  return answerPayload.correct ? Math.min(6, currentDifficulty + 1) : Math.max(1, currentDifficulty - 1);
}

function computeEstimate(session) {
  const totalCorrect = session.answers.filter((item) => item.correct).length;
  const accuracy = totalCorrect / session.answers.length;
  const baseByStage = {
    'stage-1': 520,
    'stage-2': 1320,
    'stage-3': 2380
  };
  const stageBonus = {
    'stage-1': 86,
    'stage-2': 128,
    'stage-3': 168
  };

  const estimate = Math.round(baseByStage[session.stageId] + totalCorrect * stageBonus[session.stageId] + accuracy * 220 + session.currentDifficulty * 58);
  const rangeGap = session.stageId === 'stage-1' ? 120 : session.stageId === 'stage-2' ? 180 : 220;

  return {
    value: estimate,
    range: `${Math.max(estimate - rangeGap, 200)} - ${estimate + rangeGap}`
  };
}

function buildAbilities(session) {
  return Object.keys(session.abilityBuckets).map((key) => {
    const bucket = session.abilityBuckets[key];
    const score = bucket.total ? Math.round((bucket.correct / bucket.total) * 100) : 68;
    let description = '基础表现稳定，可以继续保持。';

    if (key === 'recognition') {
      description = score >= 80 ? '看到单词后能较快联想到常见词义。' : '基础认读词还可以继续积累，会帮助整体提速。';
    }
    if (key === 'listening') {
      description = score >= 80 ? '听到发音后能较稳判断词义。' : '长词和多音节词的听辨还值得再练一练。';
    }
    if (key === 'context') {
      description = score >= 80 ? '能在基础语境中做出较稳定判断。' : '语境题已经有感觉，再加一点词义辨析会更稳。';
    }

    return {
      key,
      label: getAbilityLabel(key),
      score,
      description
    };
  });
}

function buildSuggestions(abilities, session) {
  const sortedAbilities = abilities.slice().sort((itemA, itemB) => itemA.score - itemB.score);
  const weakest = sortedAbilities[0];
  const middle = sortedAbilities[1];
  const lead = sortedAbilities[2];
  const starter = session.stageId === 'stage-1'
    ? '先稳住高频基础词，把会认会听的词做得更熟。'
    : '先巩固高频基础词和核心学习场景词，提升会更稳。';

  const focusMap = {
    recognition: '可以多做“看词找义”和“图词配对”，把认读反应速度再提起来。',
    listening: '建议先加强听辨和常见语音节奏词，多听多选会更有效。',
    context: '建议补一补常见语境词和固定搭配，做题时会更容易抓到线索。'
  };

  return [
    starter,
    focusMap[weakest.key],
    `${lead.label}已经表现不错，再带着${middle.label}一起巩固，整体提升会更快。`
  ];
}

function buildSummary(estimate, abilities) {
  const strongest = abilities.slice().sort((itemA, itemB) => itemB.score - itemA.score)[0];
  if (estimate < 900) {
    return `基础词认识得不错，${strongest.label}已经开始建立稳定感觉。`;
  }
  if (estimate < 2200) {
    return `常见高频词掌握得比较稳，${strongest.label}是这次表现更亮眼的一项。`;
  }
  return `整体词汇面已经有不错的延展，${strongest.label}表现尤其稳定。`;
}

function buildResult(session) {
  const estimateData = computeEstimate(session);
  const abilities = buildAbilities(session);
  return {
    vocabEstimate: estimateData.value,
    vocabRange: estimateData.range,
    stageReference: getStageReferenceByEstimate(estimateData.value).label,
    summary: buildSummary(estimateData.value, abilities),
    note: '这是基于短回合自适应题目生成的估算结果，用于帮助了解当前词汇覆盖和下一步巩固方向。',
    strengths: abilities
      .filter((item) => item.score >= 80)
      .map((item) => `${item.label}表现稳`)
      .slice(0, 2),
    growthFocus: abilities
      .filter((item) => item.score < 80)
      .map((item) => `${item.label}继续巩固会更快看到提升`)
      .slice(0, 2),
    abilities,
    suggestions: buildSuggestions(abilities, session)
  };
}

function getCurrentQuestion() {
  const session = getSession();
  if (!session || !session.currentQuestionId) {
    return null;
  }
  return clone(getQuestionById(session.stageId, session.currentQuestionId));
}

function submitCurrentAnswer(payload) {
  const session = getSession();
  if (!session || !session.currentQuestionId) {
    return { type: 'home' };
  }

  const question = getQuestionById(session.stageId, session.currentQuestionId);
  if (!question) {
    return { type: 'home' };
  }

  const isDirectAnswer = payload.action === 'answer';
  const correct = isDirectAnswer && payload.optionId === question.answerId;

  session.answers.push({
    questionId: question.id,
    type: question.type,
    ability: question.ability,
    optionId: payload.optionId || '',
    action: payload.action,
    correct
  });

  const abilityBucket = session.abilityBuckets[question.ability];
  if (abilityBucket) {
    abilityBucket.total += 1;
    if (correct) {
      abilityBucket.correct += 1;
    }
  }

  session.currentDifficulty = updateDifficulty(session.currentDifficulty, {
    action: payload.action,
    correct
  });

  if (session.answers.length >= session.totalQuestions) {
    session.currentQuestionId = '';
    session.result = buildResult(session);
    writeStorage(session);
    return { type: 'result' };
  }

  const milestone = session.checkpoints.find((item) => item === session.answers.length && session.shownCheckpoints.indexOf(item) === -1);
  const nextQuestion = pickNextQuestion(session);
  session.currentQuestionId = nextQuestion ? nextQuestion.id : '';

  if (milestone) {
    session.shownCheckpoints.push(milestone);
    writeStorage(session);
    return { type: 'checkpoint', milestone };
  }

  writeStorage(session);
  return { type: 'question' };
}

function clearSession() {
  removeStorage();
}

function getLatestResult() {
  const session = getSession();
  if (session && session.result) {
    return session.result;
  }
  return clone(SAMPLE_RESULT);
}

module.exports = {
  TOTAL_QUESTIONS,
  CHECKPOINTS,
  createSession,
  ensureSession,
  getSession,
  getCurrentQuestion,
  submitCurrentAnswer,
  getLatestResult,
  clearSession
};
