DROP DATABASE IF EXISTS MLR7;

CREATE DATABASE IF NOT EXISTS MLR7;
USE MLR7;
# -----------------------------------------------------------------------------
#       TABLE : ACTIONS
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ACTIONS
 (
   ACTION_ID INTEGER(11) NOT NULL  ,
   BOOK_ID INTEGER(11) NOT NULL  ,
   USER_ID INTEGER(11) NOT NULL  ,
   USER_ID_PERFORM INTEGER(11) NOT NULL  ,
   ACTION_TYPE VARCHAR(255) NOT NULL  ,
   ACTION_DATE DATETIME NOT NULL  ,
   ACTION_DETAILS TEXT NOT NULL  
   , PRIMARY KEY (ACTION_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : SEATS
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS SEATS
 (
   SEAT_ID INTEGER(11) NOT NULL  ,
   SEAT_LOCATION VARCHAR(255) NOT NULL  ,
   SEAT_TYPE VARCHAR(255) NOT NULL  ,
   SEAT_STATUS VARCHAR(255) NOT NULL  
   , PRIMARY KEY (SEAT_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : AUTHOR
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS AUTHOR
 (
   AUTHOR_ID INTEGER(11) NOT NULL  ,
   AUTHOR_NAME VARCHAR(255) NOT NULL  
   , PRIMARY KEY (AUTHOR_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : LOANS
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS LOANS
 (
   LOAN_ID INTEGER(11) NOT NULL  ,
   USER_ID INTEGER(11) NOT NULL  ,
   BOOK_ID INTEGER(11) NOT NULL  ,
   USER_ID_ISSUE INTEGER(11) NOT NULL  ,
   LOAN_ISSUE_DATE DATE NOT NULL  ,
   LOAN_RETURN_DATE DATE NOT NULL  
   , PRIMARY KEY (LOAN_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : CATEGORIES
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS CATEGORIES
 (
   CAT_ID INTEGER(11) NOT NULL  ,
   CAT_NAME VARCHAR(255) NOT NULL  
   , PRIMARY KEY (CAT_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : USERS
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS USERS
 (
   USER_ID INTEGER(11) NOT NULL  ,
   USER_FNAME VARCHAR(255) NOT NULL  ,
   USER_LNAME VARCHAR(255) NOT NULL  ,
   USER_SEX CHAR(32) NOT NULL  ,
   USER_PHONE VARCHAR(255) NOT NULL  ,
   USER_EMAIL VARCHAR(255) NOT NULL  ,
   USER_PWD VARCHAR(255) NOT NULL  ,
   USER_ROLE VARCHAR(255) NOT NULL  ,
   USER_STATUS VARCHAR(255) NOT NULL  
   , PRIMARY KEY (USER_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : BOOKS
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS BOOKS
 (
   BOOK_ID INTEGER(11) NOT NULL  ,
   CAT_ID INTEGER(11) NOT NULL  ,
   BOOK_TITLE VARCHAR(255) NOT NULL  ,
   BOOK_PAGES INTEGER(11) NOT NULL  ,
   BOOK_PUB_YEAR VARCHAR(4) NOT NULL  
   , PRIMARY KEY (BOOK_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : AUTHORING
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS AUTHORING
 (
   AUTHOR_ID INTEGER(11) NOT NULL  ,
   BOOK_ID INTEGER(11) NOT NULL  ,
   CONTRIBUTION_TYPE VARCHAR(255) NOT NULL  
   , PRIMARY KEY (AUTHOR_ID,BOOK_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : RESERVE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS RESERVE
 (
   SEAT_ID INTEGER(11) NOT NULL  ,
   USER_ID INTEGER(11) NOT NULL  ,
   RESERVE_DUE DATETIME NOT NULL  ,
   RESERVE_END DATETIME NOT NULL  
   , PRIMARY KEY (SEAT_ID,USER_ID) 
 ) 
 comment = "";


# -----------------------------------------------------------------------------
#       CREATION DES REFERENCES DE TABLE
# -----------------------------------------------------------------------------


ALTER TABLE ACTIONS 
  ADD FOREIGN KEY FK_ACTIONS_BOOKS (BOOK_ID)
      REFERENCES BOOKS (BOOK_ID) ;


ALTER TABLE ACTIONS 
  ADD FOREIGN KEY FK_ACTIONS_USERS (USER_ID)
      REFERENCES USERS (USER_ID) ;


ALTER TABLE ACTIONS 
  ADD FOREIGN KEY FK_ACTIONS_USERS1 (USER_ID_PERFORM)
      REFERENCES USERS (USER_ID) ;


ALTER TABLE LOANS 
  ADD FOREIGN KEY FK_LOANS_USERS (USER_ID)
      REFERENCES USERS (USER_ID) ;


ALTER TABLE LOANS 
  ADD FOREIGN KEY FK_LOANS_BOOKS (BOOK_ID)
      REFERENCES BOOKS (BOOK_ID) ;


ALTER TABLE LOANS 
  ADD FOREIGN KEY FK_LOANS_USERS1 (USER_ID_ISSUE)
      REFERENCES USERS (USER_ID) ;


ALTER TABLE BOOKS 
  ADD FOREIGN KEY FK_BOOKS_CATEGORIES (CAT_ID)
      REFERENCES CATEGORIES (CAT_ID) ;


ALTER TABLE AUTHORING 
  ADD FOREIGN KEY FK_AUTHORING_AUTHOR (AUTHOR_ID)
      REFERENCES AUTHOR (AUTHOR_ID) ;


ALTER TABLE AUTHORING 
  ADD FOREIGN KEY FK_AUTHORING_BOOKS (BOOK_ID)
      REFERENCES BOOKS (BOOK_ID) ;


ALTER TABLE RESERVE 
  ADD FOREIGN KEY FK_RESERVE_SEATS (SEAT_ID)
      REFERENCES SEATS (SEAT_ID) ;


ALTER TABLE RESERVE 
  ADD FOREIGN KEY FK_RESERVE_USERS (USER_ID)
      REFERENCES USERS (USER_ID) ;

