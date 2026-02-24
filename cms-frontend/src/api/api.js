import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Cards API
export const cardsApi = {
  // Create a new card (plain — kept for internal use)
  createCard: (cardData) => api.post('/cards', cardData),

  // Create a new card with RSA-encrypted fields
  createCardEncrypted: (encryptedData) => api.post('/cards/encrypted', encryptedData),

  // Get all cards
  getAllCards: () => api.get('/cards'),

  // Get card by card number
  getCardByNumber: (cardNumber) => api.get(`/cards/number/${cardNumber}`),

  // Update card
  updateCard: (cardNumber, cardData) => api.put(`/cards/${cardNumber}`, cardData),
}

// Card Requests API
export const cardRequestsApi = {
  // Create deactivation request
  requestDeactivation: (requestData) => api.post('/card-requests/deactivate', requestData),

  // Create activation request
  requestActivation: (requestData) => api.post('/card-requests/activate', requestData),

  // Get all requests
  getAllRequests: () => api.get('/card-requests'),

  // Get all requests with detailed information
  getAllRequestsWithDetails: () => api.get('/card-requests/detailed'),

  // Get requests by card number
  getRequestsByCard: (cardNumber) => api.get(`/card-requests/card/${cardNumber}`),

  // Get detailed requests by card number
  getRequestsByCardWithDetails: (cardNumber) =>
    api.get(`/card-requests/detailed/card/${cardNumber}`),

  // Approve a request
  approveRequest: (requestId, remark, approvedUser) =>
    api.put(`/card-requests/${requestId}/approve`, { remark, approvedUser }),

  // Reject a request
  rejectRequest: (requestId, remark, approvedUser) =>
    api.put(`/card-requests/${requestId}/reject`, { remark, approvedUser }),
}

// Encryption API
export const encryptionApi = {
  // Get the RSA public key (PEM format) for encrypting card data
  getPublicKey: () => api.get('/encryption/public-key'),
}

// Reports API
export const reportsApi = {
  downloadCardsPdf: () =>
    api.get('/reports/cards/pdf', { responseType: 'arraybuffer' }),
  downloadCardsCsv: () =>
    api.get('/reports/cards/csv', { responseType: 'arraybuffer' }),
  downloadCardRequestsPdf: () =>
    api.get('/reports/card-requests/pdf', { responseType: 'arraybuffer' }),
  downloadCardRequestsCsv: () =>
    api.get('/reports/card-requests/csv', { responseType: 'arraybuffer' }),
  downloadApprovalsPdf: () =>
    api.get('/reports/approvals/pdf', { responseType: 'arraybuffer' }),
  downloadApprovalsCsv: () =>
    api.get('/reports/approvals/csv', { responseType: 'arraybuffer' }),
  downloadAuditPdf: () =>
    api.get('/reports/audit/pdf', { responseType: 'arraybuffer' }),
  downloadAuditCsv: () =>
    api.get('/reports/audit/csv', { responseType: 'arraybuffer' }),
}

export default api
