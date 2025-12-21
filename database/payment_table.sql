-- Payment table for VNPay integration
CREATE TABLE IF NOT EXISTS payment (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    transaction_code VARCHAR(100) UNIQUE NOT NULL,
    vnp_txn_ref VARCHAR(100),
    amount DECIMAL(12,2) NOT NULL,
    status ENUM('PENDING','SUCCESS','FAILED','EXPIRED') DEFAULT 'PENDING',
    -- Booking info (stored until payment complete)
    guest_name VARCHAR(255) NOT NULL,
    guest_email VARCHAR(255) NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    -- After payment success
    booking_id INT NULL,
    vnp_response_code VARCHAR(10),
    vnp_transaction_no VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    FOREIGN KEY (room_id) REFERENCES room(room_id),
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);
