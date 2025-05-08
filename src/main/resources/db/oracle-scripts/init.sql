-- liquibase formatted sql
-- changeset oracle:001.1
--CREATE SCHEMA ALT_SCHEMA AUTHORIZATION toto;

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
