import React from 'react'
import { BrowserRouter as Router, Routes, Route, Link, useLocation } from 'react-router-dom'
import CardsPage from './pages/CardsPage'
import RequestsPage from './pages/RequestsPage'
import ApproveRejectPage from './pages/ApproveRejectPage'

function Navigation() {
  const location = useLocation()

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <div className="navbar-brand">
          Card <span>Management</span> System
        </div>
        <ul className="nav-links">
          <li>
            <Link to="/" className={location.pathname === '/' ? 'active' : ''}>
              Cards
            </Link>
          </li>
          <li>
            <Link to="/requests" className={location.pathname === '/requests' ? 'active' : ''}>
              Requests
            </Link>
          </li>
          <li>
            <Link to="/approvals" className={location.pathname === '/approvals' ? 'active' : ''}>
              Approvals
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
