const LETTERS = ['A', 'B', 'C', 'D'];

function withLetters(options) {
  return options.map((option, index) => ({
    ...option,
    letter: LETTERS[index]
  }));
}

const QUESTION_BANK = [
  {
    id: 'q-101',
    stageIds: ['stage-1'],
    type: 'image_to_word',
    difficulty: 1,
    prompt: '看图选词',
    stem: '这张图对应哪个英文单词？',
    stemNote: '挑一个你最熟悉的。',
    image: { emoji: '🐱', label: '小猫', tone: 'warm' },
    options: withLetters([
      { id: 'cat', label: 'cat' },
      { id: 'cake', label: 'cake' },
      { id: 'cap', label: 'cap' },
      { id: 'car', label: 'car' }
    ]),
    answerId: 'cat',
    ability: 'recognition'
  },
  {
    id: 'q-102',
    stageIds: ['stage-1'],
    type: 'word_to_cn',
    difficulty: 1,
    prompt: '看英文选中文',
    stem: 'book',
    stemNote: '你认识这个基础词吗？',
    options: withLetters([
      { id: 'a', label: '书' },
      { id: 'b', label: '桌子' },
      { id: 'c', label: '老师' },
      { id: 'd', label: '杯子' }
    ]),
    answerId: 'a',
    ability: 'recognition'
  },
  {
    id: 'q-103',
    stageIds: ['stage-1'],
    type: 'audio_choice',
    difficulty: 1,
    prompt: '听发音选答案',
    stem: '点一下按钮，选出你听到的词。',
    stemNote: '暂用占位播放演示。',
    audio: { transcript: 'banana', hint: '/bəˈnænə/' },
    options: withLetters([
      { id: 'a', label: '香蕉' },
      { id: 'b', label: '苹果' },
      { id: 'c', label: '牛奶' },
      { id: 'd', label: '帽子' }
    ]),
    answerId: 'a',
    ability: 'listening'
  },
  {
    id: 'q-104',
    stageIds: ['stage-1'],
    type: 'image_to_word',
    difficulty: 1,
    prompt: '看图选词',
    stem: '它是哪一个？',
    stemNote: '跟着图片来判断。',
    image: { emoji: '☀️', label: '太阳', tone: 'sun' },
    options: withLetters([
      { id: 'sun', label: 'sun' },
      { id: 'star', label: 'star' },
      { id: 'snow', label: 'snow' },
      { id: 'sand', label: 'sand' }
    ]),
    answerId: 'sun',
    ability: 'recognition'
  },
  {
    id: 'q-105',
    stageIds: ['stage-1'],
    type: 'audio_choice',
    difficulty: 2,
    prompt: '听发音选答案',
    stem: '听一听，哪个词最像你听到的？',
    stemNote: '可以多看一眼选项再决定。',
    audio: { transcript: 'yellow', hint: '/ˈjeləʊ/' },
    options: withLetters([
      { id: 'a', label: '黄色' },
      { id: 'b', label: '绿色' },
      { id: 'c', label: '白色' },
      { id: 'd', label: '黑色' }
    ]),
    answerId: 'a',
    ability: 'listening'
  },
  {
    id: 'q-106',
    stageIds: ['stage-1'],
    type: 'word_to_cn',
    difficulty: 2,
    prompt: '看英文选中文',
    stem: 'teacher',
    stemNote: '选出最接近的意思。',
    options: withLetters([
      { id: 'a', label: '学生' },
      { id: 'b', label: '老师' },
      { id: 'c', label: '同学' },
      { id: 'd', label: '校长' }
    ]),
    answerId: 'b',
    ability: 'recognition'
  },
  {
    id: 'q-107',
    stageIds: ['stage-1'],
    type: 'image_to_word',
    difficulty: 2,
    prompt: '看图选词',
    stem: '这张图更像哪一个词？',
    stemNote: '不用急，先看清楚。',
    image: { emoji: '🏫', label: '学校', tone: 'mint' },
    options: withLetters([
      { id: 'home', label: 'home' },
      { id: 'school', label: 'school' },
      { id: 'park', label: 'park' },
      { id: 'shop', label: 'shop' }
    ]),
    answerId: 'school',
    ability: 'recognition'
  },
  {
    id: 'q-108',
    stageIds: ['stage-1'],
    type: 'word_to_cn',
    difficulty: 2,
    prompt: '看英文选中文',
    stem: 'happy',
    stemNote: '这是一个表示感受的词。',
    options: withLetters([
      { id: 'a', label: '生气的' },
      { id: 'b', label: '安静的' },
      { id: 'c', label: '开心的' },
      { id: 'd', label: '困倦的' }
    ]),
    answerId: 'c',
    ability: 'context'
  },
  {
    id: 'q-109',
    stageIds: ['stage-1'],
    type: 'audio_choice',
    difficulty: 3,
    prompt: '听发音选答案',
    stem: '点击播放后，选出你听到的动作。',
    stemNote: '动作词也会出现哦。',
    audio: { transcript: 'jump', hint: '/dʒʌmp/' },
    options: withLetters([
      { id: 'a', label: '跑' },
      { id: 'b', label: '跳' },
      { id: 'c', label: '唱' },
      { id: 'd', label: '洗' }
    ]),
    answerId: 'b',
    ability: 'listening'
  },
  {
    id: 'q-110',
    stageIds: ['stage-1'],
    type: 'word_to_cn',
    difficulty: 3,
    prompt: '看英文选中文',
    stem: 'window',
    stemNote: '试着从日常生活里回忆它。',
    options: withLetters([
      { id: 'a', label: '门' },
      { id: 'b', label: '楼梯' },
      { id: 'c', label: '窗户' },
      { id: 'd', label: '地板' }
    ]),
    answerId: 'c',
    ability: 'recognition'
  },
  {
    id: 'q-111',
    stageIds: ['stage-1'],
    type: 'image_to_word',
    difficulty: 3,
    prompt: '看图选词',
    stem: '这份食物更接近哪个英文词？',
    stemNote: '如果不确定，可以先排除明显不对的。',
    image: { emoji: '🍳', label: '早餐', tone: 'warm' },
    options: withLetters([
      { id: 'dinner', label: 'dinner' },
      { id: 'breakfast', label: 'breakfast' },
      { id: 'dessert', label: 'dessert' },
      { id: 'snack', label: 'snack' }
    ]),
    answerId: 'breakfast',
    ability: 'context'
  },
  {
    id: 'q-112',
    stageIds: ['stage-1'],
    type: 'audio_choice',
    difficulty: 4,
    prompt: '听发音选答案',
    stem: '选出你听到的时间词。',
    stemNote: '慢慢听，没关系。',
    audio: { transcript: 'Saturday', hint: '/ˈsætədeɪ/' },
    options: withLetters([
      { id: 'a', label: '星期六' },
      { id: 'b', label: '星期一' },
      { id: 'c', label: '春天' },
      { id: 'd', label: '晚上' }
    ]),
    answerId: 'a',
    ability: 'listening'
  },
  {
    id: 'q-113',
    stageIds: ['stage-1'],
    type: 'word_to_cn',
    difficulty: 4,
    prompt: '看英文选中文',
    stem: 'thirsty',
    stemNote: '这个词和身体感觉有关。',
    options: withLetters([
      { id: 'a', label: '口渴的' },
      { id: 'b', label: '饥饿的' },
      { id: 'c', label: '受伤的' },
      { id: 'd', label: '疲倦的' }
    ]),
    answerId: 'a',
    ability: 'context'
  },
  {
    id: 'q-114',
    stageIds: ['stage-1'],
    type: 'image_to_word',
    difficulty: 4,
    prompt: '看图选词',
    stem: '图里这个职业是什么？',
    stemNote: '观察服装和场景。',
    image: { emoji: '👩‍⚕️', label: '医生', tone: 'mint' },
    options: withLetters([
      { id: 'doctor', label: 'doctor' },
      { id: 'dancer', label: 'dancer' },
      { id: 'driver', label: 'driver' },
      { id: 'farmer', label: 'farmer' }
    ]),
    answerId: 'doctor',
    ability: 'recognition'
  },
  {
    id: 'q-201',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 2,
    prompt: '看英文选中文',
    stem: 'library',
    stemNote: '从校园场景里判断。',
    options: withLetters([
      { id: 'a', label: '图书馆' },
      { id: 'b', label: '体育馆' },
      { id: 'c', label: '实验室' },
      { id: 'd', label: '礼堂' }
    ]),
    answerId: 'a',
    ability: 'recognition'
  },
  {
    id: 'q-202',
    stageIds: ['stage-2', 'stage-3'],
    type: 'image_to_word',
    difficulty: 2,
    prompt: '看图选词',
    stem: '图里的交通标识是什么？',
    stemNote: '先看图，再对照词形。',
    image: { emoji: '🚦', label: '交通灯', tone: 'deep' },
    options: withLetters([
      { id: 'crosswalk', label: 'crosswalk' },
      { id: 'traffic-light', label: 'traffic light' },
      { id: 'subway', label: 'subway' },
      { id: 'helmet', label: 'helmet' }
    ]),
    answerId: 'traffic-light',
    ability: 'recognition'
  },
  {
    id: 'q-203',
    stageIds: ['stage-2', 'stage-3'],
    type: 'audio_choice',
    difficulty: 3,
    prompt: '听发音选答案',
    stem: '点击播放后，选出你听到的动词意思。',
    stemNote: '更关注听辨后的词义判断。',
    audio: { transcript: 'borrow', hint: '/ˈbɒrəʊ/' },
    options: withLetters([
      { id: 'a', label: '借入' },
      { id: 'b', label: '归还' },
      { id: 'c', label: '讨论' },
      { id: 'd', label: '整理' }
    ]),
    answerId: 'a',
    ability: 'listening'
  },
  {
    id: 'q-204',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 3,
    prompt: '看英文选中文',
    stem: 'careful',
    stemNote: '这是一个描述状态的词。',
    options: withLetters([
      { id: 'a', label: '安静的' },
      { id: 'b', label: '细心的' },
      { id: 'c', label: '诚实的' },
      { id: 'd', label: '勇敢的' }
    ]),
    answerId: 'b',
    ability: 'context'
  },
  {
    id: 'q-205',
    stageIds: ['stage-2', 'stage-3'],
    type: 'image_to_word',
    difficulty: 3,
    prompt: '看图选词',
    stem: '图中的物品最接近哪个词？',
    stemNote: '注意不同物件的细节。',
    image: { emoji: '✉️', label: '信封', tone: 'teal' },
    options: withLetters([
      { id: 'message', label: 'message' },
      { id: 'envelope', label: 'envelope' },
      { id: 'notebook', label: 'notebook' },
      { id: 'ticket', label: 'ticket' }
    ]),
    answerId: 'envelope',
    ability: 'recognition'
  },
  {
    id: 'q-206',
    stageIds: ['stage-2', 'stage-3'],
    type: 'audio_choice',
    difficulty: 3,
    prompt: '听发音选答案',
    stem: '选出你听到的形容词。',
    stemNote: '试着先听重音位置。',
    audio: { transcript: 'delicious', hint: '/dɪˈlɪʃəs/' },
    options: withLetters([
      { id: 'a', label: '整洁的' },
      { id: 'b', label: '忙碌的' },
      { id: 'c', label: '美味的' },
      { id: 'd', label: '危险的' }
    ]),
    answerId: 'c',
    ability: 'listening'
  },
  {
    id: 'q-207',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 4,
    prompt: '看英文选中文',
    stem: 'museum',
    stemNote: '常见场馆词，优先看整体意思。',
    options: withLetters([
      { id: 'a', label: '博物馆' },
      { id: 'b', label: '植物园' },
      { id: 'c', label: '停车场' },
      { id: 'd', label: '候车室' }
    ]),
    answerId: 'a',
    ability: 'recognition'
  },
  {
    id: 'q-208',
    stageIds: ['stage-2', 'stage-3'],
    type: 'audio_choice',
    difficulty: 4,
    prompt: '听发音选答案',
    stem: '听后判断它最接近哪一项。',
    stemNote: '不用完全拼写出来，只看词义判断。',
    audio: { transcript: 'conversation', hint: '/ˌkɒnvəˈseɪʃən/' },
    options: withLetters([
      { id: 'a', label: '比较' },
      { id: 'b', label: '对话' },
      { id: 'c', label: '提议' },
      { id: 'd', label: '调查' }
    ]),
    answerId: 'b',
    ability: 'listening'
  },
  {
    id: 'q-209',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 4,
    prompt: '看英文选中文',
    stem: 'improve',
    stemNote: '放进学习语境里想一想。',
    options: withLetters([
      { id: 'a', label: '改善，提高' },
      { id: 'b', label: '浪费，损失' },
      { id: 'c', label: '保存，留下' },
      { id: 'd', label: '隐藏，遮住' }
    ]),
    answerId: 'a',
    ability: 'context'
  },
  {
    id: 'q-210',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 4,
    prompt: '看英文选中文',
    stem: 'compare',
    stemNote: '这个词常和事物之间的差异有关。',
    options: withLetters([
      { id: 'a', label: '比较' },
      { id: 'b', label: '等待' },
      { id: 'c', label: '猜测' },
      { id: 'd', label: '解释' }
    ]),
    answerId: 'a',
    ability: 'context'
  },
  {
    id: 'q-211',
    stageIds: ['stage-2', 'stage-3'],
    type: 'image_to_word',
    difficulty: 4,
    prompt: '看图选词',
    stem: '这个生活场景最像哪个词？',
    stemNote: '试着用整体场景排除干扰项。',
    image: { emoji: '🏘️', label: '街区 / 社区', tone: 'teal' },
    options: withLetters([
      { id: 'neighborhood', label: 'neighborhood' },
      { id: 'mountain', label: 'mountain' },
      { id: 'laboratory', label: 'laboratory' },
      { id: 'airport', label: 'airport' }
    ]),
    answerId: 'neighborhood',
    ability: 'recognition'
  },
  {
    id: 'q-212',
    stageIds: ['stage-2', 'stage-3'],
    type: 'audio_choice',
    difficulty: 5,
    prompt: '听发音选答案',
    stem: '选出你听到的名词意思。',
    stemNote: '词更长，但不需要紧张。',
    audio: { transcript: 'invitation', hint: '/ˌɪnvɪˈteɪʃən/' },
    options: withLetters([
      { id: 'a', label: '邀请' },
      { id: 'b', label: '讨论' },
      { id: 'c', label: '规则' },
      { id: 'd', label: '方向' }
    ]),
    answerId: 'a',
    ability: 'listening'
  },
  {
    id: 'q-213',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 5,
    prompt: '看英文选中文',
    stem: 'discover',
    stemNote: '语境里通常带有“找到新信息”的感觉。',
    options: withLetters([
      { id: 'a', label: '发现' },
      { id: 'b', label: '复制' },
      { id: 'c', label: '提醒' },
      { id: 'd', label: '拆分' }
    ]),
    answerId: 'a',
    ability: 'context'
  },
  {
    id: 'q-214',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 5,
    prompt: '看英文选中文',
    stem: 'available',
    stemNote: '常见于时间、座位、资源是否可用。',
    options: withLetters([
      { id: 'a', label: '有限的' },
      { id: 'b', label: '可获得的，可用的' },
      { id: 'c', label: '额外的' },
      { id: 'd', label: '即将结束的' }
    ]),
    answerId: 'b',
    ability: 'context'
  },
  {
    id: 'q-215',
    stageIds: ['stage-2', 'stage-3'],
    type: 'word_to_cn',
    difficulty: 5,
    prompt: '看英文选中文',
    stem: 'environment',
    stemNote: '不仅是自然环境，也可以指周围条件。',
    options: withLetters([
      { id: 'a', label: '环境' },
      { id: 'b', label: '边界' },
      { id: 'c', label: '成员' },
      { id: 'd', label: '材料' }
    ]),
    answerId: 'a',
    ability: 'recognition'
  },
  {
    id: 'q-216',
    stageIds: ['stage-2', 'stage-3'],
    type: 'audio_choice',
    difficulty: 5,
    prompt: '听发音选答案',
    stem: '点击播放后，判断词义。',
    stemNote: '留意重音位置会更好判断。',
    audio: { transcript: 'journey', hint: '/ˈdʒɜːni/' },
    options: withLetters([
      { id: 'a', label: '旅行，旅程' },
      { id: 'b', label: '作业，任务' },
      { id: 'c', label: '邻居，伙伴' },
      { id: 'd', label: '意见，建议' }
    ]),
    answerId: 'a',
    ability: 'listening'
  },
  {
    id: 'q-217',
    stageIds: ['stage-2', 'stage-3'],
    type: 'image_to_word',
    difficulty: 5,
    prompt: '看图选词',
    stem: '这个安排工具对应哪个词？',
    stemNote: '更长的词也可以分块来看。',
    image: { emoji: '🗓️', label: '日程表', tone: 'deep' },
    options: withLetters([
      { id: 'schedule', label: 'schedule' },
      { id: 'sticker', label: 'sticker' },
      { id: 'surface', label: 'surface' },
      { id: 'station', label: 'station' }
    ]),
    answerId: 'schedule',
    ability: 'recognition'
  },
  {
    id: 'q-301',
    stageIds: ['stage-3'],
    type: 'word_to_cn',
    difficulty: 5,
    prompt: '看英文选中文',
    stem: 'tradition',
    stemNote: '常见于文化和节日语境。',
    options: withLetters([
      { id: 'a', label: '传统' },
      { id: 'b', label: '顺序' },
      { id: 'c', label: '证据' },
      { id: 'd', label: '结论' }
    ]),
    answerId: 'a',
    ability: 'context'
  },
  {
    id: 'q-302',
    stageIds: ['stage-3'],
    type: 'image_to_word',
    difficulty: 5,
    prompt: '看图选词',
    stem: '图中的学习场景对应哪个词？',
    stemNote: '注意词和学科场景的联系。',
    image: { emoji: '🧪', label: '实验', tone: 'deep' },
    options: withLetters([
      { id: 'experiment', label: 'experiment' },
      { id: 'agreement', label: 'agreement' },
      { id: 'movement', label: 'movement' },
      { id: 'management', label: 'management' }
    ]),
    answerId: 'experiment',
    ability: 'recognition'
  },
  {
    id: 'q-303',
    stageIds: ['stage-3'],
    type: 'audio_choice',
    difficulty: 6,
    prompt: '听发音选答案',
    stem: '听后选出最贴近的动词意思。',
    stemNote: '保持节奏，不需要每题都很确定。',
    audio: { transcript: 'arrange', hint: '/əˈreɪndʒ/' },
    options: withLetters([
      { id: 'a', label: '安排，整理' },
      { id: 'b', label: '拒绝，反对' },
      { id: 'c', label: '怀疑，担心' },
      { id: 'd', label: '依赖，依靠' }
    ]),
    answerId: 'a',
    ability: 'listening'
  },
  {
    id: 'q-304',
    stageIds: ['stage-3'],
    type: 'word_to_cn',
    difficulty: 6,
    prompt: '看英文选中文',
    stem: 'influence',
    stemNote: '常见于人物、环境和结果之间的关系。',
    options: withLetters([
      { id: 'a', label: '影响' },
      { id: 'b', label: '趋势' },
      { id: 'c', label: '范围' },
      { id: 'd', label: '机会' }
    ]),
    answerId: 'a',
    ability: 'context'
  },
  {
    id: 'q-305',
    stageIds: ['stage-3'],
    type: 'word_to_cn',
    difficulty: 6,
    prompt: '看英文选中文',
    stem: 'efficient',
    stemNote: '这个词强调做事方式的效果。',
    options: withLetters([
      { id: 'a', label: '稳定的' },
      { id: 'b', label: '高效的' },
      { id: 'c', label: '熟悉的' },
      { id: 'd', label: '明显的' }
    ]),
    answerId: 'b',
    ability: 'context'
  }
];

function getQuestionBankByStage(stageId) {
  return QUESTION_BANK.filter((question) => question.stageIds.indexOf(stageId) > -1);
}

module.exports = {
  QUESTION_BANK,
  getQuestionBankByStage
};
