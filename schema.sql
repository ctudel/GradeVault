-- Class Table
CREATE TABLE class (
    class_id int PRIMARY KEY AUTO_INCREMENT,
    course_number varchar(20) NOT NULL,
    term varchar(4) NOT NULL,
    section_number int NOT NULL,
    description TEXT,
    UNIQUE(course_number, term, section_number)
);

-- Category Table
CREATE TABLE category (
    category_name varchar(255) PRIMARY KEY NOT NULL
);

-- Student Table
CREATE TABLE student (
    studentID int NOT NULL,
    username varchar(255) NOT NULL,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    PRIMARY KEY(username),
    UNIQUE(studentID)
);

-- Enrolled Students Table
CREATE TABLE enrolled_students (
    class_id int NOT NULL,
    username varchar(255) NOT NULL,
    PRIMARY KEY (class_id, username),
    FOREIGN KEY (class_id) REFERENCES class(class_id),
    FOREIGN KEY (username) REFERENCES student(username)
);

-- Class Categories Table
CREATE TABLE class_categories (
    class_id int NOT NULL,
    category_name varchar(255),
    weight double NOT NULL,
    PRIMARY KEY (class_id, category_name),
    FOREIGN KEY (class_id) REFERENCES class(class_id),
    FOREIGN KEY (category_name) REFERENCES category(category_name)
);

-- Assignment Table
CREATE TABLE assignment (
    assignment_name varchar(255) NOT NULL,
    class_id int NOT NULL,
    category_name varchar(255) NOT NULL,
    point_value double NOT NULL,
    description TEXT,
    PRIMARY KEY (assignment_name, class_id),
    FOREIGN KEY (class_id, category_name) REFERENCES class_categories(class_id, category_name)
);

-- Student Assignments Table
CREATE TABLE student_assignments (
    grade double DEFAULT 0.0,
    username varchar(255) NOT NULL,
    class_id int NOT NULL,
    assignment_name varchar(255) NOT NULL,
    PRIMARY KEY (username, class_id, assignment_name),
    FOREIGN KEY (username) REFERENCES student(username),
    FOREIGN KEY (assignment_name, class_id) REFERENCES assignment(assignment_name, class_id)
);
