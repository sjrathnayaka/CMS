import React, { useState, useEffect } from 'react'
import { cardRequestsApi } from '../api/api'

function ApprovalPage() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [filter, setFilter] = useState('all') // all, pending, approved, rejected

  useEffect(() => {
    fetchRequests()
  }, [])

  const fetchRequests = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await cardRequestsApi.getAllRequestsWithDetails()
      setRequests(response.data)
    } catch (err) {
      setError('Failed to fetch requests')
    } finally {
      setLoading(false)
    }
  }

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'PEND':
        return 'status-badge status-pending'
      case 'APPR':
        return 'status-badge status-approved'
      case 'RJCT':
        return 'status-badge status-rejected'
      default:
        return 'status-badge'
    }
  }

  const getCardStatusBadgeClass = (status) => {
    switch (status) {
      case 'CACT':
        return 'status-badge status-active'
      case 'IACT':
        return 'status-badge status-inactive'
      case 'DACT':
        return 'status-badge status-deactivated'
      default:
        return 'status-badge'
    }
  }

  const getStatusText = (status) => {
    switch (status) {
      case 'PEND':
        return 'Pending'
      case 'APPR':
        return 'Approved'
      case 'RJCT':
        return 'Rejected'
      default:
        return status
    }
  }

  const getCardStatusText = (status) => {
    switch (status) {
      case 'CACT':
        return 'Active'
      case 'IACT':
        return 'Inactive'
      case 'DACT':
        return 'Deactivated'
      default:
        return status
    }
  }

  const getRequestTypeText = (type) => {
    switch (type) {
      case 'ACTI':
        return 'Activation'
      case 'CDCL':
        return 'Deactivation'
      default:
        return type
    }
  }

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  const formatCurrency = (amount) => {
    if (!amount) return '-'
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount)
  }

  const maskCardNumber = (cardNumber) => {
    if (!cardNumber || cardNumber.length < 4) return cardNumber
    return 'XXXX-XXXX-XXXX-' + cardNumber.slice(-4)
  }

  const filteredRequests = requests.filter((request) => {
    if (filter === 'all') return true
    if (filter === 'pending') return request.requestStatusCode === 'PEND'
    if (filter === 'approved') return request.requestStatusCode === 'APPR'
    if (filter === 'rejected') return request.requestStatusCode === 'RJCT'
    return true
  })

  const pendingCount = requests.filter(r => r.requestStatusCode === 'PEND').length
  const approvedCount = requests.filter(r => r.requestStatusCode === 'APPR').length
  const rejectedCount = requests.filter(r => r.requestStatusCode === 'RJCT').length

  return (
    <div>
      <div className="card">
        <h2>✅ Request Approval Management</h2>
        
        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}
        
        <div className="alert alert-info">
          <strong>Note:</strong> This page displays all card requests with their current status. 
          In your backend system, requests are automatically approved/rejected based on business rules 
          (e.g., deactivation only for active cards with no balance).
        </div>

        <div style={{ marginBottom: '1.5rem', display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
          <button
            className={`btn ${filter === 'all' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setFilter('all')}
            style={filter !== 'all' ? { background: '#e2e8f0', color: '#2d3748' } : {}}
          >
            All Requests ({requests.length})
          </button>
          <button
            className={`btn ${filter === 'pending' ? 'btn-warning' : 'btn-secondary'}`}
            onClick={() => setFilter('pending')}
            style={filter !== 'pending' ? { background: '#e2e8f0', color: '#2d3748' } : {}}
          >
            ⏳ Pending ({pendingCount})
          </button>
          <button
            className={`btn ${filter === 'approved' ? 'btn-success' : 'btn-secondary'}`}
            onClick={() => setFilter('approved')}
            style={filter !== 'approved' ? { background: '#e2e8f0', color: '#2d3748' } : {}}
          >
            ✅ Approved ({approvedCount})
          </button>
          <button
            className={`btn ${filter === 'rejected' ? 'btn-danger' : 'btn-secondary'}`}
            onClick={() => setFilter('rejected')}
            style={filter !== 'rejected' ? { background: '#e2e8f0', color: '#2d3748' } : {}}
          >
            ❌ Rejected ({rejectedCount})
          </button>
        </div>
      </div>

      <div className="card">
        <h2>📋 Request Details</h2>
        
        {loading ? (
          <div className="loading">Loading requests...</div>
        ) : filteredRequests.length === 0 ? (
          <div className="empty-state">
            <p>No {filter !== 'all' ? filter : ''} requests found.</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Request ID</th>
                  <th>Card Number</th>
                  <th>Type</th>
                  <th>Current Card Status</th>
                  <th>Credit Limit</th>
                  <th>Available Credit</th>
                  <th>Cash Limit</th>
                  <th>Available Cash</th>
                  <th>Request Status</th>
                  <th>Status Description</th>
                  <th>Remark</th>
                  <th>Created At</th>
                </tr>
              </thead>
              <tbody>
                {filteredRequests.map((request) => (
                  <tr key={request.requestId}>
                    <td>#{request.requestId}</td>
                    <td>{request.maskedCardNumber || maskCardNumber(request.cardNumber)}</td>
                    <td>
                      <strong>{getRequestTypeText(request.requestReasonCode)}</strong>
                    </td>
                    <td>
                      <span className={getCardStatusBadgeClass(request.cardStatus)}>
                        {getCardStatusText(request.cardStatus)}
                      </span>
                    </td>
                    <td>{formatCurrency(request.creditLimit)}</td>
                    <td>{formatCurrency(request.availableCreditLimit)}</td>
                    <td>{formatCurrency(request.cashLimit)}</td>
                    <td>{formatCurrency(request.availableCashLimit)}</td>
                    <td>
                      <span className={getStatusBadgeClass(request.requestStatusCode)}>
                        {getStatusText(request.requestStatusCode)}
                      </span>
                    </td>
                    <td>
                      <small>{request.requestStatusDescription || '-'}</small>
                    </td>
                    <td>{request.remark || '-'}</td>
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

export default ApprovalPage
