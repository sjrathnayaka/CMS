# Quick Start - Approve/Reject Page

## 🚀 Start the Application

### 1. Start Backend (in first terminal):
```bash
cd d:\epic\cms
.\restart-backend.bat
```

### 2. Start Frontend (in second terminal):
```bash
cd d:\epic\cms-frontend
npm run dev
```

### 3. Open Browser:
```
http://localhost:5173
```

---

## 🎯 Test the New Approve/Reject Page

### Step 1: Create a Card
1. Go to **"Cards Management"** page (first tab)
2. Fill in card details:
   - Card Number: `4532123456789012`
   - Expiry Date: `2028-12-31`
   - Credit Limit: `50000`
   - Cash Limit: `10000`
3. Click **"Create Card"**
4. Card will be created with status `IACT` (Inactive)

### Step 2: Create Activation Request
1. Go to **"Request Management"** page (second tab)
2. Select the card you just created
3. Choose **"Activation"** request type
4. Add remark: `Customer requested activation`
5. Click **"Submit Request"**
6. Request will be created with status `PEND` (Pending)

### Step 3: Approve the Request ✨ (NEW PAGE)
1. Go to **"Approval Management"** page (third tab) ✨
2. You'll see the pending request in the table
3. Click **"✓ Approve"** button
4. Modal will pop up
5. Optionally add remark: `Approved by manager`
6. Click **"Confirm Approve"**
7. Success! ✅
   - Request status changes to `APPR` (Approved)
   - Card status changes to `CACT` (Active)

---

## 🎨 Page Features

### Filter Requests:
- **All Requests** - Shows everything
- **Pending Only** - Shows only actionable requests ⭐
- **Approved Only** - Shows approved requests
- **Rejected Only** - Shows rejected requests

### Request Information:
- Request ID
- Masked Card Number
- Request Type (Activation/Deactivation)
- Request Status (Pending/Approved/Rejected)
- Card Status (Active/Inactive/Deactivated)
- Remark
- Created Time

### Actions:
- **✓ Approve** - Green button (only for pending requests)
- **✗ Reject** - Red button (only for pending requests)
- Already processed requests show status text

---

## 🧪 Additional Test Scenarios

### Scenario 1: Reject a Request
1. Create another activation request
2. Go to Approval Management page
3. Click **"✗ Reject"**
4. Add remark: `Insufficient documentation`
5. Confirm
6. Request rejected (card status unchanged)

### Scenario 2: Filter Pending Requests
1. Create multiple requests
2. Go to Approval Management page
3. Select **"Pending Only"** filter
4. Only pending requests shown
5. Quick approval workflow

### Scenario 3: View History
1. Filter to **"Approved Only"**
2. See all approved requests
3. Filter to **"Rejected Only"**
4. See all rejected requests

---

## ✅ What to Look For

### Visual Elements:
- ✅ Color-coded status badges
- ✅ Green approve button / Red reject button
- ✅ Modal popup for confirmation
- ✅ Success/error messages
- ✅ Auto-refresh after actions

### Functionality:
- ✅ Only pending requests show action buttons
- ✅ Approved/rejected requests show status text
- ✅ Filter works correctly
- ✅ Modal opens/closes properly
- ✅ Remark is optional
- ✅ List refreshes after approval/rejection

---

## 🎊 Success Indicators

After approving an activation request:
1. ✅ Success message appears
2. ✅ Request status badge changes to green "Approved"
3. ✅ Card status badge changes to green "Active"
4. ✅ Action buttons disappear
5. ✅ "Already Approved" text appears

After rejecting a request:
1. ✅ Success message appears
2. ✅ Request status badge changes to red "Rejected"
3. ✅ Card status badge stays the same (unchanged)
4. ✅ Action buttons disappear
5. ✅ "Already Rejected" text appears

---

## 🎯 Quick Navigation

The application now has **3 distinct pages**:

1. **Cards Management** (`/`)
   - Create, view, update cards
   - See all cards in table

2. **Request Management** (`/requests`)
   - Create activation/deactivation requests
   - View request history

3. **Approval Management** (`/approvals`) ✨ NEW
   - View all requests with filters
   - Approve pending requests
   - Reject pending requests
   - Add optional remarks

---

**Ready to test! 🚀**
