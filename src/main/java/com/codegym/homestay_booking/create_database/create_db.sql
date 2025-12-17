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

insert into booking(guest_name, guest_email, room_id, check_in_date, check_out_date, total_price, status) values
("mr.a","a@gmail.com",1,"2025/12/17","2025/12/18",1000000,"PENDING"),
("mr.b","b@gmail.com",2,"2025/12/17","2025/12/18",1000000,"PENDING"),
("mr.c","c@gmail.com",3,"2025/12/17","2025/12/18",1000000,"PENDING");