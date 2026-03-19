Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    mode: {
      type: String,
      value: 'student'
    },
    panel: {
      type: Object,
      value: {}
    }
  },

  methods: {
    handleContinue() {
      this.triggerEvent('continue');
    }
  }
});
