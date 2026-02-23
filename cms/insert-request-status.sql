-- Insert lookup data for RequestStatus table
INSERT INTO RequestStatus (StatusCode, Description) VALUES
('PEND', 'Pending'),
('APPR', 'Approved'),
('RJCT', 'Rejected')
ON CONFLICT (StatusCode) DO NOTHING;

-- Verify the data
SELECT * FROM RequestStatus;
