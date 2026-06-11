CREATE TABLE CarRegister(
    ID int primary key AUTO_INCREMENT,
    FullName varchar(50),
    Email varchar(50),
    Phone varchar(20),
    Address varchar(100),
    Country varchar(30),
    Style varchar(20),
    Engine varchar(20),
    Automaker varchar(30),
    Quantity int,
    Price double
);