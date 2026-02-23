import React, { useState, useEffect } from 'react'
import { cardRequestsApi } from '../api/api'

function ApproveRejectPage() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [filterStatus, setFilterStatus] = useState('all')
  const [remarkModal, setRemarkModal] = useState({ show: false, requestId: null, action: null })
  const [remark, setRemark] = useState('')
  const [approvedUser, setApprovedUser] = useState('')

  useEffect(() => { fetchRequests() }, [])

  const fetchRequests = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await cardRequestsApi.getAllRequestsWithDetails()
      setRequests(response.data)
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to fetch requests')
    } finally {
      setLoading(false)
    }
  }

  const handleApprove = async (requestId) => {
    try {
      setError(null)
      setSuccess(null)
      await cardRequestsApi.approveRequest(requestId, remark || null, approvedUser.trim() || null)
      setSuccess(`Request #${requestId} approved successfully.`)
      setRemarkModal({ show: false, requestId: null, action: null })
      setRemark('')
      setApprovedUser('')
      fetchRequests()
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to approve request')
    }
  }

  const handleReject = async (requestId) => {
    try {
      setError(null)
      setSuccess(null)
      await cardRequestsApi.rejectRequest(requestId, remark || null, approvedUser.trim() || null)
      setSuccess(`Request #${requestId} rejected.`)
      setRemarkModal({ show: false, requestId: null, action: null })
      setRemark('')
      setApprovedUser('')
      fetchRequests()
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to reject request')
    }
  }

  const openRemarkModal = (requestId, action) => {
    setRemarkModal({ show: true, requestId, action })
    setRemark('')
    setApprovedUser('')
  }

  const closeRemarkModal = () => {
    setRemarkModal({ show: false, requestId: null, action: null })
    setRemark('')
    setApprovedUser('')
  }

  const handleModalSubmit = () => {
    if (remarkModal.action === 'approve') handleApprove(remarkModal.requestId)
    else if (remarkModal.action === 'reject') handleReject(remarkModal.requestId)
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
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit',
    })

  const filteredRequests = requests.filter((request) =>
    filterStatus === 'all' ? true : request.requestStatusCode === filterStatus
  )

  return (
    <div>
      <div className="card">
        <h2>Approve / Reject Requests</h2>
        <p className="subtitle">Review and process pending card requests.</p>

        {error && <div className="alert alert-error"><strong>Error:</strong> {error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="filter-section">
          <label htmlFor="statusFilter">Filter by Status:</label>
          <select
            id="statusFilter" value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="form-select"
          >
            <option value="all">All Requests</option>
            <option value="PEND">Pending</option>
            <option value="APPR">Approved</option>
            <option value="RJCT">Rejected</option>
          </select>
        </div>
      </div>

      <div className="card">
        <h3>Requests ({filteredRequests.length})</h3>

        {loading ? (
          <div className="loading">Loading requests...</div>
        ) : filteredRequests.length === 0 ? (
          <div className="no-data"><p>No requests found.</p></div>
        ) : (
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Request ID</th>
                  <th>Card Number</th>
                  <th>Request Type</th>
                  <th>Status</th>
                  <th>Card Status</th>
                  <th>Requested By</th>
                  <th>Approved By</th>
                  <th>Remark</th>
                  <th>Created</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRequests.map((request) => (
                  <tr key={request.requestId}>
                    <td>#{request.requestId}</td>
                    <td><code>{request.maskedCardNumber}</code></td>
                    <td>
                      <span className={`request-type-badge ${request.requestReasonCode === 'ACTI' ? 'type-activation' : 'type-deactivation'}`}>
                        {getRequestTypeText(request.requestReasonCode)}
                      </span>
                    </td>
                    <td>
                      <span className={getStatusBadgeClass(request.requestStatusCode)}>
                        {getStatusText(request.requestStatusCode)}
                      </span>
                    </td>
                    <td>
                      <span className={`card-status-badge ${request.cardStatus === 'CACT' ? 'card-active' : request.cardStatus === 'IACT' ? 'card-inactive' : 'card-deactivated'}`}>
                        {getCardStatusText(request.cardStatus)}
                      </span>
                    </td>
                    <td>{request.requestedUser || '—'}</td>
                    <td>{request.approvedUser || '—'}</td>
                    <td><span className="remark-text">{request.remark || '—'}</span></td>
                    <td>{formatDate(request.createdTime)}</td>
                    <td>
                      {request.requestStatusCode === 'PEND' ? (
                        <div className="action-buttons">
                          <button onClick={() => openRemarkModal(request.requestId, 'approve')} className="btn btn-approve" title="Approve this request">Approve</button>
                          <button onClick={() => openRemarkModal(request.requestId, 'reject')} className="btn btn-reject" title="Reject this request">Reject</button>
                        </div>
                      ) : (
                        <span className="processed-text">
                          {request.requestStatusCode === 'APPR' ? 'Approved' : 'Rejected'}
                        </span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {remarkModal.show && (
        <div className="modal-overlay" onClick={closeRemarkModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{remarkModal.action === 'approve' ? 'Approve Request' : 'Reject Request'}</h3>
              <button onClick={closeRemarkModal} className="modal-close" aria-label="Close">&times;</button>
            </div>
            <div className="modal-body">
              <p>
                You are about to <strong>{remarkModal.action}</strong> Request{' '}
                <strong>#{remarkModal.requestId}</strong>.
              </p>
              <label htmlFor="approvedUserInput">Your Username (optional)</label>
              <input
                id="approvedUserInput"
                type="text"
                value={approvedUser}
                onChange={(e) => setApprovedUser(e.target.value)}
                placeholder="Enter your username..."
                style={{ marginBottom: '0.75rem' }}
              />
              <label htmlFor="remarkInput">Remark (optional)</label>
              <textarea
                id="remarkInput"
                value={remark}
                onChange={(e) => setRemark(e.target.value)}
                placeholder={`Enter reason for ${remarkModal.action}...`}
                rows="4"
                className="form-textarea"
              />
            </div>
            <div className="modal-footer">
              <button onClick={closeRemarkModal} className="btn btn-secondary">Cancel</button>
              <button
                onClick={handleModalSubmit}
                className={remarkModal.action === 'approve' ? 'btn btn-approve' : 'btn btn-reject'}
              >
                {remarkModal.action === 'approve' ? 'Confirm Approve' : 'Confirm Reject'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default ApproveRejectPage
