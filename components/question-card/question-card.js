Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    question: {
      type: Object,
      value: {}
    },
    mode: {
      type: String,
      value: 'student'
    }
  }
});
