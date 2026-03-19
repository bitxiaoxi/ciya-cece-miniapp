const ABILITY_LABELS = {
  recognition: '认读词汇',
  listening: '听辨词汇',
  context: '语境理解'
};

const SAMPLE_RESULT = {
  vocabEstimate: 1680,
  vocabRange: '1500 - 1850',
  stageReference: '大致处于小学高年级到初一常见词水平',
  summary: '基础词认识得不错，常见学习场景词也有稳定判断力。',
  note: '本次结果基于短回合自适应测评估算，用来了解当前词汇面和下一步巩固方向。',
  strengths: ['高频基础词稳', '图像词识别快'],
  growthFocus: ['听辨长词时还可以再熟一点'],
  abilities: [
    {
      key: 'recognition',
      label: '认读词汇',
      score: 86,
      description: '看到单词后能较快匹配常见词义。'
    },
    {
      key: 'listening',
      label: '听辨词汇',
      score: 71,
      description: '短词较稳，长音节词还可以继续巩固。'
    },
    {
      key: 'context',
      label: '语境理解',
      score: 76,
      description: '能理解基础语境中的常见表达。'
    }
  ],
  suggestions: [
    '先巩固高频基础词，继续扩大稳定会做的词。',
    '把听辨练习和词义卡片搭配起来，提升会更快。',
    '可以开始接触更多校园和生活语境词。'
  ]
};

const RESULT_STAGE_REFERENCES = [
  { min: 0, max: 900, label: '大致处于启蒙到小学低年级高频词范围' },
  { min: 901, max: 1500, label: '大致处于小学低年级到中年级基础词范围' },
  { min: 1501, max: 2400, label: '大致处于小学高年级到初一常见词范围' },
  { min: 2401, max: 3400, label: '大致处于初中阶段常见词范围' },
  { min: 3401, max: 5200, label: '大致接近初中较强词汇覆盖范围' }
];

function getStageReferenceByEstimate(estimate) {
  return RESULT_STAGE_REFERENCES.find((item) => estimate >= item.min && estimate <= item.max) || RESULT_STAGE_REFERENCES[2];
}

function getAbilityLabel(key) {
  return ABILITY_LABELS[key] || key;
}

module.exports = {
  SAMPLE_RESULT,
  ABILITY_LABELS,
  RESULT_STAGE_REFERENCES,
  getAbilityLabel,
  getStageReferenceByEstimate
};
