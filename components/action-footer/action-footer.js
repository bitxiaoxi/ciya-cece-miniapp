Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    primaryText: {
      type: String,
      value: '继续'
    },
    primaryDisabled: {
      type: Boolean,
      value: false
    },
    secondaryActions: {
      type: Array,
      value: []
    },
    showPrimary: {
      type: Boolean,
      value: true
    }
  },

  methods: {
    handlePrimaryTap() {
      if (this.properties.primaryDisabled) {
        return;
      }
      this.triggerEvent('primarytap');
    },

    handleSecondaryTap(event) {
      const { key } = event.currentTarget.dataset;
      this.triggerEvent('secondarytap', { key });
    }
  }
});
