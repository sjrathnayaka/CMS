import React, { useState, useEffect } from 'react'
import { cardsApi, cardRequestsApi } from '../api/api'

function RequestsPage() {
  const [cards, setCards] = useState([])
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [selectedCard, setSelectedCard] = useState('')
  const [requestType, setRequestType] = useState('activate')
  const [remark, setRemark] = useState('')

  useEffect(() => {
    fetchCards()
    fetchRequests()
  }, [])

  const fetchCards = async () => {
    try {
      const response = await cardsApi.getAllCards()
      setCards(response.data)
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch cards'
      setError(errorMessage)
    }
  }

  const fetchRequests = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await cardRequestsApi.getAllRequestsWithDetails()
      setRequests(response.data)
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch requests'
      setError(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmitRequest = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccess(null)

    if (!selectedCard) {
      setError('Please select a card.')
      return
    }

    try {
      const requestData = {
        encryptedCardNumber: selectedCard,
        remark: remark || null,
      }

      if (requestType === 'activate') {
        await cardRequestsApi.requestActivation(requestData)
        setSuccess('Activation request submitted successfully.')
      } else {
        await cardRequestsApi.requestDeactivation(requestData)
        setSuccess('Deactivation request submitted successfully.')
      }

      setSelectedCard('')
      setRemark('')
      fetchRequests()
      fetchCards()

      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to submit request'
      setError(errorMessage)
    }
  }

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'PEND': return 'status-badge status-pending'
      case 'APPR': return 'status-badge status-approved'
      case 'RJCT': return 'status-badge status-rejected'
      default: return 'status-badge'
    }
  }

  const getStatusText = (status) => {
    switch (status) {
      case 'PEND': return 'Pending'
      case 'APPR': return 'Approved'
      case 'RJCT': return 'Rejected'
      default: return status
    }
  }

  const getCardStatusText = (status) => {
    switch (status) {
      case 'CACT': return 'Active'
      case 'IACT': return 'Inactive'
      case 'DACT': return 'Deactivated'
      default: return status
    }
  }

  const getRequestTypeText = (type) => {
    switch (type) {
      case 'ACTI': return 'Activation'
      case 'CDCL': return 'Deactivation'
      default: return type
    }
  }

  const formatDate = (dateString) =>
    new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })

  const maskCardNumber = (cardNumber) => {
    if (!cardNumber || cardNumber.length < 4) return cardNumber
    return 'XXXX-XXXX-XXXX-' + cardNumber.slice(-4)
  }

  return (
    <div>
      <div className="card">
        <h2>Submit Card Request</h2>

        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmitRequest}>
          <div className="form-group">
            <label htmlFor="requestType">Request Type *</label>
            <select
              id="requestType"
              value={requestType}
              onChange={(e) => setRequestType(e.target.value)}
            >
              <option value="activate">Activate Card</option>
              <option value="deactivate">Deactivate Card</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="cardSelect">Select Card *</label>
            <select
              id="cardSelect"
              value={selectedCard}
              onChange={(e) => setSelectedCard(e.target.value)}
            >
              <option value="">Select a card</option>
              {cards.map((card) => (
                <option key={card.encryptedCardNumber} value={card.encryptedCardNumber}>
                  {card.maskedCardNumber || maskCardNumber(card.cardNumber)} — {getCardStatusText(card.cardStatus)}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="remark">Remark (Optional)</label>
            <input
              type="text"
              id="remark"
              value={remark}
              onChange={(e) => setRemark(e.target.value)}
              placeholder="Enter any additional comments"
            />
          </div>

          <button type="submit" className="btn btn-primary">
            Submit Request
          </button>
        </form>
      </div>

      <div className="card">
        <h2>All Card Requests</h2>

        {loading ? (
          <div className="loading">Loading requests...</div>
        ) : requests.length === 0 ? (
          <div className="empty-state">
            <p>No requests found. Submit your first request above.</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Request ID</th>
                  <th>Card Number</th>
                  <th>Request Type</th>
                  <th>Card Status</th>
                  <th>Request Status</th>
                  <th>Remark</th>
                  <th>Created At</th>
                </tr>
              </thead>
              <tbody>
                {requests.map((request) => (
                  <tr key={request.requestId}>
                    <td>#{request.requestId}</td>
                    <td>
                      <code>{request.maskedCardNumber || maskCardNumber(request.cardNumber)}</code>
                    </td>
                    <td>{getRequestTypeText(request.requestReasonCode)}</td>
                    <td>{request.cardStatusDescription || getCardStatusText(request.cardStatus)}</td>
                    <td>
                      <span className={getStatusBadgeClass(request.requestStatusCode)}>
                        {getStatusText(request.requestStatusCode)}
                      </span>
                    </td>
                    <td>{request.remark || '—'}</td>
                    <td>{formatDate(request.createdTime)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default RequestsPage
