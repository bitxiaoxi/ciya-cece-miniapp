Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    playing: {
      type: Boolean,
      value: false
    },
    mode: {
      type: String,
      value: 'student'
    },
    transcript: {
      type: String,
      value: ''
    }
  },

  methods: {
    handleTap() {
      this.triggerEvent('play');
    }
  }
});
