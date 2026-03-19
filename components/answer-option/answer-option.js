Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    option: {
      type: Object,
      value: {}
    },
    state: {
      type: String,
      value: 'default'
    },
    mode: {
      type: String,
      value: 'student'
    }
  },

  methods: {
    handleTap() {
      this.triggerEvent('select', {
        optionId: this.properties.option.id
      });
    }
  }
});
