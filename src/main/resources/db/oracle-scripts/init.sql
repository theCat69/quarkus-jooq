-- liquibase formatted sql
-- changeset oracle:001.1

CREATE TABLE dept (
  dept_id number(10) NOT NULL,
  dept_name varchar2(50) NOT NULL,
  CONSTRAINT dept_pk PRIMARY KEY (dept_id)
);

CREATE TABLE emp (
  emp_no number(10) NOT NULL,
  emp_name varchar2(50) NOT NULL,
  dept_id number(10),
  salary number(6),
  CONSTRAINT emp_pk PRIMARY KEY (emp_no),
  CONSTRAINT dept_fk FOREIGN KEY (dept_id) REFERENCES dept(dept_id),
  CONSTRAINT dept_id_unique UNIQUE (dept_id)
);

create index emp_index_emp_name_salary on emp (emp_name, salary);
-- rollback DROP INDEX emp_index_emp_name_salary
-- rollback DROP TABLE dept
-- rollback DROP TABLE emp

-- changeset oracle:001.2
insert into dept(dept_id, dept_name)
values(1, 'departement');
insert into dept(dept_id, dept_name)
values(2, 'departement2');
insert into dept(dept_id, dept_name)
values(3, 'departement3');
insert into dept(dept_id, dept_name)
values(4, 'departement3');
insert into dept(dept_id, dept_name)
values(5, 'departement3');
insert into dept(dept_id, dept_name)
values(6, 'departement3');
insert into dept(dept_id, dept_name)
values(7, 'departement3');
insert into dept(dept_id, dept_name)
values(8, 'departement3');
insert into dept(dept_id, dept_name)
values(9, 'departement3');
insert into dept(dept_id, dept_name)
values(10, 'departement3');
insert into dept(dept_id, dept_name)
values(11, 'departement3');
insert into dept(dept_id, dept_name)
values(12, 'departement3');
insert into dept(dept_id, dept_name)
values(13, 'departement3');
insert into dept(dept_id, dept_name)
values(14, 'departement3');
insert into dept(dept_id, dept_name)
values(16, 'departement3');
insert into dept(dept_id, dept_name)
values(17, 'departement3');
insert into dept(dept_id, dept_name)
values(18, 'departement3');
insert into dept(dept_id, dept_name)
values(19, 'departement3');
insert into dept(dept_id, dept_name)
values(20, 'departement3');
insert into dept(dept_id, dept_name)
values(21, 'departement3');
insert into dept(dept_id, dept_name)
values(22, 'departement3');
insert into dept(dept_id, dept_name)
values(23, 'departement3');
insert into dept(dept_id, dept_name)
values(24, 'departement3');
insert into dept(dept_id, dept_name)
values(25, 'departement3');
insert into dept(dept_id, dept_name)
values(26, 'departement3');
insert into dept(dept_id, dept_name)
values(27, 'departement3');
insert into dept(dept_id, dept_name)
values(28, 'departement3');
insert into dept(dept_id, dept_name)
values(29, 'departement3');
insert into dept(dept_id, dept_name)
values(30, 'departement3');
insert into dept(dept_id, dept_name)
values(31, 'departement3');
insert into dept(dept_id, dept_name)
values(32, 'departement3');

insert into emp(emp_no, emp_name, dept_id, salary)
values (1, 'name', 1, 5000);
insert into emp(emp_no, emp_name, dept_id, salary)
values (2, 'name2', 2, 7000);
-- rollback TRUNCATE TABLE DEPT
-- rollback TRUNCATE TABLE EMP
