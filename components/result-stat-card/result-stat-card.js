Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    result: {
      type: Object,
      value: {}
    },
    mode: {
      type: String,
      value: 'student'
    }
  }
});
