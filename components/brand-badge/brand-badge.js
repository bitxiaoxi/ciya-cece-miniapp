Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    variant: {
      type: String,
      value: 'compact'
    },
    text: {
      type: String,
      value: '青青课堂出品'
    },
    mode: {
      type: String,
      value: 'student'
    }
  }
});
