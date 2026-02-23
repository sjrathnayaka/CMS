-- Card Management System Database Schema

DROP TABLE IF EXISTS CardRequest;
DROP TABLE IF EXISTS Card;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS CardRequestType;
DROP TABLE IF EXISTS RequestStatus;
DROP TABLE IF EXISTS CardStatus;

/* ----------------- CREATE CARD STATUS ----------------- */
CREATE TABLE CardStatus (
    StatusCode VARCHAR(20) PRIMARY KEY,
    Description VARCHAR(100) NOT NULL,
    CONSTRAINT chk_card_status_code CHECK (StatusCode IN ('IACT', 'CACT', 'DACT'))
);

/* ----------------- CREATE REQUEST STATUS ----------------- */
CREATE TABLE RequestStatus (
    StatusCode VARCHAR(20) PRIMARY KEY,
    Description VARCHAR(100) NOT NULL,
    CONSTRAINT chk_request_status_code CHECK (StatusCode IN ('PEND', 'APPR', 'RJCT'))
);

/* ----------------- CREATE CARD REQUEST TYPE ----------------- */
CREATE TABLE CardRequestType (
    Code VARCHAR(20) PRIMARY KEY,
    Description VARCHAR(100) NOT NULL,
    CONSTRAINT chk_request_type CHECK (Code IN ('ACTI', 'CDCL'))
);

/* ----------------- CREATE USERS TABLE ----------------- */
CREATE TABLE Users (
    Username VARCHAR(100) PRIMARY KEY,
    Name VARCHAR(150) NOT NULL,
    Status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_user_status
        FOREIGN KEY (Status)
        REFERENCES RequestStatus(StatusCode)
);

/* ----------------- CREATE CARD TABLE ----------------- */
CREATE TABLE Card (
    CardNumber VARCHAR(255) PRIMARY KEY,  -- Encrypted card number (AES-256)
    ExpiryDate DATE NOT NULL,
    CardStatus VARCHAR(20) NOT NULL DEFAULT 'IACT',
    CreditLimit DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    CashLimit DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    AvailableCreditLimit DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    AvailableCashLimit DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    LastUpdateTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LastUpdatedUser VARCHAR(100),
    CONSTRAINT chk_credit_limit CHECK (CreditLimit >= 0),
    CONSTRAINT chk_cash_limit CHECK (CashLimit >= 0),
    CONSTRAINT chk_available_credit CHECK (AvailableCreditLimit >= 0 AND AvailableCreditLimit <= CreditLimit),
    CONSTRAINT chk_available_cash CHECK (AvailableCashLimit >= 0 AND AvailableCashLimit <= CashLimit),
    CONSTRAINT fk_card_status
        FOREIGN KEY (CardStatus)
        REFERENCES CardStatus(StatusCode),
    CONSTRAINT fk_card_last_updated_user
        FOREIGN KEY (LastUpdatedUser)
        REFERENCES Users(Username)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

/* ----------------- CREATE CARD REQUEST TABLE ----------------- */
CREATE TABLE CardRequest (
    RequestId SERIAL PRIMARY KEY,
    CardNumber VARCHAR(255) NOT NULL,  -- Encrypted card number (AES-256)
    RequestReasonCode VARCHAR(20) NOT NULL,
    RequestStatusCode VARCHAR(20) DEFAULT 'PEND',
    Remark VARCHAR(500),
    CreatedTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ApprovedUser VARCHAR(100),
    RequestedUser VARCHAR(100),
    CONSTRAINT fk_request_card
        FOREIGN KEY (CardNumber)
        REFERENCES Card(CardNumber)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_request_type
        FOREIGN KEY (RequestReasonCode)
        REFERENCES CardRequestType(Code),
    CONSTRAINT fk_request_status
        FOREIGN KEY (RequestStatusCode)
        REFERENCES RequestStatus(StatusCode),
    CONSTRAINT fk_cardrequest_approved_user
        FOREIGN KEY (ApprovedUser)
        REFERENCES Users(Username)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_cardrequest_requested_user
        FOREIGN KEY (RequestedUser)
        REFERENCES Users(Username)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Insert initial lookup data
INSERT INTO CardStatus (StatusCode, Description) VALUES
('IACT', 'Card Inactive - Initial/Pending state'),
('CACT', 'Card Active - Normal active state'),
('DACT', 'Card Deactivated - Card has been deactivated');

INSERT INTO CardRequestType (Code, Description) VALUES
('ACTI', 'Card Activation Request'),
('CDCL', 'Card Close Request');

INSERT INTO RequestStatus (StatusCode, Description) VALUES
('PEND', 'Pending - Request awaiting approval'),
('APPR', 'Approved - Request has been approved'),
('RJCT', 'Rejected - Request has been rejected');

-- Insert sample users (status APPR = active/approved user)
INSERT INTO Users (Username, Name, Status) VALUES
('admin', 'System Administrator', 'APPR'),
('john.doe', 'John Doe', 'APPR'),
('jane.smith', 'Jane Smith', 'APPR');

-- Insert sample cards (encrypted card numbers using deterministic AES-256 with URL-safe Base64)
-- Plain card numbers: 4532015112830366, 5425233430109903, 6011111111111117, 378282246310005
INSERT INTO Card (CardNumber, ExpiryDate, CardStatus, CreditLimit, CashLimit, AvailableCreditLimit, AvailableCashLimit) VALUES
('QBPgp-CYj0ZpT_Li25AC-7dC_CuOj8RWW5AVHo2I3A4', '2027-12-31', 'CACT', 100000.00, 50000.00, 75000.00, 40000.00),
('yCga2dk7LjmC3S2vOCvSJ7dC_CuOj8RWW5AVHo2I3A4', '2028-06-30', 'CACT', 150000.00, 75000.00, 150000.00, 75000.00),
('xiJz4ficYPsR_BAKaEfmO7dC_CuOj8RWW5AVHo2I3A4', '2026-03-31', 'IACT', 80000.00, 40000.00, 80000.00, 40000.00),
('du-dYU0fjqVcte_5DcIxVQ', '2027-09-30', 'DACT', 200000.00, 100000.00, 120000.00, 60000.00);

-- Create indexes for better query performance
CREATE INDEX idx_card_status ON Card(CardStatus);
CREATE INDEX idx_card_expiry ON Card(ExpiryDate);
CREATE INDEX idx_request_card ON CardRequest(CardNumber);
CREATE INDEX idx_request_status ON CardRequest(RequestStatusCode);
