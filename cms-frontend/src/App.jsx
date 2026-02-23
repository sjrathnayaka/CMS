import React from 'react'
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom'
import CardsPage from './pages/CardsPage'
import RequestsPage from './pages/RequestsPage'
import ApproveRejectPage from './pages/ApproveRejectPage'

function Navigation() {
  const location = useLocation()
  
  return (
    <nav className="navbar">
      <div className="container">
        <h1>💳 Card Management System</h1>
        <ul className="nav-links">
          <li>
            <Link to="/" className={location.pathname === '/' ? 'active' : ''}>
              Cards Management
            </Link>
          </li>
          <li>
            <Link to="/requests" className={location.pathname === '/requests' ? 'active' : ''}>
              Request Management
            </Link>
          </li>
          <li>
            <Link to="/approvals" className={location.pathname === '/approvals' ? 'active' : ''}>
              Approval Management
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  )
}

function App() {
  return (
    <Router>
      <Navigation />
      <div className="container">
        <Routes>
          <Route path="/" element={<CardsPage />} />
          <Route path="/requests" element={<RequestsPage />} />
          <Route path="/approvals" element={<ApproveRejectPage />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App
