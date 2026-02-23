import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Cards API
export const cardsApi = {
  // Create a new card
  createCard: (cardData) => api.post('/cards', cardData),
  
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
  getRequestsByCardWithDetails: (cardNumber) => api.get(`/card-requests/detailed/card/${cardNumber}`),
  
  // Approve a request
  approveRequest: (requestId, remark) => api.put(`/card-requests/${requestId}/approve`, { remark }),
  
  // Reject a request
  rejectRequest: (requestId, remark) => api.put(`/card-requests/${requestId}/reject`, { remark }),
}

export default api
