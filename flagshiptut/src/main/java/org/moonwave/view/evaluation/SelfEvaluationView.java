package org.moonwave.view.evaluation;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.moonwave.jpa.bo.GenericBO;
import org.moonwave.jpa.bo.UserBO;
import org.moonwave.jpa.model.EvaluationObjective;
import org.moonwave.jpa.model.Semester;
import org.moonwave.jpa.model.User;
import org.moonwave.jpa.model.Week;
import org.moonwave.util.StackTrace;
import org.moonwave.view.BaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SelfEvaluation View
 *
 * @author moonwave
 *
 */
@ManagedBean
@ViewScoped
public class SelfEvaluationView extends BaseView {

    private static final long serialVersionUID = 1L;
    static final Logger LOG = LoggerFactory.getLogger(SelfEvaluationView.class);

    EvaluationObjective evaluation;
    List<User> students;
    List<User> tutors;
    List<Semester> semesters;
    List<Week> weeks;


    boolean byStudent = false; // evaluation by student 
    boolean byTutor = false;   // evaluation by tutor

    @PostConstruct
    public void init() {

        byStudent = (super.getParameter("student") != null) ? true : false;
        byTutor = (super.getParameter("tutor") != null) ? true : false;

        // get a list of students
        students = new UserBO().findAllStudents();
        Collections.sort(students);

        // get a list of tutors
        tutors = new UserBO().findAllTutors();
        Collections.sort(tutors);

        GenericBO<Semester> bo = new GenericBO<>(Semester.class);
        semesters = bo.findAll();

        GenericBO<Week> weekbo = new GenericBO<>(Week.class);
        weeks = weekbo.findAll();

        resetfields();
    }

    public EvaluationObjective getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationObjective evaluation) {
        this.evaluation = evaluation;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public List<User> getTutors() {
        return tutors;
    }

    public void setTutors(List<User> tutors) {
        this.tutors = tutors;
    }

    public List<Semester> getSemesters() {
        return semesters;
    }

    public void setSemesters(List<Semester> semesters) {
        this.semesters = semesters;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    public boolean isByStudent() {
        return byStudent;
    }

    public void setByStudent(boolean byStudent) {
        this.byStudent = byStudent;
    }

    public boolean isByTutor() {
        return byTutor;
    }

    public void setByTutor(boolean byTutor) {
        this.byTutor = byTutor;
    }

    public String save() {
        // validation
//        if (StringUtil.nullOrEmpty(subject)) {
//            super.error("Subject is empty");
//            return null;
//        }
        try {
            if (this.byStudent) {
                evaluation.setStudentEvaluation(true);
            } else if (this.byTutor) {
                evaluation.setStudentEvaluation(false);
            }
            evaluation.setCreateTime(super.getSqlTimestamp());
            super.getBasebo().persist(evaluation);

            // reset fields
            resetfields();
            super.info("Data was saved successfully");
        } catch (Exception e) {
            super.error("Sorry, an error occurred, please contact your administrator");
            LOG.error(StackTrace.toString(e));
        }
        return null;
    }

    // ========================================================= Private methods

    private void resetfields() {
        evaluation =  new EvaluationObjective();
        if (byStudent) {
            evaluation.setUserId(super.getLoggedInUser().getId());
        }
        if (byTutor) {
            evaluation.setTutorId(super.getLoggedInUser().getId());
        }
    }
}
