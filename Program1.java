/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Your solution goes in this class.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 *
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {


    /**
     * Determines whether a candidate Matching represents a solution to the stable matching problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
public boolean isStableMatching(Matching problem) {
    ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();
    ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();
    ArrayList<Integer> studentMatching = problem.getStudentMatching();

    int m = problem.getHighSchoolCount();
    int n = problem.getStudentCount();

    for (int i = 0; i < m; i++) {
        int highSchool = i;
        ArrayList<Integer> highSchoolPrefs = highschoolPrefs.get(highSchool);
        int currentMatchedStudent = studentMatching.get(highSchool);
        int currentMatchedStudentIndex = highSchoolPrefs.indexOf(currentMatchedStudent);

        // Check if there is a student who prefers the high school over the current matched student
        for (int j = currentMatchedStudentIndex + 1; j < highSchoolPrefs.size(); j++) {
            int student = highSchoolPrefs.get(j);
            ArrayList<Integer> studentPrefsForCurrentStudent = studentPrefs.get(student);

            // Check if the student prefers the high school over any other high school they are matched to
            for (int k = 0; k < studentPrefsForCurrentStudent.size(); k++) {
                int preferredHighSchool = studentPrefsForCurrentStudent.get(k);
                if (preferredHighSchool == highSchool) {
                    // The matching is unstable since the student prefers the high school over their current match
                    return false;
                }
                if (preferredHighSchool == studentMatching.get(student)) {
                    // The matching is stable for the current high school, move on to the next high school
                    break;
                }
            }
        }
    }

    return true; // All high schools
    }




        /*
         * Loop one: repeat until there are no remaining spots. This will work because there are more students than spots
         *      find a student who hasn't proposed to every school yet, if you can't, end the program and return the current matching
         *      find the next high school on his preference list
         *      
         *      
         * 
         * 
         * 
         */


    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_studentoptimal(Matching problem) {
        int m = problem.getHighSchoolCount();
        int n = problem.getStudentCount();
        ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();
        ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();
        ArrayList<Integer> highschoolSpots = problem.getHighSchoolSpots();
    
        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(n, -1)); // Initialize all students as unmatched
        ArrayList<Integer> studentNextProposeIndex = new ArrayList<>(Collections.nCopies(n, 0)); // Index to keep track of the next high school to propose
    
        int totalSpots = problem.totalHighSchoolSpots();
    
        while (totalSpots > 0) {
            int freeStudent = -1;
    
            // Find a free student who hasn't proposed to every high school yet
            for (int i = 0; i < n; i++) {
                if (studentMatching.get(i) == -1 && studentNextProposeIndex.get(i) < m) {
                    freeStudent = i;
                    break;
                }
            }
    
            if (freeStudent != -1) {
                int highSchool = studentPrefs.get(freeStudent).get(studentNextProposeIndex.get(freeStudent));
                studentNextProposeIndex.set(freeStudent, studentNextProposeIndex.get(freeStudent) + 1);
    
                if (highschoolSpots.get(highSchool) > 0) {
                    studentMatching.set(freeStudent, highSchool);
                    highschoolSpots.set(highSchool, highschoolSpots.get(highSchool) - 1);
                    totalSpots--;
                } else {
                    int currentMatchedStudent = -1;
    
                    // Find the student currently matched to the high school
                    for (int i = 0; i < n; i++) {
                        if (studentMatching.get(i) == highSchool) {
                            currentMatchedStudent = i;
                            break;
                        }
                    }
    
                    // Check if the high school prefers the free student over the current matched student
                    if (highschoolPrefs.get(highSchool).indexOf(freeStudent) < highschoolPrefs.get(highSchool).indexOf(currentMatchedStudent)) {
                        studentMatching.set(freeStudent, highSchool);
                        studentMatching.set(currentMatchedStudent, -1);
                    }
                }
            }
        }
    
        return new Matching(problem, studentMatching);
    }
    














































    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_highschooloptimal(Matching problem) {
        int m = problem.getHighSchoolCount();
        int n = problem.getStudentCount();
        ArrayList<ArrayList<Integer>> highschoolPrefs = problem.getHighSchoolPreference();
        ArrayList<ArrayList<Integer>> studentPrefs = problem.getStudentPreference();
        ArrayList<Integer> highschoolSpots = problem.getHighSchoolSpots();
    
        ArrayList<Integer> highschoolMatching = new ArrayList<>(Collections.nCopies(m, -1)); // Initialize all high schools as unmatched
        ArrayList<LinkedList<Integer>> studentProposals = new ArrayList<>(m); // Track the student proposals for each high school
    
        for (int i = 0; i < m; i++) {
            studentProposals.add(new LinkedList<>());
        }
    
        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(n, -1)); // Initialize all students as unmatched
        int totalSpots = problem.totalHighSchoolSpots();
    
        while (totalSpots > 0) {
            for (int i = 0; i < m; i++) {
                if (!studentProposals.get(i).isEmpty()) {
                    int student = studentProposals.get(i).poll(); // Get the next student to consider
    
                    if (highschoolMatching.get(i) == -1) {
                        highschoolMatching.set(i, student); // High school accepts the student
                        studentMatching.set(student, i); // Student matches with the high school
                        totalSpots--;
                    } else {
                        int currentMatchedStudent = highschoolMatching.get(i);
    
                        // Check if the high school prefers the proposing student over the current matched student
                        if (studentPrefs.get(i).indexOf(student) < studentPrefs.get(i).indexOf(currentMatchedStudent)) {
                            highschoolMatching.set(i, student); // High school accepts the proposing student
                            studentMatching.set(student, i); // Student matches with the high school
                            studentMatching.set(currentMatchedStudent, -1); // Previous match becomes unmatched
                        }
                    }
                }
            }
        }
    
        return new Matching(problem, studentMatching);
    }
    
    
}

/*
 * matching object includes
 * m number of high schools
 * n number of students
 * highschool rpeference list (arraylist of arraylist)
 * stdeunt preference 
 * high school spots
 * student matching
 * 
 * 
 */