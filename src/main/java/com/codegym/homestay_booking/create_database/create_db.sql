create database room_booking;

use room_booking;

create table room (
	room_id int primary key auto_increment,
    room_type enum("Family","Business","Honey Moon") default "Family",
    sleep_slot int not null,
    room_price float not null,
    status enum("AVAILABLE","UNAVAILABLE") default "AVAILABLE",
    image_url varchar(255),
    description varchar(200)
);

create table booking (
	booking_id int primary key auto_increment,
    guest_name varchar(200) not null,
    guest_email varchar(200) not null,
    room_id int not null,
    check_in_date date not null,
    check_out_date date not null,
    total_price float not null,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'CANCELLED REQUEST') default 'PENDING',
    constraint fk_booking_room foreign key (room_id) references room(room_id)
);

insert into room(room_type, sleep_slot, room_price, status, image_url, description) values
("Business", 20, 2000000, "AVAILABLE", "https://cf.bstatic.com/xdata/images/hotel/max1024x768/604733110.jpg?k=645f46fa90d19a09ec2313fd1c7d99ca784d5d4cd57b789d7011489ecb9b93de&o=", "A luxury room with beautifull view to the sea"),
("Family", 10, 2000000, "UNAVAILABLE", "https://cf.bstatic.com/xdata/images/hotel/max1024x768/604733110.jpg?k=645f46fa90d19a09ec2313fd1c7d99ca784d5d4cd57b789d7011489ecb9b93de&o=", "A luxury room with beautifull view to the sea"),
("Honey moon", 2, 2000000, "AVAILABLE", "https://cf.bstatic.com/xdata/images/hotel/max1024x768/604733110.jpg?k=645f46fa90d19a09ec2313fd1c7d99ca784d5d4cd57b789d7011489ecb9b93de&o=", "A luxury room with beautifull view to the sea");
insert into room(room_type, sleep_slot, room_price, status, image_url, description) values
("Honey moon", 2, 2000000, "AVAILABLE", "https://cf.bstatic.com/xdata/images/hotel/max1024x768/604733110.jpg?k=645f46fa90d19a09ec2313fd1c7d99ca784d5d4cd57b789d7011489ecb9b93de&o=", "A luxury room with beautifull view to the sea");

insert into booking(guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status) values
("mr.a","a@gmail.com",1,"2025/12/17","2025/12/18",1000000,"PENDING"),
("mr.b","b@gmail.com",2,"2025/12/17","2025/12/18",1000000,"PENDING"),
("mr.c","c@gmail.com",3,"2025/12/17","2025/12/18",1000000,"PENDING");


-- Tháng 4
INSERT INTO booking VALUES
(NULL,'Anh Minh','minh@gmail.com',1,'2025-04-10','2025-04-14',8000000,'COMPLETED'),
(NULL,'Chị Lan','lan@gmail.com',7,'2025-04-15','2025-04-18',6000000,'COMPLETED'),
(NULL,'Mr John','john@gmail.com',6,'2025-04-20','2025-04-25',25000000,'COMPLETED');

-- Tháng 5
INSERT INTO booking VALUES
(NULL,'Gia Huy','huy@gmail.com',1,'2025-05-05','2025-05-10',10000000,'COMPLETED'),
(NULL,'Thu Hà','ha@gmail.com',7,'2025-05-12','2025-05-15',6000000,'CONFIRMED'),
(NULL,'David','david@gmail.com',6,'2025-05-18','2025-05-22',20000000,'COMPLETED');

-- Tháng 6
INSERT INTO booking VALUES
(NULL,'Family Nguyen','family@gmail.com',7,'2025-06-01','2025-06-06',10000000,'COMPLETED'),
(NULL,'Tony','tony@gmail.com',6,'2025-06-10','2025-06-15',25000000,'COMPLETED'),
(NULL,'Mr Lee','lee@gmail.com',1,'2025-06-20','2025-06-24',8000000,'CONFIRMED');

-- Tháng 7
INSERT INTO booking VALUES
(NULL,'Honeymoon Kim','kim@gmail.com',3,'2025-07-05','2025-07-10',10000000,'COMPLETED'),
(NULL,'Honeymoon Linh','linh@gmail.com',4,'2025-07-12','2025-07-15',6000000,'COMPLETED'),
(NULL,'Business Team','biz@gmail.com',6,'2025-07-18','2025-07-25',35000000,'CONFIRMED');

-- Tháng 8
INSERT INTO booking VALUES
(NULL,'Family Tran','tran@gmail.com',7,'2025-08-03','2025-08-08',10000000,'COMPLETED'),
(NULL,'Mr Alex','alex@gmail.com',1,'2025-08-10','2025-08-14',8000000,'COMPLETED'),
(NULL,'Mr Ken','ken@gmail.com',6,'2025-08-20','2025-08-26',30000000,'COMPLETED');

INSERT INTO booking VALUES
(NULL,'Couple A','a@gmail.com',3,'2025-01-10','2025-01-13',6000000,'COMPLETED'),
(NULL,'Couple B','b@gmail.com',4,'2025-02-14','2025-02-17',6000000,'COMPLETED'),
(NULL,'Couple C','c@gmail.com',5,'2025-03-05','2025-03-08',3703710,'CONFIRMED');

INSERT INTO booking VALUES
(NULL,'LowSeason A','lsa@gmail.com',3,'2025-09-10','2025-09-12',4000000,'CANCELLED'),
(NULL,'LowSeason B','lsb@gmail.com',4,'2025-10-05','2025-10-07',4000000,'CANCELLED'),
(NULL,'LowSeason C','lsc@gmail.com',5,'2025-11-01','2025-11-03',2469140,'COMPLETED'),
(NULL,'LowSeason D','lsd@gmail.com',1,'2025-11-15','2025-11-17',4000000,'PENDING');

INSERT INTO booking VALUES
(NULL,'YearEnd A','yea@gmail.com',6,'2025-12-20','2025-12-27',35000000,'CONFIRMED'),
(NULL,'YearEnd B','yeb@gmail.com',7,'2025-12-22','2025-12-26',8000000,'CONFIRMED'),
(NULL,'YearEnd C','yec@gmail.com',3,'2025-12-24','2025-12-28',8000000,'PENDING');
