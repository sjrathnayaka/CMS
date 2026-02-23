import React, { useState, useEffect } from 'react'
import { cardsApi } from '../api/api'

function CardsPage() {
  const [cards, setCards] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [formData, setFormData] = useState({
    cardNumber: '',
    expiryDate: '',
    creditLimit: '',
    cashLimit: '',
  })
  const [formErrors, setFormErrors] = useState({})

  useEffect(() => {
    fetchCards()
  }, [])

  const fetchCards = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await cardsApi.getAllCards()
      setCards(response.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch cards')
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData({ ...formData, [name]: value })
    // Clear error for this field
    if (formErrors[name]) {
      setFormErrors({ ...formErrors, [name]: null })
    }
  }

  const validateForm = () => {
    const errors = {}
    
    if (!formData.cardNumber || !/^\d{13,16}$/.test(formData.cardNumber)) {
      errors.cardNumber = 'Card number must be 13-16 digits'
    }
    
    if (!formData.expiryDate) {
      errors.expiryDate = 'Expiry date is required'
    } else {
      const expiry = new Date(formData.expiryDate)
      if (expiry <= new Date()) {
        errors.expiryDate = 'Expiry date must be in the future'
      }
    }
    
    if (!formData.creditLimit || parseFloat(formData.creditLimit) <= 0) {
      errors.creditLimit = 'Credit limit must be greater than 0'
    }
    
    if (!formData.cashLimit || parseFloat(formData.cashLimit) <= 0) {
      errors.cashLimit = 'Cash limit must be greater than 0'
    }
    
    setFormErrors(errors)
    return Object.keys(errors).length === 0
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccess(null)
    
    if (!validateForm()) {
      return
    }

    try {
      const cardData = {
        cardNumber: formData.cardNumber,
        expiryDate: formData.expiryDate,
        creditLimit: parseFloat(formData.creditLimit),
        cashLimit: parseFloat(formData.cashLimit),
      }
      
      await cardsApi.createCard(cardData)
      setSuccess('Card added successfully!')
      setFormData({
        cardNumber: '',
        expiryDate: '',
        creditLimit: '',
        cashLimit: '',
      })
      fetchCards()
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(null), 3000)
    } catch (err) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.errors?.cardNumber ||
                          'Failed to add card'
      setError(errorMessage)
    }
  }

  const getStatusBadgeClass = (status) => {
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

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount)
  }

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })
  }

  const maskCardNumber = (cardNumber) => {
    if (!cardNumber || cardNumber.length < 4) return cardNumber
    return 'XXXX-XXXX-XXXX-' + cardNumber.slice(-4)
  }

  return (
    <div>
      <div className="card">
        <h2>➕ Add New Card</h2>
        
        {success && <div className="alert alert-success">{success}</div>}
        {error && <div className="alert alert-error">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="grid-2">
            <div className="form-group">
              <label htmlFor="cardNumber">Card Number *</label>
              <input
                type="text"
                id="cardNumber"
                name="cardNumber"
                value={formData.cardNumber}
                onChange={handleInputChange}
                placeholder="Enter 13-16 digit card number"
                maxLength="16"
              />
              {formErrors.cardNumber && (
                <div className="error">{formErrors.cardNumber}</div>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="expiryDate">Expiry Date *</label>
              <input
                type="date"
                id="expiryDate"
                name="expiryDate"
                value={formData.expiryDate}
                onChange={handleInputChange}
              />
              {formErrors.expiryDate && (
                <div className="error">{formErrors.expiryDate}</div>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="creditLimit">Credit Limit *</label>
              <input
                type="number"
                id="creditLimit"
                name="creditLimit"
                value={formData.creditLimit}
                onChange={handleInputChange}
                placeholder="Enter credit limit"
                step="0.01"
                min="0"
              />
              {formErrors.creditLimit && (
                <div className="error">{formErrors.creditLimit}</div>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="cashLimit">Cash Limit *</label>
              <input
                type="number"
                id="cashLimit"
                name="cashLimit"
                value={formData.cashLimit}
                onChange={handleInputChange}
                placeholder="Enter cash limit"
                step="0.01"
                min="0"
              />
              {formErrors.cashLimit && (
                <div className="error">{formErrors.cashLimit}</div>
              )}
            </div>
          </div>

          <button type="submit" className="btn btn-primary">
            ➕ Add Card
          </button>
        </form>
      </div>

      <div className="card">
        <h2>📋 All Cards</h2>
        
        {loading ? (
          <div className="loading">Loading cards...</div>
        ) : cards.length === 0 ? (
          <div className="empty-state">
            <p>No cards found. Add your first card above!</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Card Number</th>
                  <th>Expiry Date</th>
                  <th>Status</th>
                  <th>Credit Limit</th>
                  <th>Cash Limit</th>
                  <th>Available Credit</th>
                  <th>Available Cash</th>
                  <th>Last Updated</th>
                </tr>
              </thead>
              <tbody>
                {cards.map((card) => (
                  <tr key={card.encryptedCardNumber}>
                    <td>{card.maskedCardNumber || maskCardNumber(card.cardNumber)}</td>
                    <td>{formatDate(card.expiryDate)}</td>
                    <td>
                      <span className={getStatusBadgeClass(card.cardStatus)}>
                        {getStatusText(card.cardStatus)}
                      </span>
                    </td>
                    <td>{formatCurrency(card.creditLimit)}</td>
                    <td>{formatCurrency(card.cashLimit)}</td>
                    <td>{formatCurrency(card.availableCreditLimit)}</td>
                    <td>{formatCurrency(card.availableCashLimit)}</td>
                    <td>{formatDate(card.lastUpdateTime)}</td>
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

export default CardsPage
