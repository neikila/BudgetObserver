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
    groupname VARCHAR(32) NOT NULL, 
PRIMARY KEY(id));

create table purchase (
    id INT NOT NULL AUTO_INCREMENT UNIQUE, 
    product VARCHAR(32) NOT NULL, 
    amount INT NOT NULL, 
    login varchar(32) NOT NULL, 
    groupID INT NOT NULL, 
PRIMARY KEY(id));

create table usersToGroup (
    groupID INT NOT NULL, 
    login VARCHAR(32) NOT NULL
);


insert into users(login, name, surname, email) values("test", "test", "test", "test@mail.ru");
insert into groups (groupname) values('testGroup');
insert into usersToGroup(groupID, login) values((select id from groups where groupname='testGroup'),'test');
insert into purchase(product, amount, login, groupID) values("testProduct", 123, "test", (select id from groups where groupname='testGroup'));
insert into login(login, password) values("test", "test");


select users.* from users join usersToGroup as UTG where users.login = UTG.login and groupID = 1;