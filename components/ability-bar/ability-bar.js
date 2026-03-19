Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    ability: {
      type: Object,
      value: {}
    },
    mode: {
      type: String,
      value: 'student'
    }
  }
});
