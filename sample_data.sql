-- Sample Room Data for Booking Form Testing

USE room_booking;

-- Clear existing data (optional - comment out if you want to keep existing data)
-- DELETE FROM booking;
-- DELETE FROM room;

-- Insert sample rooms
INSERT INTO room (room_type, sleep_slot, room_price, status, image_url, description)
VALUES 
('Family', 4, 2000000, 'AVAILABLE', 'https://via.placeholder.com/300x200?text=Family+Room', 
 'Spacious family room with 2 double beds, perfect for families with children'),
 
('Business', 2, 1500000, 'AVAILABLE', 'https://via.placeholder.com/300x200?text=Business+Room', 
 'Modern business room with work desk, high-speed internet, and city view'),
 
('Honey_Moon', 2, 2500000, 'AVAILABLE', 'https://via.placeholder.com/300x200?text=Honeymoon+Suite', 
 'Romantic honeymoon suite with king-size bed, Jacuzzi, and ocean view'),
 
('Family', 6, 3000000, 'AVAILABLE', 'https://via.placeholder.com/300x200?text=Large+Family+Room', 
 'Large family room with 3 double beds'),
 
('Business', 1, 1200000, 'UNAVAILABLE', 'https://via.placeholder.com/300x200?text=Single+Room', 
 'Single business room (currently under maintenance)');

-- View created rooms
SELECT * FROM room;

-- Sample booking data (optional - for testing list view)
INSERT INTO booking (guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status)
VALUES 
('Nguyễn Văn A', 'nguyenvana@gmail.com', 1, '2024-12-20', '2024-12-23', 6000000, 'PENDING'),
('Trần Thị B', 'tranthib@gmail.com', 2, '2024-12-25', '2024-12-28', 4500000, 'CONFIRMED'),
('Lê Văn C', 'levanc@gmail.com', 3, '2024-12-15', '2024-12-18', 7500000, 'COMPLETED');

-- View created bookings
SELECT * FROM booking;
