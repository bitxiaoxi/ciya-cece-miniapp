Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    text: {
      type: String,
      value: '青青课堂'
    },
    mode: {
      type: String,
      value: 'student'
    },
    align: {
      type: String,
      value: 'center'
    },
    muted: {
      type: Boolean,
      value: true
    }
  }
});
