# Card Management System - Frontend

A modern React.js frontend application for managing credit cards and card requests.

## Features

### Page 1: Cards Management
- ➕ Add new credit cards with validation
- 📋 View all cards in a responsive table
- Display card details including limits, status, and timestamps
- Real-time form validation
- Masked card numbers for security

### Page 2: Request Management
- 📝 Submit activation/deactivation requests
- 📊 View all card requests with detailed information
- Filter cards by status
- Track request status (Pending/Approved/Rejected)

### Page 3: Approval Management
- ✅ View all requests with complete details
- 🔍 Filter by request status (All/Pending/Approved/Rejected)
- Display card financial information
- Request status tracking and descriptions

## Technologies

- **React 19.2.4** - UI library
- **React Router DOM 7** - Routing
- **Axios** - HTTP client
- **Vite** - Build tool and dev server

## Installation

1. Install dependencies:
\`\`\`bash
npm install
\`\`\`

## Running the Application

1. Make sure the backend is running on `http://localhost:8080`

2. Start the development server:
\`\`\`bash
npm run dev
\`\`\`

3. Open your browser and navigate to:
\`\`\`
http://localhost:3000
\`\`\`

## Build for Production

\`\`\`bash
npm run build
\`\`\`

## API Configuration

The frontend is configured to connect to the backend API at `http://localhost:8080/api`.

If you need to change this, edit `src/api/api.js`:

\`\`\`javascript
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  // ...
})
\`\`\`

## Project Structure

\`\`\`
cms-frontend/
├── src/
│   ├── api/
│   │   └── api.js              # API configuration and endpoints
│   ├── pages/
│   │   ├── CardsPage.jsx       # Page 1: Card management
│   │   ├── RequestsPage.jsx    # Page 2: Request submission
│   │   └── ApprovalPage.jsx    # Page 3: Request approval view
│   ├── App.jsx                 # Main app component with routing
│   ├── main.jsx                # App entry point
│   └── index.css               # Global styles
├── index.html                  # HTML template
├── vite.config.js              # Vite configuration
└── package.json                # Dependencies
\`\`\`

## Features Overview

### Card Management (Page 1)
- Add cards with automatic validation
- View all cards in a sortable table
- Automatic status badges with color coding
- Currency formatting for limits
- Date formatting
- Card number masking (XXXX-XXXX-XXXX-1234)

### Request Management (Page 2)
- Submit activation/deactivation requests
- Select from existing cards
- Add optional remarks
- Real-time request status updates
- Request history with full details

### Approval Management (Page 3)
- View all requests with filtering
- Filter by status (Pending/Approved/Rejected)
- Complete card financial information
- Request status descriptions
- Auto-approval/rejection based on backend rules

## Status Codes

### Card Status
- **IACT** - Inactive (new cards)
- **CACT** - Active
- **DACT** - Deactivated

### Request Status
- **PEND** - Pending
- **APPR** - Approved
- **RJCT** - Rejected

### Request Types
- **ACTI** - Activation
- **CDCL** - Deactivation

## Notes

- The backend automatically approves/rejects requests based on business rules
- Deactivation is only allowed for active cards with no outstanding balance
- Activation can be done for inactive or deactivated cards
- All card numbers are encrypted in the backend
- Frontend displays masked card numbers for security
