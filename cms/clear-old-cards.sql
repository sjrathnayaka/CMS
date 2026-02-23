-- Clear old unencrypted card data
-- Run this script using pgAdmin or psql command line
-- psql -U postgres -d cms_db -f clear-old-cards.sql

-- Delete all existing cards (old unencrypted data)
DELETE FROM Card;

-- Verify the table is empty
SELECT COUNT(*) as card_count FROM Card;

-- You can now test with fresh encrypted cards using the API
