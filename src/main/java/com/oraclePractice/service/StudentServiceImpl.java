package com.oraclePractice.service;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

@Service("studentService")
public class StudentServiceImpl implements StudentService {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void createStudent() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("addstudent");
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(4, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(5, String.class, ParameterMode.IN);
        //set value
        query.setParameter(1, Long.parseLong("302"));
        query.setParameter(2, "spring boot");
        query.setParameter(3, "spring@gmail.com");
        query.setParameter(4, "01771598949");
        query.setParameter(5, "413152");
        //now execute the query

        query.execute();


        // PL/SQL procedure below
        String procedure = """
                create PROCEDURE addstudent (
                    id   IN    student_test.id%TYPE,
                    name  IN   student_test.name%TYPE,
                    email IN  student_test.email%TYPE,
                    phone IN   student_test.phone%TYPE,
                    password VARCHAR2
                ) IS
                BEGIN
                    INSERT INTO student_test (
                        id,
                        name,
                        email,
                        password,
                        phone
                    ) VALUES (
                        id,
                        name,
                        email,
                        password,
                        phone
                    );
                                
                    dbms_output.put_line('Added student' || name);
                EXCEPTION
                    WHEN OTHERS THEN
                        dbms_output.put_line('ERROR ON Student Add : ' || name);
                END;
                /
                """;
    }


    @Override
    public Object getAllEmployee(Long studentId) {
        // PL/SQL procedure below
        String procedure = """
                create procedure getEmployeeAll(
                    id IN EMPLOYEE.ID%type,
                    e_disp OUT SYS_REFCURSOR
                )
                    is begin
                    open e_disp for select *  from EMPLOYEE where EMPLOYEE.ID = id;
                end;
                /
                """;
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getEmployeeAll");
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Object.class, ParameterMode.REF_CURSOR);
        //set value
        query.setParameter(1, Long.parseLong("5"));
        //now execute the query
        query.execute();

        //Get output parameters
        return query.getResultList();

    }

    @Override
    public Object getAllEmployeeByPackageProcedureCall() {
        String packageBodyWithProcedureInterface = """
                create PACKAGE getAllEmployeeByPackage AS
                    PROCEDURE getAll(
                        e_disp OUT SYS_REFCURSOR
                    );
                END getAllEmployeeByPackage;
                /
                """;

        String packageInterfaceImpl = """
                create package body getAllEmployeeByPackage as
                    procedure getAll(
                        e_disp OUT SYS_REFCURSOR
                    ) IS
                    BEGIN
                        open e_disp for select *  from EMPLOYEE;
                    END getAll;
                end getAllEmployeeByPackage;
                /
                """;

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getAllEmployeeByPackage2.getAll");
        query.registerStoredProcedureParameter(1, Object.class, ParameterMode.REF_CURSOR);
        //now execute the query
        query.execute();

        //Get output parameters
        return query.getResultList();
    }

    @Override
    public Object getAllEmployeeByIDUsingPackageProcedureCall(Long id) {

        String procedure = """
                 procedure getEmployeeById(
                        id_in IN EMPLOYEE.ID%type,
                        e_disp OUT SYS_REFCURSOR
                    ) IS
                        hasEmployee number;
                    BEGIN
                        hasEmployee := 0;
                        SELECT count(*) into hasEmployee from EMPLOYEE where ID = id_in;
                        IF hasEmployee <> 0 THEN --here <> means !=
                            OPEN e_disp FOR SELECT * FROM EMPLOYEE WHERE ID = id_in;
                        ELSE
                            --return empty SYS_REFCURSOR couse 1=2 not equal always
                            OPEN e_disp FOR SELECT * FROM EMPLOYEE WHERE 1=2;
                        END IF;
                    END getEmployeeById;
                """;
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getAllEmployeeByPackage2.getEmployeeById");
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Object.class, ParameterMode.REF_CURSOR);
        //set value
        query.setParameter(1, id);
        //now execute the query
        query.execute();

        //Get output parameters
        List<Object> result = (List<Object>) query.getResultList();
        if (result.isEmpty()) {
            return "No Data Found";
        }
        return result;
    }

    /**
     * show how to call a function inside a procedure
     */
    @Override
    public Object getEmployeeAllInsideFunctionCall(Long id) {
        String procedure = """
                create or replace procedure getEmployeeAllInsideFunctionCall(
                    empl_idd IN EMPLOYEE.ID%type,
                    e_disp OUT SYS_REFCURSOR
                )
                    is
                begin
                    e_disp := getEmployee(empl_idd);--call below function
                end;
                /
                """;
        String function = """
                create function getEmployee(
                    emp_id EMPLOYEE.ID%type
                ) return SYS_REFCURSOR
                    is
                    expectedEmployee SYS_REFCURSOR;
                begin
                    open expectedEmployee for select * from EMPLOYEE where ID = emp_id;
                    if SQL%FOUND then
                        return expectedEmployee;
                    end if;
                    if SQL%NOTFOUND then
                        open expectedEmployee for select * from EMPLOYEE where 4 = 6;
                        return expectedEmployee;
                    end if;
                    return expectedEmployee;
                EXCEPTION
                    WHEN OTHERS THEN
                        DBMS_OUTPUT.PUT_LINE('EMPLOYEE find error');
                end;
                /
                """;
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getEmployeeAllInsideFunctionCall");
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Object.class, ParameterMode.REF_CURSOR);
        query.setParameter(1, id);
        query.execute();
        return query.getResultList();
    }

    @Override
    public Object getPersonByCallingViewAndSequences(Long id) {
        String PL_SQL_WORK_Flow = """
                               //For Create Sequences that will return a unique val
                               CREATE SEQUENCE IncrementId
                                   INCREMENT BY 10
                                   START WITH 10
                                   MINVALUE 10
                                   MAXVALUE 100
                                   CYCLE
                                   CACHE 2;
                                   
                               //Now create 2 relational table using the Sequences
                               
                               ##Persons table
                               CREATE TABLE Persons
                               (
                                   PersonID   int,
                                   Profession varchar(255)
                               );
                               ## PersonsProfession table
                               CREATE TABLE PersonsProfession
                               (
                                   PersonID   int,
                                   Profession varchar(255)
                               );
                               // Now Insert data into two table
                               
                               insert into PERSONS values (IncrementId.nextval, 'Kibria', 'Golam', 'Tejgaon', 'Dhaka');--IncrementId.nextval call the sequence here
                               
                               
                               INSERT INTO PersonsProfession values (40, 'Teacher');
                               INSERT INTO PersonsProfession values (50, 'Engineer');
                               INSERT INTO PersonsProfession values (60, 'Doctor');
                               
                              //Now Create a View that that will return a view called PersonDetails
                              create or REPLACE VIEW PersonDetails
                              AS
                              SELECT p.*, pd.*
                              from PERSONS p
                                       left join PersonsProfession pd on p.ID = pd.PERSONID
                              order by p.ID desc;
                              
                              
                              // Now create a procedure that will return result from the view
                              create or replace procedure getPersonByCallingView(
                                  id_in IN PERSONS.ID%type,
                                  e_disp OUT SYS_REFCURSOR
                              ) IS
                                  hasPerson number;
                              BEGIN
                                  hasPerson := 0;
                                  SELECT count(*) into hasPerson from PERSONDETAILS pd where pd.ID = id_in;
                                  IF hasPerson <> 0 THEN --here <> means !=
                                      OPEN e_disp FOR SELECT * from PERSONDETAILS pd where pd.ID = id_in;
                                  ELSE
                                      --return empty SYS_REFCURSOR couse 1=2 not equal always
                                      OPEN e_disp FOR SELECT * FROM EMPLOYEE WHERE 1 = 2;
                                  END IF;
                              END getPersonByCallingView;
                """;


        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("getPersonByCallingView");
        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Object.class, ParameterMode.REF_CURSOR);
        query.setParameter(1, id);
        query.execute();
        return query.getResultList();
    }


    @Override
    public void createXmlBasedStudent() {
        String trx_xml = "<statement>";
        trx_xml = trx_xml + "<rowrecord>";
        trx_xml = trx_xml + "<STUDENT_ID>" + 5 + "</STUDENT_ID>";
        trx_xml = trx_xml + "<STUDENT_NAME>" + "manik khan" + "</STUDENT_NAME>";
        trx_xml = trx_xml + "<STUDENT_EMAIL>" + "Kibria@gmail.com" + "</STUDENT_EMAIL>";
        trx_xml = trx_xml + "</rowrecord>";
        trx_xml = trx_xml + "</statement>";

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("saveEmployeeex");
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.OUT);

        //set value
        query.setParameter(1, trx_xml);
        //now execute the query
        query.execute();

        int output = (int) query.getOutputParameterValue(2);
        System.out.println("" + output);

        // PL/SQL procedure below
        String procedure = """
                create procedure saveEmployeeex(
                     stmt IN CLOB,
                     output OUT number
                 ) AS
                     id_T    EMPLOYEE.ID%TYPE;
                     name_T  EMPLOYEE.NAME%TYPE;
                     email_T EMPLOYEE.EMAIL%TYPE;
                     tempRow number;
                     CURSOR field_cursor
                         IS
                         SELECT XMLTYPE.EXTRACT(VALUE(a),
                                                '/rowrecord/STUDENT_ID/text()').getStringVal(),
                                XMLTYPE.EXTRACT(VALUE(a),
                                                '/rowrecord/STUDENT_NAME/text()').getStringVal(),
                                XMLTYPE.EXTRACT(VALUE(a), '/rowrecord/STUDENT_EMAIL/text()').getStringVal()
                 
                         FROM TABLE (
                                  XMLSEQUENCE(
                                          XMLTYPE(stmt).EXTRACT('/statement/rowrecord'))) a;
                 begin
                 
                     output := 0;
                     tempRow:=0;
                     open field_cursor;
                 
                     LOOP
                         FETCH field_cursor
                             INTO id_T,name_T,email_T;
                         exit when field_cursor%NOTFOUND;
                     END LOOP;
                     select count(*) into tempRow from EMPLOYEE where ID = id_T;
                     IF tempRow is not null then
                         update EMPLOYEE
                         set EMPLOYEE.ID    = id_T,
                             EMPLOYEE.NAME  = name_T,
                             EMPLOYEE.EMAIL =email_T
                         where ID = id_T;
                         output := 1;
                     else
                         insert into EMPLOYEE(id, name, email) VALUES (id_T, name_T, email_T);
                         commit;
                         output := 1;
                     end if;
                 END
                """;
    }


}
