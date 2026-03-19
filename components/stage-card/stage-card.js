Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    stage: {
      type: Object,
      value: {}
    },
    selected: {
      type: Boolean,
      value: false
    }
  },

  methods: {
    handleSelect() {
      this.triggerEvent('select', { stageId: this.properties.stage.id });
    }
  }
});
