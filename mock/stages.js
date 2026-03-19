const STAGE_CONFIGS = [
  {
    id: 'stage-1',
    label: '幼儿园大班 - 小学2年级',
    shortLabel: '启蒙基础段',
    subtitle: '更适合看图、听音、认词',
    detail: '按钮更大、提示更直接，先用图像和声音建立词感。',
    mode: 'child',
    ageRange: '5-8岁',
    promptStyle: '图像化、大按钮、短文案',
    heroAccent: 'sunrise',
    startDifficulty: 1
  },
  {
    id: 'stage-2',
    label: '小学3年级 - 6年级',
    shortLabel: '进阶应用段',
    subtitle: '更适合词义理解和基础语境',
    detail: '开始关注词义判断和常见表达，反馈更接近学习工具。',
    mode: 'student',
    ageRange: '8-12岁',
    promptStyle: '更清晰的题干和语义判断',
    heroAccent: 'teal',
    startDifficulty: 3
  },
  {
    id: 'stage-3',
    label: '初中7年级 - 9年级',
    shortLabel: '语境提升段',
    subtitle: '更适合语境辨析和词义判断',
    detail: '题面更克制，强调判断效率和结果可信度。',
    mode: 'student',
    ageRange: '12-15岁',
    promptStyle: '简洁专业、信息层级更高',
    heroAccent: 'deep',
    startDifficulty: 4
  }
];

function getStageById(stageId) {
  return STAGE_CONFIGS.find((item) => item.id === stageId) || STAGE_CONFIGS[0];
}

module.exports = {
  STAGE_CONFIGS,
  getStageById
};
