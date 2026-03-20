const STORAGE_KEY = 'vocab_assessment_backend_session';

function getSession() {
  return wx.getStorageSync(STORAGE_KEY) || null;
}

function saveSession(session) {
  wx.setStorageSync(STORAGE_KEY, session);
  return session;
}

function patchSession(patch) {
  const current = getSession() || {};
  const next = Object.assign({}, current, patch);
  return saveSession(next);
}

function clearSession() {
  wx.removeStorageSync(STORAGE_KEY);
}

module.exports = {
  clearSession,
  getSession,
  patchSession,
  saveSession
};
