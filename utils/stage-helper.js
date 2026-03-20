const STAGE_META = {
  K3: {
    shortLabel: '启蒙感知段',
    subtitle: '更适合看图、听音和基础认词',
    detail: '题面更直观，先帮孩子建立词感和基础反应。',
    mode: 'child',
    ageRange: '5-6岁',
    promptStyle: '图像化、大按钮、短提示',
    heroAccent: 'sunrise'
  },
  P1_2: {
    shortLabel: '低年级基础段',
    subtitle: '高频词义和基础听辨并行',
    detail: '以常见学习和生活场景词为主，降低挫败感。',
    mode: 'child',
    ageRange: '6-8岁',
    promptStyle: '短文案、强提示、低压力',
    heroAccent: 'sun'
  },
  P3_4: {
    shortLabel: '进阶应用段',
    subtitle: '开始看词义、语境和基础判断',
    detail: '题干更清晰，重点看孩子对高频词和语境线索的掌握。',
    mode: 'student',
    ageRange: '8-10岁',
    promptStyle: '清晰题干、词义判断',
    heroAccent: 'teal'
  },
  P5_6: {
    shortLabel: '高年级拓展段',
    subtitle: '语义辨析和听辨难度继续提升',
    detail: '会更多考察词义稳定性、场景理解和难度爬升表现。',
    mode: 'student',
    ageRange: '10-12岁',
    promptStyle: '信息更完整、判断更紧凑',
    heroAccent: 'mint'
  },
  J7_9: {
    shortLabel: '语境提升段',
    subtitle: '更适合语境辨析和抽象词义判断',
    detail: '题面更克制，强调词义覆盖、稳定性和解释性结果。',
    mode: 'student',
    ageRange: '12-15岁',
    promptStyle: '简洁专业、信息层级更高',
    heroAccent: 'deep'
  }
};

const GRADE_OPTIONS = [
  { code: 'K3', label: '幼儿园大班' },
  { code: 'P1', label: '小学一年级' },
  { code: 'P2', label: '小学二年级' },
  { code: 'P3', label: '小学三年级' },
  { code: 'P4', label: '小学四年级' },
  { code: 'P5', label: '小学五年级' },
  { code: 'P6', label: '小学六年级' },
  { code: 'J7', label: '初中七年级' },
  { code: 'J8', label: '初中八年级' },
  { code: 'J9', label: '初中九年级' }
];

function getStageMeta(stageCode) {
  return STAGE_META[stageCode] || STAGE_META.P3_4;
}

function mergeStageRule(stageRule) {
  const meta = getStageMeta(stageRule.stageCode);
  return Object.assign({}, stageRule, meta, {
    id: stageRule.stageCode,
    label: stageRule.stageName,
    totalQuestionCount: (stageRule.routeQuestionCount || 0) + (stageRule.coreQuestionCount || 0) + (stageRule.anchorQuestionCount || 0)
  });
}

function getStagePresentation(stageCode, stageName) {
  return Object.assign({
    id: stageCode,
    stageCode,
    label: stageName || stageCode
  }, getStageMeta(stageCode));
}

function getStageLabel(stageCode, fallback) {
  return (getStageMeta(stageCode).shortLabel || fallback || stageCode);
}

function getGradeLabel(gradeCode) {
  const matched = GRADE_OPTIONS.find((item) => item.code === gradeCode);
  return matched ? matched.label : gradeCode;
}

module.exports = {
  GRADE_OPTIONS,
  getGradeLabel,
  getStageLabel,
  getStageMeta,
  getStagePresentation,
  mergeStageRule
};
