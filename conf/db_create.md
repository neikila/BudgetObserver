create table users (
    login VARCHAR(32) NOT NULL UNIQUE, 
    name VARCHAR(32) NOT NULL, 
    surname VARCHAR(32) NOT NULL, 
    email VARCHAR(64) NOT NULL
);

create table login (
    login VARCHAR(32) NOT NULL, 
    password VARCHAR(32) NOT NULL, 
PRIMARY KEY(login));

create table groups (
    id INT NOT NULL AUTO_INCREMENT, 
    description VARCHAR(32) default "No description", 
    author VARCHAR(32) NOT NULL,
    dateOfCreation TIMESTAMP default NOW(), 
PRIMARY KEY(id));

create table purchase (
    id INT NOT NULL AUTO_INCREMENT UNIQUE, 
    product VARCHAR(32) NOT NULL, 
    amount INT NOT NULL, 
    login varchar(32) NOT NULL, 
    groupID INT NOT NULL, 
    dateOfCreation TIMESTAMP default NOW(), 
PRIMARY KEY(id));

create table usersToGroup (
    groupID INT NOT NULL, 
    login VARCHAR(32) NOT NULL,
    groupName VARCHAR(32) NOT NULL
);


insert into users(login, name, surname, email) values("test", "test", "test", "test@mail.ru");
insert into groups (description, author) values('test group', 'test');
insert into usersToGroup(groupID, login, groupName) values(1, 'test', 'testGroup');
insert into purchase(product, amount, login, groupID) values("testProduct", 123, "test", (select id from groups where groupname='testGroup'));
insert into login(login, password) values("test", "test");


select users.* from users join usersToGroup as UTG where users.login = UTG.login and groupID = 1;